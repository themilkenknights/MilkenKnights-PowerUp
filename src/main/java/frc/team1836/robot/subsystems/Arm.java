package frc.team1836.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team1836.robot.Constants;
import frc.team1836.robot.Constants.ARM;
import frc.team1836.robot.RobotState;
import frc.team1836.robot.RobotState.ArmControlState;
import frc.team1836.robot.RobotState.ArmState;
import frc.team1836.robot.util.drivers.MkTalon;
import frc.team1836.robot.util.drivers.MkTalon.TalonPosition;
import frc.team1836.robot.util.logging.Log;
import frc.team1836.robot.util.logging.ReflectingCSVWriter;
import frc.team1836.robot.util.structure.loops.Loop;
import frc.team1836.robot.util.structure.loops.Looper;
import frc.team1836.robot.util.math.MkMath;
import frc.team1836.robot.util.structure.Subsystem;

public class Arm extends Subsystem {

  private final ReflectingCSVWriter<ArmDebugOutput> mCSVWriter;
  private final MkTalon armTalon;
  private final VictorSPX leftIntakeRollerTalon;
  private final VictorSPX rightIntakeRollerTalon;
  private ArmDebugOutput mDebug = new ArmDebugOutput();
  private boolean armSafety = true;
  private double armPosEnable = 0;
  private double rollerSetpoint = 0;

  private Arm() {
    mCSVWriter = new ReflectingCSVWriter<>(Constants.LOGGING.ARM_LOG_PATH, ArmDebugOutput.class);
    armTalon = new MkTalon(ARM.ARM_MASTER_TALON_ID, ARM.ARM_SLAVE_TALON_ID, TalonPosition.Arm);
    armTalon.setSensorPhase(ARM.ARM_SENSOR_PHASE);
    armTalon.configMotionMagic();
    armTalon.setSoftLimit(ARM.ARM_FORWARD_LIMIT, ARM.ARM_REVERSE_LIMIT);
    armTalon.setLimitEnabled(true);
    leftIntakeRollerTalon = new VictorSPX(Constants.ARM.LEFT_INTAKE_ROLLER_ID);
    rightIntakeRollerTalon = new VictorSPX(Constants.ARM.RIGHT_INTAKE_ROLLER_ID);
    leftIntakeRollerTalon.setNeutralMode(NeutralMode.Brake);
    rightIntakeRollerTalon.setNeutralMode(NeutralMode.Brake);
    armTalon.invertMaster(ARM.ARM_MASTER_DIRECTION);
    armTalon.invertSlave(ARM.ARM_SLAVE_DIRECTION);
    armTalon.zeroAbsolute();
    leftIntakeRollerTalon.setInverted(ARM.LEFT_INTAKE_DIRECTION);
    rightIntakeRollerTalon.setInverted(ARM.RIGHT_INTAKE_DIRECTION);
  }

  public static Arm getInstance() {
    return InstanceHolder.mInstance;
  }

  @Override
  public void outputToSmartDashboard() {
    armTalon.updateSmartDash();
    SmartDashboard.putNumber("Arm Current", armTalon.getCurrentOutput());
    SmartDashboard.putString("Arm Desired Position", RobotState.mArmState.toString());
    SmartDashboard.putString("Arm Control Mode", RobotState.mArmControlState.toString());
    SmartDashboard.putBoolean("Arm Status", armTalon.isEncoderConnected());
    SmartDashboard.putNumber("Roller Output", leftIntakeRollerTalon.getMotorOutputPercent());
    SmartDashboard.putNumber("Arm Absolute Position", armTalon.getAbsolutePosition());
  }

  @Override
  public void slowUpdate(double timestamp) {
    if (RobotState.mMatchState != RobotState.MatchState.DISABLED) {
      updateDebugOutput(timestamp);
      mCSVWriter.add(mDebug);
      mCSVWriter.write();
    }
  }

  @Override
  public void checkSystem() {
    if (!armTalon.isEncoderConnected()) {
      Log.marker("Arm Encoder Not Connected");
    }
    if (RobotState.mArmControlState == ArmControlState.MOTION_MAGIC) {
      for (ArmState state : ArmState.values()) {
        if (state != ArmState.ENABLE) {
          RobotState.mArmState = state;
          setIntakeRollers(-0.25);
          Timer.delay(2);
        }
      }
      armTalon.resetConfig();
    } else {
      Log.marker("Arm Test Failed");
    }
  }

  @Override
  public void registerEnabledLoops(Looper enabledLooper) {
    Loop mLoop = new Loop() {

      @Override
      public void onStart(double timestamp) {
        synchronized (Arm.this) {
          armPosEnable = armTalon.getPosition();
          RobotState.mArmState = ArmState.ENABLE;
        }
      }

      /**
       * Updated from mEnabledLoop in Robot.java
       * @param timestamp Time in seconds since code start
       */
      @Override
      public void onLoop(double timestamp) {
        synchronized (Arm.this) {
          armSafetyCheck();
          updateRollers();
          switch (RobotState.mArmControlState) {
            case MOTION_MAGIC:
              updateArmSetpoint();
              return;
            case OPEN_LOOP:
              return;
            default:
              Log.marker("Unexpected arm control state: " + RobotState.mArmControlState);
              break;
          }
        }
      }

      @Override
      public void onStop(double timestamp) {
        setIntakeRollers(0);
        mCSVWriter.flush();
      }
    };
    enabledLooper.register(mLoop);
  }

  public void setEnable() {
    armPosEnable = armTalon.getPosition();
    RobotState.mArmState = ArmState.ENABLE;
  }

  public void changeSafety() {
    armSafety = !armSafety;
    armTalon.setLimitEnabled(armSafety);
  }

  private void updateDebugOutput(double timestamp) {
    mDebug.controlMode = RobotState.mArmControlState.toString();
    mDebug.output = armTalon.getPercentOutput();
    mDebug.position = armTalon.getPosition();
    mDebug.velocity = armTalon.getSpeed();
    mDebug.setpoint = RobotState.mArmState.state;
    mDebug.timestamp = timestamp;
    mDebug.current = armTalon.getCurrentOutput();
  }

  public void zeroRel() {
    armTalon.resetEncoder();
    armPosEnable = armTalon.getPosition();
  }

  private void updateArmSetpoint() {
    double armFeed = MkMath.sin(armTalon.getPosition() + ARM.ARM_OFFSET) * ARM.FEED_CONSTANT;
    if (RobotState.mArmState.equals(ArmState.ENABLE)) {
      armTalon.set(ControlMode.MotionMagic, MkMath.angleToNativeUnits(armPosEnable), true);
    } else {
      armTalon.set(ControlMode.MotionMagic, MkMath.angleToNativeUnits(RobotState.mArmState.state), true, -armFeed);
    }
  }

  private void armSafetyCheck() {
    if (!armTalon.isEncoderConnected()) {
      Log.marker("Arm Encoder Not Connected");
      RobotState.mArmControlState = ArmControlState.OPEN_LOOP;
    }
    if (armTalon.getAbsolutePosition() > Constants.CODES_PER_REV) {
      Log.marker("Arm Absolution Position > 4096");
      RobotState.mArmControlState = ArmControlState.OPEN_LOOP;
    }
    if (armTalon.getCurrentOutput() > ARM.MAX_SAFE_CURRENT && armSafety) {
      Log.marker("Unsafe Current " + armTalon.getCurrentOutput() + " Amps");
      RobotState.mArmControlState = ArmControlState.OPEN_LOOP;
    }
  }

  public void updateRollers() {
    leftIntakeRollerTalon.set(ControlMode.PercentOutput, rollerSetpoint);
    rightIntakeRollerTalon.set(ControlMode.PercentOutput, rollerSetpoint);
  }

  public void setOpenLoop(double output) {
    armTalon.set(ControlMode.PercentOutput, output, true);
  }

  public void setIntakeRollers(double output) {
    rollerSetpoint = output;
  }

  public static class ArmDebugOutput {

    public double timestamp;
    public String controlMode;
    public double output;
    public double position;
    public double velocity;
    public double setpoint;
    public double current;
  }

  private static class InstanceHolder {

    private static final Arm mInstance = new Arm();
  }
}
