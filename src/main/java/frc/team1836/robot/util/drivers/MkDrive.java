package frc.team1836.robot.util.drivers;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.Timer;
import frc.team1836.robot.Constants;
import frc.team1836.robot.Constants.DRIVE;

public class MkDrive {

	private final TalonSRX masterTalon, slaveTalon;
	private int masterID, slaveID;

	/**
	 * @param master Talon with Encoder CAN ID
	 * @param slave  Follower Talon CAN ID
	 */
	public MkDrive(int master, int slave) {
		masterTalon = new TalonSRX(master);
		slaveTalon = new TalonSRX(slave);

		masterID = master;
		slaveID = slave;

		resetConfig();

		masterTalon.config_kF(DRIVE.kPIDLoopIdx, DRIVE.DRIVE_F, DRIVE.kTimeoutMs);
		masterTalon.config_kP(DRIVE.kPIDLoopIdx, DRIVE.DRIVE_P, DRIVE.kTimeoutMs);
		masterTalon.config_kI(DRIVE.kPIDLoopIdx, DRIVE.DRIVE_I, DRIVE.kTimeoutMs);
		masterTalon.config_kD(DRIVE.kPIDLoopIdx, DRIVE.DRIVE_D, DRIVE.kTimeoutMs);

	}

	private void resetConfig() {
		masterTalon.selectProfileSlot(DRIVE.kSlotIdx, DRIVE.kPIDLoopIdx);
		masterTalon.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 5, DRIVE.kTimeoutMs);
		masterTalon.configNominalOutputForward(0, DRIVE.kTimeoutMs);
		masterTalon.configNominalOutputReverse(0, DRIVE.kTimeoutMs);
		masterTalon.configPeakOutputForward(1, DRIVE.kTimeoutMs);
		masterTalon.configPeakOutputReverse(-1, DRIVE.kTimeoutMs);
		masterTalon
				.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, DRIVE.kTimeoutMs);

		slaveTalon.configNominalOutputForward(0, DRIVE.kTimeoutMs);
		slaveTalon.configNominalOutputReverse(0, DRIVE.kTimeoutMs);
		slaveTalon.configPeakOutputForward(1, DRIVE.kTimeoutMs);
		slaveTalon.configPeakOutputReverse(-1, DRIVE.kTimeoutMs);

		slaveTalon.set(ControlMode.Follower, masterID);
	}

	public double getError() {
		return nativeUnitsToInches(masterTalon.getClosedLoopError(DRIVE.kPIDLoopIdx));
	}


	public synchronized double getPosition() {
		return nativeUnitsToInches(masterTalon.getSelectedSensorPosition(DRIVE.kPIDLoopIdx));
	}


	public synchronized double getSpeed() {
		return nativeUnitsPer100MstoInchesPerSec(
				masterTalon.getSelectedSensorVelocity(DRIVE.kPIDLoopIdx));
	}


	public double getRPM() {
		return (masterTalon.getSelectedSensorVelocity(10) * 600) / Constants.DRIVE.CODES_PER_REV;
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
		masterTalon.setSelectedSensorPosition(0, DRIVE.kPIDLoopIdx, DRIVE.kTimeoutMs);
	}

	public void testDrive() {

		masterTalon.set(ControlMode.PercentOutput, 1);
		Timer.delay(2.0);
		if (getPosition() < Constants.DRIVE.MIN_TEST_POS || getSpeed() < Constants.DRIVE.MIN_TEST_VEL) {
			System.out.println("FAILED - DRIVE FAILED TO REACH REQUIRED SPEED OR POSITION");
			System.out.println("Position" + getPosition() + "Speed" + getSpeed());
		}

	}

	public double getPercentOutput() {
		return masterTalon.getMotorOutputPercent();
	}

	public double getSlavePercentOutput() {
		return slaveTalon.getMotorOutputPercent();
	}

	public void invert(boolean direction) {
		masterTalon.setInverted(direction);
		slaveTalon.setInverted(direction);
	}

}
