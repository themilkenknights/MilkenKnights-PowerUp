import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PathGenerator {


    public static final HashMap<String, Path[]> robotPaths = new HashMap<>();
    public static final Trajectory.Config defaultConfig = new Trajectory.Config(
            Trajectory.FitMethod.HERMITE_QUINTIC, Trajectory.Config.SAMPLES_HIGH, 0.005, 145, 95, 500);
    public static final Trajectory.Config slowConfig = new Trajectory.Config(
            Trajectory.FitMethod.HERMITE_QUINTIC, Trajectory.Config.SAMPLES_HIGH, 0.005, 130, 85, 120);
    public static final Trajectory.Config slowerConfig = new Trajectory.Config(
            Trajectory.FitMethod.HERMITE_QUINTIC, Trajectory.Config.SAMPLES_HIGH, 0.005, 100, 60, 100);
    public static final double SWITCH_X = 135;

    static {
        robotPaths.put("CSR", new Path[]{
                new Path("CSR-1", new Waypoint[]{
                        new Waypoint(23, 155, Pathfinder.d2r(0)),
                        new Waypoint(121, 104, Pathfinder.d2r(0)),
                },
                        defaultConfig),
                new Path("CSR-2", new Waypoint[]{
                        new Waypoint(121, 104, Pathfinder.d2r(0)),
                        new Waypoint(90, 113, Pathfinder.d2r(-45)),
                        new Waypoint(83, 130, Pathfinder.d2r(-90)),
                        new Waypoint(93, 147, Pathfinder.d2r(-140)),
                },
                        defaultConfig),
                new Path("CSR-3", new Waypoint[]{
                        new Waypoint(93, 147, Pathfinder.d2r(-140)),
                        new Waypoint(83, 130, Pathfinder.d2r(-90)),
                        new Waypoint(90, 113, Pathfinder.d2r(-45)),
                        new Waypoint(121, 104, Pathfinder.d2r(0)),
                }, defaultConfig),
                new Path("CSR-4", new Waypoint[]{
                        new Waypoint(121, 104, Pathfinder.d2r(0)),
                        new Waypoint(108, 110, Pathfinder.d2r(-45)),
                        new Waypoint(101, 124, Pathfinder.d2r(-90)),
                        new Waypoint(105, 140, Pathfinder.d2r(-130)),
                },
                        defaultConfig),
                new Path("CSR-5", new Waypoint[]{
                        new Waypoint(105, 140, Pathfinder.d2r(-130)),
                        new Waypoint(101, 124, Pathfinder.d2r(-90)),
                        new Waypoint(108, 110, Pathfinder.d2r(-45)),
                        new Waypoint(121, 104, Pathfinder.d2r(0)),
                }, defaultConfig)
        });


        robotPaths.put("CSL", new Path[]{
                new Path("CSL-1", new Waypoint[]{
                        new Waypoint(23, 156, Pathfinder.d2r(0)),
                        new Waypoint(SWITCH_X, 218 - 12, Pathfinder.d2r(0)),
                }, defaultConfig),
                new Path("CSL-2", new Waypoint[]{
                        new Waypoint(130, 215, Pathfinder.d2r(0)),
                        new Waypoint(105, 207, Pathfinder.d2r(45)),
                        new Waypoint(97, 187, Pathfinder.d2r(90)),
                        new Waypoint(98, 181, Pathfinder.d2r(120)),
                }, defaultConfig),
                new Path("CSL-3", new Waypoint[]{
                        new Waypoint(98, 181, Pathfinder.d2r(120)),
                        new Waypoint(97, 187, Pathfinder.d2r(90)),
                        new Waypoint(105, 207, Pathfinder.d2r(45)),
                        new Waypoint(130, 215, Pathfinder.d2r(0)),
                }, defaultConfig),
                new Path("CSL-4", new Waypoint[]{
                        new Waypoint(130, 215, Pathfinder.d2r(0)),
                        new Waypoint(116, 209, Pathfinder.d2r(45)),
                        new Waypoint(110, 194, Pathfinder.d2r(90)),
                        new Waypoint(112, 184, Pathfinder.d2r(120)),
                }, defaultConfig),
                new Path("CSL-5", new Waypoint[]{
                        new Waypoint(112, 184, Pathfinder.d2r(120)),
                        new Waypoint(110, 194, Pathfinder.d2r(90)),
                        new Waypoint(116, 209, Pathfinder.d2r(45)),
                        new Waypoint(130, 215, Pathfinder.d2r(0)),
                }, defaultConfig)
        });


        robotPaths.put("Drive Straight", new Path[]{
                new Path("Drive Straight", new Waypoint[]{
                        new Waypoint(23, 156, 0),
                        new Waypoint(SWITCH_X, 156, 0)
                }, defaultConfig)
        });

    }

    public static void main(String[] args) {
        for (Map.Entry<String, Path[]> container : robotPaths.entrySet()) {
            double lastAngle = 0;
            double pathTime = 0;
            for (Path path : container.getValue()) {
                Trajectory trajectory = Pathfinder.generate(path.getPoints(), path.getConfig());
                double offset = lastAngle - Pathfinder.boundHalfDegrees(Pathfinder.r2d(trajectory.get(0).heading));
                for (Trajectory.Segment segment : trajectory.segments) {
                    segment.heading = Pathfinder.boundHalfDegrees(Pathfinder.r2d(segment.heading) + offset);
                }
                File pathFile = new File("paths/" + path.getName() + ".csv").getAbsoluteFile();
                Pathfinder.writeToCSV(pathFile, trajectory);
                System.out
                        .println("Path: " + path.getName() + " Time: " + trajectory.length() * 0.005 + " Sec");
                lastAngle = trajectory.get(trajectory.length() - 1).heading;
                pathTime += trajectory.length() * 0.005;
            }
            System.out.println("Container: " + container.getKey() + " Time: " + pathTime + " Sec");
        }
    }


    static class Path {

        Waypoint[] points;
        Trajectory.Config config;
        String name;

        public Path(String name, Waypoint[] points, Trajectory.Config config) {
            this.points = points;
            this.config = config;
            this.name = name;
        }

        public Waypoint[] getPoints() {
            return points;
        }

        public Trajectory.Config getConfig() {
            return config;
        }

        public String getName() {
            return name;
        }

    }
}
