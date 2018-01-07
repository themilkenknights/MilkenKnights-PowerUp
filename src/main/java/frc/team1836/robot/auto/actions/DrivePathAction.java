package frc.team1836.robot.auto.actions;

import frc.team1836.robot.Constants.DRIVE;
import frc.team1836.robot.subsystems.Drive;
import frc.team1836.robot.util.auto.Action;
import frc.team254.lib.trajectory.Path;

public class DrivePathAction implements Action {

	private final Path path;

	public DrivePathAction(Path path) {
		this.path = path;
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	@Override
	public void update() {

	}

	@Override
	public void done() {

	}

	@Override
	public void start() {
		Drive.getInstance().setWantDrivePath(path, DRIVE.PATH_DIST_TOL, DRIVE.PATH_ANGLE_TOL);
	}
}
