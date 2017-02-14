package com.halfplatepoha.frnds.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by surajkumarsau on 14/02/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetPendingSongsResponse implements Serializable {

    private Body body;

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body implements Serializable {
        private String fbId;
        private ArrayList<Message> messages;

        public String getFbId() {
            return fbId;
        }

        public void setFbId(String fbId) {
            this.fbId = fbId;
        }

        public ArrayList<Message> getMessages() {
            return messages;
        }

        public void setMessages(ArrayList<Message> messages) {
            this.messages = messages;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Message implements Serializable {
            private String message;
            private String timestamp;

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            public String getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(String timestamp) {
                this.timestamp = timestamp;
            }
        }
    }
}
