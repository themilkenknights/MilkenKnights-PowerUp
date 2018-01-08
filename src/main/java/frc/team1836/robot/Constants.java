package frc.team1836.robot;

import static frc.team1836.robot.Constants.MATH.PI;

/**
 * UNLESS OTHERWISE NOTED BY RAW/NATIVE/RPM,
 * ALL POSITION UNITS ARE IN INCHES and DEGREES
 * ALL VELOCITY UNITS ARE IN INCHES PER SECOND and DEGREES PER SECOND
 * DIST DENOTES POSITION AND ANG DENOTES ANGLE
 * ID TYPICALLY DENOTES A CAN ID
 * ALL PID CONSTANTS SENT TO THE TALON ARE IN NATIVE UNITS
 */
public final class Constants {

	public static double kLooperDt = 0.005;

	public static class MATH {

		public static final double PI = 3.14159265359;
	}

	public static class DRIVE {

		public static final int LEFT_MASTER_ID = 1;
		public static final int LEFT_SLAVE_ID = 2;
		public static final int RIGHT_MASTER_ID = 8;
		public static final int RIGHT_SLAVE_ID = 7;

		public static final double WHEELBASE = 26;

		public static final int CODES_PER_REV = 4096;
		public static final double WHEEL_DIAMETER = 4;
		public static final double CIRCUMFERENCE = WHEEL_DIAMETER * PI;
		public static final double TURN_IN_PLACE_CIRCUMFERENCE = WHEELBASE * PI;

		public static final double MIN_TEST_POS = 500;
		public static final double MIN_TEST_VEL = 100;

		public static final double PATH_DIST_TOL = 1;
		public static final double PATH_ANGLE_TOL = 1;

		public static final double DRIVE_FOLLOWER_P = 1.52;
		public static final double DRIVE_FOLLOWER_V = 0;
		public static final double DRIVE_FOLLOWER_A = 0;
		public static final double DRIVE_FOLLOWER_ANG = 0;

		public static final double RPM_MAX = 840.0;
		public static final double FAKE_RPM_MAX = 420.0;
		public static final double MAX_VEL = (FAKE_RPM_MAX / 60) * (CIRCUMFERENCE); // Inches per second
		public static final double DRIVE_P = 1.0 * ((0.1 * 1023.0) / (300.00));
		public static final double DRIVE_I = DRIVE_P / 100.0;
		public static final double DRIVE_D = 15 * DRIVE_P;
		public static final double DRIVE_F = (1023.0 / ((RPM_MAX / 60.0 / 10.0) * 4096.0));

		public static final int kSlotIdx = 0;
		public static final int kPIDLoopIdx = 0;
		public static final int kTimeoutMs = 10;

		public static final double MAX_ANG_VEL = 0;
		public static final double MAX_ANG_ACC = 0;
		public static final double MAX_ANG_JERK = 0;
	}

	public static class LOGGING {

		public static final String DRIVE_LOG_PATH = "/home/lvuser/DRIVE-LOGS.csv";
		public static final String INPUT_LOG_PATH = "/home/lvuser/INPUT-LOGS.csv";
		public static final String SUPERSTRUCTURE_LOG_PATH = "/home/lvuser/SUPERSTRUCTURE-LOGS.csv";
	}


}
