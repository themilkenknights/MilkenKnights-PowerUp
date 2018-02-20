package frc.team1836.robot.util.drivers;

import com.ctre.phoenix.CANifier;
import frc.team1836.robot.util.math.HsvToRgb;
import frc.team1836.robot.util.math.MovingAverage;

public class MkLED extends CANifier {
    private static float _rgb[] = new float[3];
    private MovingAverage _averageR = new MovingAverage(10);
    private MovingAverage _averageG = new MovingAverage(10);
    private MovingAverage _averageB = new MovingAverage(10);

    public MkLED(int id) {
        super(id);
    }

    public void setHSV(float Hue, float Saturation, float Value) {
        if (Saturation > 1) {
            Saturation = 1;
        }
        if (Saturation < 0)
            Saturation = 0;

        if (Value > 1)
            Value = 1;
        if (Value < 0)
            Value = 0;

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
}
