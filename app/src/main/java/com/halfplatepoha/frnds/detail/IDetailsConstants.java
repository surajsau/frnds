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
}
