package com.halfplatepoha.frnds.home.presenter;

import com.halfplatepoha.frnds.db.ChatDAO;
import com.halfplatepoha.frnds.db.IDbConstants;
import com.halfplatepoha.frnds.db.models.Message;
import com.halfplatepoha.frnds.db.models.Song;
import com.halfplatepoha.frnds.detail.IDetailsConstants;
import com.halfplatepoha.frnds.home.view.HomeView;

import io.realm.Realm;

/**
 * Created by surajkumarsau on 16/02/17.
 */

public class HomePresenterImpl implements HomePresenter {

    private ChatDAO helper;
    private HomeView view;

    public HomePresenterImpl(HomeView view) {
        this.view = view;
        helper = new ChatDAO(Realm.getDefaultInstance());
    }

    @Override
    public void onCreate() {
        view.initReceivers();
        view.registerReceivers();
        view.setupViewPager();
        view.initAnimations();
    }

    @Override
    public void onDestroy() {
        view.unregisterReceivers();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onResume() {

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
    }

    @Override
    public void onSongDetailResult(String albumUrl, String trackTitle, String frndName) {
        view.setPlayerAlbumUrl(albumUrl);
        view.setPlayerFriendName(frndName);
        view.setPlayerTrackName(trackTitle);
    }
}
