package frc.team1836.robot.util.drivers;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import frc.team1836.robot.Constants;

public class MkDrive {

	private final TalonSRX masterTalon, slaveTalon;
	private int masterID, slaveID;

	public MkDrive(int master, int slave) {
		masterTalon = new TalonSRX(master);
		slaveTalon = new TalonSRX(slave);

		masterID = master;
		slaveID = slave;
	}

	private void resetConfig() {
		masterTalon.clearMotionProfileHasUnderrun(10);
		masterTalon.clearMotionProfileTrajectories();
		masterTalon.clearStickyFaults(10);
		masterTalon.configPeakOutputForward(100, 10);
		masterTalon.configPeakOutputReverse(-100, 10);
		masterTalon.configNominalOutputForward(0, 10);
		masterTalon.configNominalOutputReverse(-0, 10);

		slaveTalon.clearMotionProfileHasUnderrun(10);
		slaveTalon.clearMotionProfileTrajectories();
		slaveTalon.clearStickyFaults(10);
		slaveTalon.configPeakOutputForward(100, 10);
		slaveTalon.configPeakOutputReverse(-100, 10);
		slaveTalon.configNominalOutputForward(0, 10);
		slaveTalon.configNominalOutputReverse(-0, 10);

		slaveTalon.set(ControlMode.Follower, masterID);
	}

	public double getError() {
		return nativeUnitsToInches(masterTalon.getErrorDerivative(0));
	}


	public double getPosition() {
		return nativeUnitsToInches(masterTalon.getSelectedSensorPosition(0));
	}


	public double getSpeed() {
		return nativeUnitsPer100MstoInchesPerSec(masterTalon.getSelectedSensorVelocity(10));
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

}
