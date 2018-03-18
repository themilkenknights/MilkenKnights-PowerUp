package frc.team1836.robot.auto.actions;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.team1836.robot.AutoChooser;
import frc.team1836.robot.AutoChooser.GameObjectPosition;
import frc.team1836.robot.Constants.DRIVE;
import frc.team1836.robot.RobotState;
import frc.team1836.robot.auto.trajectory.Path;
import frc.team1836.robot.subsystems.Drive;
import frc.team1836.robot.util.auto.Action;

public class DrivePathAction implements Action {

  private final Path path;
  private boolean done;
  private boolean brakeMode;

  public DrivePathAction(Path path, boolean dir, boolean flip, boolean brakeMode) {
    this.path = path.copyPath();
    this.brakeMode = brakeMode;
    if (dir) {
      this.path.invert();
    }
    if (flip) {
      this.path.invertSide();
    }
    done = false;
  }

  public DrivePathAction(int pathNum, boolean dir, boolean brakeMode) {
    this(AutoChooser.autoPaths.get("CS-" + Integer.toString(pathNum) + ((RobotState.matchData.switchPosition == GameObjectPosition.LEFT) ? "L" : "R") + ((RobotState.matchData.alliance == Alliance.Blue) ? "B" : "R")), dir, false, brakeMode);
  }

  @Override
  public boolean isFinished() {
    if (done) {
      return true;
    }
    if (Drive.getInstance().isPathFinished()) {
      done = true;
      return true;
    }
    return false;
  }

  @Override
  public void update() {

  }

  @Override
  public void done() {
    RobotState.mDriveControlState = RobotState.DriveControlState.VELOCITY_SETPOINT;
  }

  @Override
  public void start() {
    Drive.getInstance().setDrivePath(path, DRIVE.PATH_DIST_TOL, DRIVE.PATH_ANGLE_TOL, brakeMode);
  }
}
