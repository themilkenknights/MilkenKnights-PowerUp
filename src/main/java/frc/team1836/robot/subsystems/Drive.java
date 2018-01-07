package frc.team1836.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import frc.team1836.robot.Constants.DRIVE;
import frc.team1836.robot.util.drivers.MkDrive;
import frc.team1836.robot.util.drivers.MkGyro;
import frc.team1836.robot.util.logging.ReflectingCSVWriter;
import frc.team1836.robot.util.loops.Loop;
import frc.team1836.robot.util.loops.Looper;
import frc.team1836.robot.util.state.DriveSignal;
import frc.team254.lib.trajectory.Path;
import frc.team254.lib.trajectory.PathFollower;

public class Drive extends Subsystem {

	private static Drive mInstance = new Drive();
	private final ReflectingCSVWriter<DriveDebugOutput> mCSVWriter;
	private final MkDrive leftDrive, rightDrive;
	private final MkGyro navX;
	private DriveControlState mDriveControlState;
	private PathFollower pathFollower = null;

	private Drive() {
		leftDrive = new MkDrive(DRIVE.LEFT_MASTER_ID, DRIVE.LEFT_SLAVE_ID);
		rightDrive = new MkDrive(DRIVE.RIGHT_MASTER_ID, DRIVE.RIGHT_SLAVE_ID);
		navX = new MkGyro(new AHRS(SPI.Port.kMXP));

		mDriveControlState = DriveControlState.OPEN_LOOP;
		mCSVWriter = new ReflectingCSVWriter<DriveDebugOutput>("/home/lvuser/DRIVE-LOGS.csv",
				DriveDebugOutput.class);
	}

	public static Drive getInstance() {
		return mInstance;
	}

	public synchronized void setOpenLoop(DriveSignal signal) {
		mDriveControlState = DriveControlState.OPEN_LOOP;
		leftDrive.set(ControlMode.PercentOutput, signal.getLeft());
		rightDrive.set(ControlMode.PercentOutput, signal.getRight());
	}

	public synchronized void setVelocitySetpoint(DriveSignal signal) {
		mDriveControlState = DriveControlState.VELOCITY_SETPOINT;
		leftDrive.set(ControlMode.Velocity, signal.getLeftNativeVel());
		rightDrive.set(ControlMode.Velocity, signal.getLeftNativeVel());
	}

	public synchronized void setWantDrivePath(Path path, double dist_tol, double ang_tol) {
			mDriveControlState = DriveControlState.PATH_FOLLOWING;
		pathFollower = new PathFollower(path, dist_tol, ang_tol);
	}

	@Override
	public void writeToLog() {
		mCSVWriter.write();
	}

	@Override
	public void outputToSmartDashboard() {

	}

	@Override
	public void stop() {
		setOpenLoop(DriveSignal.NEUTRAL);
	}

	@Override
	public void zeroSensors() {
		leftDrive.resetEncoder();
		rightDrive.resetEncoder();
	}

	@Override
	public void checkSystem() {
		leftDrive.testDrive();
		rightDrive.testDrive();
		if(!navX.isConnected()){
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

			@Override
			public void onLoop(double timestamp) {
				synchronized (Drive.this) {

				}
			}

			@Override
			public void onStop(double timestamp) {
				stop();
			}
		};
		enabledLooper.register(mLoop);
	}

	public enum DriveControlState {
		OPEN_LOOP, // open loop voltage control
		VELOCITY_SETPOINT, // velocity PID control
		PATH_FOLLOWING, // used for autonomous driving
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
	}

}
