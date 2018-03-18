package frc.team1836.robot.auto.modes;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.team1836.robot.AutoChooser;
import frc.team1836.robot.Constants;
import frc.team1836.robot.RobotState;
import frc.team1836.robot.RobotState.ArmState;
import frc.team1836.robot.auto.actions.DrivePathAction;
import frc.team1836.robot.auto.actions.RollerAction;
import frc.team1836.robot.util.auto.AutoModeBase;
import frc.team1836.robot.util.auto.AutoModeEndedException;
import frc.team1836.robot.util.logging.Log;

public class LeftSwitchMode extends AutoModeBase {

  public LeftSwitchMode() {
  }

  @Override
  protected void routine() throws AutoModeEndedException {
    switch (RobotState.matchData.switchPosition) {
      case LEFT:
        leftRoutine();
        break;
      case RIGHT:
        rightRoutine();
        break;
    }
  }

  private void leftRoutine() throws AutoModeEndedException {
    Log.marker("Starting Left Switch Mode (Left Side)");
    RobotState.mArmState = ArmState.OPPOSITE_SWITCH_PLACE;
    runAction(new DrivePathAction(AutoChooser.autoPaths.get("FS-1L" + ((RobotState.matchData.alliance == Alliance.Blue) ? "B" : "R")), true, false, false));
    runAction(new RollerAction(0.5, Constants.ARM.INTAKE_OUT_ROLLER_SPEED));
  }

  private void rightRoutine() throws AutoModeEndedException {
    Log.marker("Starting Left Switch Mode (Right Side)");
    runAction(new DrivePathAction(AutoChooser.autoPaths.get("DriveStraight"), false, false, false));
  }

}
