package frc.team1836.robot.util.drivers;

import com.kauailabs.navx.frc.AHRS;

public class MkGyro {

	private final AHRS navX;
	private double offset;

	public MkGyro(final AHRS navX) {
		this.navX = navX;
	}

	public void zeroYaw() {
		offset = -navX.getYaw();
		navX.zeroYaw();
	}

	public boolean isConnected() {
		return navX.isConnected();
	}

	public double getYaw() {
		return navX.getYaw() + offset;
	}

	public double getRawYaw(){
		return navX.getYaw();
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
