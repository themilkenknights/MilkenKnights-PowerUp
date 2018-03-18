package frc.team1836.robot.auto.trajectory;

import edu.wpi.first.wpilibj.Timer;
import frc.team1836.robot.Constants;
import frc.team1836.robot.util.state.TrajectoryStatus;
import jaci.pathfinder.Trajectory;

/**
 * PID + Feedforward controller for following a Trajectory.
 *
 * @author Jared341
 */
public class TrajectoryFollower {

  private double kp_;
  private double kAng_;
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

  public TrajectoryFollower(Trajectory profile) {
    profile_ = profile;
  }

  public void configure(double kp, double ka, double kAng, double distTol, double angTol) {
    kp_ = kp;
    kAng_ = kAng;
    ka_ = ka;
    _DistTol = distTol;
    _AngTol = angTol;
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
    current_segment = (int) (customRound(currentTime - Dt) / Constants.kLooperDt);
    if (current_segment < profile_.length()) {
      //Trajectory.Segment segment = interpolateSegments(current_segment, currentTime);

      Trajectory.Segment segment = profile_.get(current_segment);
      double error = segment.position - dist;
      double angError = segment.heading - heading;
      if (angError > 180) {
        angError = angError - 360;
      } else if (angError < -180) {
        angError = angError + 360;
      }
      double velError = segment.velocity - vel;
      double output = segment.velocity + (angError * kAng_) + (kp_ * error);
      double arbFeed = (ka_ * segment.acceleration);
      last_error_ = error;
      last_Ang_error = angError;
      current_heading = segment.heading;
      return new TrajectoryStatus(segment, error, velError, angError, arbFeed, output);
    } else {
      return TrajectoryStatus.NEUTRAL;
    }
  }

  public double getHeading() {
    return current_heading;
  }

  public boolean isFinishedTrajectory() {
    return current_segment >= profile_.length();
  }

  private double customRound(double num) {
    return Math.ceil(num * 200) / 200.0;
  }

  public double getLastError() {
    return last_error_;
  }

  public boolean onTarget() {
    return last_error_ < _DistTol && last_Ang_error < _AngTol;
  }

  private Trajectory.Segment interpolateSegments(int currentSeg, double time) {
    if (currentSeg == 0) {
      return profile_.get(currentSeg);
    }
    Trajectory.Segment firstSeg = profile_.get(currentSeg - 1);
    Trajectory.Segment lastSeg = profile_.get(currentSeg);
    double pos, vel, acc, jerk, heading, dt, x, y;
    double firstTime = firstSeg.dt * (currentSeg - 1);
    double lastTime = lastSeg.dt * (currentSeg);
    double currentTime = time - Dt;
    pos = (((currentTime - firstTime) * (lastSeg.position - firstSeg.position)) / (lastTime
        - firstTime)) + firstSeg.position;
    vel = (((currentTime - firstTime) * (lastSeg.velocity - firstSeg.velocity)) / (lastTime
        - firstTime)) + firstSeg.velocity;
    acc = (((currentTime - firstTime) * (lastSeg.acceleration - firstSeg.acceleration)) / (lastTime
        - firstTime)) + firstSeg.acceleration;
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
