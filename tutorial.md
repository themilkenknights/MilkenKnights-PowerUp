# Milken Knights Programming Tutorial
## Table of Contents
- [VS Code Setup](#VS-Code-Setup)
- [Robot.java Structure](#Robot.java-structure)
- [Motors, Joysticks, and CAN](#Motors,-Joysticks,-and-CAN)
- [Basic Teleop Control](#Basic-Teleop-Control)

## **VS Code Setup**
To get started, [go to the WPILIB Docs](https://wpilib.screenstepslive.com/s/currentCS/m/79833/l/932382-installing-vs-code) and follow the instructions to download Visual Studio Code and instal the WPILIB/Java Extensions (Java only, not C++)


## **Robot.java structure**
The main file where all robot code is called is the Robot.java file. The file acts similar to a 'main' file or function in many programming languages except in this case, we are relying on a library, WPILIB, to call the methods that we override at specific times. The IterativeRobot class that we will be extending has several methods that provide extra functionality such as running code in any robot state, in order to perform robot testing, or only when the robot is in a 'disabled state'. The three main states or modes that are important to understand are teleop, autonomous, and disabled. The first two states corrospond to periods in a match where the robot is either controlled by a human driver, or under autonomous control with no human interation. The disabled state refers to any period of time when the robot's motors and actuators are disabled which occurs before and after a match, in the brief transition between teleop and autonomous modes, and any other time during testing. 

The Iterative Robot base class assists with the most common code structure by handling the state transitions and looping in the base class instead of in the robot code. For each state (autonomous, teleop, disabled, test) there are two methods that are called:

Init methods - The init method for a given state is called each time the corresponding state is entered (for example, a transition from disabled to teleop will call teleopInit()). Any initialization code or resetting of variables between modes should be placed here.
Periodic methods - The periodic method for a given state is called each time the robot receives a Driver Station packet in the corresponding state, approximately every 20ms. This means that all of the code placed in each periodic method should finish executing in 20ms or less. The idea is to put code here that gets values from the driver station and updates the motors. You can read the joysticks and other Driver Station inputs more often, but you’ll only get the previous value until a new update is received. By synchronizing with the received updates your program will put less of a load on the roboRIO CPU leaving more time for other tasks such as camera processing.

```
public class RobotTemplate extends IterativeRobot {
    /*
     * Robot-wide initialization code should go here. Users should override this
     * method for default Robot-wide initialization which will be called when the
     * robot is first powered on. It will be called exactly one time.
     */
    public void robotInit() {

    }

    public void autonomousInit() {

    }

    public void autonomousPeriodic() {

    }

    public void teleopInit() {

    }

    public void teleopPeriodic() {

    }
}
```

## **Motors, Joysticks, and CAN**

## **Basic Teleop Control**