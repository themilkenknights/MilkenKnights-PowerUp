package frc.team1836.robot.auto.modes;

import frc.team1836.robot.auto.actions.DrivePathAction;
import frc.team1836.robot.auto.paths.StraightPath;
import frc.team1836.robot.util.auto.AutoModeBase;
import frc.team1836.robot.util.auto.AutoModeEndedException;

public class DriveStraightMode extends AutoModeBase {


	@Override
	protected void routine() throws AutoModeEndedException {
		//TODO: Deserialize txt trajectories
		//runAction(new DrivePathAction(new StraightPath()));
	/*	TextFileDeserializer textFileDeserializer = new TextFileDeserializer();
		String contents;
		try {

			contents = new String(Files.readAllBytes(Paths.get("../paths/StraightPath.txt")));
			runAction(new DrivePathAction(textFileDeserializer.deserialize(contents)));
		} catch (IOException io) {
			System.out.println(io.toString());
		} */
	runAction(new DrivePathAction(new StraightPath()));

	}

}
