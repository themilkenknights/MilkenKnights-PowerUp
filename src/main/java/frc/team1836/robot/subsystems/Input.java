package frc.team1836.robot.subsystems;

import frc.team1836.robot.Constants;
import frc.team1836.robot.Constants.ARM;
import frc.team1836.robot.RobotState;
import frc.team1836.robot.RobotState.ArmControlState;
import frc.team1836.robot.RobotState.ArmState;
import frc.team1836.robot.RobotState.DriveControlState;
import frc.team1836.robot.util.drivers.MkJoystick;
import frc.team1836.robot.util.drivers.MkJoystickButton;
import frc.team1836.robot.util.loops.Loop;
import frc.team1836.robot.util.loops.Looper;
import frc.team1836.robot.util.math.MkMath;
import frc.team1836.robot.util.other.DriveHelper;
import frc.team1836.robot.util.other.Subsystem;
import frc.team1836.robot.util.state.DriveSignal;

public class Input extends Subsystem {


	private final MkJoystick operatorJoystick = new MkJoystick(1);


	private final MkJoystick driverJoystick = new MkJoystick(0);
	private final MkJoystickButton changeDriveMode = driverJoystick
			.getButton(1,
					"Change Drive Mode");

	private final MkJoystickButton armIntakeButton = operatorJoystick.getButton(2, "Arm Intake");
	private final MkJoystickButton armSwitchButton = operatorJoystick.getButton(6, "Arm Switch");
	private final MkJoystickButton armSecondSwitchButton = operatorJoystick
			.getButton(1, "Arm Second Switch");
	private final MkJoystickButton armSwitchReverseButton = operatorJoystick
			.getButton(4, "Arm Switch Reverse");
	private final MkJoystickButton armChangeModeButton = operatorJoystick
			.getButton(8, "Arm Change Mode");
	private final MkJoystickButton armZeroButton = operatorJoystick.getButton(7, "Arm Zero");
	private final MkJoystickButton intakeRollerIn = operatorJoystick
			.getButton(3,
					"Intake Roller In");
	private final MkJoystickButton intakeRollerOut = operatorJoystick
			.getButton(5,
					"Intake Roller Out");
	private final MkJoystickButton intakeRollerInOut = operatorJoystick
			.getButton(9,
					"Intake Roller In-Out");
	private final MkJoystickButton intakeRollerOutFast = operatorJoystick
			.getButton(10,
					"Intake Roller Out Fast");


	public Input() {

	}

	public static Input getInstance() {
		return InstanceHolder.mInstance;
	}


	@Override
	public void outputToSmartDashboard() {

	}

	@Override
	public void stop() {

	}

	public void zeroSensors() {

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
			RobotState.mDriveControlState =
					RobotState.mDriveControlState.equals(DriveControlState.OPEN_LOOP)
							? DriveControlState.VELOCITY_SETPOINT : DriveControlState.OPEN_LOOP;
		}
		DriveSignal sig = DriveHelper
				.cheesyDrive((-driverJoystick.getRawAxis(2) + driverJoystick.getRawAxis(3)),
						(-driverJoystick.getRawAxis(0)), true);
		if (RobotState.mDriveControlState == DriveControlState.VELOCITY_SETPOINT) {
			Drive.getInstance().setVelocitySetpoint(sig);
		} else if (RobotState.mDriveControlState == DriveControlState.OPEN_LOOP) {
			Drive.getInstance().setOpenLoop(sig);
		}

	}

	private void updateArmInput() {
		if (armZeroButton.isPressed()) {
			RobotState.mArmControlState = ArmControlState.ZEROING;
		}
		switch (RobotState.mArmControlState) {
			case MOTION_MAGIC:
				if (armIntakeButton.isPressed()) {
					RobotState.mArmState = ArmState.INTAKE;
				} else if (armSecondSwitchButton.isPressed()) {
					RobotState.mArmState = ArmState.SECOND_SWITCH_PLACE;
				} else if (armSwitchButton.isPressed()) {
					RobotState.mArmState = ArmState.SWITCH_PLACE;
				} else if (armSwitchReverseButton.isPressed()) {
					RobotState.mArmState = ArmState.OPPOSITE_SWITCH;
				}
				if (armChangeModeButton.isPressed()) {
					RobotState.mArmControlState = ArmControlState.OPEN_LOOP;
				}
				break;
			case ZEROING:
				break;
			case OPEN_LOOP:
				Arm.getInstance()
						.setOpenLoop(MkMath
								.handleDeadband(operatorJoystick.getRawAxis(1),
										Constants.INPUT.OPERATOR_DEADBAND));
				if (armChangeModeButton.isPressed()) {
					RobotState.mArmControlState = ArmControlState.MOTION_MAGIC;
				}
				break;
			default:
				System.out
						.println("Unexpected arm control state: " + RobotState.mArmControlState);
				break;
		}

		if (intakeRollerIn.isHeld()) {
			if (Arm.getInstance().getIntakeRollerCurrent() < ARM.INTAKE_LIMIT_CURRENT) {
				Arm.getInstance().setIntakeRollers(ARM.INTAKE_IN_ROLLER_SPEED);
			} else {
				Arm.getInstance().setIntakeRollers(-ARM.SLOW_INTAKE_HOLD_SPEED);
			}
		} else if (intakeRollerOut.isHeld()) {
			Arm.getInstance().setIntakeRollers(-ARM.INTAKE_OUT_ROLLER_SPEED);
		} else if (intakeRollerInOut.isPressed()) {
			Arm.getInstance().invertRightRoller(true);
			edu.wpi.first.wpilibj.Timer.delay(0.4);
			Arm.getInstance().invertRightRoller(false);
		} else if (intakeRollerOutFast.isHeld()) {
			Arm.getInstance().setIntakeRollers(ARM.INTAKE_OUT_FAST_ROLLER_SPEED);
		} else {
			if (!RobotState.mArmControlState.equals(ArmControlState.ZEROING)) {
				Arm.getInstance().setIntakeRollers(ARM.SLOW_INTAKE_HOLD_SPEED);
			}

		}
	}

	private static class InstanceHolder {

		private static final Input mInstance = new Input();
	}

}
