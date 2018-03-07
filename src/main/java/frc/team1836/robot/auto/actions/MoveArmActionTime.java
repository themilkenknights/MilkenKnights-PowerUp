package frc.team1836.robot.auto.actions;

import edu.wpi.first.wpilibj.Timer;
import frc.team1836.robot.RobotState;
import frc.team1836.robot.RobotState.ArmState;
import frc.team1836.robot.util.auto.Action;

public class MoveArmActionTime implements Action {


    private double time;
    private ArmState state;
    private Timer timer;

    public MoveArmActionTime(RobotState.ArmState state, double time) {
        this.time = time;
        timer = new Timer();
        this.state = state;
    }

    @Override
    public boolean isFinished() {
        if (timer.get() > time) {
            RobotState.mArmState = state;
            return true;
        }
        return false;
    }

    @Override
    public void update() {

    }

    @Override
    public void done() {

    }

    @Override
    public void start() {
        timer.start();
    }
}
