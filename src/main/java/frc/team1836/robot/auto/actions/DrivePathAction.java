package frc.team1836.robot.auto.actions;

import frc.team1836.robot.Constants.DRIVE;
import frc.team1836.robot.RobotState;
import frc.team1836.robot.auto.trajectory.Path;
import frc.team1836.robot.subsystems.Drive;
import frc.team1836.robot.util.auto.Action;

public class DrivePathAction implements Action {

	private final Path path;
	private boolean done;

	public DrivePathAction(Path path, boolean dir, boolean flip) {
		this.path = path.copyPath();
		if (dir) {
			this.path.invert();
		}
		if(flip){
			this.path.flipSides();
		}
		done = false;
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
		Drive.getInstance().setDrivePath(path, DRIVE.PATH_DIST_TOL, DRIVE.PATH_ANGLE_TOL);
	}
}
