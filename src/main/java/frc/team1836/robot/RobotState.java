package frc.team1836.robot;

import frc.team1836.robot.util.state.MatchData;

public class RobotState {

    public static SystemState mSystemState = SystemState.DISCONNECTED;
    public static MatchState mMatchState = MatchState.DISABLED;
    public static DriveControlState mDriveControlState = DriveControlState.OPEN_LOOP;
    public static ArmControlState mArmControlState = ArmControlState.MOTION_MAGIC;
    public static ArmState mArmState = ArmState.ENABLE;
    public static MatchData matchData = MatchData.defaultMatch;

    // Intenal state of the system
    public enum SystemState {
        CONNECTED, DISCONNECTED
    }

    public enum MatchState {
        AUTO, TELEOP, DISABLED, TEST
    }

    public enum DriveControlState {
        OPEN_LOOP, // open loop voltage control
        VELOCITY_SETPOINT, // velocity PID control
        PATH_FOLLOWING, // used for autonomous driving
    }

    public enum ArmControlState {
        MOTION_MAGIC,
        OPEN_LOOP,
        ZEROING
    }

    public enum ArmState {
        ENABLE(0),
        OPPOSITE_STOW(28.5),
        OPPOSITE_SWITCH_PLACE(78.5),
        SWITCH_PLACE(163.5),
        SECOND_SWITCH_PLACE(219.5),
        INTAKE(243.5);

        public final double state;

        ArmState(final double state) {
            this.state = state;
        }
    }

}
