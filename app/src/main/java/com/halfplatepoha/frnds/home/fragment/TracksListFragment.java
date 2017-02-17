package com.halfplatepoha.frnds.home.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.db.ChatDAO;
import com.halfplatepoha.frnds.db.models.Song;
import com.halfplatepoha.frnds.detail.IDetailsConstants;
import com.halfplatepoha.frnds.detail.fragment.ShareSongFragment;
import com.halfplatepoha.frnds.home.adapter.TracksListAdapter;
import com.halfplatepoha.frnds.home.model.TracksModel;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by surajkumarsau on 15/09/16.
 */
public class TracksListFragment extends Fragment implements TracksListAdapter.OnAlbumClickListener{

    @Bind(R.id.rlTracks)
    RecyclerView rlTracks;

    private TracksListAdapter mAdapter;

    private ChatDAO helper;

    public TracksListFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new ChatDAO(Realm.getDefaultInstance());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tracks_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        setupRecycler();
    }

    @Override
    public void onResume() {
        super.onResume();
        addDataToTracks();
    }

    private void addDataToTracks() {
        mAdapter.clearList();

        RealmResults<Song> songs = helper.getAllSongs();
        if(songs != null) {
            for (int i = 0; i < songs.size(); i++) {
                TracksModel model = new TracksModel();
                model.setTrackImageUrl(songs.get(i).getSongImgUrl());
                model.setTrackUrl(songs.get(i).getSongUrl());
                model.setTrackName(songs.get(i).getSongTitle());
                model.setTrackUser(songs.get(i).getSongArtist());
                model.setTrackShareUrl(songs.get(i).getSongShareUrl());
                model.setFrndImageUrl(helper.getChatWithFrndId(songs.get(i).getFrndId()).getFrndImageUrl());

                mAdapter.addSongs(model);
            }
        }
    }

    private void setupRecycler() {
        rlTracks.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        SnapHelper snapHelper = new LinearSnapHelper();

        mAdapter = new TracksListAdapter(getActivity());
        mAdapter.setListener(this);
        rlTracks.setAdapter(mAdapter);
        snapHelper.attachToRecyclerView(rlTracks);
    }

    @Override
    public void onAlbumClick(TracksModel model) {
        ShareSongFragment shareSong = new ShareSongFragment();
        shareSong.setSongModel(model);
        getFragmentManager().beginTransaction()
                .replace(R.id.home, shareSong)
                .addToBackStack(IDetailsConstants.SONG_SHARE_TAG)
                .commit();
    }
}
