package frc.team1836.robot.subsystems;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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

  public Superstructure() {
    mkLED = new MkLED(Constants.SUPERSTRUCTURE.CANIFIER_ID);
    hPSignal = false;
    turnOffLED = false;
    mLastPacketTime = 0.0;
    cameraServer = CameraServer.getInstance().startAutomaticCapture();
    cameraServer.setResolution(640, 480);
   // cameraServer.setExposureAuto();
  //  cameraServer.setWhiteBalanceAuto();
  }

  public static Superstructure getInstance() {
    return InstanceHolder.mInstance;
  }

  @Override
  public void outputToSmartDashboard() {
    SmartDashboard.putString("Robot State", RobotState.mMatchState.toString());
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
      mkLED.set_rgb(MkLED.LEDColors.PURPLE);
    } else if (RobotState.matchData.alliance == DriverStation.Alliance.Red) {
      mkLED.set_rgb(LEDColors.RED);
    } else {
      mkLED.set_rgb(LEDColors.BLUE);
    }
  }

  public synchronized void setLastPacketTime(double timestamp) {
    mLastPacketTime = timestamp;
  }

  private static class InstanceHolder {

    private static final Superstructure mInstance = new Superstructure();

  }
}
