package frc.team1836.robot.subsystems;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team1836.robot.util.logging.ReflectingCSVWriter;
import frc.team1836.robot.util.loops.Loop;
import frc.team1836.robot.util.loops.Looper;

public class Superstructure extends Subsystem {

	private static Superstructure mInstance = new Superstructure();
	private SystemState mSystemState = SystemState.IDLE;
	PowerDistributionPanel pdp = new PowerDistributionPanel();
	private final ReflectingCSVWriter<DriveDebugOutput> mCSVWriter;
	private DriveDebugOutput mDebug = new DriveDebugOutput();

	public static Superstructure getInstance() {
		return mInstance;
	}

	public Superstructure(){
		mCSVWriter = new ReflectingCSVWriter<>("/home/lvuser/DRIVE-LOGS.csv",
				DriveDebugOutput.class);
	}

	@Override
	public void outputToSmartDashboard() {
		SmartDashboard.putString("System State", mSystemState.toString());
	}

	@Override
	public void stop() {

	}

	@Override
	public void writeToLog() {
		mCSVWriter.write();
	}

	@Override
	public void zeroSensors() {

	}

	@Override
	public void checkSystem() {
		if(pdp.getVoltage() < 10){
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

	// Intenal state of the system
	public enum SystemState {
		IDLE
	}

	private static class DriveDebugOutput {

		public String SuperstructureState;
		public double PDPVoltage;
		public double PDPTotalPower;
		public double PDPTemp;

	}


}
