package frc.team1836.robot.auto.modes;

import frc.team1836.robot.AutoChooser;
import frc.team1836.robot.AutoChooser.GameObjectPosition;
import frc.team1836.robot.Constants;
import frc.team1836.robot.RobotState;
import frc.team1836.robot.RobotState.ArmState;
import frc.team1836.robot.auto.actions.DrivePathAction;
import frc.team1836.robot.auto.actions.RollerAction;
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
        RobotState.mArmState = ArmState.OPPOSITE_SWITCH_PLACE;
        runAction(new DrivePathAction(AutoChooser.autoPaths.get("DriveStraight.txt")));
        runAction(new RollerAction(0.15, Constants.ARM.INTAKE_OUT_ROLLER_SPEED));
    }

    private void rightRoutine() throws AutoModeEndedException {
        runAction(new DrivePathAction(AutoChooser.autoPaths.get("DriveStraight.txt")));
    }


}
