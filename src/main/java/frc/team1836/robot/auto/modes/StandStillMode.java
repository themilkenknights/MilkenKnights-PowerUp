package frc.team1836.robot.auto.modes;

import frc.team1836.robot.util.auto.AutoModeBase;
import frc.team1836.robot.util.auto.AutoModeEndedException;
import frc.team1836.robot.util.logging.Log;

/**
 * Fallback for when all autonomous modes do not work, resulting in a robot standstill
 */
public class StandStillMode extends AutoModeBase {

    @Override
    protected void routine() throws AutoModeEndedException {
        Log.marker("Starting Stand Still Mode... Done!");
    }
}
