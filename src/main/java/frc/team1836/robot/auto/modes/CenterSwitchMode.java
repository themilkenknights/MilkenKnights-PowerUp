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
import frc.team1836.robot.util.logging.Log;
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
    Log.marker("Starting Center Switch Mode (Left Side)");
    runAction(new ParallelAction(Arrays.asList(
        new DrivePathAction(AutoChooser.autoPaths.get("CS-1L"), true, false, false),
        new MoveArmAction(ArmState.SWITCH_PLACE),
        new DelayAction(AutoChooser.autoPaths.get("CS-1L").getTime() - 0.5,
            new RollerAction(0.45, ARM.INTAKE_OUT_ROLLER_SPEED)))));
    runAction(new ParallelAction(Arrays.asList(
        new DrivePathAction(AutoChooser.autoPaths.get("CS-21L"), false, false, true),
        new DelayAction(0.3, new MoveArmAction(ArmState.INTAKE))
    )));
    runAction(new ParallelAction(Arrays.asList(
        new DrivePathAction(AutoChooser.autoPaths.get("CS-22L"), true, false, true),
        new RollerAction(AutoChooser.autoPaths.get("CS-22L").getTime(), ARM.INTAKE_IN_ROLLER_SPEED,
            true)
    )));

    runAction(
        new DrivePathAction(AutoChooser.autoPaths.get("CS-31L"), false, false, false));

    runAction(new ParallelAction(Arrays.asList(
        new DrivePathAction(AutoChooser.autoPaths.get("CS-32L"), true, false, false),
        new DelayAction(0.5, new MoveArmAction(ArmState.SWITCH_PLACE)),
        new DelayAction(AutoChooser.autoPaths.get("CS-32L").getTime() - 0.6,
            new RollerAction(0.5, ARM.INTAKE_OUT_ROLLER_SPEED))
    )));

    runAction(new DrivePathAction(AutoChooser.autoPaths.get("CS-41L"), false, false, true));

    runAction(new ParallelAction(Arrays.asList(
        new DrivePathAction(AutoChooser.autoPaths.get("CS-42L"), true, false, true),
        new RollerAction(AutoChooser.autoPaths.get("CS-42L").getTime(), ARM.INTAKE_IN_ROLLER_SPEED,
            true),
        new MoveArmAction(ArmState.SECOND_SWITCH_PLACE)
    )));

    runAction(new DrivePathAction(AutoChooser.autoPaths.get("CS-51L"), false, false, false));

    runAction(new ParallelAction(Arrays.asList(
        new DelayAction(0.5, new MoveArmAction(ArmState.SWITCH_PLACE)),
        new DrivePathAction(AutoChooser.autoPaths.get("CS-52L"), true, false, false),
        new DelayAction(AutoChooser.autoPaths.get("CS-52L").getTime() - 0.75,
            new RollerAction(0.5, ARM
                .INTAKE_OUT_ROLLER_SPEED)))));
  }

  private void rightRoutine() throws AutoModeEndedException {
    Log.marker("Starting Center Switch Mode (Right Side)");
    runAction(new ParallelAction(Arrays.asList(
        new DrivePathAction(AutoChooser.autoPaths.get("CS-1R"), true, false, false),
        new MoveArmAction(ArmState.SWITCH_PLACE),
        new DelayAction(AutoChooser.autoPaths.get("CS-1R").getTime() - 0.6,
            new RollerAction(0.5, ARM.INTAKE_OUT_ROLLER_SPEED)))));
    runAction(new ParallelAction(Arrays.asList(
        new DrivePathAction(AutoChooser.autoPaths.get("CS-21R"), false, false, true),
        new DelayAction(0.3, new MoveArmAction(ArmState.INTAKE))
    )));
    runAction(new ParallelAction(Arrays.asList(
        new DrivePathAction(AutoChooser.autoPaths.get("CS-22R"), true, false, true),
        new RollerAction(AutoChooser.autoPaths.get("CS-22R").getTime(), ARM.INTAKE_IN_ROLLER_SPEED,
            true)
    )));

    runAction(
        new DrivePathAction(AutoChooser.autoPaths.get("CS-31R"), false, false, false));

    runAction(new ParallelAction(Arrays.asList(
        new DrivePathAction(AutoChooser.autoPaths.get("CS-32R"), true, false, false),
        new DelayAction(0.5, new MoveArmAction(ArmState.SWITCH_PLACE)),
        new DelayAction(AutoChooser.autoPaths.get("CS-32R").getTime() - 0.6,
            new RollerAction(0.5, ARM.INTAKE_OUT_ROLLER_SPEED))
    )));

    runAction(new DrivePathAction(AutoChooser.autoPaths.get("CS-41R"), false, false, true));

    runAction(new ParallelAction(Arrays.asList(
        new DrivePathAction(AutoChooser.autoPaths.get("CS-42R"), true, false, true),
        new RollerAction(AutoChooser.autoPaths.get("CS-42R").getTime(), ARM.INTAKE_IN_ROLLER_SPEED,
            true),
        new MoveArmAction(ArmState.SECOND_SWITCH_PLACE)
    )));

    runAction(new DrivePathAction(AutoChooser.autoPaths.get("CS-51R"), false, false, false));

    runAction(new ParallelAction(Arrays.asList(
        new DelayAction(0.5, new MoveArmAction(ArmState.SWITCH_PLACE)),
        new DrivePathAction(AutoChooser.autoPaths.get("CS-52R"), false, false, false),
        new DelayAction(AutoChooser.autoPaths.get("CS-52R").getTime() - 0.7,
            new RollerAction(0.5, ARM
                .INTAKE_OUT_ROLLER_SPEED)))));
  }

}
