package frc.team1836.robot.subsystems;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team1836.robot.Constants;
import frc.team1836.robot.Constants.ELEVATOR;
import frc.team1836.robot.RobotState;
import frc.team1836.robot.RobotState.ArmControlState;
import frc.team1836.robot.RobotState.ElevatorState;
import frc.team1836.robot.util.logging.Log;
import frc.team1836.robot.util.structure.Subsystem;
import frc.team1836.robot.util.structure.loops.Loop;
import frc.team1836.robot.util.structure.loops.Looper;

public class Elevator extends Subsystem {


    private final TalonSRX masterTalon;
    private final TalonSRX slaveTalon1;
    private final TalonSRX slaveTalon2;
    private final TalonSRX slaveTalon3;


    private double armPosEnable = 0;

    private Elevator() {

        masterTalon = new TalonSRX(4);
        slaveTalon1 = new TalonSRX(7);
        slaveTalon2 = new TalonSRX(6);
        slaveTalon3 = new TalonSRX(1);

        masterTalon.setSensorPhase(false);
        masterTalon.config_kF(Constants.kPIDLoopIdx, Constants.ELEVATOR.ARM_F, Constants.kTimeoutMs);
        masterTalon.config_kP(Constants.kPIDLoopIdx, ELEVATOR.ARM_P, Constants.kTimeoutMs);
        masterTalon.config_kI(Constants.kPIDLoopIdx, Constants.ELEVATOR.ARM_I, Constants.kTimeoutMs);
        masterTalon.config_kD(Constants.kPIDLoopIdx, ELEVATOR.ARM_D, Constants.kTimeoutMs);
        masterTalon.configMotionCruiseVelocity((int) Constants.ELEVATOR.MOTION_MAGIC_CRUISE_VEL, Constants.kTimeoutMs);
        masterTalon.configMotionAcceleration((int) ELEVATOR.MOTION_MAGIC_ACCEL, Constants.kTimeoutMs);
        /*masterTalon.configForwardSoftLimitThreshold((int) 0,
                Constants.kTimeoutMs);
        masterTalon.configReverseSoftLimitThreshold((int) 0,
                Constants.kTimeoutMs); */

        masterTalon.selectProfileSlot(Constants.kSlotIdx, Constants.kPIDLoopIdx);
        masterTalon
                .setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, 20, Constants.kTimeoutMs);
        masterTalon
                .setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 3, Constants.kTimeoutMs);
        masterTalon.setControlFramePeriod(ControlFrame.Control_3_General, 20);
        masterTalon
                .setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, 3, Constants.kTimeoutMs);
        masterTalon
                .setStatusFramePeriod(StatusFrameEnhanced.Status_8_PulseWidth, 3, Constants.kTimeoutMs);
        masterTalon
                .setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 20, Constants.kTimeoutMs);
        masterTalon.configNominalOutputForward(0, Constants.kTimeoutMs);
        masterTalon.configNominalOutputReverse(0, Constants.kTimeoutMs);
        masterTalon.configPeakOutputForward(0.1, Constants.kTimeoutMs);
        masterTalon.configPeakOutputReverse(-0.1, Constants.kTimeoutMs);

        slaveTalon1.configPeakOutputForward(0.1, Constants.kTimeoutMs);
        slaveTalon1.configPeakOutputReverse(-0.1, Constants.kTimeoutMs);

        slaveTalon2.configPeakOutputForward(0.1, Constants.kTimeoutMs);
        slaveTalon2.configPeakOutputReverse(-0.1, Constants.kTimeoutMs);

        slaveTalon3.configPeakOutputForward(0.1, Constants.kTimeoutMs);
        slaveTalon3.configPeakOutputReverse(-0.1, Constants.kTimeoutMs);


        masterTalon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative,
                Constants.kPIDLoopIdx, Constants.kTimeoutMs);

        masterTalon.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_100Ms, Constants.kTimeoutMs);
        masterTalon.configVelocityMeasurementWindow(64, Constants.kTimeoutMs);

        masterTalon.setNeutralMode(NeutralMode.Brake);
        slaveTalon1.setNeutralMode(NeutralMode.Brake);
        slaveTalon2.setNeutralMode(NeutralMode.Brake);
        slaveTalon3.setNeutralMode(NeutralMode.Brake);


        masterTalon.setInverted(false);
        slaveTalon1.setInverted(false);
        slaveTalon2.setInverted(false);
        slaveTalon3.setInverted(false);

        slaveTalon1.follow(masterTalon);
        slaveTalon2.follow(masterTalon);
        slaveTalon3.follow(masterTalon);

    }

    public static Elevator getInstance() {
        return InstanceHolder.mInstance;
    }

    @Override
    public void outputToSmartDashboard() {
        SmartDashboard.putNumber("Elevator Current", masterTalon.getOutputCurrent());
        SmartDashboard.putString("Elevator Desired Position", RobotState.mElevatorState.toString());
        SmartDashboard.putString("Elevator Control Mode", RobotState.mArmControlState.toString());
        SmartDashboard.putBoolean("Elevator Status", isEncoderConnected());
        SmartDashboard.putNumber("Elevator Velocity", getSpeed());
        SmartDashboard.putNumber("Elevator Error", masterTalon.getClosedLoopError(Constants.kPIDLoopIdx));
        SmartDashboard.putNumber("Elevator Master Output", masterTalon.getMotorOutputPercent());
        SmartDashboard.putNumber("Elevator Current", masterTalon.getOutputCurrent());
        SmartDashboard.putNumber("Elevator Position", getPosition());
    }

    @Override
    public void slowUpdate(double timestamp) {

    }

    @Override
    public void checkSystem() {

    }

    public synchronized double getPosition() {
        return masterTalon.getSelectedSensorPosition(Constants.kPIDLoopIdx) / 4096.0;
    }

    public synchronized double getSpeed() {
        return (masterTalon.getSelectedSensorVelocity(Constants.kPIDLoopIdx) * 10.0 * 60.0) / 4096.0;
    }


    @Override
    public void registerEnabledLoops(Looper enabledLooper) {
        Loop mLoop = new Loop() {

            @Override
            public void onStart(double timestamp) {
                synchronized (Elevator.this) {
                    armPosEnable = getPosition();
                    RobotState.mElevatorState = ElevatorState.ENABLE;
                }
            }

            /**
             * Updated from mEnabledLoop in Robot.java
             * @param timestamp Time in seconds since code start
             */
            @Override
            public void onLoop(double timestamp) {
                synchronized (Elevator.this) {
                    armSafetyCheck();
                    updateRollers();
                    switch (RobotState.mArmControlState) {
                        case MOTION_MAGIC:
                            updateElevatorSetpoint();
                            return;
                        case OPEN_LOOP:
                            return;
                        default:
                            Log.marker("Unexpected arm control state: " + RobotState.mArmControlState);
                            break;
                    }
                }
            }

            @Override
            public void onStop(double timestamp) {
                setIntakeRollers(0);

            }
        };
        enabledLooper.register(mLoop);
    }

    public void setEnable() {
        armPosEnable = getPosition();
        RobotState.mElevatorState = ElevatorState.ENABLE;
    }

    public void changeSafety() {


    }

    public void zeroRel() {

    }

    private void updateElevatorSetpoint() {
        if (RobotState.mElevatorState.equals(ElevatorState.ENABLE)) {
            masterTalon.set(ControlMode.MotionMagic, armPosEnable * 4096.0);
        } else {
            masterTalon.set(ControlMode.MotionMagic, (RobotState.mElevatorState.state) * 4096.0);
        }
    }

    private void armSafetyCheck() {
        if (!isEncoderConnected()) {

            RobotState.mArmControlState = ArmControlState.OPEN_LOOP;

            Log.marker("Elevator Encoder Not Connected");
        }

        if (masterTalon.getOutputCurrent() > ELEVATOR.MAX_SAFE_CURRENT) {
            Log.marker("Unsafe Current " + masterTalon.getOutputCurrent() + " Amps");
            RobotState.mArmControlState = ArmControlState.OPEN_LOOP;
        }
    }

    public void updateRollers() {

    }

    public boolean isEncoderConnected() {
        return masterTalon.getSensorCollection().getPulseWidthRiseToRiseUs() > 100;
    }


    public void setOpenLoop(double output) {
        masterTalon.set(ControlMode.PercentOutput, output);
    }

    public void setIntakeRollers(double output) {

    }

    private static class InstanceHolder {

        private static final Elevator mInstance = new Elevator();
    }
}
