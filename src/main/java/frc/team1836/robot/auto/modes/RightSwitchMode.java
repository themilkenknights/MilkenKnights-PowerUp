package frc.team1836.robot.auto.modes;

import frc.team1836.robot.AutoChooser;
import frc.team1836.robot.AutoChooser.GameObjectPosition;
import frc.team1836.robot.auto.actions.DrivePathAction;
import frc.team1836.robot.util.auto.AutoModeBase;
import frc.team1836.robot.util.auto.AutoModeEndedException;
import frc.team1836.robot.util.auto.DeserializePath;

import java.io.IOException;

public class RightSwitchMode extends AutoModeBase {

    private GameObjectPosition position;

    public RightSwitchMode(AutoChooser.GameObjectPosition position) {
        this.position = position;
    }

    @Override
    protected void routine() throws AutoModeEndedException {
        switch (position) {
            case LEFT:
                leftRoutine();
                break;
            case RIGHT:
                rightRoutine();
                break;
        }
    }

    private void leftRoutine() throws AutoModeEndedException {
        try {
            runAction(new DrivePathAction(DeserializePath.getPathFromFile("RightLeftSwitchPath")));
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }

    private void rightRoutine() throws AutoModeEndedException {
        try {
            runAction(new DrivePathAction(DeserializePath.getPathFromFile("RightRightSwitchPath")));
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }


}
