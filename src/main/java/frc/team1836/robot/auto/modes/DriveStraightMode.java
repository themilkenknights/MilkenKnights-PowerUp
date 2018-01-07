package frc.team1836.robot.auto.modes;

import frc.team1836.robot.auto.actions.StraightOpenLoopAction;
import frc.team1836.robot.util.auto.AutoModeBase;
import frc.team1836.robot.util.auto.AutoModeEndedException;

public class DriveStraightMode extends AutoModeBase {


	@Override
	protected void routine() throws AutoModeEndedException {
		runAction(new StraightOpenLoopAction(3, 0.5));
	}

}
