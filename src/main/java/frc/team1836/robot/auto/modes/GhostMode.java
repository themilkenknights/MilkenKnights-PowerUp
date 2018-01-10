package frc.team1836.robot.auto.modes;

import frc.team1836.robot.auto.actions.GhostAction;
import frc.team1836.robot.util.auto.AutoModeBase;
import frc.team1836.robot.util.auto.AutoModeEndedException;

public class GhostMode extends AutoModeBase {


	@Override
	protected void routine() throws AutoModeEndedException {
		System.out.println("Starting Ghost Mode... Done!");
		runAction(new GhostAction());
	}

}
