package frc.team1836.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import frc.team1836.robot.RobotState.MatchState;
import frc.team1836.robot.subsystems.Arm;
import frc.team1836.robot.subsystems.Drive;
import frc.team1836.robot.subsystems.Input;
import frc.team1836.robot.subsystems.Superstructure;
import frc.team1836.robot.util.logging.Log;
import frc.team1836.robot.util.structure.SubsystemManager;
import frc.team1836.robot.util.structure.loops.Looper;

import java.util.Arrays;

public class Robot extends IterativeRobot {

    private final SubsystemManager mSubsystemManager = new SubsystemManager(Arrays
            .asList(Drive.getInstance(), Arm.getInstance(), Superstructure.getInstance(),
                    Input.getInstance()));
    private Looper mEnabledLooper = new Looper();

    public Robot() {
        Log.logRobotStartup();
    }

    @Override
    public void robotInit() {
        try {
            Log.logRobotInit();
            mSubsystemManager.registerEnabledLoops(mEnabledLooper);
            AutoChooser.loadAutos();
        } catch (Throwable t) {
            Log.logThrowableCrash(t);
            throw t;
        }

    }

    @Override
    public void disabledInit() {
        try {
            Log.logDisabledInit();
            mEnabledLooper.stop();
            AutoChooser.disableAuto();
            RobotState.resetDefaultState();
        } catch (Throwable t) {
            Log.logThrowableCrash(t);
            throw t;
        }
    }

    @Override
    public void autonomousInit() {
        try {
            Log.logAutoInit();
            AutoChooser.startAuto();
            mEnabledLooper.start();
        } catch (Throwable t) {
            Log.logThrowableCrash(t);
            throw t;
        }
    }

    @Override
    public void teleopInit() {
        try {
            Log.logTeleopInit();
            mSubsystemManager.setTimeOffset();
            mEnabledLooper.start();
        } catch (Throwable t) {
            Log.logThrowableCrash(t);
            throw t;
        }
    }

    @Override
    public void testInit() {
        try {
            Log.logTestInit();
            mEnabledLooper.start();
            mSubsystemManager.checkSystem();
        } catch (Throwable t) {
            Log.logThrowableCrash(t);
            throw t;
        }
    }

    public void robotPeriodic() {
        try {
            mSubsystemManager.slowUpdate();
            mSubsystemManager.outputToSmartDashboard();
            mEnabledLooper.outputToSmartDashboard();
        } catch (Throwable t) {
            Log.logThrowableCrash(t);
            throw t;
        }
    }
}
