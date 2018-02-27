package frc.team1836.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team1836.robot.Constants;
import frc.team1836.robot.RobotState;
import frc.team1836.robot.util.drivers.MkLED;
import frc.team1836.robot.util.loops.Loop;
import frc.team1836.robot.util.loops.Looper;
import frc.team1836.robot.util.other.Subsystem;

public class Superstructure extends Subsystem {
    private float _hue;
    private MkLED mkLED;

    public Superstructure() {
      mkLED = new MkLED(Constants.CANIFIER_ID);
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
        updateLEDStrip();
    }

    @Override
    public void stop() {

    }

    @Override
    public void zeroSensors() {

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
                			mkLED.rgbSet(0, 66, 255);
            case TELEOP:
				float[] pulse1 = {4,255,0};
				float[] pulse2 = {0,251,255};
				mkLED.setPulse(pulse1, pulse2);
            case DISABLED:
               mkLED.rgbSet(52, 52, 92);
        }

    }

    public void setLED(float[] color) {
        //mkLED.set_rgb(color[0], color[1], color[2]);
    }


    private static class InstanceHolder {

        private static final Superstructure mInstance = new Superstructure();

    }
}
