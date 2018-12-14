package frc.team1836.robot.subsystems;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.team1836.robot.Constants;
import frc.team1836.robot.Constants.SUPERSTRUCTURE;
import frc.team1836.robot.RobotState;
import frc.team1836.robot.RobotState.ArmControlState;
import frc.team1836.robot.RobotState.DriveControlState;
import frc.team1836.robot.RobotState.MatchState;
import frc.team1836.robot.util.drivers.MkLED;
import frc.team1836.robot.util.drivers.MkLED.LEDColors;
import frc.team1836.robot.util.structure.Subsystem;
import frc.team1836.robot.util.structure.loops.Loop;
import frc.team1836.robot.util.structure.loops.Looper;

public class Superstructure extends Subsystem {

    private MkLED mkLED;
    private boolean hPSignal;
    private boolean turnOffLED;
    private double mLastPacketTime;
    private UsbCamera cameraServer;
    private float _hue;
    private PowerDistributionPanel pdp;
    double x,y, area, rate = 0;

    public Superstructure() {
        mkLED = new MkLED(Constants.SUPERSTRUCTURE.CANIFIER_ID);
        pdp = new PowerDistributionPanel();
        hPSignal = false;
        turnOffLED = false;
        mLastPacketTime = 0.0;
        _hue = 0;
     //   cameraServer = CameraServer.getInstance().startAutomaticCapture();
       // cameraServer.setResolution(640, 480);
    }

    public static Superstructure getInstance() {
        return InstanceHolder.mInstance;
    }

    @Override
    public void outputToSmartDashboard() {
        SmartDashboard.putString("Robot State", RobotState.mMatchState.toString());
     /*   SmartDashboard.putNumber("Total Current Output", pdp.getTotalCurrent());
        SmartDashboard.putNumber("X", x);
        SmartDashboard.putNumber("Y", y);
        SmartDashboard.putNumber("Latency", rate);
        SmartDashboard.putNumber("Area", area);*/


    }

    @Override
    public void slowUpdate(double timestamp) {
        Superstructure.getInstance().setLastPacketTime(timestamp);
        Superstructure.getInstance().updateLEDStrip(timestamp);
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
                    mLastPacketTime = timestamp;
                }
            }

            @Override
            public void onLoop(double timestamp) {
                synchronized (Superstructure.this) {
                }
            }

            @Override
            public void onStop(double timestamp) {

            }
        };
        enabledLooper.register(mLoop);
    }

    public void toggleSignal() {
        hPSignal = !hPSignal;
    }

    public void toggleLEDOff() {
        turnOffLED = !turnOffLED;
    }

    private synchronized void updateLEDStrip(double timestamp) {
        if (turnOffLED) {
            mkLED.set_rgb(LEDColors.OFF);
        } else if (timestamp - mLastPacketTime > SUPERSTRUCTURE.CONNECTION_TIMEOUT
                || RobotState.mArmControlState == ArmControlState.OPEN_LOOP) {
            mkLED.setPulse(LEDColors.RED, LEDColors.OFF, 0.25);
        } else if (hPSignal) {
            mkLED.set_rgb(LEDColors.GREEN);
        } else if (RobotState.mDriveControlState == DriveControlState.VELOCITY_SETPOINT
                && RobotState.mMatchState != MatchState.AUTO) {
            mkLED.setPulse(LEDColors.PURPLE, LEDColors.OFF, 0.25);
        } else if (RobotState.mMatchState == MatchState.DISABLED) {
            _hue += 0.75;
            if (_hue > 360) {
                _hue = 0;
            }
            mkLED.setHSV(_hue, 1.0f, 0.1f);
        } else if (RobotState.matchData.alliance == DriverStation.Alliance.Red) {
            mkLED.set_rgb(LEDColors.RED);
        } else {
            mkLED.set_rgb(LEDColors.BLUE);
        }
    }

    public synchronized void updateLimelight(){
     /*   NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
        NetworkTableEntry tx = table.getEntry("tx");
        NetworkTableEntry ty = table.getEntry("ty");
        NetworkTableEntry ta = table.getEntry("ta");
        NetworkTableEntry tl = table.getEntry("tl");
        x = tx.getDouble(0);
        y = ty.getDouble(0);
        area = ta.getDouble(0);
        rate = tl.getDouble(0); */
    }

    public synchronized void setLastPacketTime(double timestamp) {
        mLastPacketTime = timestamp;
    }

    private static class InstanceHolder {

        private static final Superstructure mInstance = new Superstructure();

    }
}
