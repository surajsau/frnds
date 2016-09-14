package com.halfplatepoha.frnds;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.facebook.FacebookSdk;

/**
 * Created by surajkumarsau on 26/08/16.
 */
public class FrndsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(this);
        FrndsPreference.init(this, IConstants.PREFERNCE_FILE);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}
