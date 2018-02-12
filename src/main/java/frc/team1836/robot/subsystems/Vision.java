package frc.team1836.robot.subsystems;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team1836.robot.util.loops.Loop;
import frc.team1836.robot.util.loops.Looper;
import frc.team1836.robot.util.other.Subsystem;

public class Vision extends Subsystem {

    double targetOffsetAngle_Horizontal, targetOffsetAngle_Vertical, targetArea, targetSkew = 0;

    public static Vision getInstance() {
        return InstanceHolder.mInstance;
    }

    @Override
    public void outputToSmartDashboard() {
        SmartDashboard.putNumber("Horizantal Offset", targetOffsetAngle_Horizontal);
        SmartDashboard.putNumber("Vertical Offset", targetOffsetAngle_Vertical);
        SmartDashboard.putNumber("Target Area", targetArea);
        SmartDashboard.putNumber("Target Skew", targetSkew);
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
                synchronized (Vision.this) {

                }
            }

            /**
             * Updated from mEnabledLoop in Robot.java
             * @param timestamp Time in seconds since code start
             */
            @Override
            public void onLoop(double timestamp) {
                synchronized (Vision.this) {
                    readUpdate();
                }
            }

            @Override
            public void onStop(double timestamp) {
                stop();
            }
        };
        enabledLooper.register(mLoop);
    }

    private void readUpdate() {
        NetworkTable table = NetworkTable.getTable("limelight");
        targetOffsetAngle_Horizontal = table.getNumber("tx", 0);
        targetOffsetAngle_Vertical = table.getNumber("ty", 0);
        targetArea = table.getNumber("ta", 0);
        targetSkew = table.getNumber("ts", 0);
    }

    private static class InstanceHolder {

        private static final Vision mInstance = new Vision();
    }
}
