package frc.team1836.robot.subsystems;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team1836.robot.Constants;
import frc.team1836.robot.RobotState;
import frc.team1836.robot.RobotState.DriveControlState;
import frc.team1836.robot.RobotState.MatchState;
import frc.team1836.robot.util.drivers.MkLED;
import frc.team1836.robot.util.drivers.MkLED.LEDColors;
import frc.team1836.robot.util.loops.Loop;
import frc.team1836.robot.util.loops.Looper;
import frc.team1836.robot.util.other.Subsystem;

public class Superstructure extends Subsystem {

	private MkLED mkLED;
	private boolean hPSignal;
	private boolean turnOffLED;
	private int ledNum;

	public Superstructure() {
		mkLED = new MkLED(Constants.SUPERSTRUCTURE.CANIFIER_ID);
		hPSignal = false;
		turnOffLED = false;
		ledNum = 0;
	}

	public static Superstructure getInstance() {
		return InstanceHolder.mInstance;
	}

	@Override
	public void outputToSmartDashboard() {
		SmartDashboard.putString("Robot State", RobotState.mMatchState.toString());

	}

	@Override
	public void slowUpdate() {
		updateLEDStrip();
	}

	public void toggleSignal() {
		hPSignal = !hPSignal;
	}

	public void toggleLEDOff() {
		turnOffLED = !turnOffLED;
	}

	@Override
	public void checkSystem() {

	}

	@Override
	public void registerEnabledLoops(Looper enabledLooper) {
		Loop mLoop = new Loop() {

			@Override
			public void onStart(double timestamp) {
				synchronized (Superstructure.this) {
				}
			}

			@Override
			public void onLoop(double timestamp) {
				synchronized (Superstructure.this) {

				}
			}

			@Override
			public void onStop(double timestamp) {

			}
		};
		enabledLooper.register(mLoop);
	}

	private synchronized void updateLEDStrip() {
		//	mkLED.set_rgb(LEDColors.BLUE);

		if (turnOffLED) {
			mkLED.set_rgb(LEDColors.OFF);
			return;
		} else if (hPSignal) {
			mkLED.set_rgb(LEDColors.GREEN);
		} else if (RobotState.mDriveControlState == DriveControlState.VELOCITY_SETPOINT
				&& RobotState.mMatchState != MatchState.AUTO) {
			if(ledNum <= 15){
				mkLED.set_rgb(LEDColors.RED);
			} else if(ledNum <= 30){
				mkLED.set_rgb(LEDColors.OFF);
			} else{
				mkLED.set_rgb(LEDColors.RED);
				ledNum = 0;
			}
			ledNum++;
		} else if (RobotState.mMatchState == MatchState.DISABLED) {
			mkLED.set_rgb(MkLED.LEDColors.PURPLE);
		} else if (RobotState.matchData.alliance == DriverStation.Alliance.Red) {
			mkLED.set_rgb(LEDColors.RED);
		} else if (RobotState.matchData.alliance == DriverStation.Alliance.Blue) {
			mkLED.set_rgb(LEDColors.BLUE);
		} else {
			mkLED.set_rgb(LEDColors.BLUE);
		}


		/*	switch (RobotState.mMatchState) {
				case DISABLED:
					mkLED.set_rgb(LEDColors.BLUE);
				case AUTO:
					mkLED.setPulse(MkLED.LEDColors.BLUE, MkLED.LEDColors.OFF, 1);
				case TELEOP:
					if (hPSignal) {
						mkLED.set_rgb(MkLED.LEDColors.GREEN);
					} else if (RobotState.matchData.alliance == DriverStation.Alliance.Red) {
						mkLED.setPulse(MkLED.LEDColors.BLUE, MkLED.LEDColors.OFF, 1);
					} else if (RobotState.matchData.alliance == DriverStation.Alliance.Blue) {
						mkLED.setPulse(MkLED.LEDColors.RED, MkLED.LEDColors.OFF, 1);
					} else {
						mkLED.setPulse(MkLED.LEDColors.PURPLE, MkLED.LEDColors.OFF, 1);
					}
				case TEST:
					mkLED.setPulse(MkLED.LEDColors.ORANGE, MkLED.LEDColors.RED, 1);
			} */

	}


	private static class InstanceHolder {

		private static final Superstructure mInstance = new Superstructure();

	}
}
