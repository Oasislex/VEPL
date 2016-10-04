package by.scherbakov.vepl;

/**
 * Created by User on 24.04.2016.
 */
public class ScorerGoal implements Comparable<ScorerGoal> {

    private String scorerId;
    private String scorerName;
    private int scorerTime;
    private String scorerTimePlus;
    private String scorerType;

    public ScorerGoal(String scorerId, String scorerName, int scorerTime, String scorerTimePlus, String scorerType) {
        this.scorerId = scorerId;
        this.scorerName = scorerName;
        this.scorerTime = scorerTime;
        this.scorerTimePlus = scorerTimePlus;
        this.scorerType = scorerType;
    }

    public int compareTo(ScorerGoal another) {
        return this.getScorerTime()-another.getScorerTime();
    }

    public String getScorerTimePlus() {
        return scorerTimePlus;
    }

    public void setScorerTimePlus(String scorerTimePlus) {
        this.scorerTimePlus = scorerTimePlus;
    }

    public String getScorerId() {
        return scorerId;
    }

    public void setScorerId(String scorerId) {
        this.scorerId = scorerId;
    }

    public String getScorerName() {
        return scorerName;
    }

    public void setScorerName(String scorerName) {
        this.scorerName = scorerName;
    }

    public int getScorerTime() {
        return scorerTime;
    }

    public void setScorerTime(int scorerTime) {
        this.scorerTime = scorerTime;
    }

    public String getScorerType() {
        return scorerType;
    }

    public void setScorerType(String scorerType) {
        this.scorerType = scorerType;
    }


}
