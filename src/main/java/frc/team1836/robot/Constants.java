package frc.team1836.robot;

public final class Constants {

	public static double kLooperDt = 0.005;

	public static class MATH {

		public static final double PI = 3.14159265359;
	}

	public static class DRIVE {

		public static final int LEFT_MASTER_ID = 1;
		public static final int LEFT_SLAVE_ID = 2;
		public static final int RIGHT_MASTER_ID = 3;
		public static final int RIGHT_SLAVE_ID = 4;

		public static final int CODES_PER_REV = 4096;
		public static final double WHEEL_DIAMETER = 4;
		public static final double CIRCUMFERENCE = WHEEL_DIAMETER * MATH.PI;

		public static final double MIN_TEST_POS = 500;
		public static final double MIN_TEST_VEL = 100;

		public static final double PATH_DIST_TOL = 1;
		public static final double PATH_ANGLE_TOL = 1;

		public static final double DRIVE_FOLLOWER_P = 1.52;
		public static final double DRIVE_FOLLOWER_V = 0;
		public static final double DRIVE_FOLLOWER_A = 0;
		public static final double DRIVE_FOLLOWER_ANG = 0;
	}


}
