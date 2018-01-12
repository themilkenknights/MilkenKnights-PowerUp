package frc.team1836.robot.auto.modes;

import frc.team1836.robot.auto.actions.DrivePathAction;
import frc.team1836.robot.util.auto.AutoModeBase;
import frc.team1836.robot.util.auto.AutoModeEndedException;
import frc.team254.lib.trajectory.io.TextFileDeserializer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;

public class DriveStraightMode extends AutoModeBase {


	@Override
	protected void routine() throws AutoModeEndedException {
		//TODO: Deserialize txt trajectories

		//runAction(new DrivePathAction(textFileDeserializer.deserialize(s.toString())));
	}

}
