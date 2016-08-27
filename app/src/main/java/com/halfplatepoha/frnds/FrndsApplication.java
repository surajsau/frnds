package com.halfplatepoha.frnds;

import android.app.Application;

import com.batch.android.Batch;
import com.batch.android.Config;

/**
 * Created by surajkumarsau on 26/08/16.
 */
public class FrndsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Batch.Push.setGCMSenderId("848984009339");

        Batch.setConfig(new Config("DEV57C00B57C66C84EAE27F206E5EF")); // devloppement
        // Batch.setConfig(new Config("57C00B57C21C0486831A69B3508F0E")); // live
    }
}
