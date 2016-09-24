package com.halfplatepoha.frnds.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.db.models.Chat;
import com.halfplatepoha.frnds.detail.activity.SongDetailActivity;
import com.halfplatepoha.frnds.home.IFrndsConstants;
import com.halfplatepoha.frnds.home.fragment.FriendDetailDialogFragment;
import com.halfplatepoha.frnds.home.activity.HomeActivity;
import com.halfplatepoha.frnds.models.User;
import com.halfplatepoha.frnds.ui.OpenSansTextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by surajkumarsau on 07/09/16.
 */
public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.FriendViewHolder>{

    private Context mContext;
    private ArrayList<Chat> mFriends;
    private FragmentManager mFragmentManager;

    public FriendsListAdapter(Context context) {
        mContext = context;

        mFriends = new ArrayList<>();

        mFragmentManager = ((HomeActivity)mContext).getSupportFragmentManager();
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View friendRowView = LayoutInflater.from(mContext).inflate(R.layout.row_frnds_list, parent, false);
        return new FriendViewHolder(friendRowView);
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        if(mFriends.get(position) != null){
            Glide.with(mContext)
                    .load(mFriends.get(position).getFrndImageUrl())
                    .error(R.drawable.pappi)
                    .into(holder.ivFrndAvatar);

            holder.tvFrndName.setText(mFriends.get(position).getFrndName());
            holder.ivFrndAvatar.setTag(mFriends.get(position).getFrndImageUrl());

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
        }
    }

    public void addChat(Chat chat) {
        mFriends.add(chat);
        notifyItemInserted(mFriends.size() - 1);
    }

    @Override
    public int getItemCount() {
        if(mFriends == null)
            return 0;
        return mFriends.size();
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.ivFrndAvatar) CircleImageView ivFrndAvatar;
        @Bind(R.id.tvFrndName) OpenSansTextView tvFrndName;
        @Bind(R.id.tvFrndStatus) OpenSansTextView tvFrndStatus;

        public FriendViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.rowFriend)
        public void openSongDetails() {
            Intent songDetailsIntent = new Intent(mContext, SongDetailActivity.class);
            ((HomeActivity)mContext).startActivityForResult(songDetailsIntent, IFrndsConstants.FRIEND_LIST_REQUEST);
        }

        @OnClick(R.id.ivFrndAvatar)
        public void openDetailDialog() {
            FriendDetailDialogFragment dlgDetail = new FriendDetailDialogFragment();
            Bundle dlgBundle = new Bundle();
            dlgBundle.putString(IFrndsConstants.FRIEND_NAME, tvFrndName.getText().toString());
            dlgBundle.putString(IFrndsConstants.FRIEND_IMAGE_URL, ivFrndAvatar.getTag().toString());
            dlgDetail.setArguments(dlgBundle);
            dlgDetail.show(mFragmentManager, IFrndsConstants.DETAIL_DIALOG_TAG);
        }

    }

}
