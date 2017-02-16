package com.halfplatepoha.frnds.home.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.halfplatepoha.frnds.BaseActivity;
import com.halfplatepoha.frnds.FrndsLog;
import com.halfplatepoha.frnds.FrndsPreference;
import com.halfplatepoha.frnds.IConstants;
import com.halfplatepoha.frnds.IPrefConstants;
import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.db.ChatDAO;
import com.halfplatepoha.frnds.db.IDbConstants;
import com.halfplatepoha.frnds.db.models.Chat;
import com.halfplatepoha.frnds.db.models.Message;
import com.halfplatepoha.frnds.db.models.Song;
import com.halfplatepoha.frnds.detail.IDetailsConstants;
import com.halfplatepoha.frnds.detail.activity.SongDetailActivity;
import com.halfplatepoha.frnds.home.IFrndsConstants;
import com.halfplatepoha.frnds.home.adapter.TabPagerAdapter;
import com.halfplatepoha.frnds.home.fragment.FriendsListFragment;
import com.halfplatepoha.frnds.home.presenter.HomePresenter;
import com.halfplatepoha.frnds.home.presenter.HomePresenterImpl;
import com.halfplatepoha.frnds.home.view.HomeView;
import com.halfplatepoha.frnds.mediaplayer.PlayerService;
import com.halfplatepoha.frnds.ui.GlideImageView;
import com.halfplatepoha.frnds.ui.OpenSansTextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class HomeActivity extends BaseActivity implements HomeView {

    @Bind(R.id.pager) ViewPager pager;
    @Bind(R.id.tabs) TabLayout tabs;

    @Bind(R.id.player)
    View player;

    @Bind(R.id.tvPlayerTrackFrnd)
    OpenSansTextView tvPlayerTrackFrnd;

    @Bind(R.id.tvPlayerTrackTitle)
    OpenSansTextView tvPlayerTrackTitle;

    @Bind(R.id.ivPlayerAlbum)
    GlideImageView ivPlayerAlbum;

    @Bind(R.id.btnPlayNext)
    ImageButton btnPlayNext;

    @IDetailsConstants.CurrentSongStatusType int status;

//    @Bind(R.id.bottomSheet)
//    View bottomSheet;

    private TabPagerAdapter mTabAdapter;
    private BroadcastReceiver notificationReceiver;
    private BroadcastReceiver songStatusReceiver;

    private HomePresenter presenter;

    private Animation bottomUp;
    private Animation bottomDown;

    private BottomSheetBehavior bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        ButterKnife.bind(this);

        bottomSheetBehavior = BottomSheetBehavior.from(player);

        presenter = new HomePresenterImpl(this);

        presenter.onCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public void setupViewPager() {
        mTabAdapter = new TabPagerAdapter(this, getSupportFragmentManager());
        pager.setAdapter(mTabAdapter);

        tabs.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorPrimaryLight));
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        tabs.setupWithViewPager(pager);

        for(int i=0; i<IFrndsConstants.tabDrawables.length; i++) {
            tabs.getTabAt(i).setIcon(ContextCompat.getDrawable(this, IFrndsConstants.tabDrawables[i]));
        }

        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.setIcon(IFrndsConstants.tabSelectedDrawables[tab.getPosition()]);
                if(tab.getPosition() == 1) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setIcon(IFrndsConstants.tabDrawables[tab.getPosition()]);
            }
        });

        tabs.getTabAt(0).select();
    }

    @Override
    public void initAnimations() {
        bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        bottomUp.setInterpolator(new AccelerateDecelerateInterpolator());

        bottomDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        bottomDown.setInterpolator(new AccelerateDecelerateInterpolator());
    }

    @Override
    public void setPlayerAlbumUrl(String albumImageUrl) {
        ivPlayerAlbum.setImageUrl(this, albumImageUrl);
    }

    @Override
    public void setPlayerTrackName(String trackName) {
        tvPlayerTrackTitle.setText(trackName);
    }

    @Override
    public void setPlayerFriendName(String frndName) {
        tvPlayerTrackFrnd.setText(frndName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FrndsPreference.setInPref(IPrefConstants.SCREEN_TYPE, IConstants.SCREEN_LISTING);

        updatePlayer();
    }

    private void updatePlayer() {
        @IDetailsConstants.CurrentSongStatusType int currentStatus = FrndsPreference.getIntFromPref(IPrefConstants.CURRENT_SONG_STATUS, IDetailsConstants.CURRENT_SONG_STATUS_STOP);

        bottomSheetBehavior.setState(currentStatus == IDetailsConstants.CURRENT_SONG_STATUS_PLAYING ?
                    BottomSheetBehavior.STATE_EXPANDED : BottomSheetBehavior.STATE_COLLAPSED);

        tvPlayerTrackTitle.setText(FrndsPreference.getFromPref(IPrefConstants.LATEST_SONG_NAME, ""));
        tvPlayerTrackFrnd.setText(String.format("with %s", FrndsPreference.getFromPref(IPrefConstants.LATEST_FRND_NAME, "")));

        ivPlayerAlbum.setImageUrl(this, FrndsPreference.getFromPref(IPrefConstants.LATEST_SONG_IMAGE_URL, ""));
    }

    @Override
    protected void onPause() {
        super.onPause();
        FrndsPreference.setInPref(IPrefConstants.SCREEN_TYPE, IConstants.SCREEN_NONE);
    }

    @OnClick(R.id.btnProfile)
    public void openProfilesAndSettings() {
        Intent profileIntent = new Intent(this, ProfileAndSettingsActivity.class);
        startActivity(profileIntent);
    }

    @Override
    public void initReceivers() {
        notificationReceiver = new BroadcastReceiver() {
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

        songStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                @IDetailsConstants.CurrentSongStatusType int status = intent.getIntExtra(IDetailsConstants.CURRENT_SONG_STATUS, IDetailsConstants.CURRENT_SONG_STATUS_STOP);
                FrndsPreference.setInPref(IPrefConstants.CURRENT_SONG_STATUS, status);
            }
        };
    }

    @Override
    public void registerReceivers() {
        registerReceiver(notificationReceiver, new IntentFilter(IConstants.CHAT_BROADCAST));
    }

    @Override
    public void unregisterReceivers() {
        try {
            unregisterReceiver(notificationReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btnPlayNext)
    public void onPlayPauseClick() {
        if(status == IDetailsConstants.CURRENT_SONG_STATUS_PLAYING) {
            stopService(new Intent(this, PlayerService.class));
            btnPlayNext.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pause_circle));
            status = IDetailsConstants.CURRENT_SONG_STATUS_STOP;
        } else {
            Intent playerServiceIntent = new Intent(this, PlayerService.class);
            playerServiceIntent.setAction(PlayerService.ACTION_PLAY);
            playerServiceIntent.putExtra(IDetailsConstants.NOTIFICATION_SERVICE_TRACK_TITLE, FrndsPreference.getFromPref(IPrefConstants.LATEST_SONG_NAME, ""));
            playerServiceIntent.putExtra(IDetailsConstants.SERVICE_STREAM_URL, FrndsPreference.getFromPref(IPrefConstants.LATEST_SONG_URL, ""));
            playerServiceIntent.putExtra(IDetailsConstants.FRND_ID, FrndsPreference.getFromPref(IPrefConstants.LATEST_FRND_ID, ""));
            startService(playerServiceIntent);
        }
    }
}
