package frc.team1836.robot.util.drivers;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team1836.robot.Constants;
import frc.team1836.robot.Constants.ARM;
import frc.team1836.robot.Constants.DRIVE;

public class MkTalon {

	private final TalonSRX masterTalon;
	private final VictorSPX slaveTalon;
	private int masterID, slaveID;
	private TalonPosition side;
	private double maxRPM = 0;

	/**
	 * @param master Talon with Encoder CAN ID
	 * @param slave Follower Talon CAN ID
	 */
	public MkTalon(int master, int slave, TalonPosition side) {
		masterTalon = new TalonSRX(master);
		slaveTalon = new VictorSPX(slave);

		this.side = side;

		masterID = master;
		slaveID = slave;

		resetConfig();
		if (side.equals(TalonPosition.Arm)) {
			configMotionMagic();
		}
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
		//	masterTalon.configForwardSoftLimitThreshold((int) MkMath.angleToNativeUnits(forwardLimit), Constants.kTimeoutMs);
		//	masterTalon.configReverseSoftLimitThreshold((int) MkMath.angleToNativeUnits(reverseLimit), Constants.kTimeoutMs);
		masterTalon.configForwardSoftLimitEnable(false, Constants.kTimeoutMs);
		masterTalon.configReverseSoftLimitEnable(false, Constants.kTimeoutMs);
	}

	public void setLimitEnabled(boolean enabled) {
		masterTalon.configForwardSoftLimitEnable(enabled, Constants.kTimeoutMs);
		masterTalon.configReverseSoftLimitEnable(enabled, Constants.kTimeoutMs);
	}

	public void configMotionMagic() {
		masterTalon.config_kF(Constants.kPIDLoopIdx, ARM.ARM_F, Constants.kTimeoutMs);
		masterTalon.config_kP(Constants.kPIDLoopIdx, ARM.ARM_P, Constants.kTimeoutMs);
		masterTalon.config_kI(Constants.kPIDLoopIdx, ARM.ARM_I, Constants.kTimeoutMs);
		masterTalon.config_kD(Constants.kPIDLoopIdx, ARM.ARM_D, Constants.kTimeoutMs);
		masterTalon.configMotionCruiseVelocity((int) ARM.MOTION_MAGIC_CRUISE_VEL, Constants.kTimeoutMs);
		masterTalon.configMotionAcceleration((int) ARM.MOTION_MAGIC_ACCEL, Constants.kTimeoutMs);
	}

	private void resetConfig() {
		masterTalon.selectProfileSlot(Constants.kSlotIdx, Constants.kPIDLoopIdx);
		masterTalon
				.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 5, Constants.kTimeoutMs);
		masterTalon.configNominalOutputForward(0, Constants.kTimeoutMs);
		masterTalon.configNominalOutputReverse(0, Constants.kTimeoutMs);
		masterTalon.configPeakOutputForward(1, Constants.kTimeoutMs);
		masterTalon.configPeakOutputReverse(-1, Constants.kTimeoutMs);
		masterTalon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0,
				Constants.kTimeoutMs);
		masterTalon.setNeutralMode(NeutralMode.Coast);

		slaveTalon.configNominalOutputForward(0, Constants.kTimeoutMs);
		slaveTalon.configNominalOutputReverse(0, Constants.kTimeoutMs);
		slaveTalon.configPeakOutputForward(1, Constants.kTimeoutMs);
		slaveTalon.configPeakOutputReverse(-1, Constants.kTimeoutMs);
		slaveTalon.setNeutralMode(NeutralMode.Coast);
		slaveTalon.follow(masterTalon);
	}

	public void setPeakOutput(double percent) {
		masterTalon.configPeakOutputForward(percent, Constants.kTimeoutMs);
		masterTalon.configPeakOutputReverse(-percent, Constants.kTimeoutMs);
		slaveTalon.configPeakOutputForward(percent, Constants.kTimeoutMs);
		slaveTalon.configPeakOutputReverse(-percent, Constants.kTimeoutMs);
	}

	public double getError() {
		if (side == TalonPosition.Arm) {
			return nativeUnitsToDegrees(
					masterTalon.getClosedLoopError(Constants.kPIDLoopIdx));
		}
		return nativeUnitsToInches(masterTalon.getClosedLoopError(Constants.kPIDLoopIdx));
	}

	public boolean isEncoderConnected() {
		return masterTalon.getSensorCollection().getPulseWidthRiseToRiseUs() != 0;
	}

	public synchronized double getPosition() {
		if (side == TalonPosition.Arm) {
			return nativeUnitsToDegrees(
					masterTalon.getSelectedSensorPosition(Constants.kPIDLoopIdx));
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

	public void setBrakeMode() {
		masterTalon.setNeutralMode(NeutralMode.Brake);
	}

	public void setCoastMode() {
		masterTalon.setNeutralMode(NeutralMode.Coast);
	}

	public double getRPM() {
		if (side == TalonPosition.Arm) {
			return
					((masterTalon.getSelectedSensorVelocity(0) * 60.0 * 10.0) / Constants.DRIVE.CODES_PER_REV)
							* ARM.GEAR_RATIO;
		}
		return (masterTalon.getSelectedSensorVelocity(0) * 60.0 * 10.0) / Constants.DRIVE.CODES_PER_REV;
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
		return (units / Constants.DRIVE.CODES_PER_REV) * (Constants.DRIVE.CIRCUMFERENCE);
	}

	private double InchesPerSecToUnitsPer100Ms(double vel) {
		return InchesToNativeUnits(vel) / 10;
	}

	private double InchesToNativeUnits(double in) {
		return (Constants.DRIVE.CODES_PER_REV) * (in / Constants.DRIVE.CIRCUMFERENCE);
	}

	public void set(ControlMode mode, double value) {
		masterTalon.set(mode, value);
	}

	public void resetEncoder() {
		masterTalon.setSelectedSensorPosition(0, Constants.kPIDLoopIdx, Constants.kTimeoutMs);
	}

	public void setSlave(ControlMode mode, double value) {
		slaveTalon.set(mode, value);
	}

	public void testDrive() {
		masterTalon.set(ControlMode.Velocity, DRIVE.MAX_VEL);
		Timer.delay(2.0);
		if (getPosition() < Constants.DRIVE.MIN_TEST_POS || getSpeed() < Constants.DRIVE.MIN_TEST_VEL) {
			System.out.println("FAILED - DRIVE FAILED TO REACH REQUIRED SPEED OR POSITION");
			System.out.println("Position" + getPosition() + "Speed" + getSpeed());
		}
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
		SmartDashboard
				.putNumber(side.toString() + " Position", getPosition());
		if (getRPM() > maxRPM) {
			maxRPM = getRPM();
		}
		SmartDashboard.putNumber(side.toString() + " RPM", maxRPM);
	}

	public double getPercentOutput() {
		return masterTalon.getMotorOutputPercent();
	}

	public void invert(boolean direction) {
		masterTalon.setInverted(direction);
		slaveTalon.setInverted(direction);
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
