package frc.team1836.robot.auto.modes;

import frc.team1836.robot.AutoChooser;
import frc.team1836.robot.AutoChooser.GameObjectPosition;
import frc.team1836.robot.Constants.ARM;
import frc.team1836.robot.RobotState.ArmState;
import frc.team1836.robot.auto.actions.DelayAction;
import frc.team1836.robot.auto.actions.DrivePathAction;
import frc.team1836.robot.auto.actions.MoveArmAction;
import frc.team1836.robot.auto.actions.RollerAction;
import frc.team1836.robot.util.auto.AutoModeBase;
import frc.team1836.robot.util.auto.AutoModeEndedException;
import frc.team1836.robot.util.auto.ParallelAction;
import frc.team1836.robot.util.logging.CrashTracker;
import java.util.Arrays;

public class CenterSwitchMode extends AutoModeBase {

	private GameObjectPosition position;

	public CenterSwitchMode(AutoChooser.GameObjectPosition position) {
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
		CrashTracker.logMarker("Starting Center Switch Mode (Left Side)");
		runAction(new MoveArmAction(ArmState.SWITCH_PLACE));
		runAction(new ParallelAction(Arrays
				.asList(
						new DrivePathAction(AutoChooser.autoPaths.get("CS-1L"), true, false, false),
						new DelayAction(AutoChooser.autoPaths.get("CS-1L").getTime() - 0.25,
								new RollerAction(0.4, ARM.INTAKE_OUT_ROLLER_SPEED))
				)));
		runAction(new DrivePathAction(AutoChooser.autoPaths.get("CS-21L"), false, false, true));
		runAction(new ParallelAction(Arrays
				.asList(

						new DrivePathAction(AutoChooser.autoPaths.get("CS-22L"), true, false, true),
						new DelayAction(0.35, new RollerAction(AutoChooser.autoPaths.get("CS-22L").getTime(),
								ARM.INTAKE_IN_ROLLER_SPEED)),
						new MoveArmAction(ArmState.INTAKE)
				)));
		runAction(new DrivePathAction(AutoChooser.autoPaths.get("CS-31L"), false, false, false));
		runAction(new ParallelAction(Arrays
				.asList(
						new RollerAction(0.35, ARM.INTAKE_IN_ROLLER_SPEED),

						new DrivePathAction(AutoChooser.autoPaths.get("CS-32L"), true, false, false),
						new DelayAction(0.5, new MoveArmAction(ArmState.SWITCH_PLACE)),
						new DelayAction(AutoChooser.autoPaths.get("CS-32L").getTime() - 0.15,
								new RollerAction(0.4, ARM.INTAKE_OUT_ROLLER_SPEED))
				)));
		runAction(	new DrivePathAction(AutoChooser.autoPaths.get("CS-41L"), false, false, true));
		runAction(new ParallelAction(Arrays
				.asList(

						new DrivePathAction(AutoChooser.autoPaths.get("CS-42L"), true, false, true),
						new DelayAction(0.35, new RollerAction(AutoChooser.autoPaths.get("CS-42L").getTime(),
								ARM.INTAKE_IN_ROLLER_SPEED)),
						new MoveArmAction(ArmState.SECOND_SWITCH_PLACE)
				)));
		runAction(new DrivePathAction(AutoChooser.autoPaths.get("CS-51L"), false, false, false));
		runAction(new ParallelAction(Arrays
				.asList(
						new RollerAction(0.35, ARM.INTAKE_IN_ROLLER_SPEED),
						new DelayAction(0.5, new MoveArmAction(ArmState.SWITCH_PLACE)),

						new DrivePathAction(AutoChooser.autoPaths.get("CS-52L"), false, false, false),
						new DelayAction(AutoChooser.autoPaths.get("CS-52L").getTime() - 0.15,
								new RollerAction(0.4, ARM.INTAKE_OUT_ROLLER_SPEED))
				)));
	}

	private void rightRoutine() throws AutoModeEndedException {
		CrashTracker.logMarker("Starting Center Switch Mode (Right Side)");
		runAction(new MoveArmAction(ArmState.OPPOSITE_SWITCH_PLACE));
		runAction(new ParallelAction(Arrays
				.asList(
						new DrivePathAction(AutoChooser.autoPaths.get("CS-1R"), false, false, false),
						new DelayAction(AutoChooser.autoPaths.get("CS-1R").getTime() - 0.25,
								new RollerAction(0.4, ARM.INTAKE_OUT_ROLLER_SPEED))
				)));

		runAction(new ParallelAction(Arrays
				.asList(
						new DrivePathAction(AutoChooser.autoPaths.get("CS-2R"), true, false, true),
						new DelayAction(0.35, new RollerAction(AutoChooser.autoPaths.get("CS-2R").getTime(),
								ARM.INTAKE_IN_ROLLER_SPEED)),
						new MoveArmAction(ArmState.INTAKE)
				)));

		runAction(new ParallelAction(Arrays
				.asList(
						new RollerAction(0.35,
								ARM.INTAKE_IN_ROLLER_SPEED),
						new DrivePathAction(AutoChooser.autoPaths.get("CS-3R"), false, false, false),
						new DelayAction(0.5, new MoveArmAction(ArmState.OPPOSITE_SWITCH_PLACE)),
						new DelayAction(AutoChooser.autoPaths.get("CS-3R").getTime() - 0.15,
								new RollerAction(0.4, ARM.INTAKE_OUT_ROLLER_SPEED))
				)));

		runAction(new ParallelAction(Arrays
				.asList(
						new DrivePathAction(AutoChooser.autoPaths.get("CS-4R"), true, false, true),
						new DelayAction(0.35, new RollerAction(AutoChooser.autoPaths.get("CS-4R").getTime(),
								ARM.INTAKE_IN_ROLLER_SPEED)),
						new MoveArmAction(ArmState.SECOND_SWITCH_PLACE)
				)));

		runAction(new ParallelAction(Arrays
				.asList(
						new RollerAction(0.35, ARM.INTAKE_IN_ROLLER_SPEED),
						new DelayAction(0.5, new MoveArmAction(ArmState.OPPOSITE_SWITCH_PLACE)),
						new DrivePathAction(AutoChooser.autoPaths.get("CS-5R"), false, false, false),
						new DelayAction(AutoChooser.autoPaths.get("CS-5R").getTime() - 0.15,
								new RollerAction(0.4, ARM.INTAKE_OUT_ROLLER_SPEED))
				)));
	}


}
