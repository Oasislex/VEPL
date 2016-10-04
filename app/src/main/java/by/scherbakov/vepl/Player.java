package by.scherbakov.vepl;

/**
 * Created by SBT-Sherbakov-AI on 03.05.2016.
 */
public class Player implements Comparable<Player>{

    private int playerId;
    private int elementId;
    private String firstName;
    private String lastName;
    private String photo;
    private String news;
    private int teamId;
    private int positionCode;

    public Player(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getElementId() {
        return elementId;
    }

    public void setElementId(int elementId) {
        this.elementId = elementId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getNews() {
        return news;
    }

    public void setNews(String news) {
        this.news = news;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(int positionCode) {
        this.positionCode = positionCode;
    }


    @Override
    public int compareTo(Player player) {
        int result = this.positionCode - player.getPositionCode();
        if (result !=0){
            return result;
        }
        return this.lastName.compareTo(player.getLastName());
    }
}
