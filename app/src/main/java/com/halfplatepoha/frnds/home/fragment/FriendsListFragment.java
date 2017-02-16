package com.halfplatepoha.frnds.home.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.halfplatepoha.frnds.FrndsLog;
import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.TokenTracker;
import com.halfplatepoha.frnds.db.ChatDAO;
import com.halfplatepoha.frnds.db.IDbConstants;
import com.halfplatepoha.frnds.db.models.Chat;
import com.halfplatepoha.frnds.detail.IDetailsConstants;
import com.halfplatepoha.frnds.detail.activity.SongDetailActivity;
import com.halfplatepoha.frnds.home.IFrndsConstants;
import com.halfplatepoha.frnds.home.adapter.FriendsListAdapter;
import com.halfplatepoha.frnds.home.model.ChatListingModel;
import com.halfplatepoha.frnds.models.fb.InstalledFrnds;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class FriendsListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        ChatDAO.OnTransactionCompletedListener, FriendsListAdapter.OnFriendRowClickListener{

    @Bind(R.id.rlFrnds) RecyclerView rlFrnds;
    @Bind(R.id.refreshLayout) SwipeRefreshLayout refreshLayout;

    private FriendsListAdapter mAdapter;

    private ChatDAO helper;

    public FriendsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new ChatDAO(Realm.getDefaultInstance());
        helper.setOnTransactionCompletedListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        setupRecyclerView();

        getListFromDb();
    }

    private void getListFromDb() {
        RealmResults<Chat> chats = helper.getAllChats();
        for(int i=0; i<chats.size(); i++) {
            if(chats.get(i).getFrndPosition() == null)
                helper.updateChatPosition(chats.get(i), i);
            mAdapter.addChat(chats.get(i));
        }
    }

    private void setupRecyclerView() {
        mAdapter = new FriendsListAdapter(getActivity());
        mAdapter.setOnFriendRowClickListener(this);
        rlFrnds.setLayoutManager(new LinearLayoutManager(getActivity()));
        rlFrnds.setAdapter(mAdapter);

        refreshLayout.setEnabled(true);
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorAccent)
                , ContextCompat.getColor(getActivity(), R.color.soundCloud));
        refreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        updateFrndsList();
    }

    private void updateFrndsList() {
        AccessToken token = AccessToken.getCurrentAccessToken();
        if(token == null)
            token = TokenTracker.getInstance().getCurrentAccessToken();

        Bundle params = new Bundle();
        params.putString("fields", "installed, id, name, picture.type(large)");

        new GraphRequest(token, "/me/friends", params, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                if(response.getError() == null) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        FrndsLog.e(response.getRawResponse());
                        InstalledFrnds frnds = mapper.readValue(response.getRawResponse(), InstalledFrnds.class);
                        helper.updateChatList(frnds, IDbConstants.UPDATE_FRND_LIST_TRANSACTION_ID);
                    }  catch (JsonMappingException e) {
                        e.printStackTrace();
                    } catch (JsonParseException e) {
                        e.printStackTrace();
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    //TODO: error case
                }
            }
        }).executeAsync();
    }

    @Override
    public void onStop() {
        super.onStop();
//        mRealm.close();
    }

    @Override
    public void onTransactionComplete(int transcationId) {
        switch (transcationId) {
            case IDbConstants.UPDATE_FRND_LIST_TRANSACTION_ID:
                refreshLayout.setRefreshing(false);
        }
    }

    public void refreshChatDetails(int position, String message, long timestamp) {
        mAdapter.refreshChat(position, message, timestamp);
    }

    @Override
    public void onFriendRowClicked(ActivityOptionsCompat options, String frndId, int position) {
        Intent songDetailsIntent = new Intent(getActivity(), SongDetailActivity.class);
        songDetailsIntent.putExtra(IDetailsConstants.FRND_ID, frndId);
        songDetailsIntent.putExtra(IDetailsConstants.CLICKED_POSITION, position);
        startActivityForResult(songDetailsIntent, IFrndsConstants.FRIEND_LIST_REQUEST, options.toBundle());
    }

    @Override
    public void openDetailDialog(ChatListingModel frnd) {
        FriendDetailDialogFragment dlgDetail = new FriendDetailDialogFragment();
        Bundle dlgBundle = new Bundle();
        dlgBundle.putString(IFrndsConstants.FRIEND_NAME, frnd.getFrndName());
        dlgBundle.putString(IFrndsConstants.FRIEND_IMAGE_URL, frnd.getFrndImageUrl());
        dlgDetail.setArguments(dlgBundle);
        dlgDetail.show(getFragmentManager(), IFrndsConstants.DETAIL_DIALOG_TAG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case IFrndsConstants.FRIEND_LIST_REQUEST: {
                if (data != null && resultCode == Activity.RESULT_OK) {
                    String latestMessage = data.getStringExtra(IDetailsConstants.LATEST_MESSAGE);
                    String frndId = data.getStringExtra(IDetailsConstants.FRND_ID);
                    int position = data.getIntExtra(IDetailsConstants.CLICKED_POSITION, 0);
                    long latestTimestamp = data.getLongExtra(IDetailsConstants.LATEST_MESSAGE_TIMESTAMP, 0L);

                    refreshChatDetails(position, latestMessage, latestTimestamp);
                }
            }
            break;
        }
    }

}
