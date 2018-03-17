package frc.team1836.robot.auto.modes;

import frc.team1836.robot.AutoChooser;
import frc.team1836.robot.Constants;
import frc.team1836.robot.auto.actions.DelayAction;
import frc.team1836.robot.auto.actions.DrivePathAction;
import frc.team1836.robot.auto.actions.RollerAction;
import frc.team1836.robot.util.auto.AutoModeBase;
import frc.team1836.robot.util.auto.AutoModeEndedException;
import frc.team1836.robot.util.auto.ParallelAction;
import java.util.Arrays;

public class SideSwitchMode extends AutoModeBase {

	private AutoChooser.AutoPosition position;

	public SideSwitchMode(AutoChooser.AutoPosition position) {
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
		runAction(new ParallelAction(Arrays
				.asList(new DrivePathAction(AutoChooser.autoPaths.get("SB-1L"), false, false, false),
						new DelayAction(AutoChooser.autoPaths.get("SB-1L").getTime() -
								0.25, new RollerAction(0.35, Constants.ARM.INTAKE_OUT_ROLLER_SPEED)))));
		runAction(new DrivePathAction(AutoChooser.autoPaths.get("SB-2L"), false, false, false));
		runAction(new DrivePathAction(AutoChooser.autoPaths.get("SB-3L"), false, false, false));
		runAction(new DrivePathAction(AutoChooser.autoPaths.get("SB-4L"), false, false, false));
		runAction(new DrivePathAction(AutoChooser.autoPaths.get("SB-5L"), false, false, false));


	}

	private void rightRoutine() throws AutoModeEndedException {
		runAction(new ParallelAction(Arrays
				.asList(new DrivePathAction(AutoChooser.autoPaths.get("SB-1R"), false, false, false),
						new DelayAction(AutoChooser.autoPaths.get("SB-1R").getTime() -
								0.25, new RollerAction(0.35, Constants.ARM.INTAKE_OUT_ROLLER_SPEED)))));
		runAction(new DrivePathAction(AutoChooser.autoPaths.get("SB-2R"), false, false, false));
		runAction(new DrivePathAction(AutoChooser.autoPaths.get("SB-3R"), false, false, false));
		runAction(new DrivePathAction(AutoChooser.autoPaths.get("SB-4R"), false, false, false));
		runAction(new DrivePathAction(AutoChooser.autoPaths.get("SB-5R"), false, false, false));
	}

}
