package com.halfplatepoha.frnds.friendslist.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.friendslist.IFrndsConstants;
import com.halfplatepoha.frnds.friendslist.activity.FriendDetailDialogActivity;
import com.halfplatepoha.frnds.models.User;
import com.halfplatepoha.frnds.ui.OpenSansTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by surajkumarsau on 07/09/16.
 */
public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.FriendViewHolder>{

    private Context mContext;
    private ArrayList<User> mFriends;

    public FriendsListAdapter(Context context) {
        mContext = context;
        mFriends = new ArrayList<>();
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View friendRowView = LayoutInflater.from(mContext).inflate(R.layout.row_frnds_list, parent, false);
        return new FriendViewHolder(friendRowView);
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
//        if(mFriends.get(position) != null){
//            Picasso.with(mContext)
//                    .load(mFriends.get(position).getImageUrl())
//                    .into(holder.ivFrndAvatar);
//
//            holder.tvFrndName.setText(mFriends.get(position).getName());
//
//            switch (mFriends.get(position).getStatus()) {
//                case IFrndsConstants.STATUS_PLAYING:{
//                    holder.tvFrndStatus.setText(mFriends.get(position).getLastMessage());
//                }
//                break;
//
//                case IFrndsConstants.STATUS_TEXTING:{
//                    holder.tvFrndStatus.setText(mFriends.get(position).getLastMessage());
//                }
//                break;
//            }
//        }
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @Bind(R.id.ivFrndAvatar) CircleImageView ivFrndAvatar;
        @Bind(R.id.tvFrndName) OpenSansTextView tvFrndName;
        @Bind(R.id.tvFrndStatus) OpenSansTextView tvFrndStatus;

        public FriendViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            ivFrndAvatar.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ivFrndAvatar:{
                    Intent detailsIntent = new Intent(mContext, FriendDetailDialogActivity.class);
                    ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            (AppCompatActivity)mContext,
                            new Pair<View, String>(ivFrndAvatar, IFrndsConstants.FRIEND_AVATAR_TRANSITION),
                            new Pair<View, String>(tvFrndName, IFrndsConstants.FRIEND_NAME_TRANSITION)
                    );
                    ActivityCompat.startActivity((AppCompatActivity)mContext, detailsIntent, activityOptions.toBundle());
                }
                break;
            }
        }
    }

}
