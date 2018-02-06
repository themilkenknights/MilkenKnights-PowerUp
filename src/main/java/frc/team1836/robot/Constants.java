package frc.team1836.robot;

/**
 * UNLESS OTHERWISE NOTED BY RAW/NATIVE/RPM,
 * ALL POSITION UNITS ARE IN INCHES and DEGREES
 * ALL VELOCITY UNITS ARE IN INCHES PER SECOND and DEGREES PER SECOND
 * DIST DENOTES POSITION AND ANG DENOTES ANGLE
 * ID TYPICALLY DENOTES A CAN ID
 * ALL PID CONSTANTS SENT TO THE TALON ARE IN NATIVE UNITS
 */
public final class Constants {

	public static final int kSlotIdx = 0;
	public static final int kPIDLoopIdx = 0;
	public static final int kTimeoutMs = 10;
	public static final double kLooperDt = 0.005;
	public static final double PI = 3.14159265359;

	public static class DRIVE {

		public static final int LEFT_MASTER_ID = 10;
		public static final int LEFT_SLAVE_ID = 8;
		public static final int RIGHT_MASTER_ID = 5;
		public static final int RIGHT_SLAVE_ID = 3;

		public static final boolean LEFT_MASTER_INVERT = false;
		public static final boolean LEFT_SLAVE_INVERT = false;
		public static final boolean RIGHT_MASTER_INVERT = true;
		public static final boolean RIGHT_SLAVE_INVERT = true;

		public static final boolean LEFT_INVERT_SENSOR = true;
		public static final boolean RIGHT_INVERT_SENSOR = true;

		public static final double CODES_PER_REV = 4096.0;
		public static final double WHEEL_DIAMETER = 5.98;
		public static final double CIRCUMFERENCE = WHEEL_DIAMETER * PI;
		public static final double TURN_IN_PLACE_CIRCUMFERENCE = 104.1;

		public static final double MIN_TEST_POS = 500;
		public static final double MIN_TEST_VEL = 100;

		public static final double PATH_DIST_TOL = 0.25;
		public static final double PATH_ANGLE_TOL = 0.25;

		public static final double DRIVE_FOLLOWER_P = 5;
		public static final double DRIVE_FOLLOWER_A = 0;
		public static final double DRIVE_FOLLOWER_ANG = 0;

		public static final double RPM_MAX = 473.0; //Observed Max Speed for Drivetrain in RPM
		public static final double MAX_VEL =
				(RPM_MAX / 60) * (CIRCUMFERENCE); // Max Speed in Inches per second
		public static final double DRIVE_P = (0.1 * 1023.0) / (300.00);
		public static final double DRIVE_I = DRIVE_P / 100.0;
		public static final double DRIVE_D = 15 * DRIVE_P;
		public static final double DRIVE_F = (1023.0 / ((RPM_MAX / 60.0 / 10.0)
				* 4096.0)); //Feedforwrd Term for Drivetrain using MAX Motor Units / Max Speed in Native Units Per 100ms

		//Used for turn in place (Degrees) - Is converted to inches after a trajectory is generated
		public static final double MAX_ANG_VEL = 2.5 * 360;
		public static final double MAX_ANG_ACC = 1.5 * 360;
		public static final double MAX_ANG_JERK = 0.75 * 360;
	}

	public static class LOGGING {

		public static final String DRIVE_LOG_PATH = "/u/DRIVE-LOGS.csv";
		public static final String ARM_LOG_PATH = "/u/ARM-LOGS.csv";
		public static final String SUPERSTRUCTURE_LOG_PATH = "/u/SUPERSTRUCTURE-LOGS.csv";
	}

	public static class ARM {

		public static final int ARM_MASTER_TALON_ID = 4;
		public static final int ARM_SLAVE_TALON_ID = 2;


		public static final double RPM_MAX = 19.5; //RPM Max of Arm
		public static final double GEAR_RATIO = 22.0
				/ 336.0; //Gear Ratio between encoder and arm - Used to calulate arm position in degrees
		public static final double MAX_RAW_VEL =
				((RPM_MAX / 60.0 / 10.0) * 4096.0) / GEAR_RATIO; // Degrees per second
		public static final double ARM_P = 1.0 * ((0.1 * 1023.0) / (300.00));
		public static final double ARM_I = ARM_P / 100.0;
		public static final double ARM_D = 15 * ARM_P;
		public static final double ARM_F = (1023.0 / MAX_RAW_VEL);

		public static final double MOTION_MAGIC_CRUISE_VEL = MAX_RAW_VEL * 0.975;
		public static final double MOTION_MAGIC_ACCEL = MAX_RAW_VEL * 1.2;

		public static final double CURRENT_HARDSTOP_LIMIT = 8;
		public static final double ZEROING_POWER = 0.15;

		public static final double SAFE_CURRENT_OUTPUT = 16;
		public static final double MAX_SAFE_SPEED = (RPM_MAX / 60.0) * 360.0 * 1.5;


		public static final int LEFT_INTAKE_ROLLER_ID = 6; //Intake Roller Talon ID
		public static final int RIGHT_INTAKE_ROLLER_ID = 1; //Intake Roller Talon ID
		public static final double INTAKE_IN_ROLLER_SPEED = 0.95; //Intake Roller speed, reverse if it is the wrong direction
		public static final double INTAKE_OUT_ROLLER_SPEED=.40;

	}

	public static class INPUT {

		public static final double OPERATOR_DEADBAND = 0.1;
		public static final double kThrottleDeadband = 0.0;
		public static final double kWheelDeadband = 0.0;
	}


}
