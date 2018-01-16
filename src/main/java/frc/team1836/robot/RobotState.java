package frc.team1836.robot;

public class RobotState {

	public static SystemState mSystemState = SystemState.IDLE;
	public static MatchState mMatchState = MatchState.DISABLED;
	public static DriveControlState mDriveControlState = DriveControlState.OPEN_LOOP;

	// Intenal state of the system
	public enum SystemState {
		IDLE
	}

	public enum MatchState {
		AUTO, TELEOP, DISABLED
	}

	public enum DriveControlState {
		OPEN_LOOP, // open loop voltage control
		VELOCITY_SETPOINT, // velocity PID control
		PATH_FOLLOWING, // used for autonomous driving
		TURN_IN_PLACE //Field Centric Turning in place
	}

}
