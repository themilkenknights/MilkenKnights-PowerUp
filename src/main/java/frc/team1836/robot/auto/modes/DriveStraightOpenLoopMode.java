package frc.team1836.robot.auto.modes;

import frc.team1836.robot.auto.actions.OpenLoopAction;
import frc.team1836.robot.util.auto.AutoModeBase;
import frc.team1836.robot.util.auto.AutoModeEndedException;

public class DriveStraightOpenLoopMode extends AutoModeBase {

	@Override
	protected void routine() throws AutoModeEndedException {
		runAction(new OpenLoopAction(2, -0.4, false));
		runAction(new OpenLoopAction(1.5, -0.2, true));
	}
}
