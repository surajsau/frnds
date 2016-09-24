package com.halfplatepoha.frnds.db;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by surajkumarsau on 21/09/16.
 */
public interface IDbConstants {

    String SONG_ID_KEY = "songId";
    String FRND_ID_KEY = "frndId";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_MESSAGE, TYPE_MUSIC})
    public @interface MessageType{};

    int TYPE_MUSIC = 1;
    int TYPE_MESSAGE = 2;
}
