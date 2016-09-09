package com.halfplatepoha.frnds.detail.activity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.halfplatepoha.frnds.IConstants;
import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.detail.adapter.AlbumListAdapter;
import com.halfplatepoha.frnds.detail.adapter.ChatAdapter;
import com.halfplatepoha.frnds.detail.IDetailsConstants;
import com.halfplatepoha.frnds.models.Message;
import com.halfplatepoha.frnds.network.BaseSubscriber;
import com.halfplatepoha.frnds.network.clients.FrndsClient;
import com.halfplatepoha.frnds.models.request.UpdateTrackRequest;
import com.halfplatepoha.frnds.network.servicegenerators.ClientGenerator;
import com.halfplatepoha.frnds.models.response.TrackResponse;
import com.halfplatepoha.frnds.search.activity.SearchScreenActivity;
import com.halfplatepoha.frnds.ui.OpenSansEditText;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.schedulers.Schedulers;

public class SongDetailActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {

    private static final String TAG = SongDetailActivity.class.getSimpleName();

    private MediaPlayer mPlayer;
    private FrndsClient mFrndsClient;

    private long mDuration;

    private boolean isPlaylistVisible;

    @Bind(R.id.etMessage) OpenSansEditText etMessage;
    @Bind(R.id.rlAlbums) RecyclerView rlAlbums;
    @Bind(R.id.rlChat) RecyclerView rlChat;

    private String mTrackUrl;
    private String mTrackTitle;
    private String mIconUrl;

    private String mSource;

    private AlbumListAdapter mAlbumListAdapter;
    private ChatAdapter mChatAdapter;

    private LinearLayoutManager mChatLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_detail);
        ButterKnife.bind(this);

        getDataFromBundle();

        if(IDetailsConstants.SOURCE_FAB.equalsIgnoreCase(mSource)) {
            startSearchActivity();
        }

        setupToolbar();

        setupRecyclerViews();

        prepareMediaPlayer();

        buildApiClients();

        callUpdateTracksApi();
    }

    private void startSearchActivity() {
        Intent searchIntent = new Intent(this, SearchScreenActivity.class);
        searchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(searchIntent, IDetailsConstants.SONG_DETAILS_REQUEST);
    }

    private void setupRecyclerViews() {
        mAlbumListAdapter = new AlbumListAdapter(this);
        mChatAdapter = new ChatAdapter(this);
        mChatAdapter.setHasStableIds(true);

        mChatLayoutManager = new LinearLayoutManager(this);
        mChatLayoutManager.setStackFromEnd(true);
        mChatLayoutManager.setReverseLayout(false);

        rlChat.setAdapter(mChatAdapter);
        rlChat.setLayoutManager(mChatLayoutManager);
        rlAlbums.setAdapter(mAlbumListAdapter);
        rlAlbums.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void setupToolbar() {
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setTitle("Sample Title");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void callUpdateTracksApi() {
        UpdateTrackRequest req = new UpdateTrackRequest();
        req.setFbId("-KQEPGB6Hy_K_krhaVg5");
        req.setTo("-KQEPGB6Hy_K_krhaVg5");
        req.setTrackId("13658665");
        mFrndsClient.updateTrack(req)
            .subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.newThread())
            .subscribe();
    }

    private void buildApiClients() {
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
        mTrackTitle = getIntent().getStringExtra(IConstants.TRACK_TITLE);
        mTrackUrl = getIntent().getStringExtra(IConstants.TRACK_URL);
        mIconUrl = getIntent().getStringExtra(IConstants.ICON_URL);
        mSource = getIntent().getStringExtra(IDetailsConstants.SOURCE_TYPE);
    }

    private void updateTrackAlbum(String imageUrl) {
//        Picasso.with(this)
//                .load(imageUrl)
//                .into(ivIcon);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private BaseSubscriber<TrackResponse> tracksResponseSubscriber = new BaseSubscriber<TrackResponse>() {
        @Override
        public void onObjectReceived(TrackResponse trackResponse) {
            mDuration = trackResponse.getDuration();
            updateTrackAlbum(trackResponse.getArtwork_url());
            playMusic(trackResponse.getStream_url());
        }
    };

    @OnClick(R.id.btnMusic)
    public void addMusic() {
        Intent searchScreenIntent = new Intent(this, SearchScreenActivity.class);
        startActivityForResult(searchScreenIntent, IDetailsConstants.SONG_DETAILS_REQUEST);
    }

    @OnClick(R.id.back)
    public void back() {
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @OnClick(R.id.btnSend)
    public void addMessage() {
        if(!TextUtils.isEmpty(etMessage.getText())) {
            Message.Builder msgBuilder = new Message.Builder()
                    .setMessage(etMessage.getText().toString())
                    .setTimestamp(System.currentTimeMillis())
                    .setUserType(IDetailsConstants.TYPE_ME);
            mChatAdapter.addMessage(msgBuilder.build());
            rlChat.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);

            etMessage.setText("");
        }
    }

    @OnClick(R.id.btnPlaylist)
    public void togglePlaylist() {
        if(isPlaylistVisible) {

        } else {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case IDetailsConstants.SONG_DETAILS_REQUEST:{
                if(resultCode == RESULT_OK){

                }
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
