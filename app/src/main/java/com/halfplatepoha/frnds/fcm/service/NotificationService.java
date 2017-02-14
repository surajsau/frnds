package com.halfplatepoha.frnds.fcm.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Debug;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

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
import com.halfplatepoha.frnds.network.BaseSubscriber;
import com.halfplatepoha.frnds.network.clients.SoundCloudClient;
import com.halfplatepoha.frnds.network.servicegenerators.ClientGenerator;

import java.io.IOException;
import java.util.Map;

import io.realm.Realm;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
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

        Map<String, String> notificationData = remoteMessage.getData();

        try {

            NotificationModel model = mMapper.readValue(remoteMessage.getNotification().getBody(), NotificationModel.class);
            constructNotification(model);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void constructNotification(final NotificationModel model) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND|Notification.FLAG_AUTO_CANCEL);

        Intent chatIntent = new Intent(IConstants.CHAT_BROADCAST);
        chatIntent.putExtra(IConstants.FRND_ID, model.getFriendId());

        Log.e("Model type", model.getType());

        switch (model.getType()){
            case IFCMConstants.SONG:{
                final String notificationMessage = getMessageFromModel(model);

                chatIntent.putExtra(IConstants.FRND_MESSAGE, notificationMessage);
                chatIntent.putExtra(IConstants.FRND_MESSAGE_TYPE, IDbConstants.TYPE_MUSIC);
                chatIntent.putExtra(IConstants.FRND_TRACK_ID, model.getTrackId());
                chatIntent.putExtra(IConstants.FRND_TRACK_URL, model.getTrackUrl());
                chatIntent.putExtra(IConstants.FRND_TRACK_TITLE, model.getTrackName());

                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(notificationMessage))
                        .setContentTitle("Hey " + username + " ...");

                mSoundcloudClient.getTrackDetails(model.getTrackId())
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new BaseSubscriber<TrackDetails>() {
                                @Override
                                public void onObjectReceived(TrackDetails trackDetails) {
                                    Message message = new Message();
                                    message.setMsgBody(notificationMessage);
                                    message.setMsgTimestamp(System.currentTimeMillis());
                                    message.setUserType(IDetailsConstants.TYPE_FRND);
                                    message.setMsgType(IDbConstants.TYPE_MUSIC);

                                    Song song = new Song();
                                    song.setSongUrl(model.getTrackUrl());
                                    song.setFrndId(model.getFriendId());
                                    song.setSongTitle(model.getTrackName());
                                    song.setSongArtist(trackDetails.getUser().getUsername());
                                    song.setSongImgUrl(trackDetails.getArtwork_url()
                                                            .replace(IDetailsConstants.STRING_HTTPS, IDetailsConstants.STRING_HTTP)
                                                            .replace(IDetailsConstants.IMG_LARGE_SUFFIX, IDetailsConstants.IMG_500_X_500_SUFFIX));

                                    FrndsLog.e(model.getFriendId());
                                    helper.insertSongToChat(model.getFriendId(), song);
                                    helper.insertMessageToChat(model.getFriendId(), message);
                                }
                            });

            }
            break;

            case IFCMConstants.MESSAGE:{

                chatIntent.putExtra(IConstants.FRND_MESSAGE, model.getMessage());
                chatIntent.putExtra(IConstants.FRND_MESSAGE_TYPE, IDbConstants.TYPE_MESSAGE);

                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(model.getMessage()))
                        .setContentTitle(model.getFriendName() + " says");

                Observable.just(model)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .filter(new Func1<NotificationModel, Boolean>() {
                            @Override
                            public Boolean call(NotificationModel model) {
                                return (model != null && !TextUtils.isEmpty(model.getMessage()));
                            }
                        })
                        .subscribe(new BaseSubscriber<NotificationModel>() {
                            @Override
                            public void onObjectReceived(NotificationModel model) {
                                Message message = new Message();
                                message.setMsgBody(model.getMessage());
                                message.setMsgType(IDbConstants.TYPE_MESSAGE);
                                message.setUserType(IDetailsConstants.TYPE_FRND);
                                message.setMsgTimestamp(System.currentTimeMillis());

                                helper.insertMessageToChat(model.getFriendId(), message);
                            }
                        });
            }
            break;
        }

        int screenType = FrndsPreference.getIntFromPref(IPrefConstants.SCREEN_TYPE, IConstants.SCREEN_NONE);

        switch (screenType) {
            case IConstants.SCREEN_CHAT:
            case IConstants.SCREEN_LISTING:
                sendBroadcast(chatIntent);
                break;

            case IConstants.SCREEN_NONE:
                mManager.notify(IConstants.NOTIFICATION_ID, builder.build());
                break;
        }
    }

    private String getMessageFromModel(NotificationModel model) {
        if(model == null)
            return null;

        return model.getFriendName()
                + " is listening to "
                + model.getTrackName();
    }
}
