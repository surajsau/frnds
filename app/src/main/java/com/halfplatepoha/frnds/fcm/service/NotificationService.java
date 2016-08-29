package com.halfplatepoha.frnds.fcm.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.halfplatepoha.frnds.FrndsLog;
import com.halfplatepoha.frnds.IConstants;
import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.fcm.NotificationModel;

import java.io.IOException;

/**
 * Created by surajkumarsau on 26/08/16.
 */
public class NotificationService extends FirebaseMessagingService {

    private ObjectMapper mMapper;
    private NotificationManager mManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mMapper = new ObjectMapper();
        mMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        mManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        FrndsLog.e("From: " + remoteMessage.getFrom());
        FrndsLog.e("Message payload: " + remoteMessage.getData());
        FrndsLog.e("Message body: " +  remoteMessage.getNotification().getBody());

        try {
            NotificationModel model = mMapper.readValue(remoteMessage.getNotification().getBody(), NotificationModel.class);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(getMessageFromModel(model)))
                                                    .setSmallIcon(R.mipmap.ic_launcher)
                                                    .setContentTitle("Hey " + model.getName() + " ...")
                                                    .setAutoCancel(true)
                                                    .setDefaults(Notification.DEFAULT_LIGHTS
                                                            | Notification.DEFAULT_SOUND
                                                            | Notification.FLAG_AUTO_CANCEL);

            mManager.notify(IConstants.NOTIFICATION_ID, builder.build());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getMessageFromModel(NotificationModel model) {
        if(model == null)
            return null;

        return model.getFriendName()
                + " is listening to "
                + model.getTrackName()
                + ". Join in!";
    }
}
