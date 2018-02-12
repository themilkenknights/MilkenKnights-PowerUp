package frc.team254.lib.trajectory.io;

import frc.team254.lib.trajectory.Trajectory;

/**
 * Interface for methods that serialize a Path or Trajectory.
 *
 * @author Jared341
 */
public interface IPathSerializer1 {

    public String serialize(Trajectory traj);
}
