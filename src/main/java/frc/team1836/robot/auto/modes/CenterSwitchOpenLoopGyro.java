package frc.team1836.robot.auto.modes;

import frc.team1836.robot.Constants;
import frc.team1836.robot.RobotState;
import frc.team1836.robot.auto.actions.OpenLoopFollowHeading;
import frc.team1836.robot.auto.actions.RollerAction;
import frc.team1836.robot.util.auto.AutoModeBase;
import frc.team1836.robot.util.auto.AutoModeEndedException;

public class CenterSwitchOpenLoopGyro extends AutoModeBase {

    @Override
    protected void routine() throws AutoModeEndedException {
        RobotState.mArmState = RobotState.ArmState.OPPOSITE_SWITCH_PLACE;
        runAction(new OpenLoopFollowHeading(0.8, 0.75, 0.5, 1, 70));
        runAction(new RollerAction(0.35, Constants.ARM.INTAKE_OUT_ROLLER_SPEED));
    }
}
