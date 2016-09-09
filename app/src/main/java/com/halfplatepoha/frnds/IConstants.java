package com.halfplatepoha.frnds;

/**
 * Created by surajkumarsau on 24/08/16.
 */
public interface IConstants {
    String SOUNDCLOUD_BASE_URL = "http://api.soundcloud.com/";
    String FRNDS_BASE_URL = "http://frnds-server.herokuapp.com/";

    String API_KEY_PARAM = "client_id";
    String API_KEY_VALUE = "e959350cd82cd9a9371893951600e1af";

    String TRACK_ID = "track_id";
    String ICON_URL = "icon_url";
    String TRACK_TITLE = "track_title";
    String TRACK_URL = "track_url";

    String USER_ID = "user_id";

    String PREFERNCE_FILE = "preference_file";

    int NOTIFICATION_ID = 1;

    int NOTIFICATION_PLAY_PENDING_INTENT_REQUEST = 501;
    int NOTIFICATION_STOP_PLAYING_INTENT_REQUEST = 502;
    int PLAY_NOTIFICATION_ID = 401;
}
