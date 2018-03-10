package frc.team1836.robot.auto.modes;

import frc.team1836.robot.AutoChooser;
import frc.team1836.robot.AutoChooser.GameObjectPosition;
import frc.team1836.robot.Constants;
import frc.team1836.robot.Constants.ARM;
import frc.team1836.robot.RobotState;
import frc.team1836.robot.RobotState.ArmState;
import frc.team1836.robot.auto.actions.DelayAction;
import frc.team1836.robot.auto.actions.DrivePathAction;
import frc.team1836.robot.auto.actions.MoveArmAction;
import frc.team1836.robot.auto.actions.OpenLoopAction;
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
		runAction(new MoveArmAction(ArmState.OPPOSITE_SWITCH_PLACE));
		runAction(new DrivePathAction(AutoChooser.autoPaths.get("CSL-1"), false, false, false));
		runAction(new ParallelAction(Arrays
				.asList(
						new OpenLoopAction(0.2, -0.25, true),
						new DelayAction(0.1, new RollerAction(0.3, ARM.INTAKE_OUT_ROLLER_SPEED))
				)));

		runAction(new ParallelAction(Arrays
				.asList(
						new RollerAction(0.2, ARM.INTAKE_OUT_ROLLER_SPEED),
						new DrivePathAction(AutoChooser.autoPaths.get("CSL-2"), true, false, true),
						new DelayAction(0.25, new RollerAction(AutoChooser.autoPaths.get("CSL-2").getTime(),
								ARM.INTAKE_IN_ROLLER_SPEED)),
						new MoveArmAction(ArmState.INTAKE)
				)));
		runAction(new ParallelAction(Arrays
				.asList(
						new RollerAction(0.2, ARM.INTAKE_IN_ROLLER_SPEED),
						new OpenLoopAction(0.2, 0.275, true)
				)));
		runAction(new ParallelAction(Arrays
				.asList(
						new RollerAction(0.1, ARM.INTAKE_IN_ROLLER_SPEED),
						new OpenLoopAction(0.1, -0.275, true)
				)));
		runAction(new ParallelAction(Arrays
				.asList(
						new RollerAction(0.5,
								ARM.INTAKE_IN_ROLLER_SPEED),
						new DelayAction(0.5, new MoveArmAction(ArmState.OPPOSITE_SWITCH_PLACE)),
						new DrivePathAction(AutoChooser.autoPaths.get("CSL-3"), false, false, false)
				)));
		runAction(new OpenLoopAction(0.15, -0.25, true));
		runAction(new RollerAction(0.3, ARM.INTAKE_OUT_ROLLER_SPEED));
		runAction(new ParallelAction(Arrays
				.asList(
						new RollerAction(0.2, ARM.INTAKE_OUT_ROLLER_SPEED),
						new DrivePathAction(AutoChooser.autoPaths.get("CSL-4"), true, false, true),
						new DelayAction(0.25, new RollerAction(AutoChooser.autoPaths.get("CSL-4").getTime(),
								ARM.INTAKE_IN_ROLLER_SPEED)),
						new MoveArmAction(ArmState.INTAKE)
				)));
		runAction(new ParallelAction(Arrays
				.asList(
						new RollerAction(0.2, ARM.INTAKE_IN_ROLLER_SPEED),
						new OpenLoopAction(0.2, 0.275, true)
				)));
		runAction(new ParallelAction(Arrays
				.asList(
						new RollerAction(0.1, ARM.INTAKE_IN_ROLLER_SPEED),
						new OpenLoopAction(0.1, -0.275, true)
				)));
		runAction(new ParallelAction(Arrays
				.asList(
						new RollerAction(0.5,
								ARM.INTAKE_IN_ROLLER_SPEED),
						new DelayAction(0.5, new MoveArmAction(ArmState.OPPOSITE_SWITCH_PLACE)),
						new DrivePathAction(AutoChooser.autoPaths.get("CSL-5"), false, false, false)
				)));
		runAction(new OpenLoopAction(0.15, -0.25, true));
		runAction(new RollerAction(2, Constants.ARM.INTAKE_OUT_ROLLER_SPEED));
	}

	private void rightRoutine() throws AutoModeEndedException {
		CrashTracker.logMarker("Starting Center Switch Mode (Right Side)");
		RobotState.mArmState = ArmState.OPPOSITE_SWITCH_PLACE;
		runAction(new DrivePathAction(AutoChooser.autoPaths.get("CSR-1"), false, false, false));
		runAction(new ParallelAction(Arrays
				.asList(
						new OpenLoopAction(0.2, -0.25, true),
						new DelayAction(0.1, new RollerAction(0.3, ARM.INTAKE_OUT_ROLLER_SPEED))
				)));
		runAction(new ParallelAction(Arrays
				.asList(
						new RollerAction(0.2, ARM.INTAKE_OUT_ROLLER_SPEED),
						new DrivePathAction(AutoChooser.autoPaths.get("CSR-2"), true, false, true),
						new DelayAction(0.25, new RollerAction(AutoChooser.autoPaths.get("CSR-2").getTime(),
								ARM.INTAKE_IN_ROLLER_SPEED)),
						new MoveArmAction(ArmState.INTAKE)
				)));
		runAction(new ParallelAction(Arrays
				.asList(
						new RollerAction(0.2, ARM.INTAKE_IN_ROLLER_SPEED),
						new OpenLoopAction(0.2, 0.275, true)
				)));
		runAction(new ParallelAction(Arrays
				.asList(
						new RollerAction(0.1, ARM.INTAKE_IN_ROLLER_SPEED),
						new OpenLoopAction(0.1, -0.275, true)
				)));
		runAction(new ParallelAction(Arrays
				.asList(
						new RollerAction(0.5,
								ARM.INTAKE_IN_ROLLER_SPEED),
						new DelayAction(0.5, new MoveArmAction(ArmState.OPPOSITE_SWITCH_PLACE)),
						new DrivePathAction(AutoChooser.autoPaths.get("CSR-3"), false, false, false)
				)));
		runAction(new OpenLoopAction(0.15, -0.25, true));
		runAction(new RollerAction(0.3, ARM.INTAKE_OUT_ROLLER_SPEED));
		runAction(new ParallelAction(Arrays
				.asList(
						new RollerAction(0.2, ARM.INTAKE_OUT_ROLLER_SPEED),
						new DrivePathAction(AutoChooser.autoPaths.get("CSR-4"), true, false, true),
						new DelayAction(0.3, new RollerAction(AutoChooser.autoPaths.get("CSR-4").getTime(),
								ARM.INTAKE_IN_ROLLER_SPEED)),
						new MoveArmAction(ArmState.INTAKE)
				)));

			runAction(new ParallelAction(Arrays
					.asList(
							new RollerAction(0.2, ARM.INTAKE_IN_ROLLER_SPEED),
							new OpenLoopAction(0.2, 0.275, true)
					)));
			runAction(new ParallelAction(Arrays
					.asList(
							new RollerAction(0.1, ARM.INTAKE_IN_ROLLER_SPEED),
							new OpenLoopAction(0.1, -0.275, true)
					)));

		runAction(new ParallelAction(Arrays
				.asList(
						new RollerAction(0.5,
								ARM.INTAKE_IN_ROLLER_SPEED),
						new DelayAction(0.5, new MoveArmAction(ArmState.OPPOSITE_SWITCH_PLACE)),
						new DrivePathAction(AutoChooser.autoPaths.get("CSR-5"), false, false, false)
				)));
		runAction(new OpenLoopAction(0.15, -0.25, true));
		runAction(new RollerAction(2, Constants.ARM.INTAKE_OUT_ROLLER_SPEED));
	}


}
