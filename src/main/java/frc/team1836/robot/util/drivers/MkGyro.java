package frc.team1836.robot.util.drivers;

import com.kauailabs.navx.frc.AHRS;
import com.kauailabs.sf2.frc.navXSensor;
import com.kauailabs.sf2.orientation.OrientationHistory;

public class MkGyro {

	private final AHRS navX;
	OrientationHistory orientation_history;
	private double offset;

	public MkGyro(final AHRS navX) {
		this.navX = navX;
		navXSensor navx_sensor = new navXSensor(navX, "Drivetrain Orientation");
		orientation_history = new OrientationHistory(navx_sensor,
				navX.getRequestedUpdateRate() * 10);
	}

	public double getYawAtTime(double elapsedTime) {
		long navx_timestamp = navX.getLastSensorTimestamp();
		navx_timestamp -= elapsedTime * 100;
		return orientation_history.getYawDegreesAtTime(navx_timestamp);
	}

	public void zeroYaw() {
		offset = -navX.getYaw();
	}

	public boolean isConnected() {
		return navX.isConnected();
	}

	public double getYaw() {
		return navX.getYaw() + offset;
	}

	public double getFullYaw() {
		if (getYaw() <= 0) {
			return Math.abs(getYaw());
		} else {
			return 360 - Math.abs(getYaw());
		}
	}

	public double getRate() {
		return navX.getRate();
	}

	public double getRealYaw() {
		return navX.getYaw();
	}

	public double getPitch() {
		return navX.getPitch();
	}

	public double getRoll() {
		return navX.getRoll();
	}

	public boolean isMoving() {
		return navX.isMoving();
	}

}
