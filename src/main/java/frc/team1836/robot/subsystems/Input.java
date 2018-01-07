package frc.team1836.robot.subsystems;

import frc.team1836.robot.auto.modes.TurnInPlaceMode;
import frc.team1836.robot.util.auto.AutoModeExecuter;
import frc.team1836.robot.util.drivers.MkButton;
import frc.team1836.robot.util.drivers.MkJoystick;
import frc.team1836.robot.util.logging.ReflectingCSVWriter;
import frc.team1836.robot.util.loops.Loop;
import frc.team1836.robot.util.loops.Looper;
import frc.team1836.robot.util.other.DriveHelper;

public class Input extends Subsystem {

	private static Input mInstance = new Input();
	private final MkJoystick driverJoystick = new MkJoystick(0);
	private final MkButton rotate90Button = driverJoystick.getButton(2, "Rotate 90 Button");
	private final ReflectingCSVWriter<InputDebugOutput> mCSVWriter;
	private InputDebugOutput mDebug = new InputDebugOutput();
	private AutoModeExecuter mAutoModeExecuter = null;

	public static Input getInstance() {
		return mInstance;
	}

	public Input() {
		mCSVWriter = new ReflectingCSVWriter<>("/home/lvuser/INPUT-LOGS.csv",
				InputDebugOutput.class);
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
	public void checkSystem() {

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
				synchronized (Input.this) {
				}
			}

			@Override
			public void onLoop(double timestamp) {
				synchronized (Input.this) {
					updateDriveInput();
					updateDebug(timestamp);
					mCSVWriter.add(mDebug);
				}
			}

			@Override
			public void onStop(double timestamp) {
				stop();
			}
		};
		enabledLooper.register(mLoop);
	}

	public void updateDebug(double timestamp) {
		mDebug.driverY = driverJoystick.getY();
		mDebug.slowButton = rotate90Button.isHeld();
		mDebug.timestamp = timestamp;
	}

	public void updateDriveInput() {

		Drive.getInstance().setVelocitySetpoint(DriveHelper.cheesyDrive(-driverJoystick.getRawAxis(2) + driverJoystick.getRawAxis(3), -driverJoystick.getRawAxis(0)/2, false));

	/*if (rotate90Button.isPressed()) {
			setTurnInPlace(90);
		} */
	}

	public void setTurnInPlace(double angle) {
		if (mAutoModeExecuter != null) {
			mAutoModeExecuter.stop();
		}
		mAutoModeExecuter = null;
		mAutoModeExecuter = new AutoModeExecuter();
		mAutoModeExecuter.setAutoMode(new TurnInPlaceMode(angle));
		mAutoModeExecuter.start();
	}

	public static class InputDebugOutput {

		public double driverY;
		public double driverTwist;
		public boolean slowButton;
		public double timestamp;

	}


}
