package frc.team1836.robot;

public class RobotState {

	public static SystemState mSystemState = SystemState.IDLE;
	public static MatchState mMatchState = MatchState.DISABLED;
	public static DriveControlState mDriveControlState = DriveControlState.VELOCITY_SETPOINT;
	public static ArmControlState mArmControlState = ArmControlState.MOTION_MAGIC;
	public static ArmState mArmState = ArmState.ZEROED;

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

	public enum ArmControlState {
		ZEROING,
		MOTION_MAGIC,
		OPEN_LOOP,
	}

	public enum ArmState {
		ZEROED(0),
		SWITCH_PLACE(40),
		OPPOSITE_SWITCH(220);

		public final double state;

		ArmState(final double state) {
			this.state = state;
		}
	}

}
