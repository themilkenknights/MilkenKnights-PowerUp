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
    public static final int kTimeoutMs = 0;
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
        public static final double PATH_WHEELBASE = 33.75;

        public static final double PATH_DIST_TOL = 0.25;
        public static final double PATH_ANGLE_TOL = 0.25;

        public static final double DRIVE_FOLLOWER_P = 1.25;
        public static final double DRIVE_FOLLOWER_A = 0;
        public static final double DRIVE_FOLLOWER_ANG = -2;

        public static final double LEFT_RPM_MAX = 488.0; //Observed Max Speed for Drivetrain in RPM
        public static final double RIGHT_RPM_MAX = 502.0; //Observed Max Speed for Drivetrain in RPM

        public static final double MAX_VEL =
                (LEFT_RPM_MAX / 60) * (CIRCUMFERENCE); // Max Speed in Inches per second
        public static final double DRIVE_P = 7 * (0.1 * 1023.0) / (700); //300
        public static final double DRIVE_I = 0; //DRIVE_P / 100.0;
        public static final double DRIVE_D = 3 * DRIVE_P;
        public static final double LEFT_DRIVE_F = (1023.0 / ((LEFT_RPM_MAX / 60.0 / 10.0)
                * 4096.0)); //Feedforwrd Term for Drivetrain using MAX Motor Units / Max Speed in Native Units Per 100ms
        public static final double RIGHT_DRIVE_F = (1023.0 / ((RIGHT_RPM_MAX / 60.0 / 10.0)
                * 4096.0)); //Feedforwrd Term for Drivetrain using MAX Motor Units / Max Speed in Native Units Per 100ms
        public static final double MIN_TEST_POS = 720;
        public static final double MIN_TEST_VEL = 145;

        public static final double MAX_PITCH = 30;

        public static final double mPangFollower = -0.075;
    }

    public static class LOGGING {

        public static final String DRIVE_LOG_PATH = "/u/DRIVE-LOGS.csv";
        public static final String ARM_LOG_PATH = "/u/ARM-LOGS.csv";
    }

    public static class ARM {

        public static final int ARM_MASTER_TALON_ID = 4;
        public static final int ARM_SLAVE_TALON_ID = 7;
        public static final boolean ARM_SENSOR_PHASE = false;
        public static final boolean ARM_MASTER_DIRECTION = false;
        public static final boolean ARM_SLAVE_DIRECTION = true;
        public static final boolean LEFT_INTAKE_DIRECTION = false;
        public static final boolean RIGHT_INTAKE_DIRECTION = false;

        public static final double RPM_MAX = 36; //RPM Max of Arm
        public static final double GEAR_RATIO = 22.0
                / 336.0; //Gear Ratio between encoder and arm - Used to calulate arm position in degrees
        public static final double MAX_RAW_VEL =
                ((RPM_MAX / 60.0 / 10.0) * 4096.0) / GEAR_RATIO; // Degrees per second
        public static final double ARM_P = 2 * ((0.1 * 1023.0) / (1600)); //7.5 deg or 1390 units
        public static final double ARM_I = 0;
        public static final double ARM_D = ARM_P * 10;
        public static final double ARM_F = (1023.0 / MAX_RAW_VEL);

        public static final double ARM_FORWARD_LIMIT = 250;
        public static final double ARM_REVERSE_LIMIT = 0;
        public static final double MOTION_MAGIC_CRUISE_VEL = MAX_RAW_VEL;
        public static final double MOTION_MAGIC_ACCEL = MAX_RAW_VEL * 20;
        public static final double SLOW_INTAKE_HOLD_SPEED = 0.1;
        public static final int ROLLER_INTAKE_PDP_PORT = 6;
        public static final double ROLLER_INTAKE_CURRENT_LIMIT = 2;
        public static final double CURRENT_HARDSTOP_LIMIT = 2;
        public static final double ZEROING_POWER = -0.15;

        public static final int LEFT_INTAKE_ROLLER_ID = 6; //Intake Roller Talon ID
        public static final int RIGHT_INTAKE_ROLLER_ID = 1; //Intake Roller Talon ID
        public static final double INTAKE_IN_ROLLER_SPEED = 0.95; //Intake Roller speed, reverse if it is the wrong direction
        public static final double INTAKE_OUT_ROLLER_SPEED = -0.40;
        public static final double INTAKE_OUT_FAST_ROLLER_SPEED = -0.90;
        public static final double ANGLE_OFFSET = -1056;
    }

    public static class INPUT {

        public static final double OPERATOR_DEADBAND = 0.1;
        public static final double kThrottleDeadband = 0.0;
        public static final double kWheelDeadband = 0.0045;
    }

    public static class AUTO {

        public static final String pathPath = "/home/lvuser/paths/";
        public static final String[] autoNames = {"CSL-1", "CSL-2", "CSL-3",
                "CSL-4", "CSL-5", "CSR-1", "CSR-2", "CSR-3",
                "CSR-4", "CSR-5", "DriveStraight"};
    }

    public static class SUPERSTRUCTURE {

        public static final int CANIFIER_ID = 11;
        public static final int PDP_ID = 0;
    }

}
