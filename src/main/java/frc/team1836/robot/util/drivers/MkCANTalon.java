package frc.team1836.robot.util.drivers;

import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import frc.team1836.robot.Constants;
import frc.team1836.robot.Constants.DRIVE;

/**
 * CANTalon Wrapper for either Drive Talons or Rotational Talons PID Values for rotational talons
 * must be configured but not necessarily used
 */
public class MkCANTalon extends TalonSRX {

	public MkCANTalon(int deviceNumber) {
		super(deviceNumber);
		resetConfig();
	}

	private void resetConfig() {
		clearMotionProfileHasUnderrun(10);
		clearMotionProfileTrajectories();
		clearStickyFaults(10);
		setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 5, 10);
		configPeakOutputForward(100, 10);
		configPeakOutputReverse(-100, 10);
		configNominalOutputForward(0, 10);
		configNominalOutputReverse(-0, 10);
	}


	public double getError() {
		return nativeUnitsToInches(super.getErrorDerivative(0));
	}


	public double getPosition() {
		return nativeUnitsToInches(super.getSelectedSensorPosition(0));
	}


	public double getSpeed() {
		return nativeUnitsPer100MstoInchesPerSec(super.getSelectedSensorVelocity(10));
	}


	public double getRPM() {
		return (super.getSelectedSensorVelocity(10) * 600) / DRIVE.CODES_PER_REV;
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


}
