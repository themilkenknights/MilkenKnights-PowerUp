package frc.team1836.robot.auto.actions;

import edu.wpi.first.wpilibj.Timer;
import frc.team1836.robot.Constants;
import frc.team1836.robot.subsystems.Elevator;
import frc.team1836.robot.util.auto.Action;

public class RollerAction implements Action {

    private double time;
    private double speed;
    private Timer timer;
    private boolean end;

    public RollerAction(double time, double speed) {
        this(time, speed, false);
    }

    public RollerAction(double time, double speed, boolean end) {
        this.speed = speed;
        this.time = time;
        timer = new Timer();
        this.end = end;
    }


    @Override
    public boolean isFinished() {
        return timer.get() >= time;
    }

    @Override
    public void update() {
        Elevator.getInstance().setIntakeRollers(speed);
    }

    @Override
    public void done() {
        if (end) {
            Elevator.getInstance().setIntakeRollers(Constants.ELEVATOR.SLOW_INTAKE_HOLD_SPEED * 1.5);
        } else {
            Elevator.getInstance().setIntakeRollers(0);
        }

    }

    @Override
    public void start() {
        timer.start();
    }
}
