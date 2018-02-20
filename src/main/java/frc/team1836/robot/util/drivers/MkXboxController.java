package frc.team1836.robot.util.drivers;

import edu.wpi.first.wpilibj.XboxController;

import java.util.HashMap;

public class MkXboxController extends XboxController {

    private final HashMap<Integer, MkXboxControllerButton> buttons;

    /**
     * Create a new MkJoystick.
     */
    public MkXboxController(final int port) {
        super(port);

        buttons = new HashMap<Integer, MkXboxControllerButton>();
    }

    /**
     * Gets a button of the joystick. Creates a new Button object if one did not already exist.
     *
     * @param button The raw button number of the button to get
     * @return The button
     */
    public MkXboxControllerButton getButton(final int button, final String name) {
        if (!buttons.containsKey(button)) {
            buttons.put(button, new MkXboxControllerButton(this, button, name));
        }
        return buttons.get(button);
    }

}
