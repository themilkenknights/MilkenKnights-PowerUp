package frc.team1836.robot;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import frc.team1836.robot.RobotState.ArmControlState;
import frc.team1836.robot.RobotState.DriveControlState;
import frc.team1836.robot.RobotState.MatchState;
import frc.team1836.robot.subsystems.Arm;
import frc.team1836.robot.subsystems.Drive;
import frc.team1836.robot.subsystems.Input;
import frc.team1836.robot.subsystems.Superstructure;
import frc.team1836.robot.util.logging.CrashTracker;
import frc.team1836.robot.util.loops.Looper;
import frc.team1836.robot.util.other.SubsystemManager;

import java.util.Arrays;

public class Robot extends IterativeRobot {

    private final SubsystemManager mSubsystemManager = new SubsystemManager(
            Arrays.asList(Drive.getInstance(), Arm.getInstance(), Superstructure.getInstance(), Input.getInstance()));
    private Looper mEnabledLooper = new Looper();

    public Robot() {
        CrashTracker.logRobotConstruction();
    }

    @Override
    public void robotInit() {
        try {
            CrashTracker.logRobotInit();
            mSubsystemManager.registerEnabledLoops(mEnabledLooper);
            AutoChooser.loadAutos();
            CameraServer.getInstance().startAutomaticCapture().setResolution(640, 480);
        } catch (Throwable t) {
            CrashTracker.logThrowableCrash(t);
            throw t;
        }

    }

    @Override
    public void disabledInit() {
        try {
            CrashTracker.logDisabledInit();
            AutoChooser.disableAuto();
            mEnabledLooper.stop();
            RobotState.mMatchState = MatchState.DISABLED;
            RobotState.mArmState = RobotState.ArmState.ENABLE;
        } catch (Throwable t) {
            CrashTracker.logThrowableCrash(t);
            throw t;
        }
    }

    @Override
    public void autonomousInit() {
        try {
            AutoChooser.updateGameData();
            CrashTracker.logAutoInit();
            RobotState.mMatchState = MatchState.AUTO;
            mEnabledLooper.start();
            AutoChooser.startAuto();
        } catch (Throwable t) {
            CrashTracker.logThrowableCrash(t);
            throw t;
        }
    }

    @Override
    public void teleopInit() {
        try {
            CrashTracker.logTeleopInit();
            RobotState.mMatchState = MatchState.TELEOP;
            RobotState.mDriveControlState = DriveControlState.OPEN_LOOP;
            RobotState.mArmControlState = ArmControlState.MOTION_MAGIC;
            mEnabledLooper.start();
        } catch (Throwable t) {
            CrashTracker.logThrowableCrash(t);
            throw t;
        }
    }

    @Override
    public void testInit() {
        try {
            CrashTracker.logTestInit();
            mEnabledLooper.start();
            mSubsystemManager.checkSystem();
        } catch (Throwable t) {
            CrashTracker.logThrowableCrash(t);
            throw t;
        }
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
        mSubsystemManager.slowUpdate();
    }

    @Override
    public void testPeriodic() {

    }

    private void allPeriodic() {
        try {
            mSubsystemManager.outputToSmartDashboard();
            mEnabledLooper.outputToSmartDashboard();
            Superstructure.getInstance().setLastPacketTime(Timer.getFPGATimestamp());
            Superstructure.getInstance().updateLEDStrip(Timer.getFPGATimestamp());
        } catch (Throwable t) {
            CrashTracker.logThrowableCrash(t);
            throw t;
        }
    }
}
