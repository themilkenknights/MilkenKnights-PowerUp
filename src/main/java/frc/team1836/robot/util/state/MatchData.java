package frc.team1836.robot.util.state;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.team1836.robot.AutoChooser;

public class MatchData {
    public static MatchData defaultMatch = new MatchData(DriverStation.MatchType.None, 0, Alliance.Blue, AutoChooser.GameObjectPosition.INVALID, AutoChooser.GameObjectPosition.INVALID);
    public DriverStation.MatchType matchType;
    public int matchNumber;
    public DriverStation.Alliance alliance;
    public AutoChooser.GameObjectPosition switchPosition;
    public AutoChooser.GameObjectPosition scalePosition;

    public MatchData(DriverStation.MatchType matchType, int matchNumber, DriverStation.Alliance alliance, AutoChooser.GameObjectPosition switchPosition, AutoChooser.GameObjectPosition scalePosition) {
        this.matchType = matchType;
        this.matchNumber = matchNumber;
        this.alliance = alliance;
        this.switchPosition = switchPosition;
        this.scalePosition = scalePosition;
    }
}
