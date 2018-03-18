package frc.team1836.robot.util.auto;

import frc.team1836.robot.Constants;
import frc.team1836.robot.auto.trajectory.Path;
import frc.team1836.robot.util.logging.Log;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.modifiers.TankModifier;
import java.io.File;

public class DeserializePath {

	public static Path getPathFromFile(String name) {
		try {
			String filePath = Constants.AUTO.pathPath + name + ".csv";
			Trajectory traj = Pathfinder.readFromCSV(new File(filePath));
			TankModifier modifier = new TankModifier(traj).modify(Constants.DRIVE.PATH_WHEELBASE);
			Trajectory left = modifier.getLeftTrajectory();
			Trajectory right = modifier.getRightTrajectory();
			for (Trajectory.Segment segment : left.segments) {
				segment.position = -segment.position;
				segment.velocity = -segment.velocity;
				segment.acceleration = -segment.acceleration;
				segment.jerk = -segment.jerk;
			}
			for (Trajectory.Segment segment : right.segments) {
				segment.position = -segment.position;
				segment.velocity = -segment.velocity;
				segment.acceleration = -segment.acceleration;
				segment.jerk = -segment.jerk;
			}
			return new Path(name, new Path.Pair(right, left));
		} catch (Throwable t) {
			Log.marker("Crashed Trying to Deserialize Paths");
			Log.logThrowableCrash(t);
			throw t;
		}
	}
}
