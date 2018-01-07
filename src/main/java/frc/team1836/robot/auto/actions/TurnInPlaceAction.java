package frc.team1836.robot.auto.actions;

import frc.team1836.robot.Constants.DRIVE;
import frc.team1836.robot.subsystems.Drive;
import frc.team1836.robot.util.auto.Action;
import frc.team254.lib.trajectory.Path;

public class TurnInPlaceAction implements Action {

	private final Path path;

	public TurnInPlaceAction(Path path) {
		this.path = path;
	}

	@Override
	public boolean isFinished() {
		return Drive.getInstance().isPathFinished();
	}

	@Override
	public void update() {

	}

	@Override
	public void done() {

	}

	@Override
	public void start() {
		Drive.getInstance().setDrivePath(path, DRIVE.PATH_DIST_TOL, DRIVE.PATH_ANGLE_TOL);
	}
}
