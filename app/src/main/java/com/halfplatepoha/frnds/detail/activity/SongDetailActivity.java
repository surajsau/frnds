package com.halfplatepoha.frnds.detail.activity;

import android.animation.Animator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.halfplatepoha.frnds.FrndsLog;
import com.halfplatepoha.frnds.FrndsPreference;
import com.halfplatepoha.frnds.IConstants;
import com.halfplatepoha.frnds.IPrefConstants;
import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.db.ChatDAO;
import com.halfplatepoha.frnds.db.IDbConstants;
import com.halfplatepoha.frnds.db.models.Chat;
import com.halfplatepoha.frnds.db.models.Song;
import com.halfplatepoha.frnds.detail.adapter.AlbumListAdapter;
import com.halfplatepoha.frnds.detail.adapter.ChatAdapter;
import com.halfplatepoha.frnds.detail.IDetailsConstants;
import com.halfplatepoha.frnds.detail.adapter.ChatAdapter.OnPlayClickListener;
import com.halfplatepoha.frnds.detail.model.MessageModel;
import com.halfplatepoha.frnds.detail.model.SongModel;
import com.halfplatepoha.frnds.mediaplayer.PlayerService;
import com.halfplatepoha.frnds.db.models.Message;
import com.halfplatepoha.frnds.models.request.SendMessageRequest;
import com.halfplatepoha.frnds.models.response.TrackDetails;
import com.halfplatepoha.frnds.network.BaseSubscriber;
import com.halfplatepoha.frnds.network.clients.FrndsClient;
import com.halfplatepoha.frnds.models.request.UpdateTrackRequest;
import com.halfplatepoha.frnds.network.clients.SoundCloudClient;
import com.halfplatepoha.frnds.network.servicegenerators.ClientGenerator;
import com.halfplatepoha.frnds.search.activity.SearchScreenActivity;
import com.halfplatepoha.frnds.ui.GlideImageView;
import com.halfplatepoha.frnds.ui.OpenSansEditText;
import com.halfplatepoha.frnds.ui.OpenSansTextView;
import com.halfplatepoha.frnds.utils.AppUtil;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.codetail.animation.ViewAnimationUtils;
import io.realm.Realm;
import io.realm.RealmList;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class SongDetailActivity extends AppCompatActivity implements
        View.OnFocusChangeListener, OnTouchListener, OnPlayClickListener{

    private static final String TAG = SongDetailActivity.class.getSimpleName();

    private FrndsClient mFrndsClient;
    private SoundCloudClient mSoundCloudClient;

    private ChatDAO helper;

    private int deltaX;

    @Bind(R.id.etMessage) OpenSansEditText etMessage;
    @Bind(R.id.rlAlbums) RecyclerView rlAlbums;
    @Bind(R.id.rlChat) RecyclerView rlChat;
    @Bind(R.id.ivAlbumBg) GlideImageView ivAlbumBg;
    @Bind(R.id.playlist) View playlist;
    @Bind(R.id.ivFrndAvatar) CircleImageView ivFrndAvatar;
    @Bind(R.id.tvTitle) OpenSansTextView tvTitle;
    @Bind(R.id.pendingChat) View pendingChat;
    @Bind(R.id.btnPlaylist) ImageButton btnPlaylist;
    @Bind(R.id.ivSongPlayingIndicator) View songPlayedIndicator;
    @Bind(R.id.messageContainer) FrameLayout messageContainer;
    @Bind(R.id.btnSend) View btnSend;
    @Bind(R.id.btnMessageBoxMusic) View btnMessageboxMusic;
    @Bind(R.id.tvTrackTitle) OpenSansTextView tvTrackTitle;
    @Bind(R.id.tvTrackArtist) OpenSansTextView tvTrackArtist;

    private int[] btnPlaylistCoordinates;

    private String mFrndId;
    private String mFbId;
    private String mFrndImageUrl;
    private String mFrndName;

    private String latestSongTrackName;
    private String latestSongAlbumUrl;

    private String mSource;

    private AlbumListAdapter mAlbumListAdapter;
    private ChatAdapter mChatAdapter;

    private LinearLayoutManager mChatLayoutManager;

    private Chat mChat;

    private Animation leftSlide, rightSlide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_detail);
        ButterKnife.bind(this);

        helper = new ChatDAO(Realm.getDefaultInstance());

        getDataFromBundle();

        if(IDetailsConstants.SOURCE_FAB.equalsIgnoreCase(mSource)) {
            startSearchActivity();
        }

        buildApiClients();

        buildAnimations();

    }

    private void buildAnimations() {
        RotateAnimation rotate = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000);
        rotate.setRepeatCount(Animation.INFINITE);

        btnPlaylist.setAnimation(rotate);

        leftSlide = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) messageContainer.getLayoutParams();
                param.leftMargin = deltaX - (int)(deltaX * interpolatedTime);
                param.rightMargin = (int)(deltaX * interpolatedTime);
                messageContainer.setLayoutParams(param);
                btnMessageboxMusic.setAlpha(interpolatedTime);
            }
        };
        leftSlide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                btnMessageboxMusic.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        leftSlide.setInterpolator(new AccelerateDecelerateInterpolator());
        leftSlide.setDuration(300);

        rightSlide = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) messageContainer.getLayoutParams();
                param.rightMargin = deltaX - (int)(deltaX * interpolatedTime);
                param.leftMargin = (int)(deltaX * interpolatedTime);
                messageContainer.setLayoutParams(param);
                btnMessageboxMusic.setAlpha(1 - interpolatedTime);
            }
        };
        rightSlide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                btnMessageboxMusic.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        rightSlide.setInterpolator(new AccelerateDecelerateInterpolator());
        rightSlide.setDuration(300);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mNotificationReceiver, new IntentFilter(IConstants.CHAT_BROADCAST));
        registerReceiver(songFinishedReceiver, new IntentFilter(IConstants.SONG_STATUS_BROADCAST));
    }

    private void addDataToAdapters() {
        //--adding songs to adapter
        mAlbumListAdapter.refresh();
        mChatAdapter.refresh();

        Observable.just(mChat.getFrndSongs())
                .filter(new Func1<RealmList<Song>, Boolean>() {
                    @Override
                    public Boolean call(RealmList<Song> songs) {
                        return songs != null && songs.size() > 0;
                    }
                })
                .flatMap(new Func1<RealmList<Song>, Observable<Song>>() {
                    @Override
                    public Observable<Song> call(RealmList<Song> songs) {
                        return Observable.from(songs);
                    }
                })
                .subscribe(new BaseSubscriber<Song>() {
                    @Override
                    public void onObjectReceived(Song song) {
                        SongModel model = new SongModel();
                        model.setTrackName(song.getSongTitle());
                        model.setTrackTimeStamp(song.getSongTimestamp());
                        model.setTrackImageUrl(song.getSongImgUrl());
                        model.setTrackUser(song.getSongArtist());
                        model.setTrackUrl(song.getSongUrl());
                        mAlbumListAdapter.addSong(model);
                    }

                    @Override
                    public void onCompleted() {
//                        if(mAlbumListAdapter.getItemCount() != 0)
//                            rlAlbums.smoothScrollToPosition(mAlbumListAdapter.getItemCount() - 1);

                        SongModel lastSong = mAlbumListAdapter.getLastSong();
                        if(lastSong != null) {
                            tvTrackTitle.setText(lastSong.getTrackName());
                            tvTrackArtist.setText(lastSong.getTrackUser());
                        }
                    }
                });

        //--adding messages to chat
        Observable.just(mChat.getFrndMessages())
                .filter(new Func1<RealmList<Message>, Boolean>() {
                    @Override
                    public Boolean call(RealmList<Message> messages) {
                        return messages != null && messages.size() > 0;
                    }
                })
                .flatMap(new Func1<RealmList<Message>, Observable<Message>>() {
                    @Override
                    public Observable<Message> call(RealmList<Message> messages) {
                        return Observable.from(messages);
                    }
                })
                .subscribe(new BaseSubscriber<Message>() {
                    @Override
                    public void onObjectReceived(Message message) {
                        MessageModel model = new MessageModel();
                        model.setMessageType(message.getMsgType());
                        model.setMessage(message.getMsgBody());
                        model.setUserType(message.getUserType());
                        model.setMessageType(message.getMsgType());
                        model.setMessageTrackUrl(message.getMsgTrackUrl());
                        model.setMessageTimeStamp(model.getMessageTimeStamp());
                        mChatAdapter.addMessage(model);
                    }

                    @Override
                    public void onCompleted() {
                        if(mChatAdapter.getItemCount() != 0)
                            rlChat.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);
                    }
                });
    }

    private void getChatObjectFromDb() {
        mChat = helper.getFrndWithFrndId(mFrndId);

        mFrndImageUrl = mChat.getFrndImageUrl();
        mFrndName = mChat.getFrndName();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FrndsPreference.setInPref(IPrefConstants.SCREEN_TYPE, IConstants.SCREEN_CHAT);

        getChatObjectFromDb();

        setupToolbar();

        setupRecyclerViews();

        addDataToAdapters();

        if(btnPlaylistCoordinates == null) {
            btnPlaylistCoordinates = new int[2];
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            btnPlaylistCoordinates[0] = metrics.widthPixels;
            btnPlaylistCoordinates[1] = 0;
        }

        etMessage.setOnFocusChangeListener(this);

        if(messageContainer != null) {
            deltaX = ((FrameLayout.LayoutParams)(messageContainer).getLayoutParams()).getMarginStart();
        }

        rlChat.setOnTouchListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FrndsPreference.setInPref(IPrefConstants.SCREEN_TYPE, IConstants.SCREEN_NONE);
    }

    private void startSearchActivity() {
        Intent searchIntent = new Intent(this, SearchScreenActivity.class);
        searchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(searchIntent, IDetailsConstants.SONG_DETAILS_REQUEST);
    }

    private void setupRecyclerViews() {
        mAlbumListAdapter = new AlbumListAdapter(this);
        mChatAdapter = new ChatAdapter(this);

        mChatAdapter.setOnPlayClickListener(this);

        mChatLayoutManager = new LinearLayoutManager(this);
        mChatLayoutManager.setStackFromEnd(true);
        mChatLayoutManager.setReverseLayout(false);

        rlChat.setAdapter(mChatAdapter);
        rlChat.setLayoutManager(mChatLayoutManager);
        rlAlbums.setAdapter(mAlbumListAdapter);
        rlAlbums.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void setupToolbar() {
        Glide.with(this)
                .load(mFrndImageUrl)
                .into(ivFrndAvatar);

        tvTitle.setText(mFrndName);
        //TODO: current user status and user playing song status
    }

    private void callUpdateTracksApi(String trackId) {
        UpdateTrackRequest req = new UpdateTrackRequest();
        req.setFbId(mFbId);
        req.setTo(mFrndId);
        req.setTrackId(trackId);
        mFrndsClient.updateTrack(req)
            .subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.newThread())
            .subscribe(new BaseSubscriber<Void>() {
                @Override
                public void onObjectReceived(Void aVoid) {

                }
            });
    }

    private void callSendMessageApi(final Message message) {
        SendMessageRequest req  = new SendMessageRequest();
        req.setFbId(mFbId);
        req.setMessage(message.getMsgBody());
        req.setTo(mFrndId);

        mFrndsClient.sendMessage(req)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Void>() {
                    @Override
                    public void onObjectReceived(Void aVoid) {
                        helper.insertMessageToChat(mFrndId, message);
                    }
                });
    }

    private void buildApiClients() {
        mFrndsClient = new ClientGenerator.Builder()
                .setLoggingInterceptor()
                .setBaseUrl(IConstants.FRNDS_BASE_URL)
                .setClientClass(FrndsClient.class)
                .setConnectTimeout(5, TimeUnit.MINUTES)
                .setReadTimeout(5, TimeUnit.MINUTES)
                .setHeader(IConstants.CONTENT_TYPE, IConstants.APPLICATION_JSON)
                .buildClient();

        mSoundCloudClient = new ClientGenerator.Builder()
                .setLoggingInterceptor()
                .setBaseUrl(IConstants.SOUNDCLOUD_BASE_URL)
                .setClientClass(SoundCloudClient.class)
                .buildClient();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if(intent != null) {
            mFrndId = intent.getStringExtra(IDetailsConstants.FRND_ID);

            getChatObjectFromDb();
        }
    }

    private void getDataFromBundle() {
        if(getIntent() != null) {
            mSource = getIntent().getStringExtra(IDetailsConstants.SOURCE_TYPE);
            mFrndId = getIntent().getStringExtra(IDetailsConstants.FRND_ID);
        }

        mFbId = FrndsPreference.getFromPref(IPrefConstants.FB_USER_ID, "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mNotificationReceiver);
            unregisterReceiver(songFinishedReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.btnMusic, R.id.btnMessageBoxMusic})
    public void addMusic() {
        Intent searchScreenIntent = new Intent(this, SearchScreenActivity.class);
        startActivityForResult(searchScreenIntent, IDetailsConstants.SONG_DETAILS_REQUEST);
    }

    @OnClick(R.id.back)
    public void back() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(IDetailsConstants.LATEST_IMAGE_TRACK, latestSongTrackName);
        resultIntent.putExtra(IDetailsConstants.LATEST_IMAGE_URL, latestSongAlbumUrl);
        resultIntent.putExtra(IDetailsConstants.FRND_ID, mFrndId);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @OnClick(R.id.btnSend)
    public void addMessage() {
        long timestamp = System.currentTimeMillis();
        if(!TextUtils.isEmpty(etMessage.getText())) {
            Message.Builder msgBuilder = new Message.Builder()
                    .setMsgBody(etMessage.getText().toString())
                    .setMsgTimestamp(timestamp)
                    .setMsgType(IDbConstants.TYPE_MESSAGE)
                    .setUserType(IDetailsConstants.TYPE_ME);

            MessageModel messageModel = new MessageModel();
            messageModel.setMessageTimeStamp(timestamp);
            messageModel.setMessage(etMessage.getText().toString());
            messageModel.setMessageType(IDbConstants.TYPE_MESSAGE);
            messageModel.setUserType(IDetailsConstants.TYPE_ME);

            mChatAdapter.addMessage(messageModel);

            rlChat.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);

            callSendMessageApi(msgBuilder.build());

            etMessage.setText("");
        }
    }

    @OnClick(R.id.btnPlaylist)
    public void togglePlaylist() {
        if(playlist.getVisibility() == View.VISIBLE) {
            circularCollapse();
        } else {
            circularReveal();
        }
    }

    private void circularCollapse() {
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
    }

    private void circularReveal() {
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
                    String trackId = String.valueOf(data.getExtras().getLong(IDetailsConstants.TRACK_ID));
                    String trackUrl = data.getExtras().getString(IDetailsConstants.TRACK_URL);

                    String trackImageUrl = (data.getExtras().getString(IDetailsConstants.TRACK_IMAGE_URL))
                            .replace(IDetailsConstants.STRING_HTTPS, IDetailsConstants.STRING_HTTP)
                            .replace(IDetailsConstants.IMG_LARGE_SUFFIX, IDetailsConstants.IMG_500_X_500_SUFFIX);

                    FrndsLog.e(trackImageUrl);

                    String trackTitle = data.getExtras().getString(IDetailsConstants.TRACK_TITLE);
                    String trackArtist = data.getExtras().getString(IDetailsConstants.TRACK_ARTIST);

                    //--setting background image of activity
                    ivAlbumBg.setImageUrl(this, trackImageUrl);
                    tvTrackTitle.setText(trackTitle);
                    tvTrackArtist.setText(trackArtist);

                    trackImageUrl = trackImageUrl.replace(IDetailsConstants.IMG_500_X_500_SUFFIX, IDetailsConstants.IMG_300_X_300_SUFFIX);

                    addSongToPlaylist(trackUrl, trackImageUrl, trackTitle, trackArtist);

                    startPlayingTrack(trackTitle, trackUrl);

                    callUpdateTracksApi(trackId);

                    latestSongAlbumUrl = trackImageUrl.replace(IDetailsConstants.IMG_300_X_300_SUFFIX, IDetailsConstants.IMG_BADGE);
                    latestSongTrackName = trackTitle;
                }
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startPlayingTrack(String trackTitle, String trackUrl) {
        Intent playerServiceIntent = new Intent(this, PlayerService.class);
        playerServiceIntent.setAction(PlayerService.ACTION_PLAY);
        playerServiceIntent.putExtra(IDetailsConstants.NOTIFICATION_SERVICE_TRACK_TITLE, trackTitle);
        playerServiceIntent.putExtra(IDetailsConstants.SERVICE_STREAM_URL, trackUrl);
        playerServiceIntent.putExtra(IDetailsConstants.FRND_ID, mFrndId);
        startService(playerServiceIntent);
    }

    private void addSongToPlaylist(String trackUrl, String trackImageUrl, String trackTitle, String trackArtist) {
        long timestamp = System.currentTimeMillis();

        Song song = new Song();
        song.setSongArtist(trackArtist);
        song.setSongImgUrl(trackImageUrl);
        song.setSongTimestamp(timestamp);
        song.setSongTitle(trackTitle);
        song.setSongUrl(trackUrl);
        song.setFrndId(mFrndId);

        Message message = new Message.Builder()
                .setMsgBody(String.format(getString(R.string.music_chat_message), "You", trackTitle))
                .setMsgTimestamp(timestamp)
                .setMsgTrackUrl(trackUrl)
                .setMsgType(IDbConstants.TYPE_MUSIC)
                .setUserType(IDetailsConstants.TYPE_ME)
                .build();

        SongModel songModel = new SongModel();
        songModel.setTrackUrl(trackUrl);
        songModel.setTrackUser(trackArtist);
        songModel.setTrackImageUrl(trackImageUrl);
        songModel.setTrackTimeStamp(timestamp);
        songModel.setTrackName(trackTitle);

        MessageModel messageModel = new MessageModel();
        messageModel.setMessage(String.format(getString(R.string.music_chat_message), "You", trackTitle));
        messageModel.setUserType(IDetailsConstants.TYPE_ME);
        messageModel.setMessageType(IDbConstants.TYPE_MUSIC);
        messageModel.setMessageTrackUrl(trackUrl);


        mAlbumListAdapter.addSong(songModel);
        mChatAdapter.addMessage(messageModel);

        if(mAlbumListAdapter.getItemCount() != 0)
            rlAlbums.smoothScrollToPosition(mAlbumListAdapter.getItemCount() - 1);

        if(mChatAdapter.getItemCount() != 0)
            rlChat.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);

        helper.insertSongToChat(mFrndId, song);
        helper.insertMessageToChat(mFrndId, message);
    }

    private BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null) {
                String trackId = intent.getStringExtra(IConstants.FRND_TRACK_ID);
                @IDbConstants.MessageType int messageType = intent.getIntExtra(IConstants.FRND_MESSAGE_TYPE, IDbConstants.TYPE_MESSAGE);
                String messageBody = intent.getStringExtra(IConstants.FRND_MESSAGE);
                String trackUrl = intent.getStringExtra(IConstants.FRND_TRACK_URL);
                String frndId = intent.getStringExtra(IConstants.FRND_ID);
                String trackTitle = intent.getStringExtra(IConstants.FRND_TRACK_TITLE);
                long timestamp = intent.getLongExtra(IConstants.FRND_TIME_STAMP, 0L);

                Message message = new Message();
                message.setMsgBody(messageBody);
                message.setMsgTimestamp(timestamp);
                message.setUserType(IDetailsConstants.TYPE_FRND);
                message.setMsgType(messageType);
                message.setMsgTrackUrl(trackUrl);

                if(messageType == IDbConstants.TYPE_MUSIC) {
                    Song song = new Song();
                    song.setSongUrl(trackUrl);
                    song.setFrndId(frndId);
                    song.setSongTitle(trackTitle);
                    song.setSongTimestamp(timestamp);

                    helper.insertSongToChat(frndId, song);
                }

                helper.insertMessageToChat(frndId, message);

                refreshChatDetails(frndId, messageType, messageBody, trackId, trackTitle, trackUrl, timestamp);
            }
        }
    };

    private BroadcastReceiver songFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            FrndsPreference.setInPref(IPrefConstants.CURRENT_SONG_STATUS, IDetailsConstants.CURRENT_SONG_STATUS_STOP);
            FrndsPreference.setInPref(IPrefConstants.CURRENT_SONG_FRND_ID, "");

            songPlayedIndicator.setVisibility(View.GONE);
            btnPlaylist.setAnimation(null);
        }
    };

    private void refreshChatDetails(String frndId, int messageType, String message, String trackId,
                                    final String trackTitle, final String trackUrl, final long timestamp) {
        if(mFrndId.equalsIgnoreCase(frndId)) {
            if(messageType == IDbConstants.TYPE_MUSIC) {

                //--getting song artist & image url
                mSoundCloudClient.getTrackDetails(trackId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(new BaseSubscriber<TrackDetails>() {
                            @Override
                            public void onObjectReceived(TrackDetails trackDetails) {
                                SongModel songModel = new SongModel();
                                songModel.setTrackName(trackTitle);
                                songModel.setTrackImageUrl(trackDetails.getArtwork_url());
                                songModel.setTrackTimeStamp(timestamp);
                                songModel.setTrackUser(trackDetails.getUser().getUsername());
                                songModel.setTrackUrl(trackUrl);

                                Song song = new Song();
                                song.setSongImgUrl(trackDetails.getArtwork_url()
                                        .replace(IDetailsConstants.STRING_HTTPS, IDetailsConstants.STRING_HTTP)
                                        .replace(IDetailsConstants.IMG_LARGE_SUFFIX, IDetailsConstants.IMG_500_X_500_SUFFIX));
                                song.setSongTimestamp(timestamp);
                                song.setFrndId(mFrndId);
                                if(trackDetails.getUser() != null) {
                                    song.setSongArtist(trackDetails.getUser().getUsername());
                                }
                                helper.updateSong(song);

                                mAlbumListAdapter.addSong(songModel);
                            }
                        });
            }

            Message msg = new Message();
            msg.setMsgTimestamp(timestamp);
            msg.setMsgType(messageType);
            msg.setUserType(IDetailsConstants.TYPE_FRND);
            msg.setMsgBody(message);

            MessageModel messageModel = new MessageModel();
            messageModel.setMessageType(messageType);
            messageModel.setMessage(message);
            messageModel.setMessageTrackUrl(trackUrl);
            messageModel.setUserType(IDetailsConstants.TYPE_FRND);
            messageModel.setMessageType(messageType);

            mChatAdapter.addMessage(messageModel);

        } else {
            pendingChat.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFocusChange(View view, boolean isFocus) {
        if(isFocus) {
            messageContainer.startAnimation(leftSlide);
            circularCollapse();
        } else {
            if(TextUtils.isEmpty(((EditText)view).getText()))
                messageContainer.startAnimation(rightSlide);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.rlChat:{
                if(TextUtils.isEmpty(etMessage.getText())) {
                    messageContainer.startAnimation(rightSlide);
                }
                AppUtil.hideSoftKeyboard(this);
                etMessage.clearFocus();
            }
            break;
        }
        return false;
    }

    @Override
    public void onPlayClick(String trackUrl, String message, int position) {
        String trackName = message.split(" played ")[1];
        startPlayingTrack(trackName, trackUrl);
        rlAlbums.smoothScrollToPosition(position);
    }
}
