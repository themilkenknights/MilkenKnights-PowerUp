package frc.team254.lib.trajectory.io;

import frc.team254.lib.trajectory.Path;

/**
 * Interface for methods that serialize a Path or Trajectory.
 *
 * @author Jared341
 */
public interface IPathSerializer {

	public String serialize(Path path);
}
