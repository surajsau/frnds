package com.halfplatepoha.frnds.home.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.db.models.Chat;
import com.halfplatepoha.frnds.home.adapter.FriendsListAdapter;
import com.halfplatepoha.frnds.models.InstalledFrnds;
import com.halfplatepoha.frnds.models.User;
import com.halfplatepoha.frnds.network.BaseSubscriber;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class FriendsListFragment extends Fragment {

    @Bind(R.id.rlFrnds) RecyclerView rlFrnds;
    @Bind(R.id.refreshLayout) SwipeRefreshLayout refreshLayout;

    private FriendsListAdapter mAdapter;

    private Realm mRealm;

    public FriendsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getDefaultInstance();
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
        mRealm.where(Chat.class).findAll().asObservable()
                .flatMap(new Func1<RealmResults<Chat>, Observable<Chat>>() {
                    @Override
                    public Observable<Chat> call(RealmResults<Chat> chats) {
                        return Observable.from(chats);
                    }
                })
                .subscribe(chatSubscriber);
    }

    private void setupRecyclerView() {
        mAdapter = new FriendsListAdapter(getActivity());
        rlFrnds.setLayoutManager(new LinearLayoutManager(getActivity()));
        rlFrnds.setAdapter(mAdapter);

        refreshLayout.setEnabled(true);
    }

    private BaseSubscriber<Chat> chatSubscriber = new BaseSubscriber<Chat>() {
        @Override
        public void onObjectReceived(Chat chat) {
            mAdapter.addChat(chat);
        }
    };

}
