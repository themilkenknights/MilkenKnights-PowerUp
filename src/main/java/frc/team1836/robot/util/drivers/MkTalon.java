package frc.team1836.robot.util.drivers;

import com.ctre.phoenix.motorcontrol.ControlFrame;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team1836.robot.Constants;
import frc.team1836.robot.Constants.ARM;
import frc.team1836.robot.Constants.DRIVE;
import frc.team1836.robot.util.math.MkMath;
import org.opencv.core.Mat;

public class MkTalon {

  private final TalonSRX masterTalon;
  private final VictorSPX slaveTalon;
  private TalonPosition side;
  private double maxRPM = 0;
  private NeutralMode talonMode;

  /**
   * @param master Talon with Encoder CAN ID
   * @param slave Follower Talon CAN ID
   */
  public MkTalon(int master, int slave, TalonPosition side) {
    masterTalon = new TalonSRX(master);
    slaveTalon = new VictorSPX(slave);

    this.side = side;

    resetConfig();
    configMotionMagic();
    talonMode = NeutralMode.Brake;
  }

  public void setPIDF() {
    if (side.equals(TalonPosition.Left)) {
      masterTalon.config_kF(Constants.kPIDLoopIdx, DRIVE.LEFT_DRIVE_F, Constants.kTimeoutMs);
    } else if (side.equals(TalonPosition.Right)) {
      masterTalon.config_kF(Constants.kPIDLoopIdx, DRIVE.RIGHT_DRIVE_F, Constants.kTimeoutMs);
    }

    masterTalon.config_kP(Constants.kPIDLoopIdx, DRIVE.DRIVE_P, Constants.kTimeoutMs);
    masterTalon.config_kI(Constants.kPIDLoopIdx, DRIVE.DRIVE_I, Constants.kTimeoutMs);
    masterTalon.config_kD(Constants.kPIDLoopIdx, DRIVE.DRIVE_D, Constants.kTimeoutMs);
  }

  public void setSoftLimit(double forwardLimit, double reverseLimit) {
    masterTalon.configForwardSoftLimitThreshold((int) MkMath.angleToNativeUnits(forwardLimit),
        Constants.kTimeoutMs);
    masterTalon.configReverseSoftLimitThreshold((int) MkMath.angleToNativeUnits(reverseLimit),
        Constants.kTimeoutMs);
  }

  public void setLimitEnabled(boolean enabled) {
    masterTalon.configForwardSoftLimitEnable(enabled, Constants.kTimeoutMs);
    masterTalon.configReverseSoftLimitEnable(enabled, Constants.kTimeoutMs);
  }

  public void configMotionMagic() {
    if (side == TalonPosition.Arm) {
      masterTalon.config_kF(Constants.kPIDLoopIdx, ARM.ARM_F, Constants.kTimeoutMs);
      masterTalon.config_kP(Constants.kPIDLoopIdx, ARM.ARM_P, Constants.kTimeoutMs);
      masterTalon.config_kI(Constants.kPIDLoopIdx, ARM.ARM_I, Constants.kTimeoutMs);
      masterTalon.config_kD(Constants.kPIDLoopIdx, ARM.ARM_D, Constants.kTimeoutMs);
      masterTalon
          .configMotionCruiseVelocity((int) ARM.MOTION_MAGIC_CRUISE_VEL, Constants.kTimeoutMs);
      masterTalon.configMotionAcceleration((int) ARM.MOTION_MAGIC_ACCEL, Constants.kTimeoutMs);
      zeroAbsolute();
    } else {
      if (side == TalonPosition.Left) {
        masterTalon.config_kF(Constants.kPIDLoopIdx, DRIVE.LEFT_DRIVE_F, Constants.kTimeoutMs);
      } else {
        masterTalon.config_kF(Constants.kPIDLoopIdx, DRIVE.RIGHT_DRIVE_F, Constants.kTimeoutMs);
      }
      masterTalon.config_kP(Constants.kPIDLoopIdx, DRIVE.DRIVE_P, Constants.kTimeoutMs);
      masterTalon.config_kI(Constants.kPIDLoopIdx, DRIVE.DRIVE_I, Constants.kTimeoutMs);
      masterTalon.config_kD(Constants.kPIDLoopIdx, DRIVE.DRIVE_D, Constants.kTimeoutMs);
      masterTalon
          .configMotionCruiseVelocity((int) DRIVE.MOTION_MAGIC_CRUISE_VEL, Constants.kTimeoutMs);
      masterTalon.configMotionAcceleration((int) DRIVE.MOTION_MAGIC_ACCEL, Constants.kTimeoutMs);
    }
  }

  public void configTeleopVelocity() {
    if (side.equals(TalonPosition.Left)) {
      masterTalon.config_kF(Constants.kPIDLoopIdx, DRIVE.LEFT_DRIVE_F, Constants.kTimeoutMs);
    } else if (side.equals(TalonPosition.Right)) {
      masterTalon.config_kF(Constants.kPIDLoopIdx, DRIVE.RIGHT_DRIVE_F, Constants.kTimeoutMs);
    }
    masterTalon.config_kP(Constants.kPIDLoopIdx, DRIVE.TELEOP_DRIVE_P, Constants.kTimeoutMs);
    masterTalon.config_kI(Constants.kPIDLoopIdx, DRIVE.TELEOP_DRIVE_I, Constants.kTimeoutMs);
    masterTalon.config_kD(Constants.kPIDLoopIdx, DRIVE.TELEOP_DRIVE_D, Constants.kTimeoutMs);
  }

  public void resetConfig() {
    masterTalon.selectProfileSlot(Constants.kSlotIdx, Constants.kPIDLoopIdx);
    masterTalon
        .setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, 20, Constants.kTimeoutMs);
    masterTalon
        .setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 3, Constants.kTimeoutMs);
    masterTalon.setControlFramePeriod(ControlFrame.Control_3_General, 20);
    masterTalon
        .setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, 3, Constants.kTimeoutMs);
    masterTalon
        .setStatusFramePeriod(StatusFrameEnhanced.Status_8_PulseWidth, 3, Constants.kTimeoutMs);
    masterTalon
        .setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 20, Constants.kTimeoutMs);
    masterTalon.configNominalOutputForward(0, Constants.kTimeoutMs);
    masterTalon.configNominalOutputReverse(0, Constants.kTimeoutMs);
    masterTalon.configPeakOutputForward(1, Constants.kTimeoutMs);
    masterTalon.configPeakOutputReverse(-1, Constants.kTimeoutMs);

    masterTalon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative,
        Constants.kPIDLoopIdx, Constants.kTimeoutMs);

    masterTalon.setNeutralMode(NeutralMode.Brake);

    slaveTalon.configNominalOutputForward(0, Constants.kTimeoutMs);
    slaveTalon.configNominalOutputReverse(0, Constants.kTimeoutMs);
    slaveTalon.configPeakOutputForward(1, Constants.kTimeoutMs);
    slaveTalon.configPeakOutputReverse(-1, Constants.kTimeoutMs);
    slaveTalon.setNeutralMode(NeutralMode.Brake);
    slaveTalon.follow(masterTalon);

    masterTalon.configVoltageCompSaturation(12.75, Constants.kTimeoutMs);
    masterTalon.enableVoltageCompensation(true);
    masterTalon.configVoltageMeasurementFilter(32, Constants.kTimeoutMs);

    slaveTalon.configVoltageCompSaturation(12.75, Constants.kTimeoutMs);
    slaveTalon.enableVoltageCompensation(true);
    slaveTalon.configVoltageMeasurementFilter(32, Constants.kTimeoutMs);

    masterTalon
        .configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_100Ms, Constants.kTimeoutMs);
    masterTalon.configVelocityMeasurementWindow(64, Constants.kTimeoutMs);
  }

  public double getError() {
    if (side == TalonPosition.Arm) {
      return nativeUnitsToDegrees(masterTalon.getClosedLoopError(Constants.kPIDLoopIdx));
    }
    return nativeUnitsPer100MstoInchesPerSec(masterTalon.getClosedLoopError(Constants.kPIDLoopIdx));
  }

  public boolean isEncoderConnected() {
    return masterTalon.getSensorCollection().getPulseWidthRiseToRiseUs() > 100;
  }

  public void setMasterTalon(ControlMode mode, double out) {
    masterTalon.set(mode, out);
  }

  public void setSlaveTalon(ControlMode mode, double out) {
    slaveTalon.set(mode, out);
  }

  public synchronized double getPosition() {
    if (side == TalonPosition.Arm) {
      return nativeUnitsToDegrees(masterTalon.getSelectedSensorPosition(Constants.kPIDLoopIdx));
    }
    return nativeUnitsToInches(masterTalon.getSelectedSensorPosition(Constants.kPIDLoopIdx));
  }

  public synchronized double getSpeed() {
    if (side == TalonPosition.Arm) {
      return nativeUnitsPer100MstoDegreesPerSec(
          masterTalon.getSelectedSensorVelocity(Constants.kPIDLoopIdx));
    }
    return nativeUnitsPer100MstoInchesPerSec(
        masterTalon.getSelectedSensorVelocity(Constants.kPIDLoopIdx));
  }

  public double getRPM() {
    if (side == TalonPosition.Arm) {
      return ((masterTalon.getSelectedSensorVelocity(0) * 60.0 * 10.0) / Constants.CODES_PER_REV)
          * ARM.GEAR_RATIO;
    }
    return (masterTalon.getSelectedSensorVelocity(0) * 60.0 * 10.0) / Constants.CODES_PER_REV;
  }
  public double getRaw(){
    return masterTalon.getSelectedSensorPosition(Constants.kPIDLoopIdx);
  }

  public double nativeUnitsPer100MstoDegreesPerSec(double vel) {
    return nativeUnitsToDegrees(vel) * 10;
  }

  public double nativeUnitsToDegrees(double raw) {
    return ((raw / 4096.0) * 360.0) * ARM.GEAR_RATIO;
  }

  private double nativeUnitsPer100MstoInchesPerSec(double vel) {
    return 10 * nativeUnitsToInches(vel);
  }

  private double nativeUnitsToInches(double units) {
    return (units / Constants.CODES_PER_REV) * (Constants.DRIVE.CIRCUMFERENCE);
  }

  private double InchesPerSecToUnitsPer100Ms(double vel) {
    return InchesToNativeUnits(vel) / 10;
  }

  private double InchesToNativeUnits(double in) {
    return (Constants.CODES_PER_REV) * (in / Constants.DRIVE.CIRCUMFERENCE);
  }

  public void set(ControlMode mode, double value, boolean nMode) {
    if (talonMode != (nMode ? NeutralMode.Brake : NeutralMode.Coast)) {
      masterTalon.setNeutralMode(nMode ? NeutralMode.Brake : NeutralMode.Coast);
      slaveTalon.setNeutralMode(nMode ? NeutralMode.Brake : NeutralMode.Coast);
    }
    masterTalon.set(mode, value);
    talonMode = nMode ? NeutralMode.Brake : NeutralMode.Coast;
  }

  public void set(ControlMode mode, double value, boolean nMode, double arbFeed) {
    if (talonMode != (nMode ? NeutralMode.Brake : NeutralMode.Coast)) {
      masterTalon.setNeutralMode(nMode ? NeutralMode.Brake : NeutralMode.Coast);
      slaveTalon.setNeutralMode(nMode ? NeutralMode.Brake : NeutralMode.Coast);
    }
    masterTalon.set(mode, value, DemandType.ArbitraryFeedForward, arbFeed);
    talonMode = nMode ? NeutralMode.Brake : NeutralMode.Coast;
  }

  public void resetEncoder() {
    masterTalon.setSelectedSensorPosition(0, Constants.kPIDLoopIdx, Constants.kTimeoutMs);
  }

  public void setCoastMode() {
    masterTalon.setNeutralMode(NeutralMode.Coast);
    slaveTalon.setNeutralMode(NeutralMode.Coast);
  }

  public void setSensorPhase(boolean dir) {
    masterTalon.setSensorPhase(dir);
  }

  public void updateSmartDash() {
    SmartDashboard.putNumber(side.toString() + " Velocity", getSpeed());
    SmartDashboard.putNumber(side.toString() + " Error", getError());
    SmartDashboard
        .putNumber(side.toString() + " Master Output", masterTalon.getMotorOutputPercent());
    SmartDashboard.putNumber(side.toString() + " Slave Output", slaveTalon.getMotorOutputPercent());

    SmartDashboard.putNumber(side.toString() + " Current", masterTalon.getOutputCurrent());
    SmartDashboard.putNumber(side.toString() + " Position", getPosition());
    if (Math.abs(getRPM()) > maxRPM) {
      maxRPM = Math.abs(getRPM());
    }
    SmartDashboard.putNumber(side.toString() + " RPM", maxRPM);
  }

  public double getAbsolutePosition() {
    return masterTalon.getSensorCollection().getPulseWidthPosition();
  }

  public void zeroAbsolute() {
    int pulseWidth = masterTalon.getSensorCollection().getPulseWidthPosition();
		/*int newCenter;
		newCenter = (Constants.ARM.kBookEnd_0 + Constants.ARM.kBookEnd_1) / 2;
		newCenter &= 0xFFF;
		pulseWidth -= newCenter; */
    if (pulseWidth > 0) {
      pulseWidth = pulseWidth & 0xFFF;
    } else {
      System.out.println(-Math.round(((double) pulseWidth / 4096) - 0.50));
      pulseWidth += (-Math.round(((double) pulseWidth / 4096) - 0.50)) * 4096;
    }
    masterTalon.setSelectedSensorPosition(pulseWidth + (-ARM.kBookEnd_0), Constants.kPIDLoopIdx,
        Constants.kTimeoutMs);
    //System.out.println(pulseWidth + -(ARM.kBookEnd_0));
  }

  public int getZer() {
    int pulseWidth = masterTalon.getSensorCollection().getPulseWidthPosition();
		/*int newCenter;
		newCenter = (Constants.ARM.kBookEnd_0 + Constants.ARM.kBookEnd_1) / 2;
		newCenter &= 0xFFF;
		pulseWidth -= newCenter; */
    if (pulseWidth > 0) {
      pulseWidth = pulseWidth & 0xFFF;
    } else {
      System.out.println(-Math.round(((double) pulseWidth / 4096) - 0.50));
      pulseWidth += (-Math.round(((double) pulseWidth / 4096) - 0.50)) * 4096;
    }
    return pulseWidth + (-ARM.kBookEnd_0);
  }

  public double getPercentOutput() {
    return masterTalon.getMotorOutputPercent();
  }

  public void invert(boolean direction) {
    masterTalon.setInverted(direction);
    slaveTalon.setInverted(direction);
  }

  public double getMotorVoltage() {
    return masterTalon.getMotorOutputVoltage();
  }

  public void invertMaster(boolean direction) {
    masterTalon.setInverted(direction);
  }

  public void invertSlave(boolean direction) {
    slaveTalon.setInverted(direction);
  }

  public double getCurrentOutput() {
    return masterTalon.getOutputCurrent();
  }

  public enum TalonPosition {
    Left, Right, Arm
  }

}
