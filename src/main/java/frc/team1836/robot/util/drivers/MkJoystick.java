package frc.team1836.robot.util.drivers;

import edu.wpi.first.wpilibj.Joystick;
import java.util.HashMap;

public class MkJoystick extends Joystick {

	private final HashMap<Integer, MkButton> buttons;

	/**
	 * Create a new MkJoystick.
	 */
	public MkJoystick(final int port) {
		super(port);

		buttons = new HashMap<Integer, MkButton>();
	}

	/**
	 * Gets a button of the joystick. Creates a new Button object if one did not already exist.
	 *
	 * @param button The raw button number of the button to get
	 * @return The button
	 */
	public MkButton getButton(final int button, final String name) {
		if (!buttons.containsKey(button)) {
			buttons.put(button, new MkButton(this, button, name));
		}
		return buttons.get(button);
	}

}
