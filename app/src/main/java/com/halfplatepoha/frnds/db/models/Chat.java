package com.halfplatepoha.frnds.db.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by surajkumarsau on 21/09/16.
 */
public class Chat extends RealmObject {

    @PrimaryKey
    private String         frndId;

    private int            frndPosition;

    private String         frndName;
    private String         frndImageUrl;

    public RealmList<Song> frndSongs;
    public RealmList<Message> frndMessages;

    public String           frndLastMessage;

    public String getFrndId() {
        return frndId;
    }

    public void setFrndId(String frndId) {
        this.frndId = frndId;
    }

    public RealmList<Song> getFrndSongs() {
        return frndSongs;
    }

    public void setFrndSongs(RealmList<Song> frndSongs) {
        this.frndSongs = frndSongs;
    }

    public RealmList<Message> getFrndMessages() {
        return frndMessages;
    }

    public void setFrndMessages(RealmList<Message> frndMessages) {
        this.frndMessages = frndMessages;
    }

    public String getFrndName() {
        return frndName;
    }

    public void setFrndName(String frndName) {
        this.frndName = frndName;
    }

    public String getFrndImageUrl() {
        return frndImageUrl;
    }

    public void setFrndImageUrl(String frndImageUrl) {
        this.frndImageUrl = frndImageUrl;
    }

    public String getFrndLastMessage() {
        return frndLastMessage;
    }

    public void setFrndLastMessage(String frndLastMessage) {
        this.frndLastMessage = frndLastMessage;
    }

    public int getFrndPosition() {
        return frndPosition;
    }

    public void setFrndPosition(int frndPosition) {
        this.frndPosition = frndPosition;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj != null) {
            Chat chat = (Chat)obj;
            return frndId.equalsIgnoreCase(chat.getFrndId());
        }
        return false;
    }
}
