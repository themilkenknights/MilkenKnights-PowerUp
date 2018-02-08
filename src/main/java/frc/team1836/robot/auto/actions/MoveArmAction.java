package frc.team1836.robot.auto.actions;

import frc.team1836.robot.RobotState;
import frc.team1836.robot.util.auto.Action;

public class MoveArmAction implements Action {

	RobotState.ArmState desiredState;

	public MoveArmAction(RobotState.ArmState desiredState) {
		this.desiredState = desiredState;
	}

	@Override
	public boolean isFinished() {
		return true;
	}

	@Override
	public void update() {

	}

	@Override
	public void done() {

	}

	@Override
	public void start() {
		RobotState.mArmState = desiredState;
	}
}
