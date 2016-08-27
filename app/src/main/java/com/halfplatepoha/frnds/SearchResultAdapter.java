package com.halfplatepoha.frnds;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.halfplatepoha.frnds.network.TrackDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by surajkumarsau on 25/08/16.
 */
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder> {

    private Context mContext;

    private ArrayList<TrackDetails> mList;

    public SearchResultAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    @Override
    public SearchResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(mContext).inflate(R.layout.row_search_result, parent, false);
        return new SearchResultViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(SearchResultViewHolder holder, int position) {
        if(mList.get(position) != null) {
            holder.tvTitle.setText(mList.get(position).getTitle());
            Picasso.with(mContext)
                    .load(mList.get(position).getArtwork_url())
                    .into(holder.ivResultIcon);
            holder.row.setTag(mList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void refreshList() {
        if(!mList.isEmpty()) {
            int size = mList.size();

            mList.clear();
            notifyItemRangeRemoved(0, size - 1);
        }
    }

    public void addItemToList(TrackDetails details) {
        mList.add(details);
        notifyItemInserted(mList.size() - 1);
    }

    public class SearchResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @Bind(R.id.ivResultIcon) ImageView ivResultIcon;
        @Bind(R.id.tvTitle) TextView tvTitle;
        @Bind(R.id.row) View row;

        public SearchResultViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            row.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.row:
                    TrackDetails trackDetails = (TrackDetails) view.getTag();
                    openSongDetailsActivity(trackDetails);
                    break;
            }
        }
    }

    private void openSongDetailsActivity(TrackDetails trackDetails) {
        Intent songDetail = new Intent(mContext, SongDetailActivity.class);
        songDetail.putExtra(IConstants.TRACK_ID, trackDetails.getId());
        songDetail.putExtra(IConstants.ICON_URL, trackDetails.getArtwork_url());
        songDetail.putExtra(IConstants.TRACK_TITLE, trackDetails.getTitle());
        mContext.startActivity(songDetail);
    }
}