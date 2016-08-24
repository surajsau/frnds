package com.halfplatepoha.frnds;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by surajkumarsau on 24/08/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackResponse {

    private String stream_url;

    public String getStream_url() {
        return stream_url;
    }

    public void setStream_url(String stream_url) {
        this.stream_url = stream_url;
    }
}
