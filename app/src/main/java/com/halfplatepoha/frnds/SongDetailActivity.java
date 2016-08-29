package com.halfplatepoha.frnds;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.halfplatepoha.frnds.network.BaseSubscriber;
import com.halfplatepoha.frnds.network.clients.FrndsClient;
import com.halfplatepoha.frnds.network.models.request.UpdateTrackRequest;
import com.halfplatepoha.frnds.network.servicegenerators.ClientGenerator;
import com.halfplatepoha.frnds.network.clients.SoundCloudClient;
import com.halfplatepoha.frnds.network.models.response.TrackResponse;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SongDetailActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {

    private static final String TAG = SongDetailActivity.class.getSimpleName();

    private MediaPlayer mPlayer;
    private SoundCloudClient mSoundCloudClient;
    private FrndsClient mFrndsClient;

    private String trackId;

    @Bind(R.id.ivIcon) ImageView ivIcon;
    @Bind(R.id.tvTitle) TextView tvTitle;

    private String mTitle;
    private String mIconUrl;
    private String mTrackid;
    private String mTrackUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_detail);
        ButterKnife.bind(this);

        getDataFromBundle();
        prepareMediaPlayer();

        buildApiClients();

        callUpdateTracksApi();
        callTracksApi();
    }

    private void callUpdateTracksApi() {
        UpdateTrackRequest req = new UpdateTrackRequest();
        req.setFbId("-KQEPGB6Hy_K_krhaVg5");
        req.setTo("-KQEPGB6Hy_K_krhaVg5");
        req.setTrackId("13658665");
        mFrndsClient.updateTrack(req);
    }

    private void buildApiClients() {
        mSoundCloudClient = new ClientGenerator.Builder()
                .setLoggingInterceptor()
                .setBaseUrl(IConstants.SOUNDCLOUD_BASE_URL)
                .setClientClass(SoundCloudClient.class)
                .setApiKeyInterceptor(IConstants.API_KEY_PARAM, IConstants.API_KEY_VALUE)
                .buildClient();

        mFrndsClient = new ClientGenerator.Builder()
                .setLoggingInterceptor()
                .setBaseUrl(IConstants.FRNDS_BASE_URL)
                .setClientClass(FrndsClient.class)
                .buildClient();
    }

    private void prepareMediaPlayer() {
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnPreparedListener(this);
    }

    private void getDataFromBundle() {
        mTrackid = getIntent().getStringExtra(IConstants.TRACK_ID);
    }

    private void callTracksApi() {
        mSoundCloudClient.getTrackDetails(mTrackid)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tracksResponseSubscriber);
    }

    private void updateTrackAlbum(String imageUrl) {
        Picasso.with(this)
                .load(imageUrl)
                .into(ivIcon);
    }

    private void playMusic(String url) {
        Log.d(TAG, url);
        try {
            mPlayer.setDataSource(url + "?" + IConstants.API_KEY_PARAM + "=" + IConstants.API_KEY_VALUE);
            mPlayer.prepare();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.e(TAG, "is Prepared + " + mediaPlayer.getDuration());
        if(mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    private BaseSubscriber<TrackResponse> tracksResponseSubscriber = new BaseSubscriber<TrackResponse>() {
        @Override
        public void onObjectReceived(TrackResponse trackResponse) {
            updateTrackAlbum(trackResponse.getArtwork_url());
            playMusic(trackResponse.getStream_url());
        }
    };
}
