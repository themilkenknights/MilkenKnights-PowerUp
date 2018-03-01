package frc.team254.lib.trajectory;

import edu.wpi.first.wpilibj.Timer;
import frc.team1836.robot.util.state.TrajectoryStatus;

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
    private boolean firstRun;
    private double maxVel;

    public TrajectoryFollower(Trajectory profile) {
        profile_ = profile;
    }

    public void configure(double kp, double ka, double kAng, double distTol,
                          double angTol, double maxVel) {
        kp_ = kp;
        kAng_ = kAng;
        ka_ = ka;
        _DistTol = distTol;
        _AngTol = angTol;
        this.maxVel = maxVel;
        reset();
    }

    public void reset() {
        last_error_ = 0.0;
        current_segment = 0;
        firstRun = true;
    }

    public TrajectoryStatus calculate(double dist, double vel, double heading) {
        if (firstRun) {
            Dt = Timer.getFPGATimestamp();
            firstRun = false;
        }
        double currentTime = Timer.getFPGATimestamp();
        current_segment = (int) (customRound(currentTime - Dt) / 0.005);
        if (current_segment < profile_.getNumSegments()) {
            Trajectory.Segment segment = interpolateSegments(current_segment, currentTime);
            double error = segment.pos - dist;
            double angError = segment.heading - heading;
            if (angError > 180) {
                angError = angError - 360;
            } else if (angError < -180) {
                angError = angError + 360;
            }
            double velError = segment.vel - vel;
            double desired = (angError * kAng_) + segment.vel;
            double output = desired + (kp_ * error);
            double feedVel = maxVel - (ka_ * segment.acc);
            last_error_ = error;
            last_Ang_error = angError;
            current_heading = segment.heading;
            return new TrajectoryStatus(segment, error, velError,
                    angError, feedVel, output);
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

    private Trajectory.Segment interpolateSegments(int currentSeg, double time) {
        if (currentSeg == 0) {
            return profile_.getSegment(currentSeg);
        }
        Trajectory.Segment firstSeg = profile_.getSegment(currentSeg - 1);
        Trajectory.Segment lastSeg = profile_.getSegment(currentSeg);
        double pos, vel, acc, jerk, heading, dt, x, y;
        double firstTime = firstSeg.dt * (currentSeg - 1);
        double lastTime = lastSeg.dt * (currentSeg);
        double currentTime = time - Dt;
        pos = (((currentTime - firstTime) * (lastSeg.pos - firstSeg.pos)) / (lastTime - firstTime))
                + firstSeg.pos;
        vel = (((currentTime - firstTime) * (lastSeg.vel - firstSeg.vel)) / (lastTime - firstTime))
                + firstSeg.vel;
        acc = (((currentTime - firstTime) * (lastSeg.acc - firstSeg.acc)) / (lastTime - firstTime))
                + firstSeg.acc;
        jerk = (((currentTime - firstTime) * (lastSeg.jerk - firstSeg.jerk)) / (lastTime - firstTime))
                + firstSeg.jerk;
        heading = lastSeg.heading;
        dt = firstSeg.dt;
        x = (((currentTime - firstTime) * (lastSeg.x - firstSeg.x)) / (lastTime - firstTime))
                + firstSeg.x;
        y = (((currentTime - firstTime) * (lastSeg.y - firstSeg.y)) / (lastTime - firstTime))
                + firstSeg.y;
        return new Trajectory.Segment(pos, vel, acc, jerk, heading, dt, x, y);
    }


}
