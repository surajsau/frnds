package com.halfplatepoha.frnds.friendslist.fragment;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.detail.IDetailsConstants;
import com.halfplatepoha.frnds.detail.activity.SongDetailActivity;
import com.halfplatepoha.frnds.ui.OpenSansTextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FriendDetailDialogFragment extends DialogFragment {

    @Bind(R.id.ivFrndAvatar) ImageView ivFrndAvatar;
    @Bind(R.id.tvFrndName) OpenSansTextView tvFrndName;
    @Bind(R.id.btnPlay) FloatingActionButton btnPlay;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.AppDialogTheme);
    }

    @OnClick(R.id.btnPlay)
    public void openSearchScreen() {
        Intent searchIntent = new Intent(getActivity(), SongDetailActivity.class);
        searchIntent.putExtra(IDetailsConstants.SOURCE_TYPE, IDetailsConstants.SOURCE_FAB);
        startActivity(searchIntent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dlgView  = inflater.inflate(R.layout.fragment_friend_detail_dialog, container, false);
        return dlgView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
    }
}
