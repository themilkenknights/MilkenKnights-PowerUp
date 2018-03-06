import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class XYLineChart_AWT {

	public static final HashMap<String, Waypoint[]> robotPaths = new HashMap<String, Waypoint[]>();
	public static final double SWITCH_X = 130;

	static {
		robotPaths.put("CSR-1", new Waypoint[]{
				new Waypoint(23, 156, 0),
				new Waypoint(SWITCH_X, 106, Pathfinder.d2r(0))
		});

		robotPaths.put("CSR-2", new Waypoint[]{
				new Waypoint(SWITCH_X, 106, Pathfinder.d2r(0)),
				new Waypoint(105, 132, Pathfinder.d2r(90)),
		});
		robotPaths.put("CSR-3", new Waypoint[]{
				new Waypoint(105, 132, Pathfinder.d2r(90)),
				new Waypoint(SWITCH_X, 106, Pathfinder.d2r(0)),
		});

		robotPaths.put("CSR-4", new Waypoint[]{
				new Waypoint(SWITCH_X, 106, Pathfinder.d2r(0)),
				new Waypoint(100,146, Pathfinder.d2r(-120)),
		});
		robotPaths.put("CSR-5", new Waypoint[]{
				new Waypoint(100,146, Pathfinder.d2r(-120)),
				new Waypoint(35, 190, Pathfinder.d2r(0)),
		});

		robotPaths.put("CSL-1", new Waypoint[]{
				new Waypoint(23, 156, 0),
				new Waypoint(SWITCH_X, 218, Pathfinder.d2r(0)),
		});

		robotPaths.put("CSL-2", new Waypoint[]{
				new Waypoint(107, 188, Pathfinder.d2r(90)),
				new Waypoint(SWITCH_X, 218, Pathfinder.d2r(0)),
		});

		robotPaths.put("CSL-3", new Waypoint[]{
				new Waypoint(105, 186, Pathfinder.d2r(-45)),
				new Waypoint(33, 192, Pathfinder.d2r(0))
		});

		robotPaths.put("DriveStraight", new Waypoint[]{
				new Waypoint(23, 156, 0),
				new Waypoint(SWITCH_X, 156, 0)
		});
	}

	public static void main(String[] args) {
		double dt = System.nanoTime() * 1e-9;
		Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_QUINTIC,
				Trajectory.Config.SAMPLES_HIGH, 0.005, 150, 75, 200);
		HashMap<String, Trajectory> swerdPaths = new HashMap<>();
		for (Map.Entry<String, Waypoint[]> path : robotPaths.entrySet()) {
			Trajectory trajectory = Pathfinder.generate(path.getValue(), config);
			File pathFile = new File("paths/" + path.getKey() + ".csv").getAbsoluteFile();
			Pathfinder.writeToCSV(pathFile, trajectory);
			System.out
					.println("Path: " + path.getKey() + " Time: " + trajectory.length() * 0.005 + " Sec");
			System.out.println((System.nanoTime() * 1e-9) - dt);
			swerdPaths.put(path.getKey(), trajectory);
		}

     /*   XYLineChart_AWT chart = new XYLineChart_AWT("FRC 1836 Robot Auto Path",
                "Robot Path", swerdPaths);
        chart.pack();
        RefineryUtilities.centerFrameOnScreen(chart);
        chart.setVisible(true); */

	}

	/*public XYLineChart_AWT(String applicationTitle, String chartTitle,
			HashMap<String, Trajectory> rPaths) {
		super(applicationTitle);

		final XYSeriesCollection dataset = new XYSeriesCollection();
		for (Map.Entry<String, Trajectory> path : rPaths.entrySet()) {
			final XYSeries graph = new XYSeries(path.getKey());
			for (Trajectory.Segment segment : path.getValue().segments) {
				graph.add(segment.x, segment.y);
			}

			dataset.addSeries(graph);
		}

		JFreeChart xylineChart = ChartFactory.createXYLineChart(
				chartTitle,
				"X",
				"Y",
				dataset,
				PlotOrientation.HORIZONTAL,
				true, false, false);

		ChartPanel chartPanel = new ChartPanel(xylineChart);
		chartPanel.setPreferredSize(new Dimension(560, 367));
		chartPanel.getChart().getXYPlot().getRangeAxis().setInverted(true);
		BufferedImage image = null;
		File url = new File("path_generator/field.png");
		System.out.println(url.getAbsolutePath());
		try {
			image = ImageIO.read(url);
		} catch (IOException e) {
			System.out.println(e);
		}
		xylineChart.setBackgroundImage(image);
		xylineChart.getPlot().setBackgroundAlpha(0);
		xylineChart.setBackgroundImageAlignment(100);

		final XYPlot plot = xylineChart.getXYPlot();

		NumberAxis domain = (NumberAxis) plot.getDomainAxis();
		domain.setRange(0, 150);
		domain.setTickUnit(new NumberTickUnit(50));
		domain.setVerticalTickLabels(true);
		NumberAxis range = (NumberAxis) plot.getRangeAxis();
		range.setRange(0.0, 324);
		range.setTickUnit(new NumberTickUnit(50));

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

		for (int i = 0; i < rPaths.size(); i++) {
			renderer.setSeriesPaint(i, Color.RED);
			renderer.setSeriesStroke(i, new BasicStroke(1.0f));
			renderer.setSeriesShapesVisible(i, false);
		}
		plot.setRenderer(renderer);
		setContentPane(chartPanel);
	} */
}



