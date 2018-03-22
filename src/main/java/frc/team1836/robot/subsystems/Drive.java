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
import frc.team1836.robot.util.auto.trajectory.Path;
import frc.team1836.robot.util.auto.trajectory.PathFollower;
import frc.team1836.robot.util.drivers.MkGyro;
import frc.team1836.robot.util.drivers.MkTalon;
import frc.team1836.robot.util.drivers.MkTalon.TalonPosition;
import frc.team1836.robot.util.logging.Log;
import frc.team1836.robot.util.logging.ReflectingCSVWriter;
import frc.team1836.robot.util.math.MkMath;
import frc.team1836.robot.util.state.DriveSignal;
import frc.team1836.robot.util.state.TrajectoryStatus;
import frc.team1836.robot.util.structure.Subsystem;
import frc.team1836.robot.util.structure.loops.Loop;
import frc.team1836.robot.util.structure.loops.Looper;
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
   * Controls Drivetrain in Closed-loop velocity Mode Method sets Talons in Native Units per 100ms
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
   * @param path Robot Path
   * @param dist_tol Position Tolerance for Path Follower
   * @param ang_tol Robot Angle Tolerance for Path Follower (Degrees)
   */
  public synchronized void setDrivePath(Path path, double dist_tol, double ang_tol,
      boolean brakeMode) {
    Log.marker("Began Path: " + path.getName());
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

  /*
  Called from Auto Action to check when the path finishes. Saves the last angle to use with the next path and resets the Trajectory Status
   */
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
   * Called from Looper during Path Following Gets a TrajectoryStatus containing output velocity and Desired Trajectory Information for logging Inputs Position, Speed and Angle to Trajectory Follower
   * Creates a new Drive Signal that is then set as a velocity setpoint
   */
  private synchronized void updatePathFollower() {
    TrajectoryStatus leftUpdate = pathFollower
        .getLeftVelocity(leftDrive.getPosition(), leftDrive.getSpeed(), -navX.getYaw());
    TrajectoryStatus rightUpdate = pathFollower
        .getRightVelocity(rightDrive.getPosition(), rightDrive.getSpeed(), -navX.getYaw());
    leftStatus = leftUpdate;
    rightStatus = rightUpdate;
    if (isEncodersConnected()) {
      setVelocitySetpoint(
          new DriveSignal(leftUpdate.getOutput(), rightUpdate.getOutput(), brakePath),
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

  @Override
  public void outputToSmartDashboard() {
    leftDrive.updateSmartDash();
    rightDrive.updateSmartDash();
    SmartDashboard.putString("Drive State", RobotState.mDriveControlState.toString());
    SmartDashboard.putBoolean("Drivetrain Status",
        leftDrive.isEncoderConnected() && rightDrive.isEncoderConnected());
    SmartDashboard.putNumber("Current Difference",
        leftDrive.getCurrentOutput() - rightDrive.getCurrentOutput());

    if (RobotState.mDriveControlState == DriveControlState.PATH_FOLLOWING
        && leftStatus != TrajectoryStatus.NEUTRAL) {
      SmartDashboard.putNumber("NavX Yaw", navX.getYaw());
      SmartDashboard.putNumber("Left Desired Velocity", currentSetpoint.getLeft());
      SmartDashboard.putNumber("Right Desired Velocity", currentSetpoint.getRight());
      SmartDashboard.putNumber("Desired Heading", leftStatus.getSeg().heading);
      SmartDashboard.putNumber("Heading Error", leftStatus.getAngError());
      SmartDashboard.putNumber("Left Desired Position", leftStatus.getSeg().position);
      SmartDashboard.putNumber("Left Theoretical Vel", leftStatus.getSeg().velocity);
      SmartDashboard.putNumber("Left Position Error", leftStatus.getPosError());
      SmartDashboard.putNumber("Left Desired Velocity Error", leftStatus.getVelError());
      SmartDashboard.putNumber("Right Desired Position", leftStatus.getSeg().position);
      SmartDashboard.putNumber("Right Position Error", leftStatus.getPosError());
      SmartDashboard.putNumber("Right Theoretical Vel", rightStatus.getSeg().velocity);
      SmartDashboard.putNumber("Right Desired Velocity Error", leftStatus.getVelError());
      SmartDashboard.putNumber("Left Arb Feed", leftStatus.getArbFeed());
      SmartDashboard.putNumber("Right Arb Feed", rightStatus.getArbFeed());
    }
  }

  @Override
  public void slowUpdate(double timestamp) {
    if (RobotState.mMatchState != RobotState.MatchState.DISABLED) {
      updateDebugOutput(timestamp);
      mCSVWriter.add(mDebug);
      mCSVWriter.write();
    }
  }

  @Override
  public void checkSystem() {
    boolean check = true;
    leftDrive.resetEncoder();
    leftDrive.setCoastMode();
    leftDrive.setSlaveTalon(ControlMode.PercentOutput, 0);
    leftDrive.setMasterTalon(ControlMode.PercentOutput, 1);
    Timer.delay(2.0);
    if (leftDrive.getPosition() < Constants.DRIVE.MIN_TEST_POS || leftDrive.getSpeed() < Constants.DRIVE.MIN_TEST_VEL) {
      Log.marker("FAILED - LEFT MASTER DRIVE FAILED TO REACH REQUIRED SPEED OR POSITION");
      Log.marker("Left Master Drive Test Failed - Vel: " + leftDrive.getSpeed() + " Pos: " + leftDrive.getPosition());
      check = false;
    } else {
      System.out.println("Left Master Position: " + leftDrive.getPosition() + "Left Master Speed: " + leftDrive.getSpeed());
      Log.verbose("Left Master Position: " + leftDrive.getPosition() + " Left Master Speed: " + leftDrive.getSpeed());
    }

    leftDrive.setSlaveTalon(ControlMode.PercentOutput, 0);
    leftDrive.setMasterTalon(ControlMode.PercentOutput, 0);
    leftDrive.resetEncoder();
    Timer.delay(1.0);

    leftDrive.setMasterTalon(ControlMode.PercentOutput, 0);
    leftDrive.setSlaveTalon(ControlMode.PercentOutput, 1);
    Timer.delay(2.0);
    if (leftDrive.getPosition() < Constants.DRIVE.MIN_TEST_POS || leftDrive.getSpeed() < Constants.DRIVE.MIN_TEST_VEL) {
      Log.marker("FAILED - LEFT SLAVE DRIVE FAILED TO REACH REQUIRED SPEED OR POSITION");
      Log.marker("Left Slave Drive Test Failed - Vel: " + leftDrive.getSpeed() + " Pos: " + leftDrive.getPosition());
      check = false;
    } else {
      System.out.println("Left Slave Position: " + leftDrive.getPosition() + "Left Slave Speed: " + leftDrive.getSpeed());
      Log.verbose("Left Slave Position: " + leftDrive.getPosition() + " Left Slave Speed: " + leftDrive.getSpeed());
    }
    leftDrive.setSlaveTalon(ControlMode.PercentOutput, 0);
    leftDrive.setMasterTalon(ControlMode.PercentOutput, 0);
    leftDrive.resetEncoder();
    Timer.delay(1.0);

    rightDrive.setCoastMode();
    rightDrive.setSlaveTalon(ControlMode.PercentOutput, 0);
    rightDrive.setMasterTalon(ControlMode.PercentOutput, 1);
    Timer.delay(2.0);
    if (rightDrive.getPosition() < Constants.DRIVE.MIN_TEST_POS || rightDrive.getSpeed() < Constants.DRIVE.MIN_TEST_VEL) {
      Log.marker("FAILED - RIGHT MASTER DRIVE FAILED TO REACH REQUIRED SPEED OR POSITION");
      Log.marker("Right Drive Test Failed - Vel: " + leftDrive.getSpeed() + " Pos: " + leftDrive.getPosition());
      check = false;
    } else {
      System.out.println("Right Master Position: " + rightDrive.getPosition() + "Right Master Speed: " + rightDrive.getSpeed());
      Log.verbose("Right Master Position: " + rightDrive.getPosition() + " Right Master Speed: " + rightDrive.getSpeed());
    }

    rightDrive.setSlaveTalon(ControlMode.PercentOutput, 0);
    rightDrive.setMasterTalon(ControlMode.PercentOutput, 0);
    rightDrive.resetEncoder();
    Timer.delay(1.0);

    rightDrive.setMasterTalon(ControlMode.PercentOutput, 0);
    rightDrive.setSlaveTalon(ControlMode.PercentOutput, 1);
    Timer.delay(2.0);
    if (rightDrive.getPosition() < Constants.DRIVE.MIN_TEST_POS || rightDrive.getSpeed() < Constants.DRIVE.MIN_TEST_VEL) {
      Log.marker("FAILED - RIGHT SLAVE DRIVE FAILED TO REACH REQUIRED SPEED OR POSITION");
      Log.marker("Right Drive Test Failed - Vel: " + rightDrive.getSpeed() + " Pos: " + rightDrive.getPosition());
      check = false;
    } else {
      System.out.println("Right Slave Position: " + rightDrive.getPosition() + "Right Slave Speed: " + rightDrive.getSpeed());
      Log.verbose("Right Slave Position: " + rightDrive.getPosition() + " Right Slave Speed: " + rightDrive.getSpeed());
    }
    rightDrive.setMasterTalon(ControlMode.PercentOutput, 0);
    rightDrive.setSlaveTalon(ControlMode.PercentOutput, 0);

    if (!navX.isConnected()) {
      System.out.println("FAILED - NAVX DISCONNECTED");
      check = false;
    }

    if (check) {
      System.out.println("Drive Test Success");
      Log.verbose("Drive Test Success");
    }

    leftDrive.resetConfig();
    rightDrive.resetConfig();
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
            default:
              Log.marker("Unexpected drive control state: " + RobotState.mDriveControlState);
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

  public double getYaw() {
    return navX.getYaw();
  }

  /*
  Change Talon PID Constants to reduce oscillation during teleop driving
   */
  public void configVelocityControl() {
    leftDrive.configTeleopVelocity();
    rightDrive.configTeleopVelocity();
  }

  public boolean gyroConnected() {
    return navX.isConnected();
  }

  public boolean isEncodersConnected() {
    return leftDrive.isEncoderConnected() && rightDrive.isEncoderConnected();
  }

  private void updateDebugOutput(double timestamp) {

    mDebug.timestamp = timestamp;
    mDebug.controlMode = RobotState.mDriveControlState.toString();
    mDebug.leftOutput = leftDrive.getMotorVoltage();
    mDebug.rightOutput = rightDrive.getMotorVoltage();
    mDebug.rightPosition = leftDrive.getPosition();
    mDebug.leftPosition = rightDrive.getPosition();
    mDebug.leftVelocity = leftDrive.getSpeed();
    mDebug.rightVelocity = rightDrive.getSpeed();
    mDebug.heading = navX.getYaw();
    mDebug.leftSetpoint = currentSetpoint.getLeft();
    mDebug.rightSetpoint = currentSetpoint.getRight();
    mDebug.leftCurrent = leftDrive.getCurrentOutput();
    mDebug.rightCurrent = rightDrive.getCurrentOutput();

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

    public double timestamp;
    public String controlMode;
    public double leftOutput;
    public double rightOutput;
    public double leftSetpoint;
    public double rightSetpoint;
    public double leftPosition;
    public double rightPosition;
    public double leftVelocity;
    public double rightVelocity;
    public double heading;
    public double desiredHeading;
    public double headingError;
    public double leftDesiredVel;
    public double leftDesiredPos;
    public double leftPosError;
    public double leftVelError;
    public double rightDesiredVel;
    public double rightDesiredPos;
    public double rightPosError;
    public double rightVelError;
    public double desiredX;
    public double desiredY;
    public double leftCurrent;
    public double rightCurrent;
  }

  private static class InstanceHolder {

    private static final Drive mInstance = new Drive();
  }

}
