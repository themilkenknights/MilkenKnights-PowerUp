package frc.team1836.robot.auto.actions;

import edu.wpi.first.wpilibj.Timer;
import frc.team1836.robot.Constants;
import frc.team1836.robot.subsystems.Drive;
import frc.team1836.robot.util.auto.Action;
import frc.team1836.robot.util.state.DriveSignal;

public class OpenLoopFollowHeading implements Action {

	double m_t1 = 0; // Time to start deceling
	double m_t2 = 0; // Time to end decel
	double m_start_power = 0;
	double m_end_power = 0;
	private Timer timer;
	private double endAngle;
	private double otherAngle;

	public OpenLoopFollowHeading(double start_power, double time_full_on, double end_power,
			double time_to_decel, double endAngle, double otherAngle) {
		m_t1 = time_full_on;
		m_t2 = m_t1 + time_to_decel;
		m_start_power = start_power;
		m_end_power = end_power;
		this.endAngle = endAngle;
		this.otherAngle = otherAngle;
		timer = new Timer();
		Drive.getInstance().resetGyro();
	}

	/**
	 * Returns whether or not the code has finished execution. When implementing this interface, this method is used by
	 * the runAction method every cycle to know when to stop running the action
	 *
	 * @return boolean
	 */
	@Override
	public boolean isFinished() {
		return timer.get() >= m_t2;
	}

	/**
	 * Called by runAction in AutoModeBase iteratively until isFinished returns true. Iterative logic lives in this
	 * method
	 */
	@Override
	public void update() {
		double power;
		double Angslope = 0;
		double angleSetpoint = 0;
		double angPower = 0;
		if (timer.get() <= m_t1) {
			power = m_start_power;
			Angslope = (endAngle) / (m_t2);
			angleSetpoint = Angslope * timer.get();
			angPower =
					(angleSetpoint - Drive.getInstance().getYaw()) * Constants.DRIVE.mPangFollower;
		} else if (timer.get() > m_t1 && timer.get() <= m_t2) {
			double rel_t = timer.get() - m_t1;
			double slope = (m_end_power - m_start_power) / (m_t2 - m_t1);
			power = (m_start_power + (slope * rel_t));
			Angslope = (otherAngle) / (m_t2);
			angleSetpoint = Angslope * timer.get();
			angPower =
					(angleSetpoint - Drive.getInstance().getYaw()) * Constants.DRIVE.mPangFollower;
		} else {
			power = 0;
			angPower = 0;
		}

		Drive.getInstance().setOpenLoop(new DriveSignal(power - angPower, power + angPower));
	}

	/**
	 * Run code once when the action finishes, usually for clean up
	 */
	@Override
	public void done() {
		Drive.getInstance().setOpenLoop(new DriveSignal(0, 0));
	}

	/**
	 * Run code once when the action is started, for set up
	 */
	@Override
	public void start() {
		Drive.getInstance().resetGyro();
		timer.reset();
		timer.start();
	}
}
