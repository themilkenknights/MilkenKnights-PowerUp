package frc.team1836.robot.auto.modes;

import frc.team1836.robot.Constants;
import frc.team1836.robot.RobotState.ElevatorState;
import frc.team1836.robot.auto.actions.CurveOpenLoopAction;
import frc.team1836.robot.auto.actions.MoveArmAction;
import frc.team1836.robot.auto.actions.OpenLoopAction;
import frc.team1836.robot.auto.actions.RollerAction;
import frc.team1836.robot.auto.actions.WaitAction;
import frc.team1836.robot.util.auto.AutoModeBase;
import frc.team1836.robot.util.auto.AutoModeEndedException;
import frc.team1836.robot.util.logging.Log;

public class SwitchOpenLoop extends AutoModeBase {

    @Override
    protected void routine() throws AutoModeEndedException {
        Log.marker("Started Open Loop Switch Auto");
        runAction(new MoveArmAction(ElevatorState.OPPOSITE_SWITCH_PLACE));
        runAction(new CurveOpenLoopAction(0.95, 1, false));
        runAction(new OpenLoopAction(0.6, 0.35, true));
        runAction(new WaitAction(0.1));
        runAction(new RollerAction(0.35, Constants.ELEVATOR.INTAKE_OUT_ROLLER_SPEED));
    }
}
