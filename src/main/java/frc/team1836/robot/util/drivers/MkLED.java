package frc.team1836.robot.util.drivers;

import com.ctre.phoenix.CANifier;

public class MkLED extends CANifier {

    private static float _rgb[] = new float[3];
    private double startTime = 0;
    private LEDColors[] currentColors = new LEDColors[]{LEDColors.OFF, LEDColors.OFF};

    public MkLED(int id) {
        super(id);
    }

    public void set_rgb(LEDColors color) {
        double r = color.value[0];
        double g = color.value[1];
        double b = color.value[2];

        _rgb[0] = (float) g;
        _rgb[1] = (float) r;
        _rgb[2] = (float) b;

        setLEDOutput(_rgb[0],
                CANifier.LEDChannel.LEDChannelA);
        setLEDOutput(_rgb[1],
                CANifier.LEDChannel.LEDChannelB);
        setLEDOutput(_rgb[2],
                CANifier.LEDChannel.LEDChannelC);
    }

    public void setPulse(LEDColors color1, LEDColors color2, double period) {
        if (!(currentColors[0] == color1 && currentColors[1] == color2)) {
            startTime = edu.wpi.first.wpilibj.Timer.getFPGATimestamp();
            currentColors = new LEDColors[]{color1, color2};
        }
        if (((int) ((edu.wpi.first.wpilibj.Timer.getFPGATimestamp() - startTime) / period)) % 2 == 0) {
            set_rgb(color1);
        } else {
            set_rgb(color2);
        }
    }

    public enum LEDColors {
        BLUE(new double[]{0, 0, 255}),
        RED(new double[]{255, 0, 0}),
        OFF(new double[]{0, 0, 0}),
        ORANGE(new double[]{255, 165, 0}),
        PURPLE(new double[]{128, 0, 128}),
        WHITE(new double[]{255, 255, 255}),
        GREEN(new double[]{0, 255, 0});

        public final double[] value;

        LEDColors(final double[] value) {
            this.value = value;
        }
    }
}
