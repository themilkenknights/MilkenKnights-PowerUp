package frc.team1836.robot.auto.actions;

import edu.wpi.first.wpilibj.Timer;
import frc.team1836.robot.util.auto.Action;

public class DelayAction implements Action {
	private final Action mAction;
	private double mTimeToWait;
	private double mStartTime;
	private boolean start;

	public DelayAction(double timeToWait, Action action) {
		mTimeToWait = timeToWait;
		mAction = action;
		start = false;
	}

	@Override
	public boolean isFinished() {
		return start && mAction.isFinished();
	}

	/**
	 * Called by runAction in AutoModeBase iteratively until isFinished returns true. Iterative logic lives in this
	 * method
	 */
	@Override
	public void update() {
		if (Timer.getFPGATimestamp() - mStartTime >= mTimeToWait && ! start) {
			start = true;
			mAction.start();
		} else if (Timer.getFPGATimestamp() - mStartTime >= mTimeToWait) {
			mAction.update();
		}
	}

	/**
	 * Run code once when the action finishes, usually for clean up
	 */
	@Override
	public void done() {
		mAction.done();
	}

	/**
	 * Run code once when the action is started, for set up
	 */
	@Override
	public void start() {
		mStartTime = Timer.getFPGATimestamp();
	}
}
