package com.halfplatepoha.frnds.detail.presenter;

import android.text.TextUtils;

import com.halfplatepoha.frnds.FrndsPreference;
import com.halfplatepoha.frnds.IConstants;
import com.halfplatepoha.frnds.IPrefConstants;
import com.halfplatepoha.frnds.db.ChatDAO;
import com.halfplatepoha.frnds.db.IDbConstants;
import com.halfplatepoha.frnds.db.models.Message;
import com.halfplatepoha.frnds.db.models.Song;
import com.halfplatepoha.frnds.detail.IDetailsConstants;
import com.halfplatepoha.frnds.detail.model.MessageModel;
import com.halfplatepoha.frnds.detail.model.SongModel;
import com.halfplatepoha.frnds.detail.view.SongDetailView;
import com.halfplatepoha.frnds.models.request.SendMessageRequest;
import com.halfplatepoha.frnds.models.request.UpdateTrackRequest;
import com.halfplatepoha.frnds.models.response.TrackDetails;
import com.halfplatepoha.frnds.network.BaseSubscriber;
import com.halfplatepoha.frnds.network.clients.FrndsClient;
import com.halfplatepoha.frnds.network.clients.SoundCloudClient;
import com.halfplatepoha.frnds.network.servicegenerators.ClientGenerator;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by surajkumarsau on 16/02/17.
 */

public class SongDetailPresenterImpl implements SongDetailPresenter {

    private FrndsClient mFrndsClient;
    private SoundCloudClient mSoundCloudClient;

    private SongDetailView view;

    private ChatDAO helper;

    private String frndId;
    private String frndName;

    private String fbId;
    private String frndImageUrl;

    private String latestSongTrackName;
    private String latestSongAlbumUrl;
    private String latestMessage;
    private long latestTimestamp;
    
    private String currentUserPlaceholder;
    private String musicChatMessage;

    private int position;

    private String mSource;

    public SongDetailPresenterImpl(SongDetailView view) {
        this.view = view;
    }

    @Override
    public void onCreate() {
        helper = new ChatDAO(Realm.getDefaultInstance());
        buildApiClients();

        view.setupRecyclerView();
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void onStart() {
        view.setupToolbar(frndImageUrl, frndName);
        view.initReceivers();
        view.registerReceivers();
    }

    @Override
    public void onStop() {
        view.unregisterReceivers();
    }

    @Override
    public void onResume() {
        RealmResults<Song> sngs = helper.getChatWithFrndId(frndId).getFrndSongs().sort(IDbConstants.SONG_TIME_STAMP_KEY, Sort.ASCENDING);
        RealmResults<Message> msgs = helper.getChatWithFrndId(frndId).getFrndMessages().sort(IDbConstants.MSG_TIME_STAMP_KEY, Sort.ASCENDING);

        ArrayList<SongModel> songs = new ArrayList<>();
        ArrayList<MessageModel> messages = new ArrayList<>();

        for(int i=0; i<sngs.size(); i++) {
            SongModel model = new SongModel();
            model.setTrackName(sngs.get(i).getSongTitle());
            model.setTrackTimeStamp(sngs.get(i).getSongTimestamp());
            model.setTrackImageUrl(sngs.get(i).getSongImgUrl());
            model.setTrackUser(sngs.get(i).getSongArtist());
            model.setTrackUrl(sngs.get(i).getSongUrl());

            songs.add(model);
        }

        for(int i=0; i<msgs.size(); i++) {
            MessageModel model = new MessageModel();
            model.setMessageType(msgs.get(i).getMsgType());
            model.setMessage(msgs.get(i).getMsgBody());
            model.setUserType(msgs.get(i).getUserType());
            model.setMessageType(msgs.get(i).getMsgType());
            model.setMessageTrackUrl(msgs.get(i).getMsgTrackUrl());
            model.setMessageTimeStamp(msgs.get(i).getMsgTimestamp());

            messages.add(model);
        }

        view.buildAnimations();
        view.addDataToAdapters(songs, messages);
        view.smoothScrollToLast();
    }

    @Override
    public void dataFromBundle(int position, String fbId, String source, String frndId, String currentUserPlaceHolder,
                               String musicChatMessage) {
        this.position = position;
        this.fbId = fbId;
        this.mSource = source;
        this.frndId = frndId;
        this.currentUserPlaceholder = currentUserPlaceHolder;
        this.musicChatMessage = musicChatMessage;
    }

    @Override
    public void dataFromChat() {
        frndName = helper.getChatWithFrndId(frndId).getFrndName();
        frndImageUrl = helper.getChatWithFrndId(frndId).getFrndImageUrl();
    }

    @Override
    public void onBackPress() {
        view.backPressed(position, latestSongTrackName, latestSongAlbumUrl, latestMessage, latestTimestamp, frndId);
    }

    @Override
    public void onSendButtonClicked(String message, long timestamp) {
        if(!TextUtils.isEmpty(message)) {
            Message.Builder msgBuilder = new Message.Builder()
                    .setMsgBody(message)
                    .setMsgTimestamp(timestamp)
                    .setFrndId(frndId)
                    .setMsgType(IDbConstants.TYPE_MESSAGE)
                    .setUserType(IDetailsConstants.TYPE_ME);

            MessageModel messageModel = new MessageModel();
            messageModel.setMessageTimeStamp(timestamp);
            messageModel.setMessage(message);
            messageModel.setMessageType(IDbConstants.TYPE_MESSAGE);
            messageModel.setUserType(IDetailsConstants.TYPE_ME);
            
            view.addMessageToAdapter(messageModel);
            view.smoothScrollToLast();
            view.emptyMessageBox();

            callSendMessageApi(msgBuilder.build());
        }
    }

    @Override
    public void onSongBroadCastReceived(@IDetailsConstants.CurrentSongStatusType int status) {
        FrndsPreference.setInPref(IPrefConstants.CURRENT_SONG_STATUS, status);
        FrndsPreference.setInPref(IPrefConstants.CURRENT_SONG_FRND_ID, frndId);

        if(status == IDetailsConstants.CURRENT_SONG_STATUS_PLAYING) {
            view.showPlayingIndicator();
            view.animatePlaylistButton();
        } else {
            view.hidePlayingIndicator();
            view.stopAnimatePlaylistButton();
        }
    }

    @Override
    public void onChatRowPlayClick(String trackId, String trackMessageUrl, String trackMessage, int position) {
        callTrackDetailsApi(trackId);
        String trackName = trackMessage.split(" played ")[1];
        view.startPlayingTrack(trackName, trackMessageUrl, frndId);
        view.scrollToAlbumPosition(position);
    }

    @Override
    public void onFocusChange(boolean isFocus) {
        if(isFocus) {
            view.messageContainerLeftSlide();
            view.circularCollapse();
        } else {
            view.messageContainerRightSide();
        }
    }

    @Override
    public void onAddMusicClicked() {
        view.startSongSearch();
    }

    @Override
    public void dataFromNotification(String frndName, String trackId, String trackTitle,
                                     String trackUrl, String trackImageUrl, String trackArtist, String message,
                                     String type, String timestamp) {
        long ts = !TextUtils.isEmpty(timestamp) ? Long.valueOf(timestamp) : 0L;
        
        if ("SONG".equalsIgnoreCase(type)) {
            addSongToPlaylist(trackId, trackUrl, trackImageUrl, trackTitle, trackArtist, ts, IDetailsConstants.TYPE_FRND);
        } else {
            Message msg = new Message.Builder()
                    .setMsgBody(message)
                    .setMsgTimestamp(ts)
                    .setMsgType(IDbConstants.TYPE_MESSAGE)
                    .setUserType(IDetailsConstants.TYPE_FRND)
                    .setFrndId(frndId)
                    .build();

            helper.insertMessageToChat(frndId, msg);
        }
    }

    @Override
    public void onNotificationBroadcast(String trackId, int messageType, String messageBody, String friendId,
                                        String trackUrl, String trackTitle, long timestamp) {
        Message message = new Message();
        message.setMsgBody(messageBody);
        message.setMsgTimestamp(timestamp);
        message.setUserType(IDetailsConstants.TYPE_FRND);
        message.setMsgType(messageType);
        message.setMsgTrackUrl(trackUrl);

        if(messageType == IDbConstants.TYPE_MUSIC) {
            Song song = new Song();
            song.setSongUrl(trackUrl);
            song.setFrndId(friendId);
            song.setSongTitle(trackTitle);
            song.setSongTimestamp(timestamp);

            helper.insertSongToChat(friendId, song);
        }

        helper.insertMessageToChat(friendId, message);

        refreshChatDetails(friendId, messageType, messageBody, trackId, trackUrl, trackTitle, timestamp);
    }

    @Override
    public void onSongSearchResultReceived(long trackId, String trackUrl, String trackImageUrl, String trackTitle, String trackArtist) {
        String id = String.valueOf(trackId);

        addSongToPlaylist(id, trackUrl, trackImageUrl, trackTitle, trackArtist, System.currentTimeMillis(), IDetailsConstants.TYPE_ME);

        view.startPlayingTrack(trackTitle, trackUrl, frndId);

        callUpdateTracksApi(id);

        if(trackImageUrl != null) {
            latestSongAlbumUrl = trackImageUrl.replace(IDetailsConstants.IMG_300_X_300_SUFFIX, IDetailsConstants.IMG_BADGE);
        }

        latestSongTrackName = trackTitle;

        view.setBackGroundImage(trackImageUrl);
        view.setTrackArtist(trackArtist);
        view.setTrackTitle(trackTitle);

        FrndsPreference.setInPref(IPrefConstants.LATEST_SONG_URL, trackUrl);
        FrndsPreference.setInPref(IPrefConstants.LATEST_SONG_IMAGE_URL, trackImageUrl);
        FrndsPreference.setInPref(IPrefConstants.LATEST_SONG_NAME, trackTitle);
        FrndsPreference.setInPref(IPrefConstants.LATEST_FRND_NAME, frndName);
        FrndsPreference.setInPref(IPrefConstants.LATEST_FRND_ID, frndId);
    }


    private void buildApiClients() {
        mFrndsClient = new ClientGenerator.Builder()
                .setLoggingInterceptor()
                .setBaseUrl(IConstants.FRNDS_BASE_URL)
                .setClientClass(FrndsClient.class)
                .setConnectTimeout(5, TimeUnit.MINUTES)
                .setReadTimeout(5, TimeUnit.MINUTES)
                .setHeader(IConstants.CONTENT_TYPE, IConstants.APPLICATION_JSON)
                .buildClient();

        mSoundCloudClient = new ClientGenerator.Builder()
                .setLoggingInterceptor()
                .setBaseUrl(IConstants.SOUNDCLOUD_BASE_URL)
                .setApiKeyInterceptor(IConstants.API_KEY_PARAM, IConstants.API_KEY_VALUE)
                .setClientClass(SoundCloudClient.class)
                .buildClient();
    }

    private void callUpdateTracksApi(String trackId) {
        UpdateTrackRequest req = new UpdateTrackRequest();
        req.setFbId(fbId);
        req.setTo(frndId);
        req.setTrackId(trackId);
        mFrndsClient.updateTrack(req)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new BaseSubscriber<Void>() {
                    @Override
                    public void onObjectReceived(Void aVoid) {

                    }
                });
    }

    private void addSongToPlaylist(String trackId, String trackUrl, String trackImageUrl, String trackTitle, String trackArtist,
                                   long timestamp, @IDetailsConstants.UserType int userType) {

        if(trackImageUrl != null) {
            trackImageUrl = trackImageUrl.replace(IDetailsConstants.IMG_500_X_500_SUFFIX, IDetailsConstants.IMG_300_X_300_SUFFIX);
        }

        Song song = new Song();
        song.setSongArtist(trackArtist);
        song.setSongImgUrl(trackImageUrl);
        song.setSongTimestamp(timestamp);
        song.setSongTitle(trackTitle);
        song.setSongUrl(trackUrl);
        song.setFrndId(frndId);

        String user = (IDetailsConstants.TYPE_ME == userType) ? currentUserPlaceholder : frndName;

        Message message = new Message.Builder()
                .setMsgBody(String.format(musicChatMessage, user, trackTitle))
                .setMsgTimestamp(timestamp)
                .setMsgTrackUrl(trackUrl)
                .setMsgType(IDbConstants.TYPE_MUSIC)
                .setUserType(userType)
                .build();

        SongModel songModel = new SongModel();
        songModel.setTrackUrl(trackUrl);
        songModel.setTrackUser(trackArtist);
        songModel.setTrackImageUrl(trackImageUrl);
        songModel.setTrackTimeStamp(timestamp);
        songModel.setTrackName(trackTitle);

        MessageModel messageModel = new MessageModel();
        messageModel.setMessage(String.format(musicChatMessage, user, trackTitle));
        messageModel.setUserType(userType);
        messageModel.setMessageTimeStamp(timestamp);
        messageModel.setMessageType(IDbConstants.TYPE_MUSIC);
        messageModel.setMessageTrackUrl(trackUrl);
        messageModel.setMessageTrackId(trackId);

        latestMessage = messageModel.getMessage();
        latestTimestamp = messageModel.getMessageTimeStamp();

        view.addSongToAlbumAdapter(songModel);
        view.addMessageToAdapter(messageModel);

        view.smoothScrollToLast();

        helper.insertSongToChat(frndId, song);
        helper.insertMessageToChat(frndId, message);
    }

    private void callSendMessageApi(final Message message) {
        SendMessageRequest req  = new SendMessageRequest();
        req.setFbId(fbId);
        req.setMessage(message.getMsgBody());
        req.setTo(frndId);

        mFrndsClient.sendMessage(req)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Void>() {
                    @Override
                    public void onObjectReceived(Void aVoid) {
                        latestMessage = message.getMsgBody();
                        latestTimestamp = message.getMsgTimestamp();
                        helper.insertMessageToChat(frndId, message);
                    }
                });
    }

    @Override
    public void onNewIntent(String frndId) {
        this.frndId = frndId;
    }

    private void refreshChatDetails(String friendId, int messageType, String message, String trackId,
                                    final String trackTitle, final String trackUrl, final long timestamp) {
        if(frndId.equalsIgnoreCase(friendId)) {
            if(messageType == IDbConstants.TYPE_MUSIC) {

                //--getting song artist & image url
                mSoundCloudClient.getTrackDetails(trackId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(new BaseSubscriber<TrackDetails>() {
                            @Override
                            public void onObjectReceived(TrackDetails trackDetails) {
                                SongModel songModel = new SongModel();
                                songModel.setTrackName(trackTitle);
                                songModel.setTrackImageUrl(trackDetails.getArtwork_url());
                                songModel.setTrackTimeStamp(timestamp);
                                songModel.setTrackUser(trackDetails.getUser().getUsername());
                                songModel.setTrackUrl(trackUrl);

                                Song song = new Song();
                                song.setSongImgUrl(trackDetails.getArtwork_url()
                                        .replace(IDetailsConstants.STRING_HTTPS, IDetailsConstants.STRING_HTTP)
                                        .replace(IDetailsConstants.IMG_LARGE_SUFFIX, IDetailsConstants.IMG_500_X_500_SUFFIX));
                                song.setSongTimestamp(timestamp);
                                song.setFrndId(frndId);
                                if(trackDetails.getUser() != null) {
                                    song.setSongArtist(trackDetails.getUser().getUsername());
                                }
                                helper.updateSong(song);

                                view.addSongToAlbumAdapter(songModel);
                            }
                        });
            }

            Message msg = new Message();
            msg.setMsgTimestamp(timestamp);
            msg.setMsgType(messageType);
            msg.setUserType(IDetailsConstants.TYPE_FRND);
            msg.setMsgBody(message);

            MessageModel messageModel = new MessageModel();
            messageModel.setMessageType(messageType);
            messageModel.setMessage(message);
            messageModel.setMessageTrackUrl(trackUrl);
            messageModel.setUserType(IDetailsConstants.TYPE_FRND);
            messageModel.setMessageType(messageType);

            latestMessage = messageModel.getMessage();

            view.addMessageToAdapter(messageModel);
            view.smoothScrollToLast();

        } else {
            view.showPendingChat();
        }
    }

    private void callTrackDetailsApi(String trackId) {
        mSoundCloudClient.getTrackDetails(trackId)
                .observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new BaseSubscriber<TrackDetails>() {
                    @Override
                    public void onObjectReceived(TrackDetails trackDetails) {
                        FrndsPreference.setInPref(IPrefConstants.LATEST_SONG_NAME, trackDetails.getTitle());
                        FrndsPreference.setInPref(IPrefConstants.LATEST_SONG_IMAGE_URL, trackDetails.getArtwork_url());
                        FrndsPreference.setInPref(IPrefConstants.LATEST_SONG_URL, trackDetails.getStream_url());
                        FrndsPreference.setInPref(IPrefConstants.LATEST_FRND_NAME, frndName);
                        FrndsPreference.setInPref(IPrefConstants.LATEST_FRND_ID, frndId);
                    }
                });
    }
}
