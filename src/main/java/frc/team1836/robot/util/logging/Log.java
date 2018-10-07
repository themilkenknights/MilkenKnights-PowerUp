package frc.team1836.robot.util.logging;

import frc.team1836.robot.RobotState;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Tracks start-up and caught crash events, logging them to a file which dosn't roll over
 */
public class Log {

    public static void logRobotStartup() {
        verbose("robot startup");
    }

    public static void logRobotInit() {
        verbose("robot init");
    }

    public static void logTeleopInit() {
        verbose("teleop init");
        RobotState.mMatchState = RobotState.MatchState.TELEOP;
    }

    public static void logTestInit() {
        verbose("test init");
        RobotState.mMatchState = RobotState.MatchState.TEST;
    }

    public static void logAutoInit() {
        verbose("auto init");
        RobotState.mMatchState = RobotState.MatchState.AUTO;
    }

    public static void logDisabledInit() {
        verbose("disabled init");
        RobotState.mMatchState = RobotState.MatchState.DISABLED;
    }

    public static void logThrowableCrash(Throwable throwable) {
        marker("Exception", throwable);
    }

    public static void marker(String mark) {
        marker(mark, null);
        System.out.println(mark);
    }

    public static void verbose(String mark) {
        logVerboseMarker(mark, null);
    }

    private static void marker(String mark, Throwable nullableException) {
        System.out.println(mark);
        try (PrintWriter writer = new PrintWriter(new FileWriter("/u/crash_tracking.txt", true))) {
            writer.print(new SimpleDateFormat("MMMM dd yyyy hh:mm:ss aaa").format(new Date()));
            writer.print(": ");
            writer.print(mark);

            if (nullableException != null) {
                writer.print(", ");
                nullableException.printStackTrace(writer);
            }

            writer.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void logVerboseMarker(String mark, Throwable nullableException) {
        System.out.println(mark);
        try (PrintWriter writer = new PrintWriter(new FileWriter("/u/verbose_log.txt", true))) {
            writer.print(new SimpleDateFormat("MMMM dd yyyy hh:mm:ss aaa").format(new Date()));
            writer.print(": ");
            writer.print(mark);

            if (nullableException != null) {
                writer.print(", ");
                nullableException.printStackTrace(writer);
            }

            writer.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
