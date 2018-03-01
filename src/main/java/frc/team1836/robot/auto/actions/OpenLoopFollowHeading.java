package frc.team1836.robot.auto.actions;

import edu.wpi.first.wpilibj.Timer;
import frc.team1836.robot.subsystems.Drive;
import frc.team1836.robot.util.auto.Action;

public class OpenLoopFollowHeading implements Action {

    double m_t1 = 0; // Time to start deceling
    double m_t2 = 0; // Time to end decel
    double m_start_power = 0;
    double m_end_power = 0;
    private Timer timer;

    public OpenLoopFollowHeading(double start_power, double time_full_on, double end_power, double time_to_decel) {
        m_t1 = time_full_on;
        m_t2 = m_t1 + time_to_decel;
        m_start_power = start_power;
        m_end_power = end_power;
        timer = new Timer();

    }

    /**
     * Returns whether or not the code has finished execution. When implementing this interface, this method is used by
     * the runAction method every cycle to know when to stop running the action
     *
     * @return boolean
     */
    @Override
    public boolean isFinished() {
        return timer.get() >= m_t1 + m_t2;
    }

    /**
     * Called by runAction in AutoModeBase iteratively until isFinished returns true. Iterative logic lives in this
     * method
     */
    @Override
    public void update() {


        if (cur <= m_t1) {
            return Drive.getInstance().s
        } else if (cur > m_t1 && cur <= m_t2) {
            // decel
            double rel_t = cur - m_t1;
            double slope = (m_end_power - m_start_power) / (m_t2 - m_t1);
            return (m_start_power + (slope * rel_t));
        } else {
            return m_end_power;
        }


    }

    /**
     * Run code once when the action finishes, usually for clean up
     */
    @Override
    public void done() {

    }

    /**
     * Run code once when the action is started, for set up
     */
    @Override
    public void start() {

    }
}
