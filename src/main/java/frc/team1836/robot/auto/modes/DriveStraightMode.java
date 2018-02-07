package frc.team1836.robot.auto.modes;

import frc.team1836.robot.auto.actions.DrivePathAction;
import frc.team1836.robot.auto.paths.StraightPath;
import frc.team1836.robot.util.auto.AutoModeBase;
import frc.team1836.robot.util.auto.AutoModeEndedException;

public class DriveStraightMode extends AutoModeBase {

	@Override
	protected void routine() throws AutoModeEndedException {
			runAction(new DrivePathAction(new StraightPath()));
	}
}
