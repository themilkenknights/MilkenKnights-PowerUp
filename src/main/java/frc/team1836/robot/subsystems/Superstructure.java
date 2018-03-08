package frc.team1836.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team1836.robot.Constants;
import frc.team1836.robot.RobotState;
import frc.team1836.robot.util.drivers.MkLED;
import frc.team1836.robot.util.drivers.MkLED.LEDColors;
import frc.team1836.robot.util.loops.Loop;
import frc.team1836.robot.util.loops.Looper;
import frc.team1836.robot.util.other.Subsystem;

public class Superstructure extends Subsystem {
    private MkLED mkLED;
    //private PowerDistributionPanel powerDistributionPanel;
    private boolean hPSignal;
    private boolean turnOffLED;

    public Superstructure() {
        mkLED = new MkLED(Constants.SUPERSTRUCTURE.CANIFIER_ID);
      //  powerDistributionPanel = new PowerDistributionPanel(Constants.SUPERSTRUCTURE.PDP_ID);
        hPSignal = false;
        turnOffLED = false;
    }

    public static Superstructure getInstance() {
        return InstanceHolder.mInstance;
    }

    @Override
    public void outputToSmartDashboard() {
        SmartDashboard.putString("System State", RobotState.mSystemState.toString());
       // SmartDashboard.putNumber("Roller Current", powerDistributionPanel.getCurrent(ARM.ROLLER_INTAKE_PDP_PORT));
        updateLEDStrip();
    }

    @Override
    public void slowUpdate() {
    }

    public void toggleSignal() {
        hPSignal = !hPSignal;
    }

    public void toggleLEDOff() {
        turnOffLED = !turnOffLED;
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
                 //   CrashTracker.logMarker("Voltage: " + Double.toString(powerDistributionPanel.getVoltage()));
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
        mkLED.set_rgb(LEDColors.BLUE);
        /*if (turnOffLED) {
            return;
        }
        switch (RobotState.mMatchState) {
            case AUTO:
                mkLED.setPulse(MkLED.LEDColors.BLUE, MkLED.LEDColors.OFF, 1);
            case TELEOP:
                if (hPSignal) {
                    mkLED.set_rgb(MkLED.LEDColors.GREEN);
                } else if (RobotState.matchData.alliance == DriverStation.Alliance.Red) {
                    mkLED.setPulse(MkLED.LEDColors.BLUE, MkLED.LEDColors.OFF, 1);
                } else if (RobotState.matchData.alliance == DriverStation.Alliance.Blue) {
                    mkLED.setPulse(MkLED.LEDColors.RED, MkLED.LEDColors.OFF, 1);
                } else {
                    mkLED.setPulse(MkLED.LEDColors.PURPLE, MkLED.LEDColors.OFF, 1);
                }
            case DISABLED:
                if (RobotState.mSystemState == RobotState.SystemState.CONNECTED) {
                    mkLED.set_rgb(MkLED.LEDColors.PURPLE);
                } else if (RobotState.mSystemState == RobotState.SystemState.DISCONNECTED) {
                    mkLED.set_rgb(MkLED.LEDColors.WHITE);
                }
            case TEST:
                mkLED.setPulse(MkLED.LEDColors.ORANGE, MkLED.LEDColors.RED, 1);
        }
 */
    }


    private static class InstanceHolder {

        private static final Superstructure mInstance = new Superstructure();

    }
}
