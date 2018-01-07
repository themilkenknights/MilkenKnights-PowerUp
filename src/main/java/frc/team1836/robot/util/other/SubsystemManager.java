package frc.team1836.robot.util.other;


import frc.team1836.robot.subsystems.Subsystem;
import frc.team1836.robot.util.loops.Looper;
import java.util.List;

/**
 * Used to reset, start, stop, and update all subsystems at once
 */
public class SubsystemManager {

	private final List<Subsystem> mAllSubsystems;

	public SubsystemManager(List<Subsystem> allSubsystems) {
		mAllSubsystems = allSubsystems;
	}

	public void outputToSmartDashboard() {
		mAllSubsystems.forEach((s) -> s.outputToSmartDashboard());
	}

	public void writeToLog() {
		mAllSubsystems.forEach((s) -> s.writeToLog());
	}

	public void stop() {
		mAllSubsystems.forEach((s) -> s.stop());
	}

	public void zeroSensors() {
		mAllSubsystems.forEach((s) -> s.zeroSensors());
	}

	public void checkSystem() {
		mAllSubsystems.forEach((s) -> s.checkSystem());
	}

	public void registerEnabledLoops(Looper enabledLooper) {
		mAllSubsystems.forEach((s) -> s.registerEnabledLoops(enabledLooper));
	}


}
