package frc.team1836.robot.util.state;

import frc.team254.lib.trajectory.Trajectory.Segment;

public class TrajectoryStatus {


    public static TrajectoryStatus NEUTRAL = new TrajectoryStatus(new Segment(0, 0, 0, 0, 0, 0, 0, 0),
            0, 0, 0, 0, 0);

    private double output;
    private Segment seg;
    private double posError;
    private double velError;
    private double feedFoward;
    private double angError;

    public TrajectoryStatus(Segment seg, double posError, double velError, double angError, double feedFoward,
                            double output) {
        this.seg = seg;
        this.output = output;
        this.posError = posError;
        this.velError = velError;
        this.angError = angError;
        this.feedFoward = feedFoward;
    }

    public double getOutput() {
        return output;
    }

    public double getPosError() {
        return posError;
    }

    public double getVelError() {
        return velError;
    }

    public double getAngError() {
        return angError;
    }

    public double getFeedforward() {
        return feedFoward;
    }

    public Segment getSeg() {
        return seg;
    }

    public String toString() {
        return seg.toString() + "Output: " + output + " Position Error: " + posError
                + "Velocity Error: " + velError + "Angle Error: " + angError;
    }

}
