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
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.halfplatepoha.frnds.BaseActivity;
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
import com.halfplatepoha.frnds.detail.presenter.SongDetailPresenter;
import com.halfplatepoha.frnds.detail.presenter.SongDetailPresenterImpl;
import com.halfplatepoha.frnds.detail.view.SongDetailView;
import com.halfplatepoha.frnds.fcm.IFCMConstants;
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

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.codetail.animation.ViewAnimationUtils;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.R.id.message;


public class SongDetailActivity extends BaseActivity implements SongDetailView,
        View.OnFocusChangeListener, OnTouchListener, OnPlayClickListener{

    private static final String TAG = SongDetailActivity.class.getSimpleName();

    private int deltaX;

    private BroadcastReceiver songFinishedReceiver;
    private BroadcastReceiver mNotificationReceiver;

    private SongDetailPresenter presenter;

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

    private AlbumListAdapter mAlbumListAdapter;
    private ChatAdapter mChatAdapter;

    private LinearLayoutManager mChatLayoutManager;

    private Animation leftSlide, rightSlide;

    private RotateAnimation rotateAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_detail);
        ButterKnife.bind(this);

        presenter = new SongDetailPresenterImpl(this);
        presenter.onCreate();

        getDataFromBundle();

    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public void registerReceivers() {
        registerReceiver(mNotificationReceiver, new IntentFilter(IConstants.CHAT_BROADCAST));
        registerReceiver(songFinishedReceiver, new IntentFilter(IConstants.SONG_STATUS_BROADCAST));
    }

    @Override
    protected void onResume() {
        super.onResume();
        FrndsPreference.setInPref(IPrefConstants.SCREEN_TYPE, IConstants.SCREEN_CHAT);

        presenter.onResume();

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

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void buildAnimations() {
        rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);

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
    public void smoothScrollToLast() {
        if (mChatAdapter.getItemCount() != 0)
            rlChat.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);
    }

    @Override
    public void addDataToAdapters(ArrayList<SongModel> songs, ArrayList<MessageModel> messages) {
        mAlbumListAdapter.refresh();
        mChatAdapter.refresh();

        //--adding songs to chat
        for (int i = 0; i < songs.size(); i++) {
            mAlbumListAdapter.addSong(songs.get(i));
        }

        if (songs.size() > 0) {
            tvTrackTitle.setText(songs.get(songs.size() - 1).getTrackName());
            tvTrackArtist.setText(songs.get(songs.size() - 1).getTrackUser());
        } else {
            tvTrackTitle.setVisibility(View.GONE);
            tvTrackArtist.setVisibility(View.GONE);
        }

        //--adding messages to chat
        for (int i = 0; i < messages.size(); i++) {
            mChatAdapter.addMessage(messages.get(i));
        }
    }

    @Override
    public void unregisterReceivers() {
        try {
            unregisterReceiver(mNotificationReceiver);
            unregisterReceiver(songFinishedReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initReceivers() {
        mNotificationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent != null) {
                    presenter.onNotificationBroadcast(intent.getStringExtra(IConstants.FRND_TRACK_ID),
                    intent.getIntExtra(IConstants.FRND_MESSAGE_TYPE, IDbConstants.TYPE_MESSAGE),
                                    intent.getStringExtra(IConstants.FRND_MESSAGE),
                                    intent.getStringExtra(IConstants.FRND_ID),
                                    intent.getStringExtra(IConstants.FRND_TRACK_URL),
                                    intent.getStringExtra(IConstants.FRND_TRACK_TITLE),
                                    intent.getLongExtra(IConstants.FRND_TIME_STAMP, 0L));
                }
            }
        };

        songFinishedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent != null) {
                    @IDetailsConstants.CurrentSongStatusType int currentPlayingStatus =
                            intent.getIntExtra(IDetailsConstants.CURRENT_SONG_STATUS, IDetailsConstants.CURRENT_SONG_STATUS_STOP);
                    FrndsLog.e(currentPlayingStatus + "");
                    presenter.onSongBroadCastReceived(currentPlayingStatus);
                }
            }
        };
    }

    @Override
    public void showPlayingIndicator() {
        songPlayedIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void hidePlayingIndicator() {
        songPlayedIndicator.setVisibility(View.GONE);
    }

    @Override
    public void animatePlaylistButton() {
        btnPlaylist.setAnimation(rotateAnimation);
    }

    @Override
    public void stopAnimatePlaylistButton() {
        if(btnPlaylist.getAnimation() != null) {
            btnPlaylist.getAnimation().cancel();
        }
    }

    @Override
    public void setupRecyclerView() {
        mAlbumListAdapter = new AlbumListAdapter(this);
        mChatAdapter = new ChatAdapter(this);

        mChatAdapter.setOnPlayClickListener(this);

        mChatLayoutManager = new LinearLayoutManager(this);
        mChatLayoutManager.setStackFromEnd(true);
        mChatLayoutManager.setReverseLayout(false);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rlAlbums);

        rlChat.setAdapter(mChatAdapter);
        rlChat.setLayoutManager(mChatLayoutManager);
        rlAlbums.setAdapter(mAlbumListAdapter);
        rlAlbums.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public void setupToolbar(String frndImageUrl, String frndName) {
        Glide.with(this)
                .load(frndImageUrl)
                .into(ivFrndAvatar);

        tvTitle.setText(frndName);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if(intent != null) {
            String frndId = intent.getStringExtra(IDetailsConstants.FRND_ID);
            presenter.onNewIntent(frndId);

            presenter.dataFromChat();
        }
    }

    private void getDataFromBundle() {
        if(getIntent() != null) {
            String source = getIntent().getStringExtra(IDetailsConstants.SOURCE_TYPE);
            presenter.dataFromBundle(getIntent().getIntExtra(IDetailsConstants.CLICKED_POSITION, 0),
                    FrndsPreference.getFromPref(IPrefConstants.FB_USER_ID, ""),
                    source, getIntent().getStringExtra(IDetailsConstants.FRND_ID),
                    getString(R.string.current_user_placeholder),
                    getString(R.string.music_chat_message));

            if(IDetailsConstants.SOURCE_NOTIFICATION.equalsIgnoreCase(source)) {
                presenter.dataFromNotification(getIntent().getStringExtra(IFCMConstants.NOTIF_FRIEND_NAME),
                        getIntent().getStringExtra(IFCMConstants.NOTIF_TRACK_ID),
                        getIntent().getStringExtra(IFCMConstants.NOTIF_TRACK_NAME),
                        getIntent().getStringExtra(IFCMConstants.NOTIF_TRACK_URL),
                        getIntent().getStringExtra(IFCMConstants.NOTIF_TRACK_IMAGE_URL),
                        getIntent().getStringExtra(IFCMConstants.NOTIF_TRACK_ARTIST),
                        getIntent().getStringExtra(IFCMConstants.NOTIF_MESSAGE),
                        getIntent().getStringExtra(IFCMConstants.NOTIF_TYPE),
                        getIntent().getStringExtra(IFCMConstants.NOTIF_TIMESTAMP));
            } else {
                presenter.dataFromChat();
            }
        }
    }

    @OnClick({R.id.btnMusic, R.id.btnMessageBoxMusic})
    public void addMusic() {
        presenter.onAddMusicClicked();
    }

    @Override
    public void startSongSearch() {
        Intent searchScreenIntent = new Intent(this, SearchScreenActivity.class);
        startActivityForResult(searchScreenIntent, IDetailsConstants.SONG_DETAILS_REQUEST);
    }

    @OnClick(R.id.back)
    public void back() {
        presenter.onBackPress();
        finish();
    }

    @Override
    public void backPressed(int position, String latestSongTrackName, String latestSongAlbumUrl,
                            String latestMessage, long latestTimestamp, String frndId) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(IDetailsConstants.CLICKED_POSITION, position);
        resultIntent.putExtra(IDetailsConstants.LATEST_MESSAGE, latestMessage);
        resultIntent.putExtra(IDetailsConstants.LATEST_IMAGE_TRACK, latestSongTrackName);
        resultIntent.putExtra(IDetailsConstants.LATEST_IMAGE_URL, latestSongAlbumUrl);
        resultIntent.putExtra(IDetailsConstants.LATEST_MESSAGE_TIMESTAMP, latestTimestamp);
        resultIntent.putExtra(IDetailsConstants.FRND_ID, frndId);
        setResult(RESULT_OK, resultIntent);
    }

    @Override
    public void addSongToAlbumAdapter(SongModel song) {
        mAlbumListAdapter.addSong(song);
    }

    @OnClick(R.id.btnSend)
    public void addMessage() {
        long timestamp = System.currentTimeMillis();
        presenter.onSendButtonClicked(etMessage.getText().toString(), timestamp);
    }

    @Override
    public void addMessageToAdapter(MessageModel model) {
        mChatAdapter.addMessage(model);
    }

    @Override
    public void emptyMessageBox() {
        etMessage.setText("");
    }

    @OnClick(R.id.btnPlaylist)
    public void togglePlaylist() {
        if(playlist.getVisibility() == View.VISIBLE) {
            circularCollapse();
        } else {
            circularReveal();
        }
    }

    @Override
    public void circularCollapse() {
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

    @Override
    public void circularReveal() {
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
                    presenter.onSongSearchResultReceived(data.getExtras().getLong(IDetailsConstants.TRACK_ID),
                            data.getExtras().getString(IDetailsConstants.TRACK_URL),
                            data.getExtras().getString(IDetailsConstants.TRACK_IMAGE_URL),
                            data.getExtras().getString(IDetailsConstants.TRACK_TITLE),
                            data.getExtras().getString(IDetailsConstants.TRACK_ARTIST));
                }
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setBackGroundImage(String trackImageUrl) {
        ivAlbumBg.setImageUrl(this, trackImageUrl);
    }

    @Override
    public void setTrackTitle(String trackTitle) {
        tvTrackTitle.setText(trackTitle);
    }

    @Override
    public void setTrackArtist(String trackArtist) {
        tvTrackArtist.setText(trackArtist);
    }

    @Override
    public void startPlayingTrack(String trackTitle, String trackUrl, String frndId) {
        Intent playerServiceIntent = new Intent(this, PlayerService.class);
        playerServiceIntent.setAction(PlayerService.ACTION_PLAY);
        playerServiceIntent.putExtra(IDetailsConstants.NOTIFICATION_SERVICE_TRACK_TITLE, trackTitle);
        playerServiceIntent.putExtra(IDetailsConstants.SERVICE_STREAM_URL, trackUrl);
        playerServiceIntent.putExtra(IDetailsConstants.FRND_ID, frndId);
        startService(playerServiceIntent);
    }

    @Override
    public void onFocusChange(View view, boolean isFocus) {
        presenter.onFocusChange(isFocus);
    }

    @Override
    public void messageContainerLeftSlide() {
        messageContainer.startAnimation(leftSlide);
    }

    @Override
    public void messageContainerRightSide() {
        messageContainer.startAnimation(leftSlide);
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
    public void scrollToAlbumPosition(int position) {
        rlAlbums.smoothScrollToPosition(position);
    }

    @Override
    public void showPendingChat() {
        pendingChat.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPlayClick(String trackId, String trackUrl, String trackMessage, int position) {
        presenter.onChatRowPlayClick(trackId, trackUrl, trackMessage, position);
    }
}
