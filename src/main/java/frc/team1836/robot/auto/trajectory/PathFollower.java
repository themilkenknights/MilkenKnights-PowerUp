package frc.team1836.robot.auto.trajectory;

import frc.team1836.robot.Constants;
import frc.team1836.robot.util.state.TrajectoryStatus;

public class PathFollower {

	protected TrajectoryFollower lFollower;
	protected TrajectoryFollower rFollower;

	public PathFollower(Path mPath, double distTol, double angTol) {
		/*for (int i = 0; i < mPath.getLeftWheelTrajectory().segments.length; i++) {
			System.out.print(Math.abs(mPath.getLeftWheelTrajectory().get(i).velocity - mPath.getRightWheelTrajectory().get(i).velocity) + " ");
		}*/
		lFollower = new TrajectoryFollower(mPath.getLeftWheelTrajectory());
		lFollower.configure(Constants.DRIVE.DRIVE_FOLLOWER_P,
				Constants.DRIVE.DRIVE_FOLLOWER_A, Constants.DRIVE.DRIVE_FOLLOWER_ANG, distTol, angTol);
		rFollower = new TrajectoryFollower(mPath.getRightWheelTrajectory());
		rFollower.configure(Constants.DRIVE.DRIVE_FOLLOWER_P,
				Constants.DRIVE.DRIVE_FOLLOWER_A, -Constants.DRIVE.DRIVE_FOLLOWER_ANG, distTol, angTol);
	}


	public TrajectoryStatus getLeftVelocity(double dist, double vel, double angle) {
		return lFollower.calculate(dist, vel, angle);
	}

	public TrajectoryStatus getRightVelocity(double dist, double vel, double angle) {
		return rFollower.calculate(dist, vel, angle);
	}

	public boolean getFinished() {
		return lFollower.isFinishedTrajectory() && rFollower.isFinishedTrajectory();
	}
}
