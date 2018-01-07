package frc.team1836.robot.subsystems;

import frc.team1836.robot.util.drivers.MkButton;
import frc.team1836.robot.util.drivers.MkJoystick;
import frc.team1836.robot.util.logging.ReflectingCSVWriter;
import frc.team1836.robot.util.loops.Loop;
import frc.team1836.robot.util.loops.Looper;

public class Input extends Subsystem {

	private static Input mInstance = new Input();
	private final MkJoystick driverJoystick = new MkJoystick(1);
	private final MkButton slowButton = driverJoystick.getButton(2, "Slow Button");
	private final ReflectingCSVWriter<InputDebugOutput> mCSVWriter;
	private InputDebugOutput mDebug = new InputDebugOutput();

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
					mDebug.driverY = driverJoystick.getY();
					mDebug.driverTwist = driverJoystick.getTwist();
					mDebug.slowButton = slowButton.isHeld();
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

	private static class InputDebugOutput {

		public double driverY;
		public double driverTwist;
		public boolean slowButton;

	}


}
