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

    public Path(String name, Pair pair_) {
        name_ = name;
        this.pair_ = pair_;
    }

    public Path() {

    }

    public String getName() {
        return name_;
    }


    public Trajectory getLeftWheelTrajectory() {
        return pair_.right;
    }

    public Trajectory getRightWheelTrajectory() {
        return pair_.left;
    }

    public Pair getPair() {
        return pair_;
    }

    public double getEndHeading() {
        int numSegments = getLeftWheelTrajectory().length();
        Trajectory.Segment lastSegment = getLeftWheelTrajectory().get(numSegments - 1);
        return lastSegment.heading;
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
