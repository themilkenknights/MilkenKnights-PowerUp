package frc.team1836.robot.util.drivers;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;

public class MkGyro extends AHRS {

  public MkGyro(SPI.Port spi_port_id) {
    super(spi_port_id);
  }

}
