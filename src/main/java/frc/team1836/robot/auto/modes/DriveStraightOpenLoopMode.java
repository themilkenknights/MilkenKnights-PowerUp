package frc.team1836.robot.auto.modes;

import frc.team1836.robot.auto.actions.OpenLoopAction;
import frc.team1836.robot.util.auto.AutoModeBase;
import frc.team1836.robot.util.auto.AutoModeEndedException;
import frc.team1836.robot.util.logging.Log;

public class DriveStraightOpenLoopMode extends AutoModeBase {

  @Override
  protected void routine() throws AutoModeEndedException {
    Log.marker("Started Drive Straight Open Loop Auto");
    runAction(new OpenLoopAction(2, 0.4, false));
    runAction(new OpenLoopAction(1.5, 0.2, true));
  }
}
