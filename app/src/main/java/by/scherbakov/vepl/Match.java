package by.scherbakov.vepl;

/**
 * Created by SBT-Sherbakov-AI on 22.08.2016.
 */
public class Match {

    private String timestamps;
    private String captionsHome;
    private String imageIdsHome;
    private String scores;
    private String captionsAway;
    private String imageIdsAway;
    private int matchesId;

    public Match() {
    }

    public Match(String timestamps, String captionsHome, String imageIdsHome, String scores, String captionsAway, String imageIdsAway, int matchesId) {
        this.timestamps = timestamps;
        this.captionsHome = captionsHome;
        this.imageIdsHome = imageIdsHome;
        this.scores = scores;
        this.captionsAway = captionsAway;
        this.imageIdsAway = imageIdsAway;
        this.matchesId = matchesId;
    }

    public String getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(String timestamps) {
        this.timestamps = timestamps;
    }

    public String getCaptionsHome() {
        return captionsHome;
    }

    public void setCaptionsHome(String captionsHome) {
        this.captionsHome = captionsHome;
    }

    public String getImageIdsHome() {
        return imageIdsHome;
    }

    public void setImageIdsHome(String imageIdsHome) {
        this.imageIdsHome = imageIdsHome;
    }

    public String getScores() {
        return scores;
    }

    public void setScores(String scores) {
        this.scores = scores;
    }

    public String getCaptionsAway() {
        return captionsAway;
    }

    public void setCaptionsAway(String captionsAway) {
        this.captionsAway = captionsAway;
    }

    public String getImageIdsAway() {
        return imageIdsAway;
    }

    public void setImageIdsAway(String imageIdsAway) {
        this.imageIdsAway = imageIdsAway;
    }

    public int getMatchesId() {
        return matchesId;
    }

    public void setMatchesId(int matchesId) {
        this.matchesId = matchesId;
    }
}


