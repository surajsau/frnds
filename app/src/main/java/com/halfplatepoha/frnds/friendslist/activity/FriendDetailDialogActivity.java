package com.halfplatepoha.frnds.friendslist.activity;

import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.friendslist.IFrndsConstants;
import com.halfplatepoha.frnds.ui.OpenSansTextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FriendDetailDialogActivity extends AppCompatActivity {

    @Bind(R.id.ivFrndAvatar) ImageView ivFrndAvatar;
    @Bind(R.id.tvFrndName) OpenSansTextView tvFrndName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_detail_dialog);
        ButterKnife.bind(this);

        ViewCompat.setTransitionName(ivFrndAvatar, IFrndsConstants.FRIEND_AVATAR_TRANSITION);
        ViewCompat.setTransitionName(tvFrndName, IFrndsConstants.FRIEND_NAME_TRANSITION);
    }
}
