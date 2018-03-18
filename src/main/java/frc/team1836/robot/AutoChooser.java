package frc.team1836.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team1836.robot.auto.modes.CenterSwitchMode;
import frc.team1836.robot.auto.modes.CenterSwitchOpenLoopGyro;
import frc.team1836.robot.auto.modes.DriveStraightMode;
import frc.team1836.robot.auto.modes.DriveStraightOpenLoopMode;
import frc.team1836.robot.auto.modes.LeftSwitchMode;
import frc.team1836.robot.auto.modes.RightSwitchMode;
import frc.team1836.robot.auto.modes.StandStillMode;
import frc.team1836.robot.auto.modes.SwitchOpenLoop;
import frc.team1836.robot.auto.trajectory.Path;
import frc.team1836.robot.subsystems.Drive;
import frc.team1836.robot.util.auto.AutoModeBase;
import frc.team1836.robot.util.auto.AutoModeExecuter;
import frc.team1836.robot.util.auto.DeserializePath;
import frc.team1836.robot.util.logging.Log;
import java.util.HashMap;
import java.util.Map;

public class AutoChooser {

  public static final Map<String, Path> autoPaths = new HashMap<>();
  private static SendableChooser<AutoPosition> positionChooser = new SendableChooser<>();
  private static SendableChooser<AutoAction> actionChooser = new SendableChooser<>();
  private static AutoModeExecuter mAutoModeExecuter = null;

  public static void loadAutos() {
    positionChooser.addDefault("Center", AutoPosition.CENTER);
    positionChooser.addObject("Left", AutoPosition.LEFT);
    positionChooser.addObject("Right", AutoPosition.RIGHT);
    SmartDashboard.putData("Auto Position Chooser", positionChooser);
    actionChooser.addDefault("Switch", AutoAction.SWITCH);
    actionChooser.addDefault("Switch (Don't Run Across)", AutoAction.SWITCH);
    actionChooser.addObject("Standstill", AutoAction.STANDSTILL);
    actionChooser.addObject("Drive Straight", AutoAction.DRIVE_STRAIGHT);
    SmartDashboard.putData("Auto Action Chooser", actionChooser);
    SmartDashboard.putNumber("Auto Delay", 0.0);
    for (String pathName : Constants.AUTO.autoNames) {
      autoPaths.put(pathName, DeserializePath.getPathFromFile(pathName));
    }
  }

  public static AutoModeBase getAutoMode() {
    double delay = SmartDashboard.getNumber("Auto Delay", 0.0);
    if (delay > 0) {
      Timer.delay(delay);
    }
    Log.verbose(
        "Auto Mode Start: Position - " + positionChooser.getSelected().toString() + " Action - "
            + actionChooser.getSelected().toString());
    switch (actionChooser.getSelected()) {
      case STANDSTILL:
        return new StandStillMode();
      case DRIVE_STRAIGHT:
        return getStraightMode();
      case SWITCH:
        return getSwitchMode();
      default:
        Log.marker("Unexpected Auto Mode: " + actionChooser.getSelected().toString() + " + "
            + positionChooser.getSelected().toString());
        break;
    }
    return null;
  }

  private static AutoModeBase getStraightMode() {
    if (Drive.getInstance().isEncodersConnected() && Drive.getInstance().gyroConnected()) {
      return new DriveStraightMode();
    } else {
      return new DriveStraightOpenLoopMode();
    }
  }

  private static AutoModeBase getSwitchMode() {
    if (positionChooser.getSelected() == AutoPosition.CENTER) {
      return getCenterSwitch();
    } else if (positionChooser.getSelected() == AutoPosition.LEFT) {
      return getLeftSwitch();
    } else if (positionChooser.getSelected() == AutoPosition.RIGHT) {
      return getRightSwitch();
    }
    return null;
  }

  private static AutoModeBase getCenterSwitch() {
    if (Drive.getInstance().isEncodersConnected() && Drive.getInstance().gyroConnected()) {
      return new CenterSwitchMode(RobotState.matchData.switchPosition);
    } else if (Drive.getInstance().gyroConnected()) {
      return new CenterSwitchOpenLoopGyro(RobotState.matchData.switchPosition);
    } else {
      return new DriveStraightMode();
    }
  }

  private static AutoModeBase getRightSwitch() {
    if (Drive.getInstance().isEncodersConnected() && Drive.getInstance().gyroConnected()) {
      return new RightSwitchMode(RobotState.matchData.switchPosition);
    } else if (RobotState.matchData.switchPosition == GameObjectPosition.RIGHT) {
      return new SwitchOpenLoop();
    } else {
      return getStraightMode();
    }
  }

  private static AutoModeBase getLeftSwitch() {
    if (Drive.getInstance().isEncodersConnected() && Drive.getInstance().gyroConnected()) {
      return new LeftSwitchMode(RobotState.matchData.switchPosition);
    } else if (RobotState.matchData.switchPosition == GameObjectPosition.LEFT) {
      return new SwitchOpenLoop();
    } else {
      return getStraightMode();
    }
  }

  public static void startAuto() {
    if (mAutoModeExecuter != null) {
      mAutoModeExecuter.stop();
    }
    mAutoModeExecuter = null;
    mAutoModeExecuter = new AutoModeExecuter();
    mAutoModeExecuter.setAutoMode(getAutoMode());
    mAutoModeExecuter.start();
  }

  public static void disableAuto() {
    if (mAutoModeExecuter != null) {
      mAutoModeExecuter.stop();
    }
    mAutoModeExecuter = null;
  }

  public static void updateGameData() {
    RobotState.matchData.alliance = DriverStation.getInstance().getAlliance();
    RobotState.matchData.matchNumber = DriverStation.getInstance().getMatchNumber();
    RobotState.matchData.matchType = DriverStation.getInstance().getMatchType();
    String gameData = DriverStation.getInstance().getGameSpecificMessage();
    RobotState.matchData.switchPosition =
        gameData.charAt(0) == 'L' ? GameObjectPosition.LEFT : GameObjectPosition.RIGHT;
    RobotState.matchData.scalePosition =
        gameData.charAt(1) == 'L' ? GameObjectPosition.LEFT : GameObjectPosition.RIGHT;
    Log.verbose("Alliance: " + RobotState.matchData.alliance.toString() + " Match Number: "
        + RobotState.matchData.matchNumber + " Match Type: " + RobotState.matchData.matchType
        .toString() + " " +
        "Switch Position: " + RobotState.matchData.switchPosition.toString() + " Scale Position: "
        + RobotState.matchData.scalePosition.toString());
  }

  public enum AutoPosition {
    LEFT, CENTER, RIGHT
  }

  public enum GameObjectPosition {
    LEFT, RIGHT, INVALID
  }

  public enum AutoAction {
    STANDSTILL, DRIVE_STRAIGHT, SWITCH
  }

}
