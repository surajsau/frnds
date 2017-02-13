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
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

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
import com.halfplatepoha.frnds.home.IFrndsConstants;
import com.halfplatepoha.frnds.home.adapter.TabPagerAdapter;
import com.halfplatepoha.frnds.home.fragment.FriendsListFragment;
import com.halfplatepoha.frnds.ui.GlideImageView;
import com.halfplatepoha.frnds.ui.OpenSansTextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class HomeActivity extends AppCompatActivity {

//    @Bind(R.id.pager) ViewPager pager;
//    @Bind(R.id.tabs) TabLayout tabs;

    @Bind(R.id.player)
    View player;

    @Bind(R.id.tvPlayerTrackFrnd)
    OpenSansTextView tvPlayerTrackFrnd;

    @Bind(R.id.tvPlayerTrackTitle)
    OpenSansTextView tvPlayerTrackTitle;

    @Bind(R.id.ivPlayerAlbum)
    GlideImageView ivPlayerAlbum;

    private ChatDAO helper;

//    private TabPagerAdapter mTabAdapter;

    private FriendsListFragment mFriendsListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        ButterKnife.bind(this);

//        setupPager();
        helper = new ChatDAO(Realm.getDefaultInstance());

        setupListFragment();

        registerReceiver(notificationReceiver, new IntentFilter(IConstants.CHAT_BROADCAST));
    }

    private void setupListFragment() {
        mFriendsListFragment = new FriendsListFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentHome, mFriendsListFragment, IFrndsConstants.HOME_FRAGMENT_TAG)
                .commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(notificationReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    private void setupPager() {
//        mTabAdapter = new TabPagerAdapter(this, getSupportFragmentManager());
//        pager.setAdapter(mTabAdapter);
//
//        tabs.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorPrimaryLight));
//        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
//        tabs.setupWithViewPager(pager);
//
//        for(int i=0; i<IFrndsConstants.tabDrawables.length; i++) {
//            tabs.getTabAt(i).setIcon(ContextCompat.getDrawable(this, IFrndsConstants.tabDrawables[i]));
//        }
//
//        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager){
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                tab.setIcon(IFrndsConstants.tabSelectedDrawables[tab.getPosition()]);
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//                tab.setIcon(IFrndsConstants.tabDrawables[tab.getPosition()]);
//            }
//        });
//
//        tabs.getTabAt(0).select();
//    }

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
                if(data != null && resultCode == RESULT_OK) {
                    String albumUrl = data.getStringExtra(IDetailsConstants.LATEST_IMAGE_URL);
                    String track = data.getStringExtra(IDetailsConstants.LATEST_IMAGE_TRACK);
                    String frndId = data.getStringExtra(IDetailsConstants.FRND_ID);

                    Chat chatResult = helper.getFrndWithFrndId(frndId);

                    ivPlayerAlbum.setImageUrl(this, albumUrl);
                    tvPlayerTrackTitle.setText(track);
                    tvPlayerTrackFrnd.setText(String.format("with %s", chatResult.getFrndName()));

                    mFriendsListFragment.refreshChatDetails(frndId, chatResult.getFrndLastMessage());
                }
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null) {
                @IDbConstants.MessageType int messageType = intent.getIntExtra(IConstants.FRND_MESSAGE_TYPE, IDbConstants.TYPE_MESSAGE);
                String message = intent.getStringExtra(IConstants.FRND_MESSAGE);
                String frndId = intent.getStringExtra(IConstants.FRND_ID);

                Message msg = new Message.Builder()
                        .setMsgBody(message)
                        .setMsgType(messageType)
                        .setUserType(IDetailsConstants.TYPE_FRND)
                        .setMsgTimestamp(System.currentTimeMillis())
                        .build();

                mFriendsListFragment.refreshChatDetails(frndId, msg);
            }
        }
    };

    @OnClick(R.id.btnProfile)
    public void openProfilesAndSettings() {
        Intent profileIntent = new Intent(this, ProfileAndSettingsActivity.class);
        startActivity(profileIntent);
    }

}
