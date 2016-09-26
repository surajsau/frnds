package com.halfplatepoha.frnds.fcm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by surajkumarsau on 27/08/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationModel {

    private String friendId;
    private String friendName;
    private String trackName;
    private String message;
    private String trackUrl;
    private String trackId;
    private String type;

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTrackUrl() {
        return trackUrl;
    }

    public void setTrackUrl(String trackUrl) {
        this.trackUrl = trackUrl;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
