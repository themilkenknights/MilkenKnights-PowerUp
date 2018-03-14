package frc.team1836.robot.util.drivers;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;

public class MkXboxControllerButton {

    private final MkXboxController joystick;
    private final int rawButton;
    private final String buttonName;
    private boolean lastState;

    public MkXboxControllerButton(final MkXboxController joystick, final int rawButton,
                                  final String buttonName) {
        this.joystick = joystick;
        this.rawButton = rawButton;
        this.buttonName = buttonName;
    }

    /**
     * Returns true if the button is pressed and this is the first time it is being run.
     */
    public boolean isPressed() {
        final boolean isPressed = !lastState && joystick.getRawButton(rawButton);
        update();
        if (isPressed) {
            System.out
                    .println("[Joystick] " + joystick.getPort() + "\t[Button]" + rawButton + "\t Pressed ( " + buttonName + " )");
        }
        return isPressed;
    }

    /**
     * Update the last state of the button.
     */
    private void update() {
        lastState = joystick.getRawButton(rawButton);
    }

    /**
     * Returns true when the button is released.
     */
    public boolean isReleased() {
        final boolean isPressed = lastState && joystick.getRawButton(rawButton);
        update();
        return isPressed;
    }

    /**
     * Returns true when the button is being held.
     */
    public boolean isHeld() {
        update();
        return joystick.getRawButton(rawButton);
    }

    public int getRawButton() {
        return rawButton;
    }

    public XboxController getJoystick() {
        return joystick;
    }

    public boolean isJoystickConnected() {
        return joystick.getButtonCount() > 0;
    }

}
