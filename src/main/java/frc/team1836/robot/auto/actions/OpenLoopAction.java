package frc.team1836.robot.auto.actions;

import edu.wpi.first.wpilibj.Timer;
import frc.team1836.robot.RobotState;
import frc.team1836.robot.RobotState.DriveControlState;
import frc.team1836.robot.subsystems.Drive;
import frc.team1836.robot.util.auto.Action;
import frc.team1836.robot.util.state.DriveSignal;

public class OpenLoopAction implements Action {

    private double time;
    private double speed;
    private Timer timer;
    private boolean stop;

    public OpenLoopAction(double time, double speed, boolean stop) {
        this.speed = speed;
        this.time = time;
        timer = new Timer();
        this.stop = stop;
    }

    /**
     * Returns whether or not the code has finished execution. When implementing this interface, this method is used by
     * the runAction method every cycle to know when to stop running the action
     *
     * @return boolean
     */
    @Override
    public boolean isFinished() {
        return timer.get() >= time;
    }

    /**
     * Called by runAction in AutoModeBase iteratively until isFinished returns true. Iterative logic lives in this
     * method
     */
    @Override
    public void update() {
        Drive.getInstance().setOpenLoop(new DriveSignal(speed, speed));
    }

    /**
     * Run code once when the action finishes, usually for clean up
     */
    @Override
    public void done() {
        if (stop) {
            Drive.getInstance().setOpenLoop(DriveSignal.BRAKE);
        }

    }

    /**
     * Run code once when the action is started, for set up
     */
    @Override
    public void start() {
        timer.start();
        RobotState.mDriveControlState = DriveControlState.OPEN_LOOP;
    }


}
