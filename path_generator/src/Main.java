import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static final HashMap<String, Waypoint[]> robotPaths = new HashMap<String, Waypoint[]>();

    static {
        robotPaths.put("CenterSwitchRight", new Waypoint[]{
                new Waypoint(23, 156, 0),
                new Waypoint(125, 106, Pathfinder.d2r(0)),
                new Waypoint(105, 132, Pathfinder.d2r(90)),
                new Waypoint(125, 106, Pathfinder.d2r(0)),
                new Waypoint(105, 137, Pathfinder.d2r(45)),
                new Waypoint(85, 115, Pathfinder.d2r(0)),
                new Waypoint(65, 140, Pathfinder.d2r(90)),
                new Waypoint(33, 192, Pathfinder.d2r(0))
        });

        robotPaths.put("CenterSwitchLeft", new Waypoint[]{
                new Waypoint(23, 156, 0),
                new Waypoint(125, 218, Pathfinder.d2r(0)),
                new Waypoint(107, 188, Pathfinder.d2r(90)),
                new Waypoint(125, 218, Pathfinder.d2r(0)),
                new Waypoint(105, 186, Pathfinder.d2r(-45)),
                new Waypoint(33, 192, Pathfinder.d2r(0))
        });
        robotPaths.put("DriveStraight", new Waypoint[]{
                new Waypoint(23, 156, 0),
                new Waypoint(125, 156, 0)
        });
    }

    public static void main(String[] args) {
        double dt = System.nanoTime() * 1e-9;
        Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_QUINTIC, Trajectory.Config.SAMPLES_HIGH, 0.005, 150, 75, 200);
        for (Map.Entry<String, Waypoint[]> path : robotPaths.entrySet()) {
            Trajectory trajectory = Pathfinder.generate(path.getValue(), config);
            File pathFile = new File("paths/" + path.getKey() + ".csv").getAbsoluteFile();
            Pathfinder.writeToCSV(pathFile, trajectory);
            System.out.println("Path: " + path.getKey() + " Time: " + trajectory.length() * 0.005 + " Sec");
            System.out.println((System.nanoTime() * 1e-9) - dt);
        }
    }
}

