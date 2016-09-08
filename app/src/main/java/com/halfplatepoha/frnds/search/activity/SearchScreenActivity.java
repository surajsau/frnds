package com.halfplatepoha.frnds.search.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.halfplatepoha.frnds.IConstants;
import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.network.BaseSubscriber;
import com.halfplatepoha.frnds.models.response.SearchResponse;
import com.halfplatepoha.frnds.network.servicegenerators.ClientGenerator;
import com.halfplatepoha.frnds.network.clients.SoundCloudClient;
import com.halfplatepoha.frnds.models.response.TrackDetails;
import com.halfplatepoha.frnds.search.adapter.SearchResultAdapter;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SearchScreenActivity extends AppCompatActivity implements ValueEventListener {

    private static final String TAG = SearchScreenActivity.class.getSimpleName();

    private SoundCloudClient mClient;

    private SearchResultAdapter mAdapter;

    @Bind(R.id.etSearch) EditText etSearch;
    @Bind(R.id.rlSearchResult) RecyclerView rlSearchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mClient = new ClientGenerator.Builder()
                        .setBaseUrl(IConstants.SOUNDCLOUD_BASE_URL)
                        .setLoggingInterceptor()
                        .setApiKeyInterceptor(IConstants.API_KEY_PARAM, IConstants.API_KEY_VALUE)
                        .setClientClass(SoundCloudClient.class)
                        .buildClient();

        mAdapter = new SearchResultAdapter(this);

        rlSearchResult.setLayoutManager(new LinearLayoutManager(this));
        rlSearchResult.setAdapter(mAdapter);

        setupTextwatcherForEditText();
    }

    private void setupTextwatcherForEditText() {
        RxTextView.textChanges(etSearch)
                .filter(new Func1<CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence charSequence) {
                        return (charSequence != null && !TextUtils.isEmpty(charSequence.toString()));
                    }
                })
                .debounce(900, TimeUnit.MILLISECONDS)
                .subscribe(textWatcher);
    }

    private void callSearchApi(String s) {
        mClient.getSearchResult(s)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new BaseSubscriber<List<TrackDetails>>() {
                    @Override
                    public void onObjectReceived(List<TrackDetails> trackDetails) {
                        Observable.just(trackDetails)
                                .flatMap(new Func1<List<TrackDetails>, Observable<TrackDetails>>() {
                                    @Override
                                    public Observable<TrackDetails> call(List<TrackDetails> trackDetails) {
                                        return Observable.from(trackDetails);
                                    }
                                })
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new BaseSubscriber<TrackDetails>() {
                                    @Override
                                    public void onObjectReceived(TrackDetails details) {
                                        mAdapter.addItemToList(details);
                                    }
                                });
                    }
                });
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        SearchResponse value = dataSnapshot.getValue(SearchResponse.class);
        etSearch.setText(value.getSearch());
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {}

    private BaseSubscriber<CharSequence> textWatcher = new BaseSubscriber<CharSequence>() {
        @Override
        public void onObjectReceived(CharSequence s) {
            callSearchApi(s.toString());
        }
    };

}
