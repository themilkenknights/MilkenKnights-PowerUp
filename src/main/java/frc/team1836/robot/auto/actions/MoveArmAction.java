package frc.team1836.robot.auto.actions;

import frc.team1836.robot.RobotState;
import frc.team1836.robot.util.auto.Action;

public class MoveArmAction implements Action {

    private final RobotState.ArmState mState;

    public MoveArmAction(RobotState.ArmState state) {
        mState = state;
    }

    /**
     * Returns whether or not the code has finished execution. When implementing this interface, this method is used by
     * the runAction method every cycle to know when to stop running the action
     *
     * @return boolean
     */
    @Override
    public boolean isFinished() {
        return true;
    }

    /**
     * Called by runAction in AutoModeBase iteratively until isFinished returns true. Iterative logic lives in this
     * method
     */
    @Override
    public void update() {

    }

    /**
     * Run code once when the action finishes, usually for clean up
     */
    @Override
    public void done() {

    }

    /**
     * Run code once when the action is started, for set up
     */
    @Override
    public void start() {
        RobotState.mArmState = mState;
    }
}
