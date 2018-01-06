package frc.team1836.robot.subsystems;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team1836.robot.util.loops.Loop;
import frc.team1836.robot.util.loops.Looper;

public class Superstructure extends Subsystem {

	private static Superstructure mInstance = new Superstructure();
	private SystemState mSystemState = SystemState.IDLE;

	public static Superstructure getInstance() {
		return mInstance;
	}

	@Override
	public void outputToSmartDashboard() {
		SmartDashboard.putString("System State", mSystemState.toString());
	}

	@Override
	public void stop() {

	}

	@Override
	public void zeroSensors() {

	}

	@Override
	public void registerEnabledLoops(Looper enabledLooper) {
		Loop mLoop = new Loop() {

			@Override
			public void onStart(double timestamp) {
				synchronized (Superstructure.this) {
					mSystemState = SystemState.IDLE;
				}
			}

			@Override
			public void onLoop(double timestamp) {
				synchronized (Superstructure.this) {
					SystemState newState = mSystemState;
					switch (mSystemState) {
						case IDLE:
							break;
						default:
							newState = SystemState.IDLE;
					}
					if (newState != mSystemState) {
						System.out
								.println("Superstructure state " + mSystemState + " to " + newState + " Timestamp: "
										+ Timer.getFPGATimestamp());
						mSystemState = newState;
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

	// Intenal state of the system
	public enum SystemState {
		IDLE
	}
}
