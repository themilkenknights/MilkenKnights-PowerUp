package frc.team1836.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team1836.robot.Constants;
import frc.team1836.robot.Constants.ARM;
import frc.team1836.robot.RobotState;
import frc.team1836.robot.RobotState.ArmControlState;
import frc.team1836.robot.RobotState.ArmState;
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
	private final VictorSPX leftIntakeRollerTalon;
	private final VictorSPX rightIntakeRollerTalon;
	private ArmDebugOutput mDebug = new ArmDebugOutput();
	private double setpoint = 0;

	private Arm() {
		mCSVWriter = new ReflectingCSVWriter<>(Constants.LOGGING.ARM_LOG_PATH,
				ArmDebugOutput.class);
		armTalon = new MkTalon(ARM.ARM_MASTER_TALON_ID, ARM.ARM_SLAVE_TALON_ID, TalonPosition.Arm);
		armTalon.setSensorPhase(true);
		armTalon.configMotionMagic();
		armTalon.setSoftLimit(ARM.ARM_FORWARD_LIMIT, ARM.ARM_REVERSE_LIMIT);
		leftIntakeRollerTalon = new VictorSPX(Constants.ARM.LEFT_INTAKE_ROLLER_ID);
		rightIntakeRollerTalon = new VictorSPX(Constants.ARM.RIGHT_INTAKE_ROLLER_ID);
		rightIntakeRollerTalon.set(ControlMode.Follower, Constants.ARM.LEFT_INTAKE_ROLLER_ID);
		leftIntakeRollerTalon.setInverted(true);
		leftIntakeRollerTalon.setNeutralMode(NeutralMode.Brake);
		rightIntakeRollerTalon.setNeutralMode(NeutralMode.Brake);
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
		armTalon.updateSmartDash();
		SmartDashboard.putNumber("Arm Current", armTalon.getCurrentOutput());
		SmartDashboard.putNumber("Arm Setpoint", setpoint);
		SmartDashboard.putString("Arm Pos", RobotState.mArmState.toString());
		SmartDashboard.putString("Arm Control Mode", RobotState.mArmControlState.toString());
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
			RobotState.mArmControlState = ArmControlState.OPEN_LOOP;
			setOpenLoop(0);
			edu.wpi.first.wpilibj.Timer.delay(0.25);
			armTalon.resetEncoder();
			armTalon.setLimitEnabled(true);
			RobotState.mArmState = ArmState.ZEROED;
			RobotState.mArmControlState = ArmControlState.MOTION_MAGIC;
			System.out.println(armTalon.getCurrentOutput());
		} else {
			armTalon.setLimitEnabled(false);
			setOpenLoop(ARM.ZEROING_POWER);
		}

	}

	private void armSafetyCheck() {
		if (armTalon.getCurrentOutput() > ARM.SAFE_CURRENT_OUTPUT) {
			RobotState.mArmControlState = ArmControlState.OPEN_LOOP;
			setOpenLoop(0);
		}
		if (!armTalon.isEncoderConnected()) {
			RobotState.mArmControlState = ArmControlState.OPEN_LOOP;
		}
	}

	public void setOpenLoop(double output) {
		armTalon.set(ControlMode.PercentOutput, output);
		setpoint = output;
	}

	public void setIntakeRollers(double output) {
		leftIntakeRollerTalon.set(ControlMode.PercentOutput, output);
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
