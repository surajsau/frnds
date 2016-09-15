package com.halfplatepoha.frnds.friendslist.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.friendslist.adapter.FriendsListAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FriendsListFragment extends Fragment {

    @Bind(R.id.rlFrnds) RecyclerView rlFrnds;

    private FriendsListAdapter mAdapter;

    public FriendsListFragment() {
        // Required empty public constructor
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
    }

    private void setupRecyclerView() {
        mAdapter = new FriendsListAdapter(getActivity());
        rlFrnds.setLayoutManager(new LinearLayoutManager(getActivity()));
        rlFrnds.setAdapter(mAdapter);
    }

}
