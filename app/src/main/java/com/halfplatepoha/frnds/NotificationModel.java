package com.halfplatepoha.frnds;

/**
 * Created by surajkumarsau on 27/08/16.
 */
public class NotificationModel {

    private String friendName;
    private String trackName;
    private boolean isLastSongPlayedByUser;
    private String message;
    private boolean male;

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public boolean isLastSongPlayedByUser() {
        return isLastSongPlayedByUser;
    }

    public void setLastSongPlayedByUser(boolean lastSongPlayedByUser) {
        isLastSongPlayedByUser = lastSongPlayedByUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isMale() {
        return male;
    }

    public void setMale(boolean male) {
        this.male = male;
    }
}
