package frc.team1836.robot.util.logging;

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
  }

  public static void logTestInit() {
    verbose("test init");
  }

  public static void logAutoInit() {
    verbose("auto init");
  }

  public static void logDisabledInit() {
    verbose("disabled init");
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
