package com.halfplatepoha.frnds.friendslist.activity;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.friendslist.IFrndsConstants;
import com.halfplatepoha.frnds.friendslist.adapter.FriendsListAdapter;
import com.halfplatepoha.frnds.friendslist.fragment.FriendsListFragment;
import com.halfplatepoha.frnds.friendslist.fragment.SoundCloudSearchFragment;
import com.halfplatepoha.frnds.friendslist.fragment.TracksListFragment;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FriendsListActivity extends AppCompatActivity implements OnTabSelectListener {

    @Bind(R.id.bottomBar) BottomBar bottomBar;

    private FriendsListFragment mFriendsListFragment;
    private SoundCloudSearchFragment mSoundCloudSearchFragment;
    private TracksListFragment mTracksListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        ButterKnife.bind(this);

        bottomBar.setDefaultTab(R.id.tabChat);
        bottomBar.setOnTabSelectListener(this);

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
        }
    }

    //TODO: animate colour of title like bottom bar
    private void animateTitle() {

    }
}
