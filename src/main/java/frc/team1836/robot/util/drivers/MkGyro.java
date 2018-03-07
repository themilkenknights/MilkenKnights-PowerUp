package frc.team1836.robot.util.drivers;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;

public class MkGyro extends AHRS {

	public double value_offset;

	public MkGyro(SPI.Port spi_port_id) {
		super(spi_port_id);
		value_offset = 0;
	}

	public double getOffset() {
		return value_offset;
	}

	public void setOffset(double val) {
		value_offset = val;
	}

	public double getGyroAngle() {
		return - (getYaw() + value_offset);
	}

}
