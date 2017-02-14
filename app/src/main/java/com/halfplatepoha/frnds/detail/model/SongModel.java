package com.halfplatepoha.frnds.detail.model;

import com.halfplatepoha.frnds.db.models.Song;

/**
 * Created by surajkumarsau on 14/02/17.
 */

public class SongModel {
    private String trackUrl;
    private String trackName;
    private long trackTimeStamp;
    private String trackUser;
    private String trackImageUrl;

    public String getTrackUrl() {
        return trackUrl;
    }

    public void setTrackUrl(String trackUrl) {
        this.trackUrl = trackUrl;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public long getTrackTimeStamp() {
        return trackTimeStamp;
    }

    public void setTrackTimeStamp(long trackTimeStamp) {
        this.trackTimeStamp = trackTimeStamp;
    }

    public String getTrackUser() {
        return trackUser;
    }

    public void setTrackUser(String trackUser) {
        this.trackUser = trackUser;
    }

    public String getTrackImageUrl() {
        return trackImageUrl;
    }

    public void setTrackImageUrl(String trackImageUrl) {
        this.trackImageUrl = trackImageUrl;
    }
}
