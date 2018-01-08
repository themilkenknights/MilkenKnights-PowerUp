package frc.team1836.robot.auto.modes.left;

import frc.team1836.robot.AutoChooser;
import frc.team1836.robot.AutoChooser.GameObjectPosition;
import frc.team1836.robot.auto.actions.DrivePathAction;
import frc.team1836.robot.auto.paths.StraightPath;
import frc.team1836.robot.util.auto.AutoModeBase;
import frc.team1836.robot.util.auto.AutoModeEndedException;

public class LeftScaleMode extends AutoModeBase {

	private GameObjectPosition position;

	public LeftScaleMode(AutoChooser.GameObjectPosition position) {
		this.position = position;
	}

	@Override
	protected void routine() throws AutoModeEndedException {
		switch (position) {
			case LEFT:
				leftRoutine();
				break;
			case RIGHT:
				rightRoutine();
				break;
		}
	}

	private void leftRoutine() throws AutoModeEndedException {
		runAction(new DrivePathAction(new StraightPath()));
	}

	private void rightRoutine() throws AutoModeEndedException {
		runAction(new DrivePathAction(new StraightPath()));
	}


}
