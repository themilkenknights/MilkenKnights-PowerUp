package frc.team1836.robot.auto.trajectory;

import jaci.pathfinder.Trajectory;

/**
 * Base class for an autonomous path.
 *
 * @author Jared341
 */
public class Path {

	protected Pair pair_;
	protected String name_;
	private boolean invert;

	public Path(String name, Pair pair_) {
		name_ = name;
		this.pair_ = pair_;
		invert = false;
	}

	public Path() {

	}

	public String getName() {
		return name_;
	}

	public void invert() {
		invert = true;
		for (Trajectory.Segment segment : pair_.left.segments) {
			segment.position = - segment.position;
			segment.velocity = - segment.velocity;
			segment.acceleration = - segment.acceleration;
			segment.jerk = - segment.jerk;
		}

		for (Trajectory.Segment segment : pair_.right.segments) {
			segment.position = - segment.position;
			segment.velocity = - segment.velocity;
			segment.acceleration = - segment.acceleration;
			segment.jerk = - segment.jerk;
		}
	}

	public void flipSides() {
		this.pair_ = new Pair(pair_.right, pair_.left);
	}

	public void invertSide(){
		invert = !invert;
	}

	public Trajectory getLeftWheelTrajectory() {
		return invert ? pair_.right : pair_.left;
	}

	public Trajectory getRightWheelTrajectory() {
		return invert ? pair_.left : pair_.right;
	}

	public Pair getPair() {
		return pair_;
	}

	public double getEndHeading() {
		int numSegments = getLeftWheelTrajectory().length();
		Trajectory.Segment lastSegment = getLeftWheelTrajectory().get(numSegments - 1);
		return lastSegment.heading;
	}

	public double getTime() {
		return pair_.left.length() * pair_.left.get(0).dt;
	}

	public Path copyPath() {
		return new Path(name_, new Pair(getLeftWheelTrajectory().copy(), getRightWheelTrajectory().copy()));
	}

	public static class Pair {

		public Trajectory left;
		public Trajectory right;

		public Pair(Trajectory left, Trajectory right) {
			this.left = left;
			this.right = right;
		}
	}
}
