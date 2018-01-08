package frc.team1836.robot.subsystems;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team1836.robot.util.logging.ReflectingCSVWriter;
import frc.team1836.robot.util.loops.Loop;
import frc.team1836.robot.util.loops.Looper;

public class Superstructure extends Subsystem {

	private static Superstructure mInstance = new Superstructure();
	private final ReflectingCSVWriter<SupertructureDebugOutput> mCSVWriter;
	PowerDistributionPanel pdp = new PowerDistributionPanel();
	private SystemState mSystemState = SystemState.IDLE;
	private SupertructureDebugOutput mDebug = new SupertructureDebugOutput();
	private MatchState matchState = MatchState.IDLE;

	public Superstructure() {
		mCSVWriter = new ReflectingCSVWriter<>("/home/lvuser/SUPERSTRUCTURE-LOGS.csv",
				SupertructureDebugOutput.class);
	}

	public static Superstructure getInstance() {
		return mInstance;
	}

	@Override
	public void writeToLog() {
		mCSVWriter.write();
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
	public void checkSystem() {
		if (pdp.getVoltage() < 10) {
			System.out.println("FAILED - PDP VOLTAGE LOW");
		}
	}

	@Override
	public void registerEnabledLoops(Looper enabledLooper) {
		Loop mLoop = new Loop() {

			@Override
			public void onStart(double timestamp) {
				synchronized (Superstructure.this) {
					mSystemState = SystemState.IDLE;
					pdp.clearStickyFaults();
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
					mDebug.SuperstructureState = mSystemState.toString();
					mDebug.PDPVoltage = pdp.getVoltage();
					mDebug.PDPTotalPower = pdp.getTotalPower();
					mDebug.PDPTemp = pdp.getTemperature();
					mDebug.timestamp = timestamp;
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

	public MatchState getMatchState() {
		return matchState;
	}

	public void setMatchState(MatchState state) {
		matchState = state;
	}

	// Intenal state of the system
	public enum SystemState {
		IDLE
	}

	public enum MatchState {
		AUTO, TELEOP, IDLE
	}

	public static class SupertructureDebugOutput {

		public String SuperstructureState;
		public double PDPVoltage;
		public double PDPTotalPower;
		public double PDPTemp;
		public double timestamp;

	}


}
