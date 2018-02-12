package frc.team254.lib.trajectory.io;

import frc.team254.lib.trajectory.Trajectory;
import frc.team254.lib.trajectory.Trajectory.Segment;

/**
 * Serializes a Path to a simple space and CR separated text file.
 *
 * @author Jared341
 */
public class StringSer implements IPathSerializer1 {

    /**
     * Format:
     * PathName
     * NumSegments
     * LeftSegment1
     * ...
     * LeftSegmentN
     * RightSegment1
     * ...
     * RightSegmentN
     * <p>
     * Each segment is in the format:
     * pos vel acc jerk heading dt x y
     *
     * @return A string representation.
     */
    public String serialize(Trajectory traj) {
        String content = "Position,Velocity,Acceleration,Jerk,Heading,DeltaTime,X,Y" + "\n";
        content += serializeTrajectory(traj);
        return content;
    }

    private String serializeTrajectory(Trajectory trajectory) {
        String content = "";
        for (int i = 0; i < trajectory.getNumSegments(); ++i) {
            Segment segment = trajectory.getSegment(i);
            content += String.format(
                    "%.3f, %.3f, %.3f, %.3f, %.3f, %.3f, %.3f, %.3f\n",
                    segment.pos, segment.vel, segment.acc, segment.jerk,
                    segment.heading, segment.dt, segment.x, segment.y);
        }
        return content;
    }

}
