package com.halfplatepoha.frnds.db.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by surajkumarsau on 21/09/16.
 */
public class Song extends RealmObject {

    @PrimaryKey
    private long         songId;

    private String      songUrl;
    private String      songImgUrl;
    private String      songTitle;
    private String      songArtist;
    private long        frndId;
    private long        songTimestamp;

    public long getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public String getSongImgUrl() {
        return songImgUrl;
    }

    public void setSongImgUrl(String songImgUrl) {
        this.songImgUrl = songImgUrl;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public long getFrndId() {
        return frndId;
    }

    public void setFrndId(long frndId) {
        this.frndId = frndId;
    }

    public long getSongTimestamp() {
        return songTimestamp;
    }

    public void setSongTimestamp(long songTimestamp) {
        this.songTimestamp = songTimestamp;
    }
}
