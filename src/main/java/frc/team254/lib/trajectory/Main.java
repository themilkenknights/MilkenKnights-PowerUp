package frc.team254.lib.trajectory;

import frc.team254.lib.trajectory.io.TextFileSerializer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
        //String directory = "/Users/aswerdlow/MilkenKnights-PowerUp/src/main/java/frc/team1836/robot/auto/paths";
        String directory = "/Users/aswerdlow/Desktop";
        if (args.length >= 1) {
            directory = args[0];
        }

        TrajectoryGenerator.Config config = new TrajectoryGenerator.Config();
        config.dt = .005;
        config.max_acc = 30;
        config.max_jerk = 60;
        config.max_vel = 87.96;

        final double kWheelbaseWidth = 26;

        // Path name must be a valid Java class name.
        final String path_name = "StraightPath";

        //double Dt = System.nanoTime();

		/*Trajectory left = TrajectoryGenerator.generate(config,
				TrajectoryGenerator.AutomaticStrategy, 0, 0,
				50, 0.0, 0); */

        //System.out.println(((System.nanoTime() - Dt) * 1e-6) + " MS");

        WaypointSequence p = new WaypointSequence(10);
        p.addWaypoint(new WaypointSequence.Waypoint(0, 0, 0));
        p.addWaypoint(new WaypointSequence.Waypoint(2, 2, 0));

        Path path = PathGenerator.makePath(p, config,
                kWheelbaseWidth, path_name);
        // Outputs to the directory supplied as the first argument.
        TextFileSerializer js = new TextFileSerializer();
        String serialized = js.serialize(path);
        //System.out.print(serialized);
        String fullpath = joinPath(directory, path_name + ".csv");
        if (!writeFile(fullpath, serialized)) {
            System.err.println(fullpath + " could not be written!!!!1");
            System.exit(1);
        } else {
            System.out.println("Wrote " + fullpath);
        }

    }
}
