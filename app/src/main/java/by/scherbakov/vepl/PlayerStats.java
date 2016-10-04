package by.scherbakov.vepl;

/**
 * Created by SBT-Sherbakov-AI on 13.05.2016.
 */
public class PlayerStats {


    private String opponent;
    private String minutesPlayed;
    private String goalsScores;
    private String assists;
    private String cleanSheets;
    private String ownGoals;

    public PlayerStats(String opponent, String minutesPlayed, String goalsScores, String assists, String cleanSheets, String ownGoals) {
        this.opponent = opponent;
        this.minutesPlayed = minutesPlayed;
        this.goalsScores = goalsScores;
        this.assists = assists;
        this.cleanSheets = cleanSheets;
        this.ownGoals = ownGoals;
    }



    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public String getMinutesPlayed() {
        return minutesPlayed;
    }

    public void setMinutesPlayed(String minutesPlayed) {
        this.minutesPlayed = minutesPlayed;
    }

    public String getGoalsScores() {
        return goalsScores;
    }

    public void setGoalsScores(String goalsScores) {
        this.goalsScores = goalsScores;
    }

    public String getAssists() {
        return assists;
    }

    public void setAssists(String assists) {
        this.assists = assists;
    }

    public String getCleanSheets() {
        return cleanSheets;
    }

    public void setCleanSheets(String cleanSheets) {
        this.cleanSheets = cleanSheets;
    }

    public String getOwnGoals() {
        return ownGoals;
    }

    public void setOwnGoals(String ownGoals) {
        this.ownGoals = ownGoals;
    }
}
