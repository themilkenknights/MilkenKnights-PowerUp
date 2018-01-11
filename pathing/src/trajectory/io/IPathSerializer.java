package trajectory.io;

import trajectory.Path;

/**
 * Interface for methods that serialize a Path or Trajectory.
 *
 * @author Jared341
 */
public interface IPathSerializer {

  public String serialize(Path path);
}
