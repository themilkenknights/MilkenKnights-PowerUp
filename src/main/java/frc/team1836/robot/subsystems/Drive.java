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
import frc.team1836.robot.util.drivers.MkGyro;
import frc.team1836.robot.util.drivers.MkTalon;
import frc.team1836.robot.util.drivers.MkTalon.TalonPosition;
import frc.team1836.robot.util.logging.ReflectingCSVWriter;
import frc.team1836.robot.util.loops.Loop;
import frc.team1836.robot.util.loops.Looper;
import frc.team1836.robot.util.other.Subsystem;
import frc.team1836.robot.util.state.DriveSignal;
import frc.team1836.robot.util.state.TrajectoryStatus;
import frc.team254.lib.trajectory.Path;
import frc.team254.lib.trajectory.PathFollower;

public class Drive extends Subsystem {

    private final ReflectingCSVWriter<DriveDebugOutput> mCSVWriter;
    private final MkTalon leftDrive, rightDrive;
    private final MkGyro navX;
    private PathFollower pathFollower = null;
    private DriveDebugOutput mDebug = new DriveDebugOutput();
    private TrajectoryStatus leftStatus;
    private TrajectoryStatus rightStatus;
    private DriveSignal currentSetpoint;

    private Drive() {
        leftDrive = new MkTalon(DRIVE.LEFT_MASTER_ID, DRIVE.LEFT_SLAVE_ID, TalonPosition.Left);
        rightDrive = new MkTalon(DRIVE.RIGHT_MASTER_ID, DRIVE.RIGHT_SLAVE_ID, TalonPosition.Right);
        leftDrive.setPIDF();
        rightDrive.setPIDF();
        navX = new MkGyro(Port.kMXP);
        navX.zeroYaw();

        leftDrive.invertMaster(DRIVE.LEFT_MASTER_INVERT);
        leftDrive.invertSlave(DRIVE.LEFT_SLAVE_INVERT);
        leftDrive.setSensorPhase(DRIVE.LEFT_INVERT_SENSOR);

        rightDrive.invertMaster(DRIVE.RIGHT_MASTER_INVERT);
        rightDrive.invertSlave(DRIVE.RIGHT_SLAVE_INVERT);
        rightDrive.setSensorPhase(DRIVE.RIGHT_INVERT_SENSOR);

        mCSVWriter = new ReflectingCSVWriter<>(LOGGING.DRIVE_LOG_PATH,
                DriveDebugOutput.class);
        leftStatus = TrajectoryStatus.NEUTRAL;
        rightStatus = TrajectoryStatus.NEUTRAL;
        currentSetpoint = DriveSignal.NEUTRAL;
        leftDrive.setBrakeMode();
        rightDrive.setBrakeMode();
    }

    public static Drive getInstance() {
        return InstanceHolder.mInstance;
    }

    /*
                    Controls Drivetrain in PercentOutput Mode (without closed loop control)
*/
    public synchronized void setOpenLoop(DriveSignal signal) {
        RobotState.mDriveControlState = DriveControlState.OPEN_LOOP;
        leftDrive.set(ControlMode.PercentOutput, signal.getLeft());
        rightDrive.set(ControlMode.PercentOutput, signal.getRight());
        currentSetpoint = signal;
    }

    /**
     * Controls Drivetrain in Closed-loop velocity Mode
     * Method sets Talons in Native Units per 100ms
     *
     * @param signal An object that contains left and right velocities (inches per sec)
     */

    public synchronized void setVelocitySetpoint(DriveSignal signal) {
        if (RobotState.mDriveControlState == DriveControlState.PATH_FOLLOWING) {
            leftDrive.set(ControlMode.Velocity, signal.getLeftNativeVelTraj());
            rightDrive.set(ControlMode.Velocity, signal.getRightNativeVelTraj());
        } else {
            RobotState.mDriveControlState = DriveControlState.VELOCITY_SETPOINT;
            leftDrive.set(ControlMode.Velocity, signal.getLeftNativeVel());
            rightDrive.set(ControlMode.Velocity, signal.getRightNativeVel());
        }

        currentSetpoint = signal;
    }

    /**
     * @param path     Robot Path
     * @param dist_tol Position Tolerance for Path Follower
     * @param ang_tol  Robot Angle Tolerance for Path Follower (Degrees)
     */
    public synchronized void setDrivePath(Path path, double dist_tol, double ang_tol) {
        leftDrive.resetEncoder();
        rightDrive.resetEncoder();
        navX.zeroYaw();
        pathFollower = new PathFollower(path, dist_tol, ang_tol);
        RobotState.mDriveControlState = RobotState.DriveControlState.PATH_FOLLOWING;
    }

    public boolean isPathFinished() {
        if (pathFollower.getFinished()) {
            RobotState.mDriveControlState = DriveControlState.OPEN_LOOP;
            setOpenLoop(DriveSignal.NEUTRAL);
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
    private void updatePathFollower() {
        TrajectoryStatus leftUpdate = pathFollower
                .getLeftVelocity(leftDrive.getPosition(), leftDrive.getSpeed(),
                        navX.getFullYaw());
        TrajectoryStatus rightUpdate = pathFollower
                .getRightVelocity(rightDrive.getPosition(), rightDrive.getSpeed(),
                        navX.getFullYaw());

        leftStatus = leftUpdate;
        rightStatus = rightUpdate;
        setVelocitySetpoint(new DriveSignal(leftUpdate.getOutput(), rightUpdate.getOutput()));
    }

    @Override
    public void writeToLog() {
        mCSVWriter.write();
    }

    @Override
    public void outputToSmartDashboard() {
        leftDrive.updateSmartDash();
        rightDrive.updateSmartDash();
        SmartDashboard.putString("Drive State", RobotState.mDriveControlState.toString());
        SmartDashboard.putNumber("NavX Yaw", navX.getYaw());
        SmartDashboard.putBoolean("Drivetrain Status",
                leftDrive.isEncoderConnected() && rightDrive.isEncoderConnected());
        if (RobotState.mDriveControlState == DriveControlState.PATH_FOLLOWING) {
            SmartDashboard.putNumber("Left Desired Velocity", currentSetpoint.getLeft());
            SmartDashboard.putNumber("Right Desired Velocity", currentSetpoint.getRight());
            SmartDashboard.putNumber("NavX Full Yaw", navX.getFullYaw());
            SmartDashboard.putNumber("Desired Heading", leftStatus.getSeg().heading);
            SmartDashboard.putNumber("Heading Error", leftStatus.getAngError());
            SmartDashboard.putNumber("Left Desired Position", leftStatus.getSeg().pos);
            SmartDashboard.putNumber("Left Theoretical Vel", leftStatus.getSeg().vel);
            SmartDashboard.putNumber("Left Position Error", leftStatus.getPosError());
            SmartDashboard.putNumber("Left Desired Velocity Error", leftStatus.getVelError());
            SmartDashboard.putNumber("Right Desired Position", leftStatus.getSeg().pos);
            SmartDashboard.putNumber("Right Position Error", leftStatus.getPosError());
            SmartDashboard.putNumber("Right Theoretical Vel", rightStatus.getSeg().vel);
            SmartDashboard.putNumber("Right Desired Velocity Error", leftStatus.getVelError());
        }
    }

    @Override
    public void stop() {
        setOpenLoop(DriveSignal.NEUTRAL);
    }

    @Override
    public void zeroSensors() {
        leftDrive.resetEncoder();
        rightDrive.resetEncoder();
        navX.zeroYaw();
    }

    @Override
    public void checkSystem() {
        leftDrive.set(ControlMode.PercentOutput, 0.5);
        rightDrive.set(ControlMode.PercentOutput, 0.5);
        Timer.delay(2.0);
        leftDrive.set(ControlMode.PercentOutput, 0);
        rightDrive.set(ControlMode.PercentOutput, 0);
        if (leftDrive.getPosition() < Constants.DRIVE.MIN_TEST_POS
                || leftDrive.getSpeed() < Constants.DRIVE.MIN_TEST_VEL) {
            System.out.println("FAILED - LEFT DRIVE FAILED TO REACH REQUIRED SPEED OR POSITION");
            System.out.println("Position: " + leftDrive.getPosition() + "Speed: " + leftDrive.getSpeed());
        }
        if (rightDrive.getPosition() < Constants.DRIVE.MIN_TEST_POS
                || rightDrive.getSpeed() < Constants.DRIVE.MIN_TEST_VEL) {
            System.out.println("FAILED - RIGHT DRIVE FAILED TO REACH REQUIRED SPEED OR POSITION");
            System.out
                    .println("Position: " + rightDrive.getPosition() + "Speed: " + rightDrive.getSpeed());
        }

        if (!navX.isConnected()) {
            System.out.println("FAILED - NAVX DISCONNECTED");
        }

    }

    @Override
    public void registerEnabledLoops(Looper enabledLooper) {
        Loop mLoop = new Loop() {

            @Override
            public void onStart(double timestamp) {
                synchronized (Drive.this) {

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
                    updateDebugOutput(timestamp);
                    mCSVWriter.add(mDebug);
                    switch (RobotState.mDriveControlState) {
                        case OPEN_LOOP:
                            zeroTrajectoryStatus();
                            return;
                        case VELOCITY_SETPOINT:
                            zeroTrajectoryStatus();
                            return;
                        case PATH_FOLLOWING:
                            updatePathFollower();
                            updateTrajectoryStatus();
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
                stop();
            }
        };
        enabledLooper.register(mLoop);
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
        mDebug.heading = navX.getFullYaw();
        mDebug.leftSetpoint = currentSetpoint.getLeft();
        mDebug.rightSetpoint = currentSetpoint.getRight();
    }

    private void zeroTrajectoryStatus() {
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

    private void updateTrajectoryStatus() {
        mDebug.leftDesiredPos = leftStatus.getSeg().pos;
        mDebug.leftDesiredVel = leftStatus.getSeg().vel;
        mDebug.rightDesiredPos = rightStatus.getSeg().pos;
        mDebug.rightDesiredVel = rightStatus.getSeg().vel;
        mDebug.desiredHeading = leftStatus.getSeg().heading;
        mDebug.headingError = leftStatus.getAngError();
        mDebug.leftVelError = leftStatus.getVelError();
        mDebug.leftPosError = leftStatus.getPosError();
        mDebug.rightVelError = rightStatus.getVelError();
        mDebug.rightPosError = rightStatus.getPosError();
        mDebug.desiredX = (leftStatus.getSeg().x + rightStatus.getSeg().x) / 2;
        mDebug.desiredY = (leftStatus.getSeg().y + rightStatus.getSeg().y) / 2;
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
