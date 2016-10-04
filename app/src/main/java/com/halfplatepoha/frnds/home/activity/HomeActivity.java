package com.halfplatepoha.frnds.home.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.halfplatepoha.frnds.FrndsPreference;
import com.halfplatepoha.frnds.IConstants;
import com.halfplatepoha.frnds.IPrefConstants;
import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.db.IDbConstants;
import com.halfplatepoha.frnds.home.IFrndsConstants;
import com.halfplatepoha.frnds.home.adapter.TabPagerAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity {

    @Bind(R.id.pager) ViewPager pager;
    @Bind(R.id.tabs) TabLayout tabs;

    private TabPagerAdapter mTabAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        ButterKnife.bind(this);

        setupPager();

        registerReceiver(notificationReceiver, new IntentFilter(IConstants.CHAT_BROADCAST));
    }

    private void setupPager() {
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
                super.onTabSelected(tab);
                DrawableCompat.setTint(ContextCompat.getDrawable(HomeActivity.this, IFrndsConstants.tabDrawables[tab.getPosition()]),
                        ContextCompat.getColor(HomeActivity.this, R.color.colorPrimaryLight));
                tab.setIcon(IFrndsConstants.tabDrawables[tab.getPosition()]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                DrawableCompat.setTint(ContextCompat.getDrawable(HomeActivity.this, IFrndsConstants.tabDrawables[tab.getPosition()]),
                        ContextCompat.getColor(HomeActivity.this, R.color.colorPrimaryDark));
                tab.setIcon(IFrndsConstants.tabDrawables[tab.getPosition()]);
            }
        });

        tabs.getTabAt(0).select();
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

//                mFriendsListFragment.refreshChatDetails(frndId, messageType, message, trackId, trackTitle);
            }
        }
    };

    @OnClick(R.id.btnProfile)
    public void openProfilesAndSettings() {
        Intent profileIntent = new Intent(this, ProfileAndSettingsActivity.class);
        startActivity(profileIntent);
    }

}
