package frc.team1836.robot.util.drivers;

import com.ctre.phoenix.CANifier;
import frc.team1836.robot.util.math.HsvToRgb;
import frc.team1836.robot.util.math.MovingAverage;

public class MkLED extends CANifier {

    private static float _rgb[] = new float[3];
    private MovingAverage _averageR = new MovingAverage(10);
    private MovingAverage _averageG = new MovingAverage(10);
    private MovingAverage _averageB = new MovingAverage(10);
    private double startTime = 0;
    private boolean isPulsing;
    private float[][] currentColors;

    public MkLED(int id) {
        super(id);
    }

    public void setHSV(float Hue, float Saturation, float Value) {
        if (Saturation > 1) {
            Saturation = 1;
        }
        if (Saturation < 0) {
            Saturation = 0;
        }

        if (Value > 1) {
            Value = 1;
        }
        if (Value < 0) {
            Value = 0;
        }

        /* Convert to HSV to RGB */
        _rgb = HsvToRgb.convert(Hue, Saturation, Value);

        _rgb[0] = _averageR.process(_rgb[0]);
        _rgb[1] = _averageG.process(_rgb[1]);
        _rgb[2] = _averageB.process(_rgb[2]);

        /* Update CANifier's LED strip */
        setLEDOutput(_rgb[0],
                CANifier.LEDChannel.LEDChannelA);
        setLEDOutput(_rgb[1],
                CANifier.LEDChannel.LEDChannelB);
        setLEDOutput(_rgb[2],
                CANifier.LEDChannel.LEDChannelC);
    }


    public void set_rgb(LEDColors color) {
        double r = color.value[0];
        double g = color.value[1];
        double b = color.value[2];

        _rgb[0] = (float) g;
        _rgb[1] = (float) r;
        _rgb[2] = (float) b;
        //Weirdly Inverted

        _rgb[0] = _averageR.process(_rgb[0]);
        _rgb[1] = _averageG.process(_rgb[1]);
        _rgb[2] = _averageB.process(_rgb[2]);

        /* Update CANifier's LED strip */
        setLEDOutput(_rgb[0],
                CANifier.LEDChannel.LEDChannelA);
        setLEDOutput(_rgb[1],
                CANifier.LEDChannel.LEDChannelB);
        setLEDOutput(_rgb[2],
                CANifier.LEDChannel.LEDChannelC);
    }

    public void setPulse(LEDColors color1, LEDColors color2, double period) {
        if (!currentColors.equals(new LEDColors[]{color1, color2})) {
            startTime = edu.wpi.first.wpilibj.Timer.getFPGATimestamp();
        }
        if ((int) ((edu.wpi.first.wpilibj.Timer.getFPGATimestamp() - startTime) / period) % 2 == 0) {
            set_rgb(color1);
        } else {
            set_rgb(color2);
        }

    }

    public enum LEDColors {
        BLUE(new double[]{0, 2, 3}),
        RED(new double[]{0, 2, 3}),
        GREEN(new double[]{0, 2, 3});

        public final double[] value;

        LEDColors(final double[] value) {
            this.value = value;
        }
    }
}
