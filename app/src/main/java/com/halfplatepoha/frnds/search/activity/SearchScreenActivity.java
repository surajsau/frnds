package com.halfplatepoha.frnds.search.activity;

import android.animation.Animator;
import android.graphics.Point;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

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
import io.codetail.animation.ViewAnimationUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SearchScreenActivity extends AppCompatActivity implements ValueEventListener,
        View.OnTouchListener, View.OnClickListener {

    private static final String TAG = SearchScreenActivity.class.getSimpleName();

    private int mToolbarLength;

    private SoundCloudClient mClient;

    private SearchResultAdapter mAdapter;

    @Bind(R.id.searchBar) ViewGroup searchBar;
    @Bind(R.id.etSearch) EditText etSearch;
    @Bind(R.id.rlSearchResult) RecyclerView rlSearchResult;
    @Bind(R.id.back) ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);
        ButterKnife.bind(this);

        mClient = new ClientGenerator.Builder()
                        .setBaseUrl(IConstants.SOUNDCLOUD_BASE_URL)
                        .setLoggingInterceptor()
                        .setApiKeyInterceptor(IConstants.API_KEY_PARAM, IConstants.API_KEY_VALUE)
                        .setClientClass(SoundCloudClient.class)
                        .buildClient();

        mToolbarLength = Math.max(searchBar.getHeight(), searchBar.getWidth());

        setupRecyclerView();

        setupSearchEditText();

        back.setOnClickListener(this);
    }

    private void setupRecyclerView() {
        mAdapter = new SearchResultAdapter(this);
        mAdapter.setHasStableIds(true);
        rlSearchResult.setLayoutManager(new LinearLayoutManager(this));
        rlSearchResult.setAdapter(mAdapter);
    }

    private void setupSearchEditText() {
        etSearch.clearFocus();
        etSearch.setOnTouchListener(this);

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

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
            //--by default taking first view (darkToolbar)
            View nextView = searchBar.getChildAt(0);

            //--choosing lightToolbar when etSearch is in focus
            if(view.hasFocus())
                nextView = searchBar.getChildAt(1);

            nextView.setVisibility(View.VISIBLE);

            final float finalRadius = (float) Math.hypot(nextView.getWidth() / 2f, nextView.getHeight() / 2f) + hypo(motionEvent);

            Animator reveal =
                    ViewAnimationUtils.createCircularReveal(nextView, (int) motionEvent.getX(), (int) motionEvent.getY(), 0,
                            finalRadius, View.LAYER_TYPE_HARDWARE);
            reveal.setDuration(300);
            reveal.setInterpolator(new FastOutLinearInInterpolator());
            reveal.start();
        }
        return false;
    }

    private float hypo(MotionEvent event) {
        Point p1 = new Point((int) event.getX(), (int) event.getY());
        Point p2 = new Point(searchBar.getWidth() / 2, searchBar.getHeight() / 2);

        return (float) Math.sqrt(Math.pow(p1.y - p2.y, 2) + Math.pow(p1.x - p2.x, 2));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:{
                onBackPressed();
            }
            break;
        }
    }
}
