package com.halfplatepoha.frnds.friendslist.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.friendslist.IFrndsConstants;
import com.halfplatepoha.frnds.friendslist.adapter.FriendsListAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FriendsListActivity extends AppCompatActivity {

    private FriendsListAdapter mAdapter;

    @Bind(R.id.rlFrnds) RecyclerView rlFrnds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        ButterKnife.bind(this);

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        mAdapter = new FriendsListAdapter(this);
        rlFrnds.setLayoutManager(new LinearLayoutManager(this));
        rlFrnds.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case IFrndsConstants.FRIEND_LIST_REQUEST:{

            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
