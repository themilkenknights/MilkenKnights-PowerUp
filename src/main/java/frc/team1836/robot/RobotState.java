package frc.team1836.robot;

import frc.team1836.robot.util.state.MatchData;

public class RobotState {

    public static MatchState mMatchState = MatchState.DISABLED;
    public static DriveControlState mDriveControlState = DriveControlState.OPEN_LOOP;
    public static ArmControlState mArmControlState = ArmControlState.MOTION_MAGIC;
    public static ElevatorState mElevatorState = ElevatorState.ENABLE;
    public static MatchData matchData = MatchData.defaultMatch;

    public static void resetDefaultState() {
        RobotState.mElevatorState = ElevatorState.ENABLE;
        RobotState.mArmControlState = ArmControlState.MOTION_MAGIC;
        RobotState.mDriveControlState = DriveControlState.OPEN_LOOP;
        RobotState.matchData = MatchData.defaultMatch;
    }

    public enum MatchState {
        AUTO, TELEOP, DISABLED, TEST
    }

    public enum DriveControlState {
        OPEN_LOOP, // Open Loop PercentVBus Control
        VELOCITY_SETPOINT, // Closed-loop velocity pidf on talons - Only used as a backup
        PATH_FOLLOWING, // Used in autonomous to follow a pregenerated path
    }

    public enum ArmControlState {
        MOTION_MAGIC, // Closed Loop Motion Profile following on the talons used in nearly all circumstances
        OPEN_LOOP // Direct PercentVBus control of the arm as a failsafe
    }

    public enum ElevatorState {
        ENABLE(0), //State directly after robot is enabled (not mapped to a specific angle)
        OPPOSITE_STOW(5), //Used to Outtake into the exchange or store cube at start of auto
        OPPOSITE_SWITCH_PLACE(10), //Outtakes into the switch on the backside of the robot
        SWITCH_PLACE(13), //Main switch outtake position
        SECOND_SWITCH_PLACE(15), //Used to intake cubes from the second-floor of the pyramid
        INTAKE(17); //Intake Setpoint to get cubes from the ground

        public final double state;

        ElevatorState(final double state) {
            this.state = state;
        }
    }

}
