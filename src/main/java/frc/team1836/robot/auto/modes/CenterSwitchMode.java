package frc.team1836.robot.auto.modes;

import frc.team1836.robot.AutoChooser;
import frc.team1836.robot.AutoChooser.GameObjectPosition;
import frc.team1836.robot.Constants;
import frc.team1836.robot.auto.actions.DrivePathAction;
import frc.team1836.robot.util.auto.AutoModeBase;
import frc.team1836.robot.util.auto.AutoModeEndedException;

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
        runAction(new DrivePathAction(Constants.AUTO.autoPaths.get("CenterLeftSwitchPath")));
        //runAction(new MoveArmAction(RobotState.ArmState.SWITCH_PLACE));
        //runAction(new RollerAction(2, Constants.ARM.INTAKE_OUT_ROLLER_SPEED));
    }

    private void rightRoutine() throws AutoModeEndedException {
        runAction(new DrivePathAction(Constants.AUTO.autoPaths.get("CenterRightSwitchPath")));
    //    runAction(new MoveArmAction(RobotState.ArmState.SWITCH_PLACE));
      //  runAction(new RollerAction(2, Constants.ARM.INTAKE_OUT_ROLLER_SPEED));
    }


}
