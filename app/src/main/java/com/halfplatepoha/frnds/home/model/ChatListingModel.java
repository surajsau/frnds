package com.halfplatepoha.frnds.home.model;

import com.halfplatepoha.frnds.db.models.Chat;

/**
 * Created by surajkumarsau on 14/02/17.
 */

public class ChatListingModel {

    private String frndName;
    private int frndPosition;
    private String frndId;
    private String lastMessage;
    private String frndImageUrl;
    private long lastTimestamp;
    private boolean isRead;

    public ChatListingModel(Chat chat) {
        this.frndName = chat.getFrndName();
        this.frndId = chat.getFrndId();
        this.frndPosition = chat.getFrndPosition();

        if(chat.getFrndLastMessage() != null) {
            this.lastMessage = chat.getFrndLastMessage().getMsgBody();
        }

        this.lastTimestamp = chat.getFrndLastMessageTimestamp();
        this.frndImageUrl = chat.getFrndImageUrl();
    }

    public String getFrndName() {
        return frndName;
    }

    public void setFrndName(String frndName) {
        this.frndName = frndName;
    }

    public int getFrndPosition() {
        return frndPosition;
    }

    public void setFrndPosition(int frndPosition) {
        this.frndPosition = frndPosition;
    }

    public String getFrndId() {
        return frndId;
    }

    public void setFrndId(String frndId) {
        this.frndId = frndId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getFrndImageUrl() {
        return frndImageUrl;
    }

    public void setFrndImageUrl(String frndImageUrl) {
        this.frndImageUrl = frndImageUrl;
    }

    public long getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

}
