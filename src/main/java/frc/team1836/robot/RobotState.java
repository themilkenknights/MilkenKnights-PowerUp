package frc.team1836.robot;

public class RobotState {
	public static MatchState mMatchState = MatchState.DISABLED;
	public static DriveControlState mDriveControlState = DriveControlState.VELOCITY_SETPOINT;
	public static ArmControlState mArmControlState = ArmControlState.MOTION_MAGIC;
	public static ArmState mArmState = ArmState.ZEROED;

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
		INTAKE(10),
		SWITCH_PLACE(40),
		SCALE_PLACE(80),
		OPPOSITE_SWITCH(100),
		FULL_EXTENSION(150);

		public final double state;

		ArmState(final double state) {
			this.state = state;
		}
	}

}
