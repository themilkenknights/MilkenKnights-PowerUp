package frc.team254.lib.trajectory;

import frc.team1836.robot.util.state.TrajectoryStatus;
import frc.team254.lib.trajectory.Trajectory.Segment;

/**
 * PID + Feedforward controller for following a Trajectory.
 *
 * @author Jared341
 */
public class TrajectoryFollower {

	private double kp_;
	private double kAng_;
	private double kv_;
	private double ka_;
	private double last_error_;
	private double current_heading = 0;
	private int current_segment;
	private Trajectory profile_;
	private double Dt;
	private double last_Ang_error;
	private double _DistTol;
	private double _AngTol;

	public TrajectoryFollower(Trajectory profile) {

		profile_ = profile;
	}

	public void configure(double kp, double ka, double kAng, double distTol,
			double angTol) {
		kp_ = kp;
		kAng_ = kAng;
		ka_ = ka;
		_DistTol = distTol;
		_AngTol = Math.toRadians(angTol);
		reset();
	}

	public void reset() {
		last_error_ = 0.0;
		current_segment = 0;
	}

	public TrajectoryStatus calculate(double dist, double vel, double heading) {
		if (current_segment == 0) {
			Dt = System.nanoTime();
		}

		current_segment = (int) (customRound((System.nanoTime() - Dt) * 1e-9) / 0.005);

		if (current_segment < profile_.getNumSegments()) {
			Trajectory.Segment segment = profile_.getSegment(current_segment);
			double error = segment.pos - dist;
			double angError = segment.heading - heading;
			double velError = segment.vel - vel;
			double desired = (angError * kAng_) + segment.vel;
			double output = desired + (kp_ * error) + (ka_ * segment.acc);

			last_error_ = error;
			last_Ang_error = angError;
			current_heading = segment.heading;
			current_segment++;
			return new TrajectoryStatus(segment, error, velError,
					Math.toDegrees(angError), output);
		} else {
			return TrajectoryStatus.NEUTRAL;
		}
	}

	public double getHeading() {
		return current_heading;
	}

	public boolean isFinishedTrajectory() {
		return current_segment >= profile_.getNumSegments();
	}

	private double customRound(double num) {
		return Math.round(num * 200) / 200.0;
	}

	public double getLastError() {
		return last_error_;
	}

	public boolean onTarget() {
		return last_error_ < _DistTol && last_Ang_error < _AngTol;
	}

	/*private Segment interpolateSegments(Segment firstSeg, Segment lastSeg, double time){

	}*/
}
