package com.halfplatepoha.frnds.detail.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.halfplatepoha.frnds.FrndsLog;
import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.db.models.Song;
import com.halfplatepoha.frnds.detail.IDetailsConstants;
import com.halfplatepoha.frnds.detail.activity.SongDetailActivity;
import com.halfplatepoha.frnds.detail.fragment.ShareSongFragment;
import com.halfplatepoha.frnds.ui.GlideImageView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by surajkumarsau on 31/08/16.
 */
public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.AlbumListViewHolder> {

    private Context mContext;
    private ArrayList<Song> albums;

    private FragmentManager mFragmentManager;

    private int mPlayingPosition;

    public AlbumListAdapter(Context context) {
        mContext = context;
        albums = new ArrayList<>();
        mFragmentManager = ((SongDetailActivity)mContext).getSupportFragmentManager();
    }

    @Override
    public AlbumListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View albumView = LayoutInflater.from(mContext).inflate(R.layout.row_album_art, parent, false);
        return new AlbumListViewHolder(albumView);
    }

    @Override
    public void onBindViewHolder(AlbumListViewHolder holder, int position) {
        if (albums.get(position) != null) {
            FrndsLog.e(albums.get(position).getSongImgUrl());
            holder.ivAlbum.setImageUrl(mContext, albums.get(position).getSongImgUrl());
        }
    }

    public void addSong(Song song) {
        if(albums == null)
            albums = new ArrayList<>();
        albums.add(song);
        notifyItemInserted(albums.size() - 1);
    }

    public void refresh() {
        if(albums != null) {
            albums.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public class AlbumListViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.ivAlbum) GlideImageView ivAlbum;

        public AlbumListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.ivAlbum)
        public void openSongDetailsDialog() {
            ShareSongFragment shareSong = new ShareSongFragment();
            mFragmentManager.beginTransaction()
                    .replace(R.id.home, shareSong)
                    .addToBackStack(IDetailsConstants.SONG_SHARE_TAG)
                    .commit();
        }
    }
}
