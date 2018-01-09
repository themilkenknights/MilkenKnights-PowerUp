package frc.team1836.robot.auto.modes;

import frc.team1836.robot.auto.actions.TurnInPlaceAction;
import frc.team1836.robot.util.auto.AutoModeBase;
import frc.team1836.robot.util.auto.AutoModeEndedException;
import frc.team254.lib.trajectory.Path;

public class TurnInPlaceMode extends AutoModeBase {

	private double angle;
	private Path path;

	public TurnInPlaceMode(double angle) {
		this.angle = angle;
	}

	@Override
	protected void routine() throws AutoModeEndedException {
	/*	TrajectoryGenerator.Config config = new TrajectoryGenerator.Config();

		config.dt = Constants.kLooperDt;
		config.max_acc = DRIVE.MAX_ANG_ACC;
		config.max_jerk = DRIVE.MAX_ANG_JERK;
		config.max_vel = DRIVE.MAX_ANG_VEL;

		Trajectory right = TrajectoryGenerator.generate(config,
				TrajectoryGenerator.TrapezoidalStrategy, 0.0, 0,
				angle, 0.0, 0);

		Trajectory left = right;
		left.scale(-1);
		path = new Path("Turn In Place", new Trajectory.Pair(left, right));

		runAction(new DrivePathAction(path));*/
	runAction(new TurnInPlaceAction());
	}


}
