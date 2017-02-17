package com.halfplatepoha.frnds.detail.fragment;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.halfplatepoha.frnds.FrndsLog;
import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.detail.adapter.FrndsShareSuggestionListAdapter;
import com.halfplatepoha.frnds.detail.model.SongModel;
import com.halfplatepoha.frnds.home.model.TracksModel;
import com.halfplatepoha.frnds.ui.GlideImageView;
import com.halfplatepoha.frnds.ui.OpenSansTextView;
import com.halfplatepoha.frnds.utils.AppUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShareSongFragment extends Fragment {

    @Bind(R.id.ivAlbum)
    GlideImageView ivAlbum;

    @Bind(R.id.tvAlbumArtist)
    OpenSansTextView tvAlbumArtist;

    @Bind(R.id.tvAlbumTitle)
    OpenSansTextView tvAlbumTitle;

    private SongModel songModel;
    private TracksModel tracksModel;

    private StringBuilder shareMsg;

    public ShareSongFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share_song, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        ivAlbum.setImageUrl(getActivity(), songModel.getTrackImageUrl());
        tvAlbumArtist.setText(songModel.getTrackUser());
        tvAlbumTitle.setText(songModel.getTrackName());
    }

    public void setSongModel(SongModel songModel) {
        this.songModel = songModel;

        shareMsg = new StringBuilder("Check out ");
        shareMsg.append(songModel.getTrackName())
                .append(" on SoundCloud ")
                .append(songModel.getTrackShareUrl())
                .append("Sync with your friends, download from ")
                .append("https://play.google.com/store/apps/details?id=com.whatsapp");
    }

    public void setSongModel(TracksModel trackModel) {
        this.tracksModel = trackModel;

        shareMsg = new StringBuilder("Check out ");
        shareMsg.append(tracksModel.getTrackName())
                .append(" on SoundCloud ")
                .append(tracksModel.getTrackShareUrl())
                .append("Sync with your friends, download from ")
                .append("https://play.google.com/store/apps/details?id=com.whatsapp");
    }

    @OnClick(R.id.back)
    public void close() {
        AppUtil.hideSoftKeyboard(getActivity());
        getActivity().onBackPressed();
    }

    @OnClick(R.id.btnShare)
    public void onShareClick() {
        FrndsLog.e(shareMsg.toString());
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, shareMsg.toString() );
        try {
            startActivity(whatsappIntent);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "WhatsApp isn\'t installed", Toast.LENGTH_SHORT).show();
        }
    }
}
