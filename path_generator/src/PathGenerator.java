import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PathGenerator {


    public static final HashMap<String, Path> robotPaths = new HashMap<>();
    public static final Trajectory.Config fastConfig = new Trajectory.Config(
            Trajectory.FitMethod.HERMITE_QUINTIC, Trajectory.Config.SAMPLES_HIGH, 0.005, 145, 125, 600);
    public static final Trajectory.Config defaultConfig = new Trajectory.Config(
            Trajectory.FitMethod.HERMITE_QUINTIC, Trajectory.Config.SAMPLES_HIGH, 0.005, 140, 100, 400);

    public static final Trajectory.Config slowerConfig = new Trajectory.Config(
            Trajectory.FitMethod.HERMITE_QUINTIC, Trajectory.Config.SAMPLES_HIGH, 0.005, 100, 60, 100);

    public static final Trajectory.Config slowConfig = new Trajectory.Config(
            Trajectory.FitMethod.HERMITE_QUINTIC, Trajectory.Config.SAMPLES_HIGH, 0.005, 120, 80, 250);

    public static final double SWITCH_X_OFFSET = 0;
    public static final double SWITCH_Y_OFFSET = 0;

    static {
        robotPaths.put("CS-1R",
                new Path(new Waypoint[]{
                        new Waypoint(22, -7, Pathfinder.d2r(0)),
                        new Waypoint(121, -59, Pathfinder.d2r(0))
                }, fastConfig, false));

        robotPaths.put("CS-1L",
                new Path(new Waypoint[]{
                        new Waypoint(22, -7, Pathfinder.d2r(0)),
                        new Waypoint(121, 59, Pathfinder.d2r(0)),
                }, fastConfig, false));


        robotPaths.put("CS-2",
                new Path(new Waypoint[]{
                        new Waypoint(121, -59, Pathfinder.d2r(0)),
                        new Waypoint(75, -59, Pathfinder.d2r(0)),
                        new Waypoint(60, -56, Pathfinder.d2r(-30)),
                        new Waypoint(48, -44, Pathfinder.d2r(-60)),
                        new Waypoint(45, -31, Pathfinder.d2r(-90)),
                        new Waypoint(52, -12, Pathfinder.d2r(-120)),
                        new Waypoint(64, -3, Pathfinder.d2r(-150)),
                        new Waypoint(90, 2, Pathfinder.d2r(-180)),
                }, defaultConfig, true));


        robotPaths.put("CS-3",
                new Path(new Waypoint[]{
                        new Waypoint(90, 2, Pathfinder.d2r(-180)),
                        new Waypoint(64, -3, Pathfinder.d2r(-150)),
                        new Waypoint(52, -12, Pathfinder.d2r(-120)),
                        new Waypoint(45, -31, Pathfinder.d2r(-90)),
                        new Waypoint(48, -44, Pathfinder.d2r(-60)),
                        new Waypoint(60, -56, Pathfinder.d2r(-30)),
                        new Waypoint(75, -59, Pathfinder.d2r(0)),
                        new Waypoint(121, -59, Pathfinder.d2r(0)),
                }, defaultConfig, true));

        robotPaths.put("CS-4", new Path(new Waypoint[]{
                new Waypoint(121, -59, Pathfinder.d2r(0)),
                new Waypoint(105, -59, Pathfinder.d2r(0)),
                new Waypoint(78, -52, Pathfinder.d2r(-30)),
                new Waypoint(68, -42, Pathfinder.d2r(-60)),
                new Waypoint(64, -29, Pathfinder.d2r(-90)),
                new Waypoint(68, -15, Pathfinder.d2r(-120)),
                new Waypoint(80, -4, Pathfinder.d2r(-150)),
                new Waypoint(105, 0, Pathfinder.d2r(-180)),
        }, defaultConfig, true));

        robotPaths.put("CS-5", new Path(new Waypoint[]{
                new Waypoint(105, 0, Pathfinder.d2r(-180)),
                new Waypoint(80, -4, Pathfinder.d2r(-150)),
                new Waypoint(68, -15, Pathfinder.d2r(-120)),
                new Waypoint(64, -29, Pathfinder.d2r(-90)),
                new Waypoint(68, -42, Pathfinder.d2r(-60)),
                new Waypoint(78, -52, Pathfinder.d2r(-30)),
                new Waypoint(105, -59, Pathfinder.d2r(0)),
                new Waypoint(121, -59, Pathfinder.d2r(0)),
        }, defaultConfig, true));


        robotPaths.put("DriveStraight",
                new Path(new Waypoint[]{
                        new Waypoint(23, 156, 0),
                        new Waypoint(127, 156, 0)
                }, defaultConfig, false));

        robotPaths.put("CBS-1L",
                new Path(new Waypoint[]{
                        new Waypoint(22, -7, Pathfinder.d2r(0)),
                        new Waypoint(209, -137, Pathfinder.d2r(0)),
                        new Waypoint(234, -124, Pathfinder.d2r(45)),
                        new Waypoint(244, -94, Pathfinder.d2r(90)),
                        new Waypoint(236, -76, Pathfinder.d2r(135)),
                        new Waypoint(221, -71, Pathfinder.d2r(180))
                }, defaultConfig, false));

        robotPaths.put("CBS-1R",
                new Path(new Waypoint[]{
                        new Waypoint(22, -7, Pathfinder.d2r(0)),
                        new Waypoint(209, 137, Pathfinder.d2r(0)),
                        new Waypoint(234, 124, Pathfinder.d2r(45)),
                        new Waypoint(244, 94, Pathfinder.d2r(90)),
                        new Waypoint(236, 76, Pathfinder.d2r(135)),
                        new Waypoint(221, 71, Pathfinder.d2r(180))
                }, defaultConfig, false));


    }

    public static void main(String[] args) {
        for (Map.Entry<String, Path> container : robotPaths.entrySet()) {
            container.getValue().setOffset(SWITCH_X_OFFSET, SWITCH_Y_OFFSET);
            if (container.getValue().bothSides) {
                File leftPathFile = new File("paths/" + container.getKey() + "L.csv").getAbsoluteFile();
                File rightPathFile = new File("paths/" + container.getKey() + "R.csv").getAbsoluteFile();
                Trajectory leftTraj = Pathfinder.generate(container.getValue().getPoints(), container.getValue().getConfig());
                Trajectory rightTraj = Pathfinder.generate(container.getValue().getRightPoints(), container.getValue().getConfig());
                Pathfinder.writeToCSV(leftPathFile, leftTraj);
                Pathfinder.writeToCSV(rightPathFile, rightTraj);
                System.out.println("Path: " + container.getKey() + " Time: " + leftTraj.length() * 0.005 + " Sec");
            } else {
                File pathFile = new File("paths/" + container.getKey() + ".csv").getAbsoluteFile();
                Trajectory trajectory = Pathfinder.generate(container.getValue().getPoints(), container.getValue().getConfig());
                Pathfinder.writeToCSV(pathFile, trajectory);
                System.out.println("Path: " + container.getKey() + " Time: " + trajectory.length() * 0.005 + " Sec");
            }
        }
    }


    static class Path {

        Waypoint[] points;
        Trajectory.Config config;
        boolean bothSides;

        public Path(Waypoint[] points, Trajectory.Config config, boolean bothSides) {
            this.points = points;
            this.config = config;
            this.bothSides = bothSides;
        }

        public Path(Waypoint[] points, Trajectory.Config config) {
            this(points, config, true);
        }

        public void setOffset(double x, double y) {
            for (Waypoint waypoint : points) {
                waypoint.y = waypoint.y + y;
                waypoint.x = waypoint.x + x;
            }
        }


        public Waypoint[] getPoints() {
            return points;

        }

        public Waypoint[] getRightPoints() {
            Waypoint[] waypoints = points.clone();
            for (Waypoint waypoint : waypoints) {
                waypoint.y = -waypoint.y;
                waypoint.angle = -waypoint.angle;
            }
            return waypoints;
        }

        public boolean getBothSides() {
            return bothSides;
        }

        public Trajectory.Config getConfig() {
            return config;
        }

    }

}
