import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import trajectory.Path;
import trajectory.PathGenerator;
import trajectory.TrajectoryGenerator;
import trajectory.WaypointSequence;
import trajectory.io.JavaSerializer;
import trajectory.io.StringSer;

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
		String directory = "../MilkenKnights-PowerUp/pathing";
		String directory1 = "../MilkenKnights-PowerUp/src/main/java/frc/team1836/robot/auto/paths";
		if (args.length >= 1) {
			directory = args[0];
		}
		TrajectoryGenerator.Config config = new TrajectoryGenerator.Config();
		config.dt = 0.005;
		config.max_acc = 125;
		config.max_jerk = 40;
		config.max_vel = 175;
		final double kWheelbaseWidth = 30;

		final String left_path_name = "LeftPath";
		final String right_path_name = "RightPath";
		final String actual_path_name = "StraightPath";
		WaypointSequence p = new WaypointSequence(10);
		p.addWaypoint(new WaypointSequence.Waypoint(0, 0, 0));
		p.addWaypoint(new WaypointSequence.Waypoint(100, 0, 0));
		Path path = PathGenerator.makePath(p, config,
				kWheelbaseWidth, "StraightPath");
		path.goLeft();

		StringSer js = new StringSer();

		String serializedLeft = js.serialize(path.getLeftWheelTrajectory());
		String leftPath = joinPath(directory, left_path_name + ".csv");
		if (!writeFile(leftPath, serializedLeft)) {
			System.err.println(left_path_name + " could not be written!!!!1");
			System.exit(1);
		} else {
			System.out.println("Wrote " + left_path_name);
		}

		String serializedRight = js.serialize(path.getRightWheelTrajectory());
		String rightPath = joinPath(directory, right_path_name + ".csv");
		if (!writeFile(rightPath, serializedRight)) {
			System.err.println(leftPath + " could not be written!!!!1");
			System.exit(1);
		} else {
			System.out.println("Wrote " + right_path_name);
		}

		JavaSerializer actualSer = new JavaSerializer();
		String fullSer = actualSer.serialize(path);
		String fullpath = joinPath(directory1, "StraightPath" + ".java");
		if (!writeFile(fullpath, fullSer)) {
			System.err.println(fullpath + " could not be written!!!!1");
			System.exit(1);
		} else {
			System.out.println("Wrote " + actual_path_name);
		}

	}
}
