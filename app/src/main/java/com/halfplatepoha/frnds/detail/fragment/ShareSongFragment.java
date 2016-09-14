package com.halfplatepoha.frnds.detail.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.detail.IDetailsConstants;
import com.halfplatepoha.frnds.detail.adapter.FrndsShareSuggestionListAdapter;
import com.halfplatepoha.frnds.utils.AppUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShareSongFragment extends Fragment {

    @Bind(R.id.rlFrndsSuggestion) RecyclerView rlFrndsSuggestion;

    private FrndsShareSuggestionListAdapter mAdapter;

    public ShareSongFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share_song, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        initRecyclerView();
    }

    private void initRecyclerView() {
        mAdapter = new FrndsShareSuggestionListAdapter(getActivity());
        rlFrndsSuggestion.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rlFrndsSuggestion.setAdapter(mAdapter);
    }

    @OnClick(R.id.back)
    public void close() {
        AppUtil.hideSoftKeyboard(getActivity());
        getActivity().onBackPressed();
    }
}
