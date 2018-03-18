package frc.team1836.robot.auto.modes;

import frc.team1836.robot.AutoChooser;
import frc.team1836.robot.AutoChooser.GameObjectPosition;
import frc.team1836.robot.Constants;
import frc.team1836.robot.RobotState;
import frc.team1836.robot.auto.actions.OpenLoopFollowHeading;
import frc.team1836.robot.auto.actions.RollerAction;
import frc.team1836.robot.util.auto.AutoModeBase;
import frc.team1836.robot.util.auto.AutoModeEndedException;

public class CenterSwitchOpenLoopGyro extends AutoModeBase {


  public CenterSwitchOpenLoopGyro() {

  }

  @Override
  protected void routine() throws AutoModeEndedException {
    RobotState.mArmState = RobotState.ArmState.OPPOSITE_SWITCH_PLACE;
    switch (RobotState.matchData.switchPosition) {
      case LEFT:
        leftRoutine();
        break;
      case RIGHT:
        rightRoutine();
        break;
    }
  }

  protected void leftRoutine() throws AutoModeEndedException {
    runAction(new OpenLoopFollowHeading(-0.5, 1.75, -0.75, 1, -70, 0));
    runAction(new RollerAction(0.45, Constants.ARM.INTAKE_OUT_ROLLER_SPEED));
  }

  protected void rightRoutine() throws AutoModeEndedException {
    runAction(new OpenLoopFollowHeading(-0.5, 1.75, -0.75, 1, 70, 0));
    runAction(new RollerAction(0.45, Constants.ARM.INTAKE_OUT_ROLLER_SPEED));
  }
}
