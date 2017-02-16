package com.halfplatepoha.frnds.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.home.model.TracksModel;
import com.halfplatepoha.frnds.ui.GlideImageView;
import com.halfplatepoha.frnds.ui.OpenSansTextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by surajkumarsau on 15/02/17.
 */

public class TracksListAdapter extends RecyclerView.Adapter<TracksListAdapter.TracksViewHolder> {

    private ArrayList<TracksModel> songs;

    private Context context;

    public TracksListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public TracksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(context).inflate(R.layout.row_tracks, parent, false);
        return new TracksViewHolder(row);
    }

    @Override
    public void onBindViewHolder(TracksViewHolder holder, int position) {
        if(songs.get(position) != null) {
            holder.tvPlayerTrackUser.setText(String.format("with %s", songs.get(position).getTrackUser()));
            holder.tvPlayerTrackTitle.setText(songs.get(position).getTrackName());
            holder.ivPlayerAlbum.setImageUrl(context, songs.get(position).getTrackImageUrl());

            Glide.with(context)
                    .load(songs.get(position).getFrndImageUrl())
                    .error(R.drawable.pappi)
                    .into(holder.ivFrndAvatar);
        }
    }

    @Override
    public int getItemCount() {
        if(songs != null)
            return songs.size();
        return 0;
    }

    public void addSongs(TracksModel song) {
        if(songs == null)
            songs = new ArrayList<>();
        songs.add(song);
        notifyItemInserted(songs.size() - 1);
    }

    public class TracksViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tvPlayerTrackTitle)
        OpenSansTextView tvPlayerTrackTitle;

        @Bind(R.id.tvPlayerTrackUser)
        OpenSansTextView tvPlayerTrackUser;

        @Bind(R.id.ivPlayerAlbum)
        GlideImageView ivPlayerAlbum;

        @Bind(R.id.ivFrndAvatar)
        CircleImageView ivFrndAvatar;

        public TracksViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
