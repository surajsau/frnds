package com.halfplatepoha.frnds.fcm;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.halfplatepoha.frnds.FrndsLog;

/**
 * Created by surajkumarsau on 26/08/16.
 */
public class NotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        FrndsLog.e("From: " + remoteMessage.getFrom());
        FrndsLog.e("Message payload: " + remoteMessage.getData());
        FrndsLog.e("Message body: " +  remoteMessage.getNotification().getBody());
    }
}
