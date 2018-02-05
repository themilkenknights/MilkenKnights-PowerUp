import java.util.ArrayList;
import javafx.util.Pair;
import trajectory.WaypointSequence;
import trajectory.WaypointSequence.Waypoint;

public final class Constants {

	public static final double SCALE_X = 107;
	public static final double SCALE_Y = 82;

	public static ArrayList<Pair<String, WaypointSequence>> paths = new ArrayList<>();
	public static WaypointSequence centerLeftScale = new WaypointSequence(10);
	public static WaypointSequence centerRightScale = new WaypointSequence(10);

	static {
		centerLeftScale.addWaypoint(new Waypoint(0, 0, 0));
		centerLeftScale.addWaypoint(new Waypoint(SCALE_X, -SCALE_Y, 0));
		paths.add(new Pair<>("CenterLeftScale", centerLeftScale));
	}

	static {
		centerRightScale.addWaypoint(new Waypoint(0, 0, 0));
		centerRightScale.addWaypoint(new Waypoint(SCALE_X, SCALE_Y, 0));
		paths.add(new Pair<>("CenterRightScale", centerRightScale));
	}


}
