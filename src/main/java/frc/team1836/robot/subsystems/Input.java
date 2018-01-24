package frc.team1836.robot.subsystems;

import frc.team1836.robot.RobotState;
import frc.team1836.robot.RobotState.ArmControlState;
import frc.team1836.robot.RobotState.ArmState;
import frc.team1836.robot.util.drivers.MkButton;
import frc.team1836.robot.util.drivers.MkJoystick;
import frc.team1836.robot.util.loops.Loop;
import frc.team1836.robot.util.loops.Looper;
import frc.team1836.robot.util.other.DriveHelper;
import frc.team1836.robot.util.other.Subsystem;

public class Input extends Subsystem {

	private final MkJoystick driverJoystick = new MkJoystick(0);
	private final MkButton armBumperButton = driverJoystick.getButton(2, "Arm Bumper");
	private final MkButton armIntakeButton = driverJoystick.getButton(3, "Arm Intake");
	private final MkButton armSwitchButton = driverJoystick.getButton(4, "Arm Switch");
	private final MkButton armScaleButton = driverJoystick.getButton(5, "Arm Scale");
	private final MkButton armSwitchReverseButton = driverJoystick.getButton(6, "Arm Switch Reverse");
	private final MkButton armIntakeReverseButton = driverJoystick.getButton(7, "Arm Intake Reverse");
	private final MkButton armChangeModeButton = driverJoystick.getButton(8, "Arm Change Mode");
	private final MkButton armZeroButton = driverJoystick.getButton(9, "Arm Zero");

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

	public void updateDriveInput() {
		Drive.getInstance().setVelocitySetpoint(DriveHelper
				.cheesyDrive(-driverJoystick.getRawAxis(1),
						-driverJoystick.getRawAxis(2) / 2, false));
	}

	private void updateArmInput() {
		if (armZeroButton.isPressed()) {
			RobotState.mArmControlState = ArmControlState.ZEROING;
		}
		switch (RobotState.mArmControlState) {
			case MOTION_MAGIC:
				if (armBumperButton.isPressed()) {
					RobotState.mArmState = ArmState.ZEROED;
				} else if (armIntakeButton.isPressed()) {
					RobotState.mArmState = ArmState.INTAKE;
				} else if (armSwitchButton.isPressed()) {
					RobotState.mArmState = ArmState.SWITCH_PLACE;
				} else if (armScaleButton.isPressed()) {
					RobotState.mArmState = ArmState.SCALE_PLACE;
				} else if (armSwitchReverseButton.isPressed()) {
					RobotState.mArmState = ArmState.OPPOSITE_SWITCH;
				} else if (armIntakeReverseButton.isPressed()) {
					RobotState.mArmState = ArmState.FULL_EXTENSION;
				}
				if (armChangeModeButton.isPressed()) {
					RobotState.mArmControlState = ArmControlState.OPEN_LOOP;
				}
				return;
			case ZEROING:
				return;
			case OPEN_LOOP:
				Arm.getInstance().setOpenLoop(driverJoystick.getRawAxis(3));
				if (armChangeModeButton.isPressed()) {
					RobotState.mArmControlState = ArmControlState.MOTION_MAGIC;
				}
				return;
			default:
				System.out
						.println("Unexpected arm control state: " + RobotState.mArmControlState);
				break;
		}
	}

	private static class InstanceHolder {

		private static final Input mInstance = new Input();
	}

}
