package com.halfplatepoha.frnds.detail;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by surajkumarsau on 07/09/16.
 */
public interface IDetailsConstants {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_ME, TYPE_FRND})
    @interface UserType {}

    int TYPE_ME = 1;
    int TYPE_FRND = 2;

    String SOURCE_TYPE = "source_type";
    String SOURCE_FAB = "source_fab";
    String SOURCE_LIST = "source_list";

    int SONG_DETAILS_REQUEST = 101;

    String TRACK_URL = "track_url";
    String TRACK_IMAGE_URL = "track_image_url";
    String TRACK_TITLE = "track_title";
    String TRACK_ARTIST = "track_artist";
    String FRND_ID = "frnd_id";

    String SERVICE_STREAM_URL = "service_stream_url";
    String NOTIFICATION_SERVICE_TRACK_TITLE = "notification_track_title";

    String SONG_SHARE_TAG = "song_share";

    String IMG_LARGE_SUFFIX = "large";
    String IMG_500_X_500_SUGGIX = "t500x500";
}
