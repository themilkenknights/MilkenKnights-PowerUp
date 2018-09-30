package frc.team1836.robot.util.math;

import frc.team1836.robot.Constants;
import frc.team1836.robot.Constants.ELEVATOR;
import frc.team1836.robot.Constants.DRIVE;

public class MkMath {

    public static double nativeUnitsPer100MstoInchesPerSec(double vel) {
        return 10 * nativeUnitsToInches(vel);
    }

    public static double nativeUnitsToInches(double units) {
        return (units / Constants.CODES_PER_REV) * (Constants.DRIVE.CIRCUMFERENCE);
    }

    public static double InchesPerSecToUnitsPer100Ms(double vel) {
        return InchesToNativeUnits(vel) / 10;
    }

    public static double InchesToNativeUnits(double in) {
        return (Constants.CODES_PER_REV) * (in / Constants.DRIVE.CIRCUMFERENCE);
    }

    public static double AngleToVel(double angle) {
        return (angle / 360) * DRIVE.TURN_IN_PLACE_CIRCUMFERENCE;
    }

    public static double handleDeadband(double val, double deadband) {
        return (Math.abs(val) > Math.abs(deadband)) ? val : 0.0;
    }

    public static double RPMToInchesPerSec(double vel) {
        return (vel / 60.0) * DRIVE.CIRCUMFERENCE;
    }

    public static double angleToNativeUnits(double ang) {
        return ((ang / 360.0) / ELEVATOR.GEAR_RATIO) * 4096.0;
    }

    public static double sin(double deg) {
        return Math.sin(Math.toRadians(deg));
    }

}
