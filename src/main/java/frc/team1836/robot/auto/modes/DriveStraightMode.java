package frc.team1836.robot.auto.modes;

import frc.team1836.robot.auto.actions.DrivePathAction;
import frc.team1836.robot.util.auto.AutoModeBase;
import frc.team1836.robot.util.auto.AutoModeEndedException;
import frc.team254.lib.trajectory.io.TextFileDeserializer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DriveStraightMode extends AutoModeBase {


	@Override
	protected void routine() throws AutoModeEndedException {
		TextFileDeserializer textFileDeserializer = new TextFileDeserializer();
		try {
			String contents = new String(Files.readAllBytes(Paths.get("../paths/StraightPath.txt")));
			runAction(new DrivePathAction(textFileDeserializer.deserialize(contents)));
		} catch (IOException io) {
			System.out.println(io.toString());
		}
	}

}
