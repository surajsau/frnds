package com.halfplatepoha.frnds.models;

import com.halfplatepoha.frnds.detail.IDetailsConstants;

/**
 * Created by surajkumarsau on 07/09/16.
 */
public class Message {

    private @IDetailsConstants.UserType
    int userType;
    private String name;
    private String imgUrl;
    private String message;
    private long timestamp;

    public @IDetailsConstants.UserType int getUserType() {
        return userType;
    }

    public void setUserType(@IDetailsConstants.UserType int userType) {
        this.userType = userType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public static class Builder {

        private @IDetailsConstants.UserType int userType;
        private String name;
        private String imgUrl;
        private String message;
        private long timestamp;

        public Builder setUserType(@IDetailsConstants.UserType int userType) {
            this.userType = userType;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setTimestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Message build() {
            Message msg = new Message();
            msg.setImgUrl(imgUrl);
            msg.setMessage(message);
            msg.setUserType(userType);
            msg.setTimestamp(timestamp);
            msg.setName(name);

            return msg;
        }
    }
}
