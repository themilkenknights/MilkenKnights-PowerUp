# MilkenKnights-PowerUp
##### 2018 Competition Season Code

## Installation Instructions:
1. Install Java JDK 8 (macOS or Windows x64)
2. (ONLY ON WINDOWS) Go to Control Panel -> System and Security -> System -> Advanced System Settings -> Enviorment Variables. Add a new system variable called "JAVA_HOME" -> Click on Browse Directory and go to "C:\Program Files\Java\jdk1.8.0_151" (Java Version Number may differ)
2. Install Intellij IDEA (Either Version)
3. Go to File -> Settings -> Editor -> Code Style -> Java. Click the Gear Icon -> Import Scheme -> IntelliJ XML file -> then browse to the repo and select MilkenKnightsStyle.xml
4. Go to File -> Open and open the repo
A box will pop up -> Select Auto-Import and select JAVA_HOME if needed -> Click OK
5. WAIT UNTIL THE PROGRESS BAR AT THE BOTTOM IS FULL COMPLETE -> It will take a minute to download everything
6. Go into the terminal either through IntelliJ or through Terminal on Mac/Command Prompt on Windows, in the Project direcory and type 'gradlew build', and then 'gradlew idea'
## Code Deploy Instructions:
1. In Intellij, Click Terminal at the bottom right hand corner (You may have to click a square with a line below it and then open terminal). If the terminal option is unavailable go to step 3.
2. Type "gradlew deploy" (Without the quotes). The robot must be connected over usb, ethernet, or wifi
2. Install Intellij IDEA (Either Version)
3. Open Command Prompt or Terminal and navigate to the project directory ('cd ..' goes up a directory level and 'cd [directory_name' goes into a directory). Use 'dir' on windows or 'ls' on mac or linux to view the current directory
4. (EASIER) - Press the green run button in intelliJ (must show deploy next to project name to work)

## Package Functions
- frc.team1836.subsystems

  Subsystems are consolidated into one central class per subsystem, all of which implement the Subsystem abstract class. Each Subsystem uses state machines for control.
  Each Subsystem is also a singleton, meaning that there is only one instance of each Subsystem class. To modify a subsystem, one would get the instance of the subsystem and change its desired state. The Subsystem class will work on setting the desired state.
	- Drive controls the Drivetrain
	- Input holds HID (Human Input Device) objects to receive control input from the Driver Station and control various subsystems during Teleop
	- Superstructure contains any hardware object that does not fit into a subsystem and contains the current robot state to allow for easy system control
	- Subsystem is the interface for all subsystems requiring default methods

- frc.team1836.auto

	Handles the excecution of autonomous routines.  Also contains the auto actions and auto modes packages.

- frc.team1836.auto.actions

	Contains all actions used during the autonomous period (Also can be used during teleop if desired), which all share a common interface, Action (also in this package). Examples include driving a path, moving an arm, etc. Routines interact with the Subsystems, which interact with the hardware.

- frc.team1836.auto.modes

	Contains all autonomous modes. Autonomous modes consist of a list of autonomous actions excecuted in a certain order.

- frc.team1836.util.loops

	Loops are routines that run periodically on the robot, generally updating subsystems. All Loops implement the Loop interface and are handled (started, stopped, added) by the Looper class, which runs at 200 Hz.
	The Robot class has one main Looper, mEnabledLooper, that runs all loops when the robot is enabled.

- frc.team1836.auto.paths

	Contains all paths that the robot drives during autonomous mode. Each path is pregenerated on the Driver Station to save compute time on the RoboRIO.

## General Info
- UNLESS OTHERWISE NOTED BY RAW/NATIVE/RPM, ALL POSITION UNITS ARE IN INCHES and DEGREES
- ALL VELOCITY UNITS ARE IN INCHES PER SECOND and DEGREES PER SECOND
- DIST DENOTES POSITION AND ANG DENOTES ANGLE
- ID TYPICALLY DENOTES A CAN ID
- ALL PID CONSTANTS SENT TO THE TALON ARE IN NATIVE UNITS (4096 Per Rotation)
