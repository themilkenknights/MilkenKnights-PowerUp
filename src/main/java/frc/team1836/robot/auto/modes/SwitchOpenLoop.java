package frc.team1836.robot.auto.modes;

import frc.team1836.robot.Constants.ARM;
import frc.team1836.robot.RobotState;
import frc.team1836.robot.RobotState.ArmState;
import frc.team1836.robot.auto.actions.CurveOpenLoopAction;
import frc.team1836.robot.auto.actions.OpenLoopAction;
import frc.team1836.robot.auto.actions.RollerAction;
import frc.team1836.robot.auto.actions.WaitAction;
import frc.team1836.robot.util.auto.AutoModeBase;
import frc.team1836.robot.util.auto.AutoModeEndedException;

public class SwitchOpenLoop extends AutoModeBase {

    @Override
    protected void routine() throws AutoModeEndedException {
        RobotState.mArmState = ArmState.OPPOSITE_SWITCH_PLACE;
        runAction(new CurveOpenLoopAction(0.95, -1, false));
        runAction(new OpenLoopAction(0.6, -0.35, true));
        runAction(new WaitAction(0.1));
        runAction(new RollerAction(0.35, ARM.INTAKE_OUT_ROLLER_SPEED));
    }
}
