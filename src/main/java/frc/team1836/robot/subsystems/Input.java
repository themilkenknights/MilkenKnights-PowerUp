package frc.team1836.robot.subsystems;

import frc.team1836.robot.Constants;
import frc.team1836.robot.Constants.ELEVATOR;
import frc.team1836.robot.RobotState;
import frc.team1836.robot.RobotState.ElevatorControlState;
import frc.team1836.robot.RobotState.ElevatorState;
import frc.team1836.robot.RobotState.DriveControlState;
import frc.team1836.robot.util.drivers.MkJoystick;
import frc.team1836.robot.util.drivers.MkJoystickButton;
import frc.team1836.robot.util.logging.Log;
import frc.team1836.robot.util.math.DriveHelper;
import frc.team1836.robot.util.math.MkMath;
import frc.team1836.robot.util.state.DriveSignal;
import frc.team1836.robot.util.structure.Subsystem;
import frc.team1836.robot.util.structure.loops.Loop;
import frc.team1836.robot.util.structure.loops.Looper;

public class Input extends Subsystem {

    private final MkJoystick operatorJoystick = new MkJoystick(1);

    private final MkJoystick driverJoystick = new MkJoystick(0);
    private final MkJoystickButton changeDriveMode = driverJoystick.getButton(1, "Change Drive Mode");
    private final MkJoystickButton toggleLEDSignal = driverJoystick.getButton(2, "Toggle HP Signal");
    private final MkJoystickButton turnOffLED = driverJoystick.getButton(3, "Turn Off LED");

    private final MkJoystickButton armIntakeButton = operatorJoystick.getButton(2, "Elevator Intake");
    private final MkJoystickButton armDisableSafety = operatorJoystick
            .getButton(11, "Elevator Disable Current Limit");
    private final MkJoystickButton armSwitchButton = operatorJoystick.getButton(6, "Elevator Switch");
    private final MkJoystickButton armSecondSwitchButton = operatorJoystick
            .getButton(1, "Elevator Second Switch");
    private final MkJoystickButton armSwitchReverseButton = operatorJoystick
            .getButton(4, "Elevator Switch Reverse");
    private final MkJoystickButton armChangeModeButton = operatorJoystick
            .getButton(8, "Elevator Change Mode");
    private final MkJoystickButton armZeroButton = operatorJoystick.getButton(7, "Elevator Zero");
    private final MkJoystickButton intakeRollerIn = operatorJoystick.getButton(3, "Intake Roller In");
    private final MkJoystickButton intakeRollerOut = operatorJoystick
            .getButton(5, "Intake Roller Out");
    private final MkJoystickButton intakeRollerOutFast = operatorJoystick
            .getButton(9, "Intake Roller Out Fast");

    public Input() {

    }

    public static Input getInstance() {
        return InstanceHolder.mInstance;
    }

    @Override
    public void outputToSmartDashboard() {

    }

    @Override
    public void checkSystem() {

    }

    @Override
    public void registerEnabledLoops(Looper enabledLooper) {
        Loop mLoop = new Loop() {

            @Override
            public void onStart(double timestamp) {
                synchronized (Input.this) {

                }
            }

            @Override
            public void onLoop(double timestamp) {
                synchronized (Input.this) {
                    if (RobotState.mMatchState.equals(RobotState.MatchState.TELEOP)) {
                        updateDriveInput();
                        updateArmInput();
                    }
                }
            }

            @Override
            public void onStop(double timestamp) {
            }
        };
        enabledLooper.register(mLoop);
    }

    private void updateDriveInput() {
        if (changeDriveMode.isPressed()) {
            Drive.getInstance().configVelocityControl();
            RobotState.mDriveControlState =
                    RobotState.mDriveControlState.equals(DriveControlState.OPEN_LOOP)
                            ? DriveControlState.VELOCITY_SETPOINT : DriveControlState.OPEN_LOOP;
        }
        DriveSignal sig = DriveHelper
                .cheesyDrive((-driverJoystick.getRawAxis(2) + driverJoystick.getRawAxis(3)),
                        (-driverJoystick.getRawAxis(0)), true);

        if (RobotState.mDriveControlState == DriveControlState.VELOCITY_SETPOINT) {
            Drive.getInstance().setVelocitySetpoint(sig, 0, 0);
        } else if (RobotState.mDriveControlState == DriveControlState.OPEN_LOOP) {
            Drive.getInstance().setOpenLoop(sig);
        }

        if (toggleLEDSignal.isPressed()) {
            Superstructure.getInstance().toggleSignal();
        }
        if (turnOffLED.isPressed()) {
            Superstructure.getInstance().toggleLEDOff();
        }
    }

    private void updateArmInput() {
        if (armDisableSafety.isPressed()) {
            Arm.getInstance().changeSafety();
        }
        if (armZeroButton.isPressed()) {
            Arm.getInstance().zeroRel();
        }
        switch (RobotState.mElevatorControlState) {
            case MOTION_MAGIC:
                if (armIntakeButton.isPressed()) {
                    RobotState.mElevatorState = ElevatorState.INTAKE;
                } else if (armSecondSwitchButton.isPressed()) {
                    RobotState.mElevatorState = ElevatorState.SECOND_SWITCH_PLACE;
                } else if (armSwitchButton.isPressed()) {
                    RobotState.mElevatorState = ElevatorState.SWITCH_PLACE;
                } else if (armSwitchReverseButton.isPressed()) {
                    RobotState.mElevatorState = ElevatorState.OPPOSITE_STOW;
                } else if (operatorJoystick.getPOV() != -1) {
                    RobotState.mElevatorState = ElevatorState.OPPOSITE_SWITCH_PLACE;
                }
                if (armChangeModeButton.isPressed()) {
                    RobotState.mElevatorControlState = ElevatorControlState.OPEN_LOOP;
                }
                break;
            case OPEN_LOOP:
                Arm.getInstance().setOpenLoop(MkMath
                        .handleDeadband(operatorJoystick.getRawAxis(1), Constants.INPUT.OPERATOR_DEADBAND));
                if (armChangeModeButton.isPressed()) {
                    Arm.getInstance().setEnable();
                    RobotState.mElevatorControlState = ElevatorControlState.MOTION_MAGIC;
                }
                break;
            default:
                Log.marker("Unexpected Elevator control state: " + RobotState.mElevatorControlState);
                break;
        }

        if (intakeRollerIn.isHeld()) {
            Arm.getInstance().setIntakeRollers(Constants.ELEVATOR.INTAKE_IN_ROLLER_SPEED);
        } else if (intakeRollerOut.isHeld()) {
            Arm.getInstance().setIntakeRollers(ELEVATOR.INTAKE_OUT_ROLLER_SPEED);
        } else if (intakeRollerOutFast.isHeld()) {
            Arm.getInstance().setIntakeRollers(ELEVATOR.INTAKE_OUT_FAST_ROLLER_SPEED);
        } else {
            if (!RobotState.mElevatorState.equals(ElevatorState.ENABLE)) {
                Arm.getInstance().setIntakeRollers(ELEVATOR.SLOW_INTAKE_HOLD_SPEED);
            } else {
                Arm.getInstance().setIntakeRollers(0);
            }
        }
    }

    private static class InstanceHolder {

        private static final Input mInstance = new Input();
    }

}
