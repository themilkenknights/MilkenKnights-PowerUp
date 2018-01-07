package frc.team1836.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import frc.team1836.robot.auto.modes.DriveStraightMode;
import frc.team1836.robot.auto.modes.StandStillMode;
import frc.team1836.robot.subsystems.Drive;
import frc.team1836.robot.subsystems.Superstructure;
import frc.team1836.robot.util.auto.AutoModeBase;
import frc.team1836.robot.util.auto.AutoModeExecuter;
import frc.team1836.robot.util.logging.CrashTracker;
import frc.team1836.robot.util.loops.Looper;
import frc.team1836.robot.util.other.SubsystemManager;
import frc.team1836.robot.util.state.DriveSignal;
import java.util.Arrays;

public class Robot extends IterativeRobot {

	private AutoModeExecuter mAutoModeExecuter = null;
	private SendableChooser<AutoModeBase> positionChooser = new SendableChooser<>();
	private final SubsystemManager mSubsystemManager = new SubsystemManager(
			Arrays.asList(Drive.getInstance(), Superstructure.getInstance()));
	private Looper mEnabledLooper = new Looper();

	public Robot() {
		CrashTracker.logRobotConstruction();
	}

	@Override
	public void robotInit() {
		try {
			CrashTracker.logRobotInit();
			mSubsystemManager.registerEnabledLoops(mEnabledLooper);
			mSubsystemManager.zeroSensors();
			positionChooser.addObject("Drive Straight", new DriveStraightMode());
			positionChooser.addObject("Stand Still", new StandStillMode());
		} catch (Throwable t) {
			CrashTracker.logThrowableCrash(t);
			throw t;
		}

	}

	@Override
	public void disabledInit() {
		try {
			CrashTracker.logDisabledInit();
			if (mAutoModeExecuter != null) {
				mAutoModeExecuter.stop();
			}
			mAutoModeExecuter = null;
			mEnabledLooper.stop();
			mSubsystemManager.stop();
			Drive.getInstance().setOpenLoop(DriveSignal.NEUTRAL);
		} catch (Throwable t) {
			CrashTracker.logThrowableCrash(t);
			throw t;
		}
	}

	@Override
	public void autonomousInit() {
		try {
			CrashTracker.logAutoInit();

			System.out.println("Auto start timestamp: " + Timer.getFPGATimestamp());

			if (mAutoModeExecuter != null) {
				mAutoModeExecuter.stop();
			}

			mSubsystemManager.zeroSensors();
			mAutoModeExecuter = null;
			mEnabledLooper.start();
			mAutoModeExecuter = new AutoModeExecuter();
			mAutoModeExecuter.setAutoMode(positionChooser.getSelected());
			mAutoModeExecuter.start();

		} catch (Throwable t) {
			CrashTracker.logThrowableCrash(t);
			throw t;
		}
	}

	@Override
	public void teleopInit() {
		try {
			CrashTracker.logTeleopInit();
			mEnabledLooper.start();
		} catch (Throwable t) {
			CrashTracker.logThrowableCrash(t);
			throw t;
		}
	}

	@Override
	public void testInit() {
	}


	@Override
	public void disabledPeriodic() {
		allPeriodic();
	}

	@Override
	public void autonomousPeriodic() {
		allPeriodic();
	}

	@Override
	public void teleopPeriodic() {
		allPeriodic();
	}

	@Override
	public void testPeriodic() {
	}


	public void allPeriodic() {
		try {
			mSubsystemManager.outputToSmartDashboard();
			mSubsystemManager.writeToLog();
			mEnabledLooper.outputToSmartDashboard();
		} catch (Throwable t) {
			CrashTracker.logThrowableCrash(t);
			throw t;
		}
	}
}
