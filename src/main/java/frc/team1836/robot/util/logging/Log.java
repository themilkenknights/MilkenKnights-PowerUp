package frc.team1836.robot.util.logging;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.UUID;

/**
 * Tracks start-up and caught crash events, logging them to a file which dosn't roll over
 */
public class Log {

	private static final UUID RUN_INSTANCE_UUID = UUID.randomUUID();

	public static void logRobotStartup() {
		marker("robot startup");
	}

	public static void logRobotConstruction() {
		marker("robot startup");
	}

	public static void logRobotInit() {
		marker("robot init");
	}

	public static void logTeleopInit() {
		marker("teleop init");
	}

	public static void logTestInit() {
		marker("test init");
	}

	public static void logAutoInit() {
		marker("auto init");
	}

	public static void logDisabledInit() {
		marker("disabled init");
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
			writer.print(RUN_INSTANCE_UUID.toString());
			writer.print(", ");
			writer.print(mark);
			writer.print(", ");
			writer.print(new Date().toString());

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
			writer.print(RUN_INSTANCE_UUID.toString());
			writer.print(", ");
			writer.print(mark);
			writer.print(", ");
			writer.print(new Date().toString());

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
