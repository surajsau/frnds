package com.halfplatepoha.frnds.mediaplayer;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.halfplatepoha.frnds.FrndsLog;
import com.halfplatepoha.frnds.IConstants;
import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.detail.IDetailsConstants;
import com.halfplatepoha.frnds.detail.activity.SongDetailActivity;

import java.io.IOException;

/**
 * Created by surajkumarsau on 09/09/16.
 */
public class PlayerService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener{

    private MediaPlayer mPlayer;
    public static final String ACTION_PLAY = "com.halfplatepoha.frnds.PLAY";
    public static final String ACTION_STOP = "com.halfplatepoha.frnds.STOP";
    private String mStreamUrl;

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mPlayer.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FrndsLog.e(intent.getAction());
        FrndsLog.e(intent.getStringExtra(IDetailsConstants.SERVICE_STREAM_URL));
        try {
            if (intent.getAction().equals(ACTION_PLAY)) {
                if(mPlayer == null) {
                    mPlayer = new MediaPlayer();
                    mPlayer.setOnPreparedListener(this);
                    mPlayer.setOnErrorListener(this);
                }

                mStreamUrl = intent.getStringExtra(IDetailsConstants.SERVICE_STREAM_URL);
                String title = intent.getStringExtra(IDetailsConstants.NOTIFICATION_SERVICE_TRACK_TITLE);

                if(!TextUtils.isEmpty(mStreamUrl)) {
                    mStreamUrl = mStreamUrl.concat("?" + IConstants.API_KEY_PARAM + "=" + IConstants.API_KEY_VALUE);

                    mPlayer.reset();
                    mPlayer.setDataSource(mStreamUrl);
                    mPlayer.prepareAsync();

                    PendingIntent songDetailsIntent = PendingIntent.getActivity(getApplicationContext(), IConstants.NOTIFICATION_PLAY_PENDING_INTENT_REQUEST,
                            new Intent(getApplicationContext(), SongDetailActivity.class),
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    Intent stopPlay = new Intent(this, PlayerService.class);
                    stopPlay.setAction(PlayerService.ACTION_STOP);

                    PendingIntent stopPlayService = PendingIntent.getService(getApplicationContext(), IConstants.NOTIFICATION_STOP_PLAYING_INTENT_REQUEST,
                            stopPlay, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder playNotificationBuilder = new NotificationCompat.Builder(this)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText("You\'re listening to " + title))
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .addAction(R.drawable.profile, "Profile", songDetailsIntent)
                            .addAction(R.drawable.stop, "Stop", stopPlayService);

                    startForeground(IConstants.PLAY_NOTIFICATION_ID, playNotificationBuilder.build());
                }
            } else if(intent.getAction().equals(ACTION_STOP)) {
                if(mPlayer != null)
                    mPlayer.release();
                stopSelf();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        try{
            mediaPlayer.reset();
            mediaPlayer.setDataSource(mStreamUrl);
            mediaPlayer.prepareAsync();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
