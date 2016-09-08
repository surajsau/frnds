package com.halfplatepoha.frnds.detail.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.halfplatepoha.frnds.FrndsLog;
import com.halfplatepoha.frnds.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by surajkumarsau on 31/08/16.
 */
public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.AlbumListViewHolder>{

    private Context mContext;
    private ArrayList<String> albums;

    public AlbumListAdapter(Context context) {
        mContext = context;
        albums = new ArrayList<>();
    }

    @Override
    public AlbumListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View albumView = LayoutInflater.from(mContext).inflate(R.layout.row_album_art, parent, false);
        return new AlbumListViewHolder(albumView);
    }

    @Override
    public void onBindViewHolder(AlbumListViewHolder holder, int position) {
        holder.ivAlbum.setBackgroundResource(R.drawable.warriors);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class AlbumListViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.ivAlbum) ImageView ivAlbum;

        public AlbumListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
