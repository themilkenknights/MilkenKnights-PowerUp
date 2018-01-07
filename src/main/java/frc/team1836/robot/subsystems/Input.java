package frc.team1836.robot.subsystems;

import frc.team1836.robot.util.drivers.MkButton;
import frc.team1836.robot.util.drivers.MkJoystick;
import frc.team1836.robot.util.loops.Loop;
import frc.team1836.robot.util.loops.Looper;

public class Input extends Subsystem {

	private static Input mInstance = new Input();
	private final MkJoystick driverJoystick = new MkJoystick(1);
	private final MkButton slowButton = driverJoystick.getButton(2, "Slow Button");

	public static Input getInstance() {
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
	public void checkSystem() {

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

				}
			}

			@Override
			public void onStop(double timestamp) {
				stop();
			}
		};
		enabledLooper.register(mLoop);
	}


}
