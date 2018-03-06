package frc.team1836.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team1836.robot.RobotState;
import frc.team1836.robot.util.loops.Loop;
import frc.team1836.robot.util.loops.Looper;
import frc.team1836.robot.util.other.Subsystem;

public class Superstructure extends Subsystem {
    //private MkLED mkLED;

    public Superstructure() {
        //mkLED = new MkLED(Constants.SUPERSTRUCTURE.CANIFIER_ID);
    }

    public static Superstructure getInstance() {
        return InstanceHolder.mInstance;
    }

    @Override
    public void writeToLog() {

    }

    @Override
    public void outputToSmartDashboard() {
        SmartDashboard.putString("System State", RobotState.mSystemState.toString());
        //  updateLEDStrip();
    }

    @Override
    public void stop() {

    }

    @Override
    public void zeroSensors() {

    }

    @Override
    public void updateLogger() {

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
                }
            }

            @Override
            public void onLoop(double timestamp) {
                synchronized (Superstructure.this) {
                    updateLEDStrip();
                    switch (RobotState.mSystemState) {
                        case IDLE:
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onStop(double timestamp) {
                stop();
            }
        };
        enabledLooper.register(mLoop);
    }

    private void updateLEDStrip() {

        switch (RobotState.mMatchState) {
            case AUTO:
                //     mkLED.setPulse(MkLED.LEDColors.BLUE, MkLED.LEDColors.OFF, 0.5);
            case TELEOP:
                //   mkLED.setPulse(MkLED.LEDColors.GREEN, MkLED.LEDColors.RED, 0.5);
            case DISABLED:
                // mkLED.set_rgb(MkLED.LEDColors.RED);
            case TEST:
                //mkLED.setPulse(MkLED.LEDColors.ORANGE, MkLED.LEDColors.RED, 0.5);
        }

    }


    private static class InstanceHolder {

        private static final Superstructure mInstance = new Superstructure();

    }
}
