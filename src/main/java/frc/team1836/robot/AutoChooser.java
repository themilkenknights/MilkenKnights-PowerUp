package frc.team1836.robot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team1836.robot.auto.modes.DriveStraightMode;
import frc.team1836.robot.util.auto.AutoModeBase;
import frc.team1836.robot.util.auto.AutoModeExecuter;

public class AutoChooser {


	private static SendableChooser<AutoPosition> positionChooser = new SendableChooser<>();
	private static SendableChooser<AutoAction> actionChooser = new SendableChooser<>();
	private static AutoModeExecuter mAutoModeExecuter = null;

	public static void loadChooser() {
		positionChooser.addDefault("Center", AutoPosition.CENTER);
		positionChooser.addObject("Left", AutoPosition.LEFT);
		positionChooser.addObject("Right", AutoPosition.RIGHT);
		SmartDashboard.putData("Auto Position Chooser", positionChooser);

		actionChooser.addDefault("Standstill", AutoAction.STANDSTILL);
		actionChooser.addObject("Drive Straight", AutoAction.DRIVE_STRAIGHT);
		actionChooser.addObject("Switch", AutoAction.SWITCH);
		actionChooser.addObject("Scale", AutoAction.SCALE);
		SmartDashboard.putData("Auto Action Chooser", actionChooser);
	}

	public static AutoModeBase getAutoMode() {
		return new DriveStraightMode();
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

	public enum AutoPosition {
		LEFT,
		CENTER,
		RIGHT
	}

	public enum AutoAction {

		STANDSTILL,
		DRIVE_STRAIGHT,
		SWITCH,
		SCALE

	}

	public static void disableAuto() {
		if (mAutoModeExecuter != null) {
			mAutoModeExecuter.stop();
		}
		mAutoModeExecuter = null;
	}

}
