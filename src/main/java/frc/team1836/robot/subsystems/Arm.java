package frc.team1836.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team1836.robot.Constants;
import frc.team1836.robot.Constants.ARM;
import frc.team1836.robot.RobotState;
import frc.team1836.robot.RobotState.ArmControlState;
import frc.team1836.robot.util.drivers.MkTalon;
import frc.team1836.robot.util.drivers.MkTalon.TalonPosition;
import frc.team1836.robot.util.logging.ReflectingCSVWriter;
import frc.team1836.robot.util.loops.Loop;
import frc.team1836.robot.util.loops.Looper;
import frc.team1836.robot.util.math.MkMath;
import frc.team1836.robot.util.other.Subsystem;

public class Arm extends Subsystem {

	private final ReflectingCSVWriter<ArmDebugOutput> mCSVWriter;
	private final MkTalon armTalon;
	private ArmDebugOutput mDebug = new ArmDebugOutput();
	private double setpoint = 0;

	private Arm() {
		mCSVWriter = new ReflectingCSVWriter<>(Constants.LOGGING.ARM_LOG_PATH,
				ArmDebugOutput.class);
		armTalon = new MkTalon(ARM.ARM_MASTER_TALON_ID, ARM.ARM_SLAVE_TALON_ID, TalonPosition.Arm);
		armTalon.setSensorPhase(true);
		armTalon.configMotionMagic();
	}

	public static Arm getInstance() {
		return InstanceHolder.mInstance;
	}

	@Override
	public void writeToLog() {
		mCSVWriter.write();
	}

	@Override
	public void outputToSmartDashboard() {
		SmartDashboard.putNumber("Arm Velocity", armTalon.getSpeed());
		SmartDashboard.putNumber("Arm RPM", armTalon.getRPM());
		SmartDashboard.putNumber("Arm Current", armTalon.getCurrentOutput());
		SmartDashboard.putNumber("Arm PercentVBus", armTalon.getPercentOutput());
		SmartDashboard.putNumber("Arm Position", armTalon.getPosition());
		SmartDashboard.putNumber("Arm Setpoint", setpoint);
	}

	@Override
	public void stop() {
		setpoint = 0;
	}

	@Override
	public void zeroSensors() {
		armTalon.resetEncoder();
	}

	@Override
	public void checkSystem() {

	}

	@Override
	public void registerEnabledLoops(Looper enabledLooper) {
		Loop mLoop = new Loop() {

			@Override
			public void onStart(double timestamp) {
				synchronized (Arm.this) {

				}
			}

			/**
			 * Updated from mEnabledLoop in Robot.java
			 * @param timestamp Time in seconds since code start
			 */
			@Override
			public void onLoop(double timestamp) {
				synchronized (Arm.this) {
					armSafetyCheck();
					updateDebugOutput(timestamp);
					mCSVWriter.add(mDebug);
					switch (RobotState.mArmControlState) {
						case MOTION_MAGIC:
							updateArmSetpoint();
							return;
						case ZEROING:
							zeroArm();
							return;
						case OPEN_LOOP:
							return;
						default:
							System.out
									.println("Unexpected arm control state: " + RobotState.mArmControlState);
							break;
					}
				}
			}

			@Override
			public void onStop(double timestamp) {
				stop();
			}
		};
		enabledLooper.register(mLoop);
	}

	private void updateDebugOutput(double timestamp) {
		mDebug.controlMode = RobotState.mArmControlState.toString();
		mDebug.output = armTalon.getPercentOutput();
		mDebug.position = armTalon.getPosition();
		mDebug.velocity = armTalon.getSpeed();
		mDebug.setpoint = RobotState.mArmState.state;
		mDebug.timestamp = timestamp;
	}

	private void updateArmSetpoint() {
		armTalon.set(ControlMode.MotionMagic, MkMath.angleToNativeUnits(RobotState.mArmState.state));
		setpoint = RobotState.mArmState.state;
	}

	private void zeroArm() {
		if (armTalon.getCurrentOutput() > ARM.CURRENT_HARDSTOP_LIMIT) {
			setOpenLoop(0);
			edu.wpi.first.wpilibj.Timer.delay(0.25);
			armTalon.resetEncoder();
			RobotState.mArmControlState = ArmControlState.MOTION_MAGIC;
		} else {
			setOpenLoop(ARM.ZEROING_POWER);
		}

	}

	private void armSafetyCheck() {
		if (armTalon.getCurrentOutput() > ARM.SAFE_CURRENT_OUTPUT
				|| armTalon.getSpeed() > Constants.ARM.MAX_SAFE_SPEED) {
			setOpenLoop(0);
		}
		if (!armTalon.isEncoderConnected()) {
			RobotState.mArmControlState = ArmControlState.OPEN_LOOP;
		}
	}

	public void setOpenLoop(double output) {
		RobotState.mArmControlState = ArmControlState.OPEN_LOOP;
		armTalon.set(ControlMode.PercentOutput, output);
		setpoint = output;
	}

	public static class ArmDebugOutput {

		double timestamp;
		String controlMode;
		double output;
		double position;
		double velocity;
		double setpoint;
	}

	private static class InstanceHolder {

		private static final Arm mInstance = new Arm();
	}
}
