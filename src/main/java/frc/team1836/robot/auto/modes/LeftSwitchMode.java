package frc.team1836.robot.auto.modes;

import frc.team1836.robot.AutoChooser;
import frc.team1836.robot.AutoChooser.GameObjectPosition;
import frc.team1836.robot.util.auto.AutoModeBase;
import frc.team1836.robot.util.auto.AutoModeEndedException;

public class LeftSwitchMode extends AutoModeBase {

	private GameObjectPosition position;

	public LeftSwitchMode(AutoChooser.GameObjectPosition position) {
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

	}

	private void rightRoutine() throws AutoModeEndedException {

	}


}
