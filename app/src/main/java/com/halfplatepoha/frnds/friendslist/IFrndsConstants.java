package com.halfplatepoha.frnds.friendslist;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by surajkumarsau on 08/09/16.
 */
public interface IFrndsConstants {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATUS_PLAYING, STATUS_TEXTING})
    @interface UserStatus{}

    int STATUS_PLAYING = 1;
    int STATUS_TEXTING = 2;

    String FRIEND_AVATAR_TRANSITION = "friend_avatar_transition";
    String FRIEND_NAME_TRANSITION = "friend_name_transition";
}
