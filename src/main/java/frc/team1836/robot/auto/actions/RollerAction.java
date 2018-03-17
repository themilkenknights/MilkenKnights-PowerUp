package frc.team1836.robot.auto.actions;

import edu.wpi.first.wpilibj.Timer;
import frc.team1836.robot.subsystems.Arm;
import frc.team1836.robot.util.auto.Action;

public class RollerAction implements Action {

	private double time;
	private double speed;
	private Timer timer;

	public RollerAction(double time, double speed) {
		this.speed = speed;
		this.time = time;
		timer = new Timer();
	}

	@Override
	public boolean isFinished() {
		return timer.get() >= time;
	}

	@Override
	public void update() {
		Arm.getInstance().setIntakeRollers(speed);
	}

	@Override
	public void done() {
		Arm.getInstance().setIntakeRollers(0);
	}

	@Override
	public void start() {
		timer.start();
	}
}
