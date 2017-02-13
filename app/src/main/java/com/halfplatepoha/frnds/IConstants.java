package com.halfplatepoha.frnds;

import static com.halfplatepoha.frnds.detail.IDetailsConstants.TRACK_ID;

/**
 * Created by surajkumarsau on 24/08/16.
 */
public interface IConstants {
    String SOUNDCLOUD_BASE_URL = "http://api.soundcloud.com/";
    String FRNDS_BASE_URL = "http://192.168.0.5:50000/";

    String API_KEY_PARAM = "client_id";
    String API_KEY_VALUE = "e959350cd82cd9a9371893951600e1af";

    String CONTENT_TYPE = "Content-Type";
    String APPLICATION_JSON = "application/json";

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

    int PAGE_SIZE = 10;

    int SCREEN_CHAT = 1001;
    int SCREEN_LISTING = 1002;
    int SCREEN_NONE = 1003;

    String CHAT_BROADCAST = "sync_chat_broadcast";
    String SONG_STATUS_BROADCAST = "sync_song_status_broadcast";
    String FRND_MESSAGE = "frnd_message";
    String FRND_MESSAGE_TYPE = "frnd_message_type";
    String FRND_ID = "frnd_id";
    String FRND_TRACK_ID = "frnd_track_id";
    String FRND_TRACK_TITLE = "frnd_track_title";
    String FRND_TRACK_URL = "frnd_track_image_url";
}
