package trajectory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import trajectory.io.JavaSerializer;
import trajectory.io.StringSer;
import trajectory.Path;
import trajectory.Trajectory;

/**
 * @author Jared341
 */
public class Main {

	public static String joinPath(String path1, String path2) {
		File file1 = new File(path1);
		File file2 = new File(file1, path2);
		return file2.getPath();
	}

	private static boolean writeFile(String path, String data) {
		try {
			File file = new File(path);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(data);
			bw.close();
		} catch (IOException e) {
			return false;
		}

		return true;
	}

	public static void main(String[] args) {
		String directory = "/Users/aswerdlow/MilkenKnights-PowerUp/pathing";
		String directory1 = "/Users/aswerdlow/MilkenKnights-PowerUp/src/main/java/frc/team1836/robot/auto/paths";
		if (args.length >= 1) {
			directory = args[0];
		}

		TrajectoryGenerator.Config config = new TrajectoryGenerator.Config();
		config.dt = .005;
		config.max_acc = 125;
		config.max_jerk = 40;
		config.max_vel = 175;

		final double kWheelbaseWidth = 30;

		// Path name must be a valid Java class name.
		final String path_name = "LeftPath";
		final String path_name1 = "RightPath";
		//double Dt = System.nanoTime();

		/*Trajectory left = TrajectoryGenerator.generate(config,
				TrajectoryGenerator.AutomaticStrategy, 0, 0,
				50, 0.0, 0); */

		//System.out.println(((System.nanoTime() - Dt) * 1e-6) + " MS");

		WaypointSequence p = new WaypointSequence(10);
		p.addWaypoint(new WaypointSequence.Waypoint(0, 0, 0));
		p.addWaypoint(new WaypointSequence.Waypoint(40, 0, 0));
		Path path = PathGenerator.makePath(p, config,
				kWheelbaseWidth, "StraightPath");
		path.goLeft();
		// Outputs to the directory supplied as the first argument.
		StringSer js = new StringSer();
		String serialized = js.serialize(path.getLeftWheelTrajectory());
		//System.out.print(serialized);
		String fullpath = joinPath(directory, path_name + ".csv");
		if (!writeFile(fullpath, serialized)) {
			System.err.println(fullpath + " could not be written!!!!1");
			System.exit(1);
		} else {
			System.out.println("Wrote " + fullpath);
		}

		String serialized1 = js.serialize(path.getRightWheelTrajectory());
		//System.out.print(serialized);
		String fullpath1 = joinPath(directory, path_name1 + ".csv");
		if (!writeFile(fullpath1, serialized1)) {
			System.err.println(fullpath + " could not be written!!!!1");
			System.exit(1);
		} else {
			System.out.println("Wrote " + fullpath);
		}


		JavaSerializer js2 = new JavaSerializer();
		String serialized2 = js2.serialize(path);
		//System.out.print(serialized);
		String fullpath2 = joinPath(directory1, "StraightPath" + ".java");
		if (!writeFile(fullpath2, serialized2)) {
			System.err.println(fullpath + " could not be written!!!!1");
			System.exit(1);
		} else {
			System.out.println("Wrote " + fullpath);
		}

	}
}
