package frc.team1836.robot.util.auto;

import frc.team1836.robot.util.logging.CrashTrackingRunnable;

/**
 * This class selects, runs, and stops (if necessary) a specified autonomous mode.
 */
public class AutoModeExecuter {

	private AutoModeBase m_auto_mode;
	private Thread m_thread = null;

	/*public void setDesiredAuto(AutoModeBase.AutoPosition pos, AutoModeBase.AutoAction act) {

		String gameData;
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		if(gameData.charAt(0) == 'L')
		{
			//Put left auto code here
		} else {
			//Put right auto code here
		}

		if (act.equals(AutoAction.STANDSTILL)) {
			m_auto_mode = new StandStillMode();
		}

		switch (pos) {
			case LEFT:
				if(act.equals(AutoAction.SWITCH)){
					m_auto_mode = new LeftSwitchMode();
				}



		}


	} */

	public void setAutoMode(AutoModeBase new_auto_mode) {
		m_auto_mode = new_auto_mode;
	}

	public void start() {
		if (m_thread == null) {
			m_thread = new Thread(new CrashTrackingRunnable() {
				@Override
				public void runCrashTracked() {
					if (m_auto_mode != null) {
						m_auto_mode.run();
					}
				}
			});

			m_thread.start();
		}

	}

	public void stop() {
		if (m_auto_mode != null) {
			m_auto_mode.stop();
		}

		m_thread = null;
	}

}
