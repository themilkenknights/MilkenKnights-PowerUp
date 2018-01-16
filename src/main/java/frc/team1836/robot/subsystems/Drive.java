package frc.team1836.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team1836.robot.Constants.DRIVE;
import frc.team1836.robot.Constants.LOGGING;
import frc.team1836.robot.RobotState;
import frc.team1836.robot.RobotState.DriveControlState;
import frc.team1836.robot.util.drivers.MkDrive;
import frc.team1836.robot.util.drivers.MkDrive.DrivetrainSide;
import frc.team1836.robot.util.drivers.MkGyro;
import frc.team1836.robot.util.logging.ReflectingCSVWriter;
import frc.team1836.robot.util.loops.Loop;
import frc.team1836.robot.util.loops.Looper;
import frc.team1836.robot.util.math.MkMath;
import frc.team1836.robot.util.other.Subsystem;
import frc.team1836.robot.util.state.DriveSignal;
import frc.team1836.robot.util.state.TrajectoryStatus;
import frc.team254.lib.trajectory.Path;
import frc.team254.lib.trajectory.PathFollower;

public class Drive extends Subsystem {

	private final ReflectingCSVWriter<DriveDebugOutput> mCSVWriter;
	private final MkDrive leftDrive, rightDrive;
	private final MkGyro navX;
	private PathFollower pathFollower = null;
	private DriveDebugOutput mDebug = new DriveDebugOutput();
	private TrajectoryStatus leftStatus;
	private TrajectoryStatus rightStatus;
	private DriveSignal currentSetpoint;
	private double angSpeed;

	private Drive() {
		leftDrive = new MkDrive(DRIVE.LEFT_MASTER_ID, DRIVE.LEFT_SLAVE_ID, DrivetrainSide.Left);
		rightDrive = new MkDrive(DRIVE.RIGHT_MASTER_ID, DRIVE.RIGHT_SLAVE_ID, DrivetrainSide.Right);
		navX = new MkGyro(SPI.Port.kMXP);

		leftDrive.invert(true);
		rightDrive.invert(false);
		mCSVWriter = new ReflectingCSVWriter<>(LOGGING.DRIVE_LOG_PATH,
				DriveDebugOutput.class);
		leftStatus = TrajectoryStatus.NEUTRAL;
		rightStatus = TrajectoryStatus.NEUTRAL;
		currentSetpoint = DriveSignal.NEUTRAL;
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
		pathFollower = new PathFollower(path, dist_tol, ang_tol);
	}

	public boolean isPathFinished() {
		if (pathFollower.getFinished() && pathFollower.onTarget()) {
			pathFollower = null;
			return true;
		}
		return false;
	}

	private synchronized void updateTurnInPlace() {
		TrajectoryStatus leftUpdate = pathFollower
				.getLeftVelocity(navX.getYaw(), navX.getRawGyroZ(), 0);
		TrajectoryStatus rightUpdate = pathFollower
				.getRightVelocity(navX.getYaw(), navX.getRawGyroZ(), 0);
		setVelocitySetpoint(new DriveSignal(MkMath.AngleToVel(leftUpdate.getOutput()),
				MkMath.AngleToVel(rightUpdate.getOutput())));
		leftStatus = leftUpdate;
		rightStatus = rightUpdate;
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
						Math.toRadians(navX.getFullYaw()));
		TrajectoryStatus rightUpdate = pathFollower
				.getRightVelocity(rightDrive.getPosition(), rightDrive.getSpeed(),
						Math.toRadians(navX.getFullYaw()));

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
		SmartDashboard.putNumber("NavX Velocity", navX.getRawGyroZ());
		SmartDashboard.putNumber("NavX Yaw", navX.getFullYaw());
		SmartDashboard.putNumber("Left Desired Velocity", currentSetpoint.getLeft());
		SmartDashboard.putNumber("Right Desired Velocity", currentSetpoint.getRight());
		SmartDashboard.putString("Drive State", RobotState.mDriveControlState.toString());
		if (RobotState.mDriveControlState == DriveControlState.PATH_FOLLOWING) {
			SmartDashboard.putNumber("Desired Heading", leftStatus.getSeg().heading);
			SmartDashboard.putNumber("Heading Error", leftStatus.getAngError());
			SmartDashboard.putNumber("Left Desired Position", leftStatus.getSeg().pos);
			SmartDashboard.putNumber("Left Position Error", leftStatus.getPosError());
			SmartDashboard.putNumber("Left Desired Velocity Error", leftStatus.getVelError());
			SmartDashboard.putNumber("Right Desired Position", leftStatus.getSeg().pos);
			SmartDashboard.putNumber("Right Position Error", leftStatus.getPosError());
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
		leftDrive.testDrive();
		rightDrive.testDrive();
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
			 * @param timestamp
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
						case TURN_IN_PLACE:
							updateTurnInPlace();
							updateTrajectoryStatus();
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

	public void zeroGyro() {
		navX.zeroYaw();
	}

	public double getYaw() {
		return navX.getYaw();
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
