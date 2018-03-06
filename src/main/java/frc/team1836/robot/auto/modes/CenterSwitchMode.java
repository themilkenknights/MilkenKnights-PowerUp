package frc.team1836.robot.auto.modes;

import frc.team1836.robot.AutoChooser;
import frc.team1836.robot.AutoChooser.GameObjectPosition;
import frc.team1836.robot.Constants;
import frc.team1836.robot.Constants.ARM;
import frc.team1836.robot.RobotState;
import frc.team1836.robot.RobotState.ArmState;
import frc.team1836.robot.auto.actions.DrivePathAction;
import frc.team1836.robot.auto.actions.MoveArmAction;
import frc.team1836.robot.auto.actions.RollerAction;
import frc.team1836.robot.util.auto.AutoModeBase;
import frc.team1836.robot.util.auto.AutoModeEndedException;
import frc.team1836.robot.util.logging.CrashTracker;

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
        RobotState.mArmState = ArmState.OPPOSITE_SWITCH_PLACE;
        runAction(new DrivePathAction(AutoChooser.autoPaths.get("CenterSwitchLeft")));
        runAction(new RollerAction(0.35, ARM.INTAKE_OUT_ROLLER_SPEED));
        runAction(new MoveArmAction(RobotState.ArmState.SWITCH_PLACE));
        runAction(new RollerAction(2, Constants.ARM.INTAKE_OUT_ROLLER_SPEED));
    }

    private void rightRoutine() throws AutoModeEndedException {
        CrashTracker.logMarker("Starting Center Switch Mode (Right Side)");
        RobotState.mArmState = ArmState.OPPOSITE_SWITCH_PLACE;
        runAction(new DrivePathAction(AutoChooser.autoPaths.get("CenterSwitchRight")));
        runAction(new RollerAction(0.35, ARM.INTAKE_OUT_ROLLER_SPEED));
        /*RobotState.mArmState = ArmState.INTAKE;
        runAction(new ParallelAction(Arrays
                .asList(
                        new DrivePathAction(AutoChooser.autoPaths.get("MarcusPath")),
                        new RollerAction(2, 0.5)
                )));
        RobotState.mArmState = ArmState.OPPOSITE_SWITCH_PLACE;
        runAction(new DrivePathAction(AutoChooser.autoPaths.get("SwerdPath")));
        runAction(new RollerAction(0.15, ARM.INTAKE_OUT_ROLLER_SPEED));
        runAction(new DrivePathAction(AutoChooser.autoPaths.get("YoelPath")));
        //    runAction(new MoveArmAction(RobotState.ArmState.SWITCH_PLACE));
        //  runAction(new RollerAction(2, Constants.ARM.INTAKE_OUT_ROLLER_SPEED));
        */
    }


}
