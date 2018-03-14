package frc.team1836.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.SPI.Port;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team1836.robot.Constants;
import frc.team1836.robot.Constants.DRIVE;
import frc.team1836.robot.Constants.LOGGING;
import frc.team1836.robot.RobotState;
import frc.team1836.robot.RobotState.DriveControlState;
import frc.team1836.robot.auto.trajectory.Path;
import frc.team1836.robot.auto.trajectory.PathFollower;
import frc.team1836.robot.util.drivers.MkGyro;
import frc.team1836.robot.util.drivers.MkTalon;
import frc.team1836.robot.util.drivers.MkTalon.TalonPosition;
import frc.team1836.robot.util.logging.CrashTracker;
import frc.team1836.robot.util.logging.ReflectingCSVWriter;
import frc.team1836.robot.util.loops.Loop;
import frc.team1836.robot.util.loops.Looper;
import frc.team1836.robot.util.math.MkMath;
import frc.team1836.robot.util.other.Subsystem;
import frc.team1836.robot.util.state.DriveSignal;
import frc.team1836.robot.util.state.TrajectoryStatus;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;

public class Drive extends Subsystem {

    private final ReflectingCSVWriter<DriveDebugOutput> mCSVWriter;
    private final MkTalon leftDrive, rightDrive;
    private final MkGyro navX;
    private DriveDebugOutput mDebug = new DriveDebugOutput();
    private PathFollower pathFollower = null;
    private TrajectoryStatus leftStatus;
    private TrajectoryStatus rightStatus;
    private DriveSignal currentSetpoint;
    private double lastAngle = 0;
    private boolean brakePath = true;
    private double goalAngle = 0;

    private Drive() {
        leftDrive = new MkTalon(DRIVE.LEFT_MASTER_ID, DRIVE.LEFT_SLAVE_ID, TalonPosition.Left);
        rightDrive = new MkTalon(DRIVE.RIGHT_MASTER_ID, DRIVE.RIGHT_SLAVE_ID, TalonPosition.Right);
        leftDrive.setPIDF();
        rightDrive.setPIDF();
        navX = new MkGyro(Port.kMXP);

        leftDrive.invertMaster(DRIVE.LEFT_MASTER_INVERT);
        leftDrive.invertSlave(DRIVE.LEFT_SLAVE_INVERT);
        leftDrive.setSensorPhase(DRIVE.LEFT_INVERT_SENSOR);

        rightDrive.invertMaster(DRIVE.RIGHT_MASTER_INVERT);
        rightDrive.invertSlave(DRIVE.RIGHT_SLAVE_INVERT);
        rightDrive.setSensorPhase(DRIVE.RIGHT_INVERT_SENSOR);

        mCSVWriter = new ReflectingCSVWriter<DriveDebugOutput>(LOGGING.DRIVE_LOG_PATH,
                DriveDebugOutput.class);
        leftStatus = TrajectoryStatus.NEUTRAL;
        rightStatus = TrajectoryStatus.NEUTRAL;
        currentSetpoint = DriveSignal.BRAKE;
    }

    public static Drive getInstance() {
        return InstanceHolder.mInstance;
    }

    /* Controls Drivetrain in PercentOutput Mode (without closed loop control) */
    public synchronized void setOpenLoop(DriveSignal signal) {
        RobotState.mDriveControlState = DriveControlState.OPEN_LOOP;
        leftDrive.set(ControlMode.PercentOutput, signal.getLeft(), signal.getBrakeMode());
        rightDrive.set(ControlMode.PercentOutput, signal.getRight(), signal.getBrakeMode());
        currentSetpoint = signal;
    }

    /**
     * Controls Drivetrain in Closed-loop velocity Mode
     * Method sets Talons in Native Units per 100ms
     *
     * @param signal An object that contains left and right velocities (inches per sec)
     */

    public synchronized void setVelocitySetpoint(DriveSignal signal, double leftFeed,
                                                 double rightFeed) {
        if (RobotState.mDriveControlState == DriveControlState.PATH_FOLLOWING) {
            leftDrive.set(ControlMode.Velocity, signal.getLeftNativeVelTraj(), signal.getBrakeMode(),
                    leftFeed);
            rightDrive.set(ControlMode.Velocity, signal.getRightNativeVelTraj(), signal.getBrakeMode(),
                    rightFeed);
        } else {
            RobotState.mDriveControlState = DriveControlState.VELOCITY_SETPOINT;
            leftDrive.set(ControlMode.Velocity, signal.getLeftNativeVel(), signal.getBrakeMode());
            rightDrive.set(ControlMode.Velocity, signal.getRightNativeVel(), signal.getBrakeMode());
        }
        currentSetpoint = signal;
    }

    /**
     * @param path     Robot Path
     * @param dist_tol Position Tolerance for Path Follower
     * @param ang_tol  Robot Angle Tolerance for Path Follower (Degrees)
     */
    public synchronized void setDrivePath(Path path, double dist_tol, double ang_tol,
                                          boolean brakeMode) {
        CrashTracker.logMarker("Began Path: " + path.getName());
        brakePath = brakeMode;
        double offset = lastAngle - Pathfinder
                .boundHalfDegrees(Pathfinder.r2d(path.getLeftWheelTrajectory().get(0).heading));
        for (Trajectory.Segment segment : path.getLeftWheelTrajectory().segments) {
            segment.heading = Pathfinder.boundHalfDegrees(Pathfinder.r2d(segment.heading) + offset);
        }
        for (Trajectory.Segment segment : path.getRightWheelTrajectory().segments) {
            segment.heading = Pathfinder.boundHalfDegrees(Pathfinder.r2d(segment.heading) + offset);
        }
        leftDrive.resetEncoder();
        rightDrive.resetEncoder();
        pathFollower = new PathFollower(path, dist_tol, ang_tol);
        RobotState.mDriveControlState = RobotState.DriveControlState.PATH_FOLLOWING;
    }

    public synchronized boolean isPathFinished() {
        if (pathFollower.getFinished()) {
            lastAngle = pathFollower.getEndHeading();
            RobotState.mDriveControlState = DriveControlState.OPEN_LOOP;
            pathFollower = null;
            leftStatus = TrajectoryStatus.NEUTRAL;
            rightStatus = TrajectoryStatus.NEUTRAL;
            return true;
        }
        return false;
    }

    /**
     * Called from Looper during Path Following
     * Gets a TrajectoryStatus containing output velocity and Desired Trajectory Information for logging
     * Inputs Position, Speed and Angle to Trajectory Follower
     * Creates a new Drive Signal that is then set as a velocity setpoint
     */
    private synchronized void updatePathFollower() {
        TrajectoryStatus leftUpdate = pathFollower
                .getLeftVelocity(leftDrive.getPosition(), leftDrive.getSpeed(),
                        -navX.getYaw());
        TrajectoryStatus rightUpdate = pathFollower
                .getRightVelocity(rightDrive.getPosition(), rightDrive.getSpeed(),
                        -navX.getYaw());
        leftStatus = leftUpdate;
        rightStatus = rightUpdate;
        if (isEncodersConnected()) {
            setVelocitySetpoint(new DriveSignal(leftUpdate.getOutput(), rightUpdate.getOutput(),
                            brakePath),
                    leftUpdate.getArbFeed(), rightUpdate.getArbFeed());
        } else {
            leftDrive.set(ControlMode.PercentOutput,
                    ((1.0 / MkMath.RPMToInchesPerSec(DRIVE.RIGHT_RPM_MAX)) * leftUpdate.getOutput()), false,
                    leftUpdate.getArbFeed());
            rightDrive.set(ControlMode.PercentOutput,
                    ((1.0 / MkMath.RPMToInchesPerSec(DRIVE.LEFT_RPM_MAX)) * rightUpdate.getOutput()), false,
                    rightUpdate.getArbFeed());
        }
    }

    private synchronized void setTurnGoal(double goal) {
        goalAngle = goal;
        RobotState.mDriveControlState = DriveControlState.TURN_IN_PLACE;
    }

    private synchronized void updateTurnInPlace() {
        double angleDelta = navX.getYaw() - goalAngle;
        double delta_v = DRIVE.PATH_WHEELBASE * Math.toRadians(angleDelta) / (2 * Constants.DRIVE.kTrackScrubFactor);
        leftDrive.set(ControlMode.MotionMagic, -delta_v + leftDrive.getPosition(), true);
        rightDrive.set(ControlMode.MotionMagic, delta_v + rightDrive.getPosition(), true);
    }

    @Override
    public void outputToSmartDashboard() {
        leftDrive.updateSmartDash();
        rightDrive.updateSmartDash();
        //SmartDashboard.putString("Drive State", RobotState.mDriveControlState.toString());
        SmartDashboard.putBoolean("Drivetrain Status",
                leftDrive.isEncoderConnected() && rightDrive.isEncoderConnected());

        if (RobotState.mDriveControlState == DriveControlState.PATH_FOLLOWING
                && leftStatus != TrajectoryStatus.NEUTRAL) {
            SmartDashboard.putNumber("NavX Yaw", navX.getYaw());
            //SmartDashboard.putNumber("Left Desired Velocity", currentSetpoint.getLeft());
            //SmartDashboard.putNumber("Right Desired Velocity", currentSetpoint.getRight());
            SmartDashboard.putNumber("Desired Heading", leftStatus.getSeg().heading);
            SmartDashboard.putNumber("Heading Error", leftStatus.getAngError());
            //SmartDashboard.putNumber("Left Desired Position", leftStatus.getSeg().position);
            SmartDashboard.putNumber("Left Theoretical Vel", leftStatus.getSeg().velocity);
            SmartDashboard.putNumber("Left Position Error", leftStatus.getPosError());
            SmartDashboard.putNumber("Left Desired Velocity Error", leftStatus.getVelError());
            //SmartDashboard.putNumber("Right Desired Position", leftStatus.getSeg().position);
            SmartDashboard.putNumber("Right Position Error", leftStatus.getPosError());
            SmartDashboard.putNumber("Right Theoretical Vel", rightStatus.getSeg().velocity);
            SmartDashboard.putNumber("Right Desired Velocity Error", leftStatus.getVelError());
            //SmartDashboard.putNumber("Left Arb Feed", leftStatus.getArbFeed());
            //SmartDashboard.putNumber("Right Arb Feed", rightStatus.getArbFeed());
        }
    }

    public double getYaw() {
        return navX.getYaw();
    }

    @Override
    public void slowUpdate() {
        updateDebugOutput(Timer.getMatchTime());
        mCSVWriter.add(mDebug);
        mCSVWriter.write();
    }

    @Override
    public void checkSystem() {
        leftDrive.set(ControlMode.PercentOutput, 1, true);
        rightDrive.set(ControlMode.PercentOutput, 1, true);
        Timer.delay(5.0);
        leftDrive.set(ControlMode.PercentOutput, 0, true);
        rightDrive.set(ControlMode.PercentOutput, 0, true);
        boolean check = true;
        if (leftDrive.getPosition() < Constants.DRIVE.MIN_TEST_POS
                || leftDrive.getSpeed() < Constants.DRIVE.MIN_TEST_VEL) {
            System.out.println("FAILED - LEFT DRIVE FAILED TO REACH REQUIRED SPEED OR POSITION");
            System.out
                    .println("Position: " + leftDrive.getPosition() + " Speed: " + leftDrive.getSpeed());
            check = false;
            CrashTracker.logMarker(
                    "Left Drive Test Failed - Vel: " + leftDrive.getSpeed() + " Pos: " + leftDrive
                            .getPosition());
        } else {
            System.out
                    .println("Position: " + leftDrive.getPosition() + " Speed: " + leftDrive.getSpeed());
        }
        if (rightDrive.getPosition() < Constants.DRIVE.MIN_TEST_POS
                || rightDrive.getSpeed() < Constants.DRIVE.MIN_TEST_VEL) {
            System.out.println("FAILED - RIGHT DRIVE FAILED TO REACH REQUIRED SPEED OR POSITION");
            System.out
                    .println("Position: " + rightDrive.getPosition() + "Speed: " + rightDrive.getSpeed());
            check = false;
            CrashTracker.logMarker(
                    "Right Drive Test Failed - Vel: " + rightDrive.getSpeed() + " Pos: " + rightDrive
                            .getPosition());
        } else {
            System.out
                    .println("Position: " + rightDrive.getPosition() + " Speed: " + rightDrive.getSpeed());
        }

        if (!navX.isConnected()) {
            System.out.println("FAILED - NAVX DISCONNECTED");
            check = false;
        }

        if (check) {
            System.out.println("Drive Test Success");
        }

        leftDrive.resetConfig();
        rightDrive.resetConfig();
    }

    public void configVelocityControl(){
        leftDrive.configTeleopVelocity();
        rightDrive.configTeleopVelocity();
    }

    public boolean gyroConnected(){
        return navX.isConnected();
    }
    @Override
    public void registerEnabledLoops(Looper enabledLooper) {
        Loop mLoop = new Loop() {

            @Override
            public void onStart(double timestamp) {
                synchronized (Drive.this) {
                    leftDrive.resetEncoder();
                    rightDrive.resetEncoder();
                    navX.zeroYaw();
                }
            }

            /**
             * Updated from mEnabledLoop in Robot.java
             * Controls drivetrain during Path Following and Turn In Place and logs
             * Drivetrain data in all modes
             * @param timestamp In Seconds Since Code Start
             */
            @Override
            public void onLoop(double timestamp) {
                synchronized (Drive.this) {
                    switch (RobotState.mDriveControlState) {
                        case OPEN_LOOP:
                            return;
                        case VELOCITY_SETPOINT:
                            return;
                        case PATH_FOLLOWING:
                            if (pathFollower != null) {
                                updatePathFollower();
                            }
                            return;
                        case TURN_IN_PLACE:
                            updateTurnInPlace();
                            return;
                        default:
                            System.out
                                    .println("Unexpected drive control state: " + RobotState.mDriveControlState);
                            break;
                    }
                }
            }

            @Override
            public void onStop(double timestamp) {
                setOpenLoop(DriveSignal.BRAKE);
                mCSVWriter.flush();
            }
        };
        enabledLooper.register(mLoop);
    }

    public boolean isEncodersConnected() {
        return leftDrive.isEncoderConnected() && rightDrive.isEncoderConnected();
    }

    private void updateDebugOutput(double timestamp) {

        mDebug.timestamp = timestamp;
        mDebug.controlMode = RobotState.mDriveControlState.toString();
        mDebug.leftOutput = leftDrive.getPercentOutput();
        mDebug.rightOutput = rightDrive.getPercentOutput();
        mDebug.rightPosition = leftDrive.getPosition();
        mDebug.leftPosition = rightDrive.getPosition();
        mDebug.leftVelocity = leftDrive.getSpeed();
        mDebug.rightVelocity = rightDrive.getSpeed();
        mDebug.heading = navX.getYaw();
        mDebug.leftSetpoint = currentSetpoint.getLeft();
        mDebug.rightSetpoint = currentSetpoint.getRight();

        if (RobotState.mDriveControlState == DriveControlState.PATH_FOLLOWING) {
            mDebug.leftDesiredPos = leftStatus.getSeg().position;
            mDebug.leftDesiredVel = leftStatus.getSeg().velocity;
            mDebug.rightDesiredPos = rightStatus.getSeg().position;
            mDebug.rightDesiredVel = rightStatus.getSeg().velocity;
            mDebug.desiredHeading = leftStatus.getSeg().heading;
            mDebug.headingError = leftStatus.getAngError();
            mDebug.leftVelError = leftStatus.getVelError();
            mDebug.leftPosError = leftStatus.getPosError();
            mDebug.rightVelError = rightStatus.getVelError();
            mDebug.rightPosError = rightStatus.getPosError();
            mDebug.desiredX = (leftStatus.getSeg().x + rightStatus.getSeg().x) / 2;
            mDebug.desiredY = (leftStatus.getSeg().y + rightStatus.getSeg().y) / 2;
        } else {
            mDebug.leftDesiredPos = 0;
            mDebug.leftDesiredVel = 0;
            mDebug.rightDesiredPos = 0;
            mDebug.rightDesiredVel = 0;
            mDebug.desiredHeading = 0;
            mDebug.headingError = 0;
            mDebug.leftVelError = 0;
            mDebug.leftPosError = 0;
            mDebug.rightVelError = 0;
            mDebug.rightPosError = 0;
            mDebug.desiredX = 0;
            mDebug.desiredY = 0;
        }

    }

    public static class DriveDebugOutput {

        double timestamp;
        String controlMode;
        double leftOutput;
        double rightOutput;
        double leftSetpoint;
        double rightSetpoint;
        double leftPosition;
        double rightPosition;
        double leftVelocity;
        double rightVelocity;
        double heading;
        double desiredHeading;
        double headingError;
        double leftDesiredVel;
        double leftDesiredPos;
        double leftPosError;
        double leftVelError;
        double rightDesiredVel;
        double rightDesiredPos;
        double rightPosError;
        double rightVelError;
        double desiredX;
        double desiredY;
    }

    private static class InstanceHolder {

        private static final Drive mInstance = new Drive();
    }

}
