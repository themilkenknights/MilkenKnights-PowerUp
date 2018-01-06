package frc.team1836.robot.subsystems;

import frc.team1836.robot.util.loops.Loop;
import frc.team1836.robot.util.loops.Looper;

public class Drive extends Subsystem {

	private static Drive mInstance = new Drive();


	public static Drive getInstance() {
		return mInstance;
	}


	@Override
	public void outputToSmartDashboard() {

	}

	@Override
	public void stop() {

	}

	@Override
	public void zeroSensors() {

	}

	@Override
	public void writeToLog() {
		mCSVWriter.write();
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
