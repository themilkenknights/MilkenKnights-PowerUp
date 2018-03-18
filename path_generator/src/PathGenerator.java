import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PathGenerator {

  public static final HashMap<String, Path> robotPaths = new HashMap<>();
  public static final Trajectory.Config fastConfig = new Trajectory.Config(
      Trajectory.FitMethod.HERMITE_QUINTIC, Trajectory.Config.SAMPLES_HIGH,
      0.005, 145, 130, 800);

  public static final Trajectory.Config defaultConfig = new Trajectory.Config(
      Trajectory.FitMethod.HERMITE_QUINTIC, Trajectory.Config
      .SAMPLES_HIGH, 0.005, 140, 100, 400);

  public static final Trajectory.Config slowerConfig = new Trajectory.Config(
      Trajectory.FitMethod.HERMITE_QUINTIC, Trajectory.Config.SAMPLES_HIGH,
      0.005, 100, 100, 400);

  public static final double BLUE_LEFT_SWITCH_TO_SIDE_WALL = 86;
  public static final double BLUE_RIGHT_SWITCH_TO_SIDE_WALL = 84;
  public static final double BLUE_SWITCH_TO_WALL = 145;
  public static final double BLUE_SWITCH_X_OFFSET = BLUE_SWITCH_TO_WALL - 140;
  public static final double BLUE_SWITCH_Y_OFFSET =
      (BLUE_RIGHT_SWITCH_TO_SIDE_WALL - BLUE_LEFT_SWITCH_TO_SIDE_WALL) / 2;

  public static final double RED_LEFT_SWITCH_TO_SIDE_WALL = 86;
  public static final double RED_RIGHT_SWITCH_TO_SIDE_WALL = 84;
  public static final double RED_SWITCH_TO_WALL = 145;
  public static final double RED_SWITCH_X_OFFSET = RED_SWITCH_TO_WALL - 140;
  public static final double RED_SWITCH_Y_OFFSET =
      (RED_RIGHT_SWITCH_TO_SIDE_WALL - RED_LEFT_SWITCH_TO_SIDE_WALL) / 2;

  static {
    robotPaths.put("CS-1", new Path(new Waypoint[]{
        new Waypoint(22, -7, Pathfinder.d2r(0)),
        new Waypoint(121, -59, Pathfinder.d2r(0))},
        fastConfig, true));

    robotPaths.put("CS-2", new Path(new Waypoint[]{
        new Waypoint(121, -59, Pathfinder.d2r(0)),
        new Waypoint(60, 8, Pathfinder.d2r(-30)),
    }, fastConfig));

    robotPaths.put("CS-3", new Path(new Waypoint[]{
        new Waypoint(60, 8, Pathfinder.d2r(-30)),
        new Waypoint(84, 0, Pathfinder.d2r(0)),
    }, fastConfig));

    robotPaths.put("CS-4", new Path(new Waypoint[]{
        new Waypoint(84, 0, Pathfinder.d2r(0)),
        new Waypoint(65, 4, Pathfinder.d2r(-30)),
    }, fastConfig));

    robotPaths.put("CS-5", new Path(new Waypoint[]{
        new Waypoint(65, 4, Pathfinder.d2r(-30)),
        new Waypoint(123, -54, Pathfinder.d2r(0)),
    }, fastConfig));

    robotPaths.put("CS-6", new Path(new Waypoint[]{
        new Waypoint(121, -54, Pathfinder.d2r(0)),
        new Waypoint(68, 6, Pathfinder.d2r(-30)),
    }, fastConfig));

    robotPaths.put("CS-7", new Path(new Waypoint[]{
        new Waypoint(68, 6, Pathfinder.d2r(-30)),
        new Waypoint(96, 0, Pathfinder.d2r(0)),
    }, slowerConfig));

    robotPaths.put("CS-8", new Path(new Waypoint[]{
        new Waypoint(96, 0, Pathfinder.d2r(0)),
        new Waypoint(69, 6, Pathfinder.d2r(-30)),
    }, fastConfig));

    robotPaths.put("CS-9", new Path(new Waypoint[]{
        new Waypoint(69, 6, Pathfinder.d2r(-30)),
        new Waypoint(118, -49, Pathfinder.d2r(-15)),
    }, fastConfig));

    robotPaths.put("DriveStraight", new Path(new Waypoint[]{
        new Waypoint(23, 156, 0),
        new Waypoint(127, 156, 0)
    }, fastConfig));

    robotPaths.put("FS-1", new Path(new Waypoint[]{
        new Waypoint(22, -113, Pathfinder.d2r(0)),
        new Waypoint(119, -131, Pathfinder.d2r(0)),
        new Waypoint(163, -114, Pathfinder.d2r(45)),
        new Waypoint(170, -96, Pathfinder.d2r(90)),
    }, fastConfig, false, true));

  }

  public static void main(String[] args) {
    for (Map.Entry<String, Path> container : robotPaths.entrySet()) {
      if (container.getValue().isBothSides()) {
        Path bTraj = container.getValue();
        bTraj.setOffset(BLUE_SWITCH_X_OFFSET, BLUE_SWITCH_Y_OFFSET);

        Path rTraj = container.getValue();
        rTraj.setOffset(RED_SWITCH_X_OFFSET, RED_SWITCH_Y_OFFSET);

        File leftBPathFile = new File("paths/" + container.getKey() + "LB.csv").getAbsoluteFile();
        File rightBPathFile = new File("paths/" + container.getKey() + "RB.csv").getAbsoluteFile();

        Trajectory leftBTraj = Pathfinder
            .generate(bTraj.getLeftPoints(), bTraj.getConfig());
        Trajectory rightBTraj = Pathfinder
            .generate(bTraj.getPoints(), bTraj.getConfig());

        Pathfinder.writeToCSV(leftBPathFile, leftBTraj);
        Pathfinder.writeToCSV(rightBPathFile, rightBTraj);

        File leftRPathFile = new File("paths/" + container.getKey() + "LR.csv").getAbsoluteFile();
        File rightRPathFile = new File("paths/" + container.getKey() + "RR.csv").getAbsoluteFile();

        Trajectory leftRTraj = Pathfinder
            .generate(rTraj.getLeftPoints(), rTraj.getConfig());
        Trajectory rightRTraj = Pathfinder
            .generate(rTraj.getPoints(), rTraj.getConfig());

        Pathfinder.writeToCSV(leftRPathFile, leftRTraj);
        Pathfinder.writeToCSV(rightRPathFile, rightRTraj);

        System.out.println(
            "Path: " + container.getKey() + " Time: " + leftBTraj.length() * 0.005 + " Sec");
      } else {
        File pathFile = new File("paths/" + container.getKey() + ".csv").getAbsoluteFile();
        Trajectory trajectory = Pathfinder
            .generate(container.getValue().getPoints(), container.getValue().getConfig());
        Pathfinder.writeToCSV(pathFile, trajectory);
        System.out.println(
            "Path: " + container.getKey() + " Time: " + trajectory.length() * 0.005 + " Sec");
      }
    }
  }

  static class Path {

    Waypoint[] points;
    Trajectory.Config config;
    boolean first;
    boolean bothSides;

    public Path(Waypoint[] points, Trajectory.Config config, boolean first, boolean bothSides) {
      this.points = points;
      this.config = config;
      this.first = first;
      this.bothSides = bothSides;
    }

    public Path(Waypoint[] points, Trajectory.Config config, boolean first) {
      this(points, config, first, true);
    }

    public Path(Waypoint[] points, Trajectory.Config config) {
      this(points, config, false, true);
    }


    public void setOffset(double x, double y) {
      if (first) {
        for (int i = 1; i < points.length; i++) {
          points[i].x = points[i].x + x;
          points[i].y = points[i].y + y;
        }
      } else {
        for (Waypoint waypoint : points) {
          waypoint.x = waypoint.x + x;
          waypoint.y = waypoint.y + y;
        }
      }
    }

    public Waypoint[] getPoints() {
      return points;
    }

    public boolean isBothSides() {
      return bothSides;
    }

    public Waypoint[] getLeftPoints() {
      Waypoint[] waypoints = points.clone();
      if (first) {
        for (int i = 1; i < waypoints.length; i++) {
          waypoints[i].y = -waypoints[i].y;
          waypoints[i].angle = -waypoints[i].angle;
        }
      } else {
        for (Waypoint waypoint : waypoints) {
          waypoint.y = -waypoint.y;
          waypoint.angle = -waypoint.angle;
        }
      }
      return waypoints;
    }

    public Trajectory.Config getConfig() {
      return config;
    }

  }

}
