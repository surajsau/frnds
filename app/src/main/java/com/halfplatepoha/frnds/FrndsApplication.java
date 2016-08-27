package com.halfplatepoha.frnds;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by surajkumarsau on 26/08/16.
 */
public class FrndsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(this);
        FrndsPreference.init(this, IConstants.PREFERNCE_FILE);
    }
}
