package frc.team1836.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team1836.robot.Constants;
import frc.team1836.robot.Constants.ELEVATOR;
import frc.team1836.robot.RobotState;
import frc.team1836.robot.RobotState.ElevatorControlState;
import frc.team1836.robot.RobotState.ElevatorState;
import frc.team1836.robot.util.drivers.MkTalon;
import frc.team1836.robot.util.drivers.MkTalon.TalonPosition;
import frc.team1836.robot.util.drivers.TalonSRXFactory;
import frc.team1836.robot.util.logging.Log;
import frc.team1836.robot.util.logging.ReflectingCSVWriter;
import frc.team1836.robot.util.math.MkMath;
import frc.team1836.robot.util.structure.Subsystem;
import frc.team1836.robot.util.structure.loops.Loop;
import frc.team1836.robot.util.structure.loops.Looper;

public class Elevator extends Subsystem {

    private final ReflectingCSVWriter<ElevatorDebugOutput> mCSVWriter;
    private final TalonSRX mMaster;
    private final VictorSPX mRightSlave, mLeftSlaveA, mLeftSlaveB;
    private final VictorSPX leftIntakeRollerTalon;
    private final VictorSPX rightIntakeRollerTalon;
    private ElevatorDebugOutput mDebug = new ElevatorDebugOutput();
    private boolean elevatorSafety = true;
    private double elevatorPosEnable = 0;
    private double rollerSetpoint = 0;
    private double startDis = 0;
    private boolean disCon = false;
    private boolean mHasBeenZeroed = false;

    private Elevator() {
        mMaster = TalonSRXFactory.createDefaultTalon(ELEVATOR.ELEVATOR_MASTER_TALON_ID);
        mCSVWriter = new ReflectingCSVWriter<>(Constants.LOGGING.ARM_LOG_PATH, ElevatorDebugOutput.class);
        elevatorTalon = new MkTalon(ELEVATOR.ELEVATOR_MASTER_TALON_ID, Constants.ELEVATOR.ARM_SLAVE_TALON_ID, TalonPosition.Elevator);
        elevatorTalon.setSensorPhase(ELEVATOR.ARM_SENSOR_PHASE);
        elevatorTalon.configMotionMagic();
        elevatorTalon.setSoftLimit(ELEVATOR.ARM_FORWARD_LIMIT, ELEVATOR.ARM_REVERSE_LIMIT);
        elevatorTalon.setLimitEnabled(true);
        leftIntakeRollerTalon = new VictorSPX(Constants.ELEVATOR.LEFT_INTAKE_ROLLER_ID);
        rightIntakeRollerTalon = new VictorSPX(ELEVATOR.RIGHT_INTAKE_ROLLER_ID);
        leftIntakeRollerTalon.setNeutralMode(NeutralMode.Brake);
        rightIntakeRollerTalon.setNeutralMode(NeutralMode.Brake);
        elevatorTalon.invertMaster(Constants.ELEVATOR.ARM_MASTER_DIRECTION);


        mRightSlave = TalonSRXFactory.createPermanentSlaveTalon(Constants.kElevatorRightSlaveId,
                Constants.kElevatorMasterId);
        mRightSlave.setInverted(true);

        mLeftSlaveA = TalonSRXFactory.createPermanentSlaveTalon(Constants.kElevatorLeftSlaveAId,
                Constants.kElevatorMasterId);
        mLeftSlaveA.setInverted(false);

        mLeftSlaveB = TalonSRXFactory.createPermanentSlaveTalon(Constants.kElevatorLeftSlaveBId,
                Constants.kElevatorMasterId);
        mLeftSlaveB.setInverted(false);





        leftIntakeRollerTalon.setInverted(ELEVATOR.LEFT_INTAKE_DIRECTION);
        rightIntakeRollerTalon.setInverted(ELEVATOR.RIGHT_INTAKE_DIRECTION);
    }

    public static Elevator getInstance() {
        return InstanceHolder.mInstance;
    }

    @Override
    public void outputToSmartDashboard() {
        elevatorTalon.updateSmartDash();
        SmartDashboard.putNumber("Elevator Current", elevatorTalon.getCurrentOutput());
        SmartDashboard.putString("Elevator Desired Position", RobotState.mElevatorState.toString());
        SmartDashboard.putString("Elevator Control Mode", RobotState.mElevatorControlState.toString());
        SmartDashboard.putBoolean("Elevator Status", elevatorTalon.isEncoderConnected() && mHasBeenZeroed);
        SmartDashboard.putNumber("Roller Output", leftIntakeRollerTalon.getMotorOutputPercent());
        SmartDashboard.putNumber("Elevator Position", elevatorTalon.getPosition());
    }

    @Override
    public void slowUpdate(double timestamp) {
        if (RobotState.mMatchState != RobotState.MatchState.DISABLED) {
            updateDebugOutput(timestamp);
            mCSVWriter.add(mDebug);
            mCSVWriter.write();
        }
    }

    @Override
    public void checkSystem() {
        if (!elevatorTalon.isEncoderConnected()) {
            Log.marker("Elevator Encoder Not Connected");
        }
        if (RobotState.mElevatorControlState == ElevatorControlState.MOTION_MAGIC) {
            for (ElevatorState state : ElevatorState.values()) {
                if (state != ElevatorState.ENABLE) {
                    RobotState.mElevatorState = state;
                    setIntakeRollers(-0.25);
                    Timer.delay(2);
                }
            }

            Timer.delay(1);

            elevatorTalon.setCoastMode();
            elevatorTalon.setMasterTalon(ControlMode.PercentOutput, 0);
            elevatorTalon.setSlaveTalon(ControlMode.PercentOutput, -0.3);

            Timer.delay(1);

            elevatorTalon.setCoastMode();
            elevatorTalon.setSlaveTalon(ControlMode.PercentOutput, 0);
            elevatorTalon.setMasterTalon(ControlMode.PercentOutput, -0.3);

            Timer.delay(1);

            elevatorTalon.setMasterTalon(ControlMode.PercentOutput, 0);
            elevatorTalon.setSlaveTalon(ControlMode.PercentOutput, 0);

            elevatorTalon.resetConfig();
        } else {
            Log.marker("Elevator Test Failed");
        }
    }

    public synchronized boolean hasBeenZeroed() {
        return mHasBeenZeroed;
    }

    public synchronized void resetIfAtLimit() {
        if (elevatorTalon.getForwardLimitSwitch()) {
            elevatorTalon.resetEncoder();
            elevatorPosEnable = elevatorTalon.getPosition();
            mHasBeenZeroed = true;
        }
    }

    @Override
    public void registerEnabledLoops(Looper enabledLooper) {
        Loop mLoop = new Loop() {

            @Override
            public void onStart(double timestamp) {
                synchronized (Elevator.this) {
                    elevatorPosEnable = elevatorTalon.getPosition();
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
                    elevatorSafetyCheck();
                    updateRollers();
                    switch (RobotState.mElevatorControlState) {
                        case MOTION_MAGIC:
                            updateElevatorSetpoint();
                            return;
                        case OPEN_LOOP:
                            return;
                        default:
                            Log.marker("Unexpected elevator control state: " + RobotState.mElevatorControlState);
                            break;
                    }
                }
            }

            @Override
            public void onStop(double timestamp) {
                setIntakeRollers(0);
                mCSVWriter.flush();
            }
        };
        enabledLooper.register(mLoop);
    }

    public void setEnable() {
        elevatorPosEnable = elevatorTalon.getPosition();
        RobotState.mElevatorState = ElevatorState.ENABLE;
    }

    public void changeSafety() {
        elevatorSafety = !elevatorSafety;
        elevatorTalon.setLimitEnabled(elevatorSafety);
    }

    private void updateDebugOutput(double timestamp) {
        mDebug.controlMode = RobotState.mElevatorControlState.toString();
        mDebug.output = elevatorTalon.getPercentOutput();
        mDebug.position = elevatorTalon.getPosition();
        mDebug.velocity = elevatorTalon.getSpeed();
        mDebug.setpoint = RobotState.mElevatorState.state;
        mDebug.timestamp = timestamp;
        mDebug.current = elevatorTalon.getCurrentOutput();
    }

    private void updateElevatorSetpoint() {
        if (RobotState.mElevatorState.equals(ElevatorState.ENABLE)) {
            elevatorTalon.set(ControlMode.MotionMagic, MkMath.angleToNativeUnits(elevatorPosEnable), true);
        } else {
            elevatorTalon.set(ControlMode.MotionMagic, MkMath.angleToNativeUnits(RobotState.mElevatorState.state), true);
        }
    }

    private void elevatorSafetyCheck() {
        if (!elevatorTalon.isEncoderConnected()) {
            if (disCon) {
                if (Timer.getFPGATimestamp() - startDis > 0.5) {
                    RobotState.mElevatorControlState = ElevatorControlState.OPEN_LOOP;
                    disCon = false;
                    startDis = 0;
                }
            } else {
                disCon = true;
                startDis = Timer.getFPGATimestamp();
            }
            Log.marker("Elevator Encoder Not Connected");
        } else {
            if (disCon) {
                disCon = false;
                startDis = 0;
                elevatorTalon.resetEncoder();
                Timer.delay(0.05);
                setEnable();
                RobotState.mElevatorControlState = ElevatorControlState.MOTION_MAGIC;
            }
        }

        if (elevatorTalon.getCurrentOutput() > Constants.ELEVATOR.MAX_SAFE_CURRENT && elevatorSafety) {
            Log.marker("Unsafe Current " + elevatorTalon.getCurrentOutput() + " Amps");
            RobotState.mElevatorControlState = ElevatorControlState.OPEN_LOOP;
        }
    }

    public void updateRollers() {
        leftIntakeRollerTalon.set(ControlMode.PercentOutput, rollerSetpoint);
        rightIntakeRollerTalon.set(ControlMode.PercentOutput, rollerSetpoint);
    }

    public void setOpenLoop(double output) {
        elevatorTalon.set(ControlMode.PercentOutput, output, true);
    }

    public void setIntakeRollers(double output) {
        rollerSetpoint = output;
    }

    public static class ElevatorDebugOutput {

        public double timestamp;
        public String controlMode;
        public double output;
        public double position;
        public double velocity;
        public double setpoint;
        public double current;
    }

    private static class InstanceHolder {

        private static final Elevator mInstance = new Elevator();
    }
}
