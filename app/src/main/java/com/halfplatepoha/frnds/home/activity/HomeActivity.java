package com.halfplatepoha.frnds.home.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.halfplatepoha.frnds.FrndsPreference;
import com.halfplatepoha.frnds.IConstants;
import com.halfplatepoha.frnds.IPrefConstants;
import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.db.IDbConstants;
import com.halfplatepoha.frnds.home.IFrndsConstants;
import com.halfplatepoha.frnds.home.fragment.FriendsListFragment;
import com.halfplatepoha.frnds.home.fragment.SettingsFragment;
import com.halfplatepoha.frnds.home.fragment.SoundCloudSearchFragment;
import com.halfplatepoha.frnds.home.fragment.TracksListFragment;
import com.halfplatepoha.frnds.models.fb.InstalledFrnds;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements OnTabSelectListener {

    @Bind(R.id.bottomBar) BottomBar bottomBar;

    private FriendsListFragment mFriendsListFragment;
    private SoundCloudSearchFragment mSoundCloudSearchFragment;
    private TracksListFragment mTracksListFragment;
    private SettingsFragment mSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        ButterKnife.bind(this);

        bottomBar.setDefaultTab(R.id.tabChat);
        bottomBar.setOnTabSelectListener(this);

        registerReceiver(notificationReceiver, new IntentFilter(IConstants.CHAT_BROADCAST));
    }

    private void updateFrndsList(InstalledFrnds frnds) {
        if(frnds != null && frnds.getData() != null && frnds.getData().size() > 0) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FrndsPreference.setInPref(IPrefConstants.SCREEN_TYPE, IConstants.SCREEN_LISTING);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FrndsPreference.setInPref(IPrefConstants.SCREEN_TYPE, IConstants.SCREEN_NONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case IFrndsConstants.FRIEND_LIST_REQUEST:{

            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null) {
                String trackId = intent.getStringExtra(IConstants.FRND_TRACK_ID);
                int messageType = intent.getIntExtra(IConstants.FRND_MESSAGE_TYPE, IDbConstants.TYPE_MESSAGE);
                String message = intent.getStringExtra(IConstants.FRND_MESSAGE);
                String frndId = intent.getStringExtra(IConstants.FRND_ID);
                String trackTitle = intent.getStringExtra(IConstants.FRND_TRACK_TITLE);

                mFriendsListFragment.refreshChatDetails(frndId, messageType, message, trackId, trackTitle);
            }
        }
    };

    @Override
    public void onTabSelected(@IdRes int tabId) {
        switch (tabId) {
            case R.id.tabChat:
                if(mFriendsListFragment == null)
                    mFriendsListFragment = new FriendsListFragment();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentHome, mFriendsListFragment)
                        .commit();
                break;

            case R.id.tabListen:
                if(mSoundCloudSearchFragment == null)
                    mSoundCloudSearchFragment = new SoundCloudSearchFragment();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentHome, mSoundCloudSearchFragment)
                        .commit();
                break;

            case R.id.tabTracks:
                if(mTracksListFragment == null)
                    mTracksListFragment = new TracksListFragment();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentHome, mTracksListFragment)
                        .commit();
                break;

            case R.id.tabSettings:
                if(mSettingsFragment == null)
                    mSettingsFragment = new SettingsFragment();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentHome, mSettingsFragment)
                        .commit();
                break;
        }
    }

    //TODO: animate colour of title like bottom bar
    private void animateTitle() {

    }
}
