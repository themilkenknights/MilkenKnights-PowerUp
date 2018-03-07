package frc.team1836.robot.subsystems;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team1836.robot.Constants;
import frc.team1836.robot.RobotState;
import frc.team1836.robot.util.drivers.MkLED;
import frc.team1836.robot.util.logging.CrashTracker;
import frc.team1836.robot.util.loops.Loop;
import frc.team1836.robot.util.loops.Looper;
import frc.team1836.robot.util.other.Subsystem;

public class Superstructure extends Subsystem {
    private MkLED mkLED;
    private PowerDistributionPanel powerDistributionPanel;
    private boolean cubeCurrentLimit;
    private boolean hPSignal;

    public Superstructure() {
        mkLED = new MkLED(Constants.SUPERSTRUCTURE.CANIFIER_ID);
        powerDistributionPanel = new PowerDistributionPanel(Constants.SUPERSTRUCTURE.PDP_ID);
        cubeCurrentLimit = false;
        hPSignal = false;
    }

    public static Superstructure getInstance() {
        return InstanceHolder.mInstance;
    }

    @Override
    public void outputToSmartDashboard() {
        SmartDashboard.putString("System State", RobotState.mSystemState.toString());
        updateLEDStrip();
    }

    @Override
    public void slowUpdate() {
        if (powerDistributionPanel.getCurrent(Constants.ARM.ROLLER_INTAKE_PDP_PORT) > Constants.ARM.ROLLER_INTAKE_CURRENT_LIMIT) {
            cubeCurrentLimit = true;
            hPSignal = false;
        } else {
            cubeCurrentLimit = false;
        }
    }

    public void toggleSignal() {
        hPSignal = !hPSignal;
    }

    @Override
    public void checkSystem() {

    }

    @Override
    public void registerEnabledLoops(Looper enabledLooper) {
        Loop mLoop = new Loop() {

            @Override
            public void onStart(double timestamp) {
                synchronized (Superstructure.this) {
                    CrashTracker.logMarker("Voltage: " + Double.toString(powerDistributionPanel.getVoltage()));
                }
            }

            @Override
            public void onLoop(double timestamp) {
                synchronized (Superstructure.this) {
                    updateLEDStrip();
                    switch (RobotState.mSystemState) {
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onStop(double timestamp) {

            }
        };
        enabledLooper.register(mLoop);
    }

    private void updateLEDStrip() {

        switch (RobotState.mMatchState) {
            case AUTO:
                mkLED.setPulse(MkLED.LEDColors.BLUE, MkLED.LEDColors.OFF, 0.5);
            case TELEOP:
                if (hPSignal) {
                    mkLED.set_rgb(MkLED.LEDColors.GREEN);
                }
                if (cubeCurrentLimit) {
                    mkLED.setPulse(MkLED.LEDColors.ORANGE, MkLED.LEDColors.OFF, 0.25);
                } else if (RobotState.matchData.alliance == DriverStation.Alliance.Red) {
                    mkLED.setPulse(MkLED.LEDColors.BLUE, MkLED.LEDColors.OFF, 0.5);
                } else if (RobotState.matchData.alliance == DriverStation.Alliance.Blue) {
                    mkLED.setPulse(MkLED.LEDColors.RED, MkLED.LEDColors.OFF, 0.5);
                } else {
                    mkLED.setPulse(MkLED.LEDColors.PURPLE, MkLED.LEDColors.OFF, 0.5);
                }
            case DISABLED:
                if (RobotState.mSystemState == RobotState.SystemState.CONNECTED) {
                    mkLED.set_rgb(MkLED.LEDColors.PURPLE);
                } else if (RobotState.mSystemState == RobotState.SystemState.DISCONNECTED) {
                    mkLED.set_rgb(MkLED.LEDColors.WHITE);
                }
            case TEST:
                mkLED.setPulse(MkLED.LEDColors.ORANGE, MkLED.LEDColors.RED, 0.5);
        }

    }


    private static class InstanceHolder {

        private static final Superstructure mInstance = new Superstructure();

    }
}
