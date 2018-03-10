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
			Trajectory.FitMethod.HERMITE_QUINTIC, Trajectory.Config.SAMPLES_HIGH, 0.005, 100, 65, 120);
	public static final Trajectory.Config slowerConfig = new Trajectory.Config(
			Trajectory.FitMethod.HERMITE_QUINTIC, Trajectory.Config.SAMPLES_HIGH, 0.005, 100, 60, 100);
	public static final double SWITCH_X = 135;

	static {
		robotPaths.put("CSR", new Path[]{
				new Path("CSR-1", new Waypoint[]{
						new Waypoint(23, 155, Pathfinder.d2r(0)),
						new Waypoint(121 + 10, 104, Pathfinder.d2r(0)),
				},
						defaultConfig),
				new Path("CSR-2", new Waypoint[]{
						new Waypoint(121 + 10, 104, Pathfinder.d2r(0)),
						new Waypoint(94 + 10, 113, Pathfinder.d2r(-45)),
						new Waypoint(86 + 10, 129, Pathfinder.d2r(-90)),
						new Waypoint(94 + 10, 148, Pathfinder.d2r(-130)),
				}, slowerConfig),
				new Path("CSR-3", new Waypoint[]{
						new Waypoint(94 + 10, 148, Pathfinder.d2r(-130)),
						new Waypoint(86 + 10, 129, Pathfinder.d2r(-90)),
						new Waypoint(94 + 10, 113, Pathfinder.d2r(-45)),
						new Waypoint(121 + 10, 104, Pathfinder.d2r(0)),


				}, defaultConfig),
				new Path("CSR-4", new Waypoint[]{
						new Waypoint(121 + 10, 104, Pathfinder.d2r(0)),
						new Waypoint(108 + 10, 109, Pathfinder.d2r(-45)),
						new Waypoint(101 + 10, 124, Pathfinder.d2r(-90)),
						new Waypoint(107 + 10, 140, Pathfinder.d2r(-130)),
				},
						slowerConfig),
				new Path("CSR-5", new Waypoint[]{
						new Waypoint(107 + 10, 140, Pathfinder.d2r(-130)),
						new Waypoint(101 + 10, 120, Pathfinder.d2r(-90)),
						new Waypoint(108 + 10, 106, Pathfinder.d2r(-45)),
						new Waypoint(121 + 10, 100, Pathfinder.d2r(0)),


				}, defaultConfig)
		});

		robotPaths.put("CSL", new Path[]{
				new Path("CSL-1", new Waypoint[]{
						new Waypoint(23, 155, Pathfinder.d2r(0)),
						new Waypoint(121 + 10, 216, Pathfinder.d2r(0)),
				}, defaultConfig),
				new Path("CSL-2", new Waypoint[]{
						new Waypoint(121 + 10, 216, Pathfinder.d2r(0)),
						new Waypoint(97 + 10, 208, Pathfinder.d2r(45)),
						new Waypoint(90 + 10, 192, Pathfinder.d2r(90)),
						new Waypoint(95 + 10, 176, Pathfinder.d2r(135)),
				}, slowerConfig),
				new Path("CSL-3", new Waypoint[]{
						new Waypoint(95, 176, Pathfinder.d2r(135)),
						new Waypoint(90, 192, Pathfinder.d2r(90)),
						new Waypoint(97, 214, Pathfinder.d2r(45)),
						new Waypoint(121, 224, Pathfinder.d2r(0)),

				}, defaultConfig),
				new Path("CSL-4", new Waypoint[]{
						new Waypoint(121 + 10, 216, Pathfinder.d2r(0)),
						new Waypoint(108 + 10, 211, Pathfinder.d2r(45)),
						new Waypoint(103 + 10, 198, Pathfinder.d2r(90)),
						new Waypoint(111 + 10, 180, Pathfinder.d2r(130)),
				},
						slowerConfig),
				new Path("CSL-5", new Waypoint[]{
						new Waypoint(97 + 10, 182, Pathfinder.d2r(130)),
						new Waypoint(94 + 10, 193, Pathfinder.d2r(90)),
						new Waypoint(102 + 10, 210, Pathfinder.d2r(45)),
						new Waypoint(123 + 10, 219, Pathfinder.d2r(0)),
				}, defaultConfig)
		});

		robotPaths.put("DriveStraight", new Path[]{
				new Path("DriveStraight", new Waypoint[]{
						new Waypoint(23, 156, 0),
						new Waypoint(127, 156, 0)
				}, defaultConfig)
		});

	}

	public static void main(String[] args) {
		for (Map.Entry<String, Path[]> container : robotPaths.entrySet()) {
			double lastAngle = 0;
			double pathTime = 0;
			for (Path path : container.getValue()) {
				Trajectory trajectory = Pathfinder.generate(path.getPoints(), path.getConfig());
              /*  double offset = lastAngle - Pathfinder.boundHalfDegrees(Pathfinder.r2d(trajectory.get(0).heading));
                for (Trajectory.Segment segment : trajectory.segments) {
                    segment.heading = Pathfinder.boundHalfDegrees(Pathfinder.r2d(segment.heading) + offset);
                } */
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
