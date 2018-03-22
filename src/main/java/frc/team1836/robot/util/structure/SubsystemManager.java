package frc.team1836.robot.util.structure;

import edu.wpi.first.wpilibj.Timer;
import frc.team1836.robot.util.structure.loops.Looper;
import java.util.List;

/**
 * Used to reset, start, stop, and update all subsystems at once
 */
public class SubsystemManager {

  private final List<Subsystem> mAllSubsystems;
  private double timeOffset;

  public SubsystemManager(List<Subsystem> allSubsystems) {
    mAllSubsystems = allSubsystems;
    timeOffset = 0;
  }

  public void outputToSmartDashboard() {
    mAllSubsystems.forEach((s) -> s.outputToSmartDashboard());
  }

  public void slowUpdate() {
    double timestamp = Timer.getFPGATimestamp() + timeOffset;
    mAllSubsystems.forEach((s) -> s.slowUpdate(timestamp));
  }

  public void checkSystem() {
    mAllSubsystems.forEach((s) -> s.checkSystem());
  }

  public void registerEnabledLoops(Looper enabledLooper) {
    mAllSubsystems.forEach((s) -> s.registerEnabledLoops(enabledLooper));
  }

  public void setTimeOffset(){
    timeOffset = -Timer.getFPGATimestamp();
  }

}
