package com.halfplatepoha.frnds.db.models;

import com.halfplatepoha.frnds.db.*;
import com.halfplatepoha.frnds.db.IDbConstants;
import com.halfplatepoha.frnds.detail.IDetailsConstants;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by surajkumarsau on 21/09/16.
 */
public class Message extends RealmObject {

    private String      msgBody;
    private int         userType;
    private long        msgTimestamp;
    private int         msgType;

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(@IDetailsConstants.UserType int userType) {
        this.userType = userType;
    }

    public long getMsgTimestamp() {
        return msgTimestamp;
    }

    public void setMsgTimestamp(long msgTimestamp) {
        this.msgTimestamp = msgTimestamp;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(@IDbConstants.MessageType int msgType) {
        this.msgType = msgType;
    }

    public static class Builder {
        private String      msgBody;
        private int         userType;
        private long        msgTimestamp;
        private int         msgType;

        public Builder setMsgBody(String msgBody) {
            this.msgBody = msgBody;
            return this;
        }

        public Builder setUserType(@IDetailsConstants.UserType int userType) {
            this.userType = userType;
            return this;
        }

        public Builder setMsgTimestamp(long msgTimestamp) {
            this.msgTimestamp = msgTimestamp;
            return this;
        }

        public Builder setMsgType(@IDbConstants.MessageType int msgType) {
            this.msgType = msgType;
            return this;
        }

        public Message build() {
            Message message = new Message();
            message.setMsgType(msgType);
            message.setUserType(userType);
            message.setMsgBody(msgBody);
            message.setMsgTimestamp(msgTimestamp);

            return message;
        }
    }
}
