package frc.team1836.robot.auto.modes;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.team1836.robot.AutoChooser;
import frc.team1836.robot.AutoChooser.GameObjectPosition;
import frc.team1836.robot.Constants;
import frc.team1836.robot.RobotState;
import frc.team1836.robot.RobotState.ArmState;
import frc.team1836.robot.auto.actions.DrivePathAction;
import frc.team1836.robot.auto.actions.RollerAction;
import frc.team1836.robot.util.auto.AutoModeBase;
import frc.team1836.robot.util.auto.AutoModeEndedException;
import frc.team1836.robot.util.logging.Log;

public class RightSwitchMode extends AutoModeBase {
  public RightSwitchMode() {

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
    Log.marker("Starting Right Switch Mode (Left Side)");
    runAction(new DrivePathAction(AutoChooser.autoPaths.get("DriveStraight"), false, false, false));
  }

  private void rightRoutine() throws AutoModeEndedException {
    Log.marker("Starting Right Switch Mode (Right Side)");
    RobotState.mArmState = ArmState.OPPOSITE_SWITCH_PLACE;
    runAction(new DrivePathAction(AutoChooser.autoPaths.get("FS-1R" + ((RobotState.matchData.alliance == Alliance.Blue) ? "B" : "R")), true, false, false));
    runAction(new RollerAction(0.5, Constants.ARM.INTAKE_OUT_ROLLER_SPEED));
  }

}
