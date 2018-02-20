package frc.team1836.robot.auto.modes;

import frc.team1836.robot.auto.actions.DrivePathAction;
import frc.team1836.robot.util.auto.AutoModeBase;
import frc.team1836.robot.util.auto.AutoModeEndedException;
import frc.team1836.robot.util.auto.DeserializePath;

import java.io.IOException;

public class DriveStraightMode extends AutoModeBase {

    @Override
    protected void routine() throws AutoModeEndedException {
        try {
            runAction(new DrivePathAction(DeserializePath.getPathFromFile("StraightPath")));
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }
}
