package com.halfplatepoha.frnds.home.activity;

import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.halfplatepoha.frnds.FrndsLog;
import com.halfplatepoha.frnds.FrndsPreference;
import com.halfplatepoha.frnds.IConstants;
import com.halfplatepoha.frnds.IPrefConstants;
import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.TokenTracker;
import com.halfplatepoha.frnds.db.IDbConstants;
import com.halfplatepoha.frnds.db.ChatDAO;
import com.halfplatepoha.frnds.db.models.Message;
import com.halfplatepoha.frnds.db.models.Song;
import com.halfplatepoha.frnds.detail.IDetailsConstants;
import com.halfplatepoha.frnds.login.activity.LoginActivity;
import com.halfplatepoha.frnds.models.fb.InstalledFrnds;
import com.halfplatepoha.frnds.models.request.GetPendingRequest;
import com.halfplatepoha.frnds.models.response.GetPendingResponse;
import com.halfplatepoha.frnds.models.response.TrackDetails;
import com.halfplatepoha.frnds.network.BaseSubscriber;
import com.halfplatepoha.frnds.network.clients.FrndsClient;
import com.halfplatepoha.frnds.network.clients.SoundCloudClient;
import com.halfplatepoha.frnds.network.servicegenerators.ClientGenerator;
import com.halfplatepoha.frnds.ui.OpenSansTextView;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SplashScreenActivity extends AppCompatActivity implements ChatDAO.OnTransactionCompletedListener{

    private FirebaseAuth mAuth;

    private ChatDAO helper;

    @Bind(R.id.appName) OpenSansTextView appName;

    private FrndsClient frndsClient;
    private SoundCloudClient soundCloudClient;

    private String fbId;

    boolean isPendingMessageComplete, isPendingSongComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null) {
            helper = new ChatDAO(Realm.getDefaultInstance());
            helper.setOnTransactionCompletedListener(this);

            fbId = FrndsPreference.getFromPref(IPrefConstants.FB_USER_ID, "");

            getFriendsWhoInstalledApp();

            frndsClient = new ClientGenerator.Builder()
                    .setLoggingInterceptor()
                    .setBaseUrl(IConstants.FRNDS_BASE_URL)
                    .setClientClass(FrndsClient.class)
                    .setConnectTimeout(5, TimeUnit.MINUTES)
                    .setReadTimeout(5, TimeUnit.MINUTES)
                    .setHeader(IConstants.CONTENT_TYPE, IConstants.APPLICATION_JSON)
                    .buildClient();

            soundCloudClient = new ClientGenerator.Builder()
                    .setLoggingInterceptor()
                    .setBaseUrl(IConstants.SOUNDCLOUD_BASE_URL)
                    .setClientClass(SoundCloudClient.class)
                    .buildClient();
        } else {
            startLoginActivity();
        }
    }

    private void startLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void getFriendsWhoInstalledApp() {
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

    private void startHomeActivity() {
//        if(isPendingSongComplete && isPendingMessageComplete) {
            Intent homeIntent = new Intent(this, HomeActivity.class);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, appName, getString(R.string.app_name_transition));
            startActivity(homeIntent, options.toBundle());
//        }
    }

    @Override
    public void onTransactionComplete(int transcationId) {
        switch (transcationId) {
            case IDbConstants.UPDATE_FRND_LIST_TRANSACTION_ID:
//                getPendingMessagesAndSongs();
                startHomeActivity();
        }
    }

    private void getPendingMessagesAndSongs() {
        GetPendingRequest request = new GetPendingRequest();
        request.setFbId(fbId);
        Observable<GetPendingResponse> response = frndsClient.getPending(request)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        response.flatMap(new Func1<GetPendingResponse, Observable<GetPendingResponse.PendingMessage>>() {
                    @Override
                    public Observable<GetPendingResponse.PendingMessage> call(GetPendingResponse getPendingResponse) {
                        return Observable.from(getPendingResponse.getPendingMessages());
                    }
                })
                .filter(new Func1<GetPendingResponse.PendingMessage, Boolean>() {
                    @Override
                    public Boolean call(GetPendingResponse.PendingMessage pendingMessage) {
                        return pendingMessage != null &&
                                (!TextUtils.isEmpty(pendingMessage.getFbId())) &&
                                pendingMessage.getMessages() != null && !pendingMessage.getMessages().isEmpty();
                    }
                })
                .subscribe(pendingMessageSubscriber);

        response.flatMap(new Func1<GetPendingResponse, Observable<GetPendingResponse.PendingSong>>() {
                    @Override
                    public Observable<GetPendingResponse.PendingSong> call(GetPendingResponse getPendingResponse) {
                        return Observable.from(getPendingResponse.getPendingSongs());
                    }
                })
                .filter(new Func1<GetPendingResponse.PendingSong, Boolean>() {
                    @Override
                    public Boolean call(GetPendingResponse.PendingSong pendingSong) {
                        return pendingSong != null &&
                                (!TextUtils.isEmpty(pendingSong.getFbId())) &&
                                pendingSong.getTracks() != null && !pendingSong.getTracks().isEmpty();
                    }
                })
                .subscribe(pendingSongSubscriber);
    }

    private BaseSubscriber<GetPendingResponse.PendingMessage> pendingMessageSubscriber = new BaseSubscriber<GetPendingResponse.PendingMessage>() {
        @Override
        public void onObjectReceived(GetPendingResponse.PendingMessage pendingMessage) {
            String fbId = pendingMessage.getFbId();
            for(GetPendingResponse.PendingMessage.Message message : pendingMessage.getMessages()) {
                if(!helper.doesMessageExist(fbId, message.getTimestamp())) {
                    Message msg = new Message();
                    msg.setMsgTimestamp(message.getTimestamp());
                    msg.setMsgType(IDbConstants.TYPE_MESSAGE);
                    msg.setMsgBody(message.getMessage());
                    msg.setUserType(IDetailsConstants.TYPE_FRND);

                    helper.insertMessageToChat(fbId, msg);
                }
            }
        }

        @Override
        public void onCompleted() {
            isPendingMessageComplete = true;
            startHomeActivity();
        }
    };

    private BaseSubscriber<GetPendingResponse.PendingSong> pendingSongSubscriber = new BaseSubscriber<GetPendingResponse.PendingSong>() {
        @Override
        public void onObjectReceived(GetPendingResponse.PendingSong pendingSong) {
            final String fbId = pendingSong.getFbId();
            Observable.just(pendingSong)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.newThread())
                    .flatMap(new Func1<GetPendingResponse.PendingSong, Observable<GetPendingResponse.PendingSong.Track>>() {
                        @Override
                        public Observable<GetPendingResponse.PendingSong.Track> call(GetPendingResponse.PendingSong pendingSong) {
                            return Observable.from(pendingSong.getTracks());
                        }
                    })
                    .filter(new Func1<GetPendingResponse.PendingSong.Track, Boolean>() {
                        @Override
                        public Boolean call(GetPendingResponse.PendingSong.Track track) {
                            return track != null && !TextUtils.isEmpty(track.getTrackId());
                        }
                    })
                    .subscribe(new BaseSubscriber<GetPendingResponse.PendingSong.Track>() {
                        @Override
                        public void onObjectReceived(final GetPendingResponse.PendingSong.Track track) {
                            final long timestamp = track.getTimestamp();
                            soundCloudClient.getTrackDetails(track.getTrackId())
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new BaseSubscriber<TrackDetails>() {
                                        @Override
                                        public void onObjectReceived(TrackDetails trackDetails) {
                                            if(!helper.doesSongExist(fbId, timestamp)) {
                                                Song song = new Song();
                                                song.setSongTimestamp(timestamp);
                                                song.setFrndId(fbId);
                                                song.setSongArtist(trackDetails.getUser().getUsername());
                                                song.setSongImgUrl(trackDetails.getArtwork_url());
                                                song.setSongUrl(trackDetails.getStream_url());
                                                song.setSongTitle(trackDetails.getTitle());

                                                helper.insertSongToChat(fbId, song);
                                            }
                                        }
                                    });
                        }

                        @Override
                        public void onCompleted() {
                            isPendingSongComplete = true;
                            startHomeActivity();
                        }
                    });
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if(!pendingMessageSubscriber.isUnsubscribed())
            pendingMessageSubscriber.unsubscribe();

        if(!pendingSongSubscriber.isUnsubscribed())
            pendingSongSubscriber.isUnsubscribed();
    }
}
