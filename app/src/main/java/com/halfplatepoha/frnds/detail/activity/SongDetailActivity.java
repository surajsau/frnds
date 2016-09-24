package com.halfplatepoha.frnds.detail.activity;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.halfplatepoha.frnds.IConstants;
import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.db.IDbConstants;
import com.halfplatepoha.frnds.db.models.Chat;
import com.halfplatepoha.frnds.db.models.Song;
import com.halfplatepoha.frnds.detail.adapter.AlbumListAdapter;
import com.halfplatepoha.frnds.detail.adapter.ChatAdapter;
import com.halfplatepoha.frnds.detail.IDetailsConstants;
import com.halfplatepoha.frnds.mediaplayer.PlayerService;
import com.halfplatepoha.frnds.db.models.Message;
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
import io.codetail.animation.ViewAnimationUtils;
import io.realm.Realm;
import rx.schedulers.Schedulers;

public class SongDetailActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {

    private static final String TAG = SongDetailActivity.class.getSimpleName();

    private MediaPlayer mPlayer;
    private FrndsClient mFrndsClient;

    @Bind(R.id.etMessage) OpenSansEditText etMessage;
    @Bind(R.id.rlAlbums) RecyclerView rlAlbums;
    @Bind(R.id.rlChat) RecyclerView rlChat;
    @Bind(R.id.ivAlbumBg) ImageView ivAlbumBg;
    @Bind(R.id.playlist) View playlist;

    private int[] btnPlaylistCoordinates;

    private String mTrackUrl;
    private String mTrackImageUrl;
    private String mTrackTitle;
    private String mTrackArtist;

    private long mFrndId;

    private String mSource;

    private AlbumListAdapter mAlbumListAdapter;
    private ChatAdapter mChatAdapter;

    private LinearLayoutManager mChatLayoutManager;

    private Realm mRealm;

    private Chat mChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_detail);
        ButterKnife.bind(this);

        mRealm = Realm.getDefaultInstance();

        getDataFromBundle();

        getChatObjectFromDb();

        if(IDetailsConstants.SOURCE_FAB.equalsIgnoreCase(mSource)) {
            startSearchActivity();
        }

        setupToolbar();

        setupRecyclerViews();

        prepareMediaPlayer();

        buildApiClients();

    }

    private void getChatObjectFromDb() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(btnPlaylistCoordinates == null) {
            btnPlaylistCoordinates = new int[2];
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            btnPlaylistCoordinates[0] = metrics.widthPixels;
            btnPlaylistCoordinates[1] = 0;
        }
    }

    private void startSearchActivity() {
        Intent searchIntent = new Intent(this, SearchScreenActivity.class);
        searchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(searchIntent, IDetailsConstants.SONG_DETAILS_REQUEST);
    }

    private void setupRecyclerViews() {
        mAlbumListAdapter = new AlbumListAdapter(this);
        mChatAdapter = new ChatAdapter(this);

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
        mTrackTitle = getIntent().getStringExtra(IDetailsConstants.TRACK_TITLE);
        mTrackUrl = getIntent().getStringExtra(IDetailsConstants.TRACK_URL);
        mTrackImageUrl = getIntent().getStringExtra(IDetailsConstants.TRACK_IMAGE_URL);
        mSource = getIntent().getStringExtra(IDetailsConstants.SOURCE_TYPE);
        mFrndId = getIntent().getLongExtra(IDetailsConstants.FRND_ID, -1);
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
//            mDuration = trackResponse.getDuration();
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
                    .setMsgBody(etMessage.getText().toString())
                    .setMsgTimestamp(System.currentTimeMillis())
                    .setMsgType(IDbConstants.TYPE_MESSAGE)
                    .setUserType(IDetailsConstants.TYPE_ME);
            mChatAdapter.addMessage(msgBuilder.build());
            rlChat.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);

            etMessage.setText("");
        }
    }

    @OnClick(R.id.btnPlaylist)
    public void togglePlaylist() {
        if(playlist.getVisibility() == View.VISIBLE) {
            Animator anim = ViewAnimationUtils.createCircularReveal(playlist,
                    btnPlaylistCoordinates[0],
                    btnPlaylistCoordinates[1],
                    getfFinalRadius(playlist, btnPlaylistCoordinates[0], btnPlaylistCoordinates[1]),
                    0,
                    View.LAYER_TYPE_HARDWARE);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {}

                @Override
                public void onAnimationEnd(Animator animator) {
                    playlist.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {}

                @Override
                public void onAnimationRepeat(Animator animator) {}
            });
            anim.setDuration(300);
            anim.setInterpolator(new FastOutLinearInInterpolator());
            anim.start();
        } else {
            playlist.setVisibility(View.VISIBLE);
            Animator anim = ViewAnimationUtils.createCircularReveal(playlist,
                    btnPlaylistCoordinates[0],
                    btnPlaylistCoordinates[1],
                    0,
                    getfFinalRadius(playlist, btnPlaylistCoordinates[0], btnPlaylistCoordinates[1]),
                    View.LAYER_TYPE_HARDWARE);
            anim.setDuration(300);
            anim.setInterpolator(new FastOutLinearInInterpolator());
            anim.start();
        }
    }

    private float getfFinalRadius(View view, int centerX, int centerY) {
        return (float) Math.hypot(view.getWidth() / 2f, view.getHeight() / 2f)
                + hypo(view, centerX, centerY);
    }

    private float hypo(View view, float x, float y) {
        Point p1 = new Point((int)x, (int)y);
        Point p2 = new Point(view.getWidth() / 2, view.getHeight() / 2);

        return (float) Math.sqrt(Math.pow(p1.y - p2.y, 2) + Math.pow(p1.x - p2.x, 2));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case IDetailsConstants.SONG_DETAILS_REQUEST:{
                if(resultCode == RESULT_OK){
                    mTrackUrl = data.getExtras().getString(IDetailsConstants.TRACK_URL);
                    mTrackImageUrl = data.getExtras().getString(IDetailsConstants.TRACK_IMAGE_URL);
                    mTrackTitle = data.getExtras().getString(IDetailsConstants.TRACK_TITLE);
                    mTrackArtist = data.getExtras().getString(IDetailsConstants.TRACK_URL);

                    //--setting background image of activity
                    Glide.with(this).load(mTrackImageUrl.replace(IDetailsConstants.IMG_LARGE_SUFFIX, IDetailsConstants.IMG_500_X_500_SUGGIX)).into(ivAlbumBg);

                    addSongToPlaylist();

//                    startPlayingTrack();

//                    callUpdateTracksApi();
                }
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startPlayingTrack() {
        Intent playerServiceIntent = new Intent(this, PlayerService.class);
        playerServiceIntent.setAction(PlayerService.ACTION_PLAY);
        playerServiceIntent.putExtra(IDetailsConstants.NOTIFICATION_SERVICE_TRACK_TITLE, mTrackTitle);
        playerServiceIntent.putExtra(IDetailsConstants.SERVICE_STREAM_URL, mTrackUrl);
        startService(playerServiceIntent);
    }

    private void addSongToPlaylist() {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Song song = realm.createObject(Song.class);
                song.setSongId(getNextSongId());
                song.setSongArtist(mTrackArtist);
                song.setSongImgUrl(mTrackImageUrl);
                song.setSongTimestamp(System.currentTimeMillis());
                song.setSongTitle(mTrackTitle);
                song.setSongUrl(mTrackUrl);
                song.setFrndId(mFrndId);

                Message message = realm.createObject(Message.class);
                message.setMsgBody(String.format(getString(R.string.music_chat_message), "You", mTrackTitle));
                message.setMsgTimestamp(System.currentTimeMillis());
                message.setUserType(IDetailsConstants.TYPE_ME);
                message.setMsgType(IDbConstants.TYPE_MUSIC);

//                mChat.frndMessages.add(message);
//                mChat.frndSongs.add(song);

                mAlbumListAdapter.addSong(realm.copyFromRealm(song));
                mChatAdapter.addMessage(realm.copyFromRealm(message));
            }
        });
    }

    private int getNextSongId() {
        if(mRealm.where(Song.class).max(IDbConstants.SONG_ID_KEY) == null)
            return 1;
        return mRealm.where(Song.class).max(IDbConstants.SONG_ID_KEY).intValue() + 1;
    }

//    private class AddSongToDbTask extends AsyncTask<Song, Void, Song> {
//
//        @Override
//        protected Song doInBackground(Song... songs) {
//            return null;
//        }
//    }
//
//    private class FetchChatFromDbTask extends AsyncTask<Void, Void, Chat> {
//
//        @Override
//        protected Chat doInBackground(Void... voids) {
//            if(mRealm.where(Chat.class).equalTo(IDbConstants.FRND_ID_KEY, mFrndId) == null) {
//                Chat chat = mRealm.createObject(Chat.class);
//                chat.setFrndId(mFrndId);
//                return chat;
//            } else {
//                return mRealm.where(Chat.class).equalTo(IDbConstants.FRND_ID_KEY, mFrndId).findFirst();
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Chat chat) {
//            mChat = chat;
//        }
//    }

}
