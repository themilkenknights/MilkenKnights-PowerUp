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

    public double applyOffset(double value) {
        float offseted_value = (float) (value - value_offset);
        if (offseted_value < -180) {
            offseted_value += 360;
        }
        if (offseted_value > 180) {
            offseted_value -= 360;
        }
        return offseted_value;
    }

    public double getGyroAngle() {
        return getYaw() + value_offset;
    }

}
