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
import com.halfplatepoha.frnds.FrndsPreference;
import com.halfplatepoha.frnds.IConstants;
import com.halfplatepoha.frnds.IPrefConstants;
import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.db.ChatDAO;
import com.halfplatepoha.frnds.db.IDbConstants;
import com.halfplatepoha.frnds.db.models.Message;
import com.halfplatepoha.frnds.db.models.Song;
import com.halfplatepoha.frnds.detail.IDetailsConstants;
import com.halfplatepoha.frnds.fcm.IFCMConstants;
import com.halfplatepoha.frnds.fcm.NotificationModel;
import com.halfplatepoha.frnds.models.response.TrackDetails;
import com.halfplatepoha.frnds.models.response.TrackResponse;
import com.halfplatepoha.frnds.network.BaseSubscriber;
import com.halfplatepoha.frnds.network.clients.SoundCloudClient;
import com.halfplatepoha.frnds.network.servicegenerators.ClientGenerator;

import java.io.IOException;

import io.realm.Realm;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by surajkumarsau on 26/08/16.
 */
public class NotificationService extends FirebaseMessagingService {

    private ObjectMapper mMapper;
    private NotificationManager mManager;
    private String username;

    private ChatDAO helper;
    private SoundCloudClient mSoundcloudClient;

    @Override
    public void onCreate() {
        super.onCreate();

        helper = new ChatDAO(Realm.getDefaultInstance());

        mSoundcloudClient = new ClientGenerator.Builder()
                .setClientClass(SoundCloudClient.class)
                .setBaseUrl(IConstants.SOUNDCLOUD_BASE_URL)
                .setApiKeyInterceptor(IConstants.API_KEY_PARAM, IConstants.API_KEY_VALUE)
                .setLoggingInterceptor()
                .buildClient();

        mMapper = new ObjectMapper();
        mMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        mManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        username = FrndsPreference.getFromPref(IPrefConstants.USER_NAME, "there");
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        FrndsLog.e("From: " + remoteMessage.getFrom());
        FrndsLog.e("ChatMessage payload: " + remoteMessage.getData());
        FrndsLog.e("ChatMessage body: " +  remoteMessage.getNotification().getBody());

        try {
            NotificationModel model = mMapper.readValue(remoteMessage.getNotification().getBody(), NotificationModel.class);
            constructNotification(model);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void constructNotification(final NotificationModel model) {
        switch (model.getType()){
            case IFCMConstants.SONG:{
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(getMessageFromModel(model)))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Hey " + username + " ...")
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_LIGHTS
                                | Notification.DEFAULT_SOUND
                                | Notification.FLAG_AUTO_CANCEL);

                mManager.notify(IConstants.NOTIFICATION_ID, builder.build());

                Message message = new Message();
                message.setMsgBody(getMessageFromModel(model));
                message.setMsgTimestamp(System.currentTimeMillis());
                message.setUserType(IDetailsConstants.TYPE_FRND);
                message.setMsgType(IDbConstants.TYPE_MUSIC);

                helper.insertMessageToChat(model.getFriendId(), message);

                mSoundcloudClient.getTrackDetails(model.getTrackId())
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new BaseSubscriber<TrackDetails>() {
                                @Override
                                public void onObjectReceived(TrackDetails trackDetails) {
                                    Song song = new Song();
                                    song.setSongUrl(model.getTrackUrl());
                                    song.setFrndId(model.getFriendId());
                                    song.setSongTitle(model.getTrackName());
                                    song.setSongArtist(trackDetails.getUser().getUsername());
                                    song.setSongImgUrl(trackDetails.getArtwork_url()
                                                            .replace(IDetailsConstants.STRING_HTTPS, IDetailsConstants.STRING_HTTP)
                                                            .replace(IDetailsConstants.IMG_LARGE_SUFFIX, IDetailsConstants.IMG_500_X_500_SUFFIX));

                                    helper.insertSongToChat(model.getFriendId(), song);
                                }
                            });

            }
            break;

            case IFCMConstants.MESSAGE:{
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(model.getMessage()))
                        .setContentTitle(model.getFriendName() + " says")
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_LIGHTS
                                | Notification.DEFAULT_SOUND
                                | Notification.FLAG_AUTO_CANCEL);

                mManager.notify(IConstants.NOTIFICATION_ID, builder.build());

                Message message = new Message();
                message.setMsgBody(model.getMessage());
                message.setMsgType(IDbConstants.TYPE_MESSAGE);
                message.setUserType(IDetailsConstants.TYPE_FRND);
                message.setMsgTimestamp(System.currentTimeMillis());

                helper.insertMessageToChat(model.getFriendId(), message);
            }
            break;
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
