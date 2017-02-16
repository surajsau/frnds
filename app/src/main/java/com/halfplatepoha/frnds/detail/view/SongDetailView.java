package com.halfplatepoha.frnds.detail.view;

import com.halfplatepoha.frnds.BaseView;
import com.halfplatepoha.frnds.db.models.Chat;
import com.halfplatepoha.frnds.db.models.Message;
import com.halfplatepoha.frnds.db.models.Song;
import com.halfplatepoha.frnds.detail.IDetailsConstants;
import com.halfplatepoha.frnds.detail.model.MessageModel;
import com.halfplatepoha.frnds.detail.model.SongModel;

import java.util.ArrayList;

import io.realm.RealmResults;

/**
 * Created by surajkumarsau on 16/02/17.
 */

public interface SongDetailView extends BaseView {
    void unregisterReceivers();
    void registerReceivers();
    void initReceivers();
    void setupRecyclerView();
    void setupToolbar(String frndImageUrl, String frndName);
    void buildAnimations();
    void smoothScrollToLast();
    void scrollToAlbumPosition(int position);
    void addDataToAdapters(ArrayList<SongModel> songs, ArrayList<MessageModel> messages);

    void showPlayingIndicator();
    void hidePlayingIndicator();
    void animatePlaylistButton();
    void stopAnimatePlaylistButton();

    void emptyMessageBox();
    void addMessageToAdapter(MessageModel model);

    void startPlayingTrack(String trackName, String trackUrl, String frndId);

    void circularCollapse();
    void circularReveal();
    void messageContainerLeftSlide();
    void messageContainerRightSide();

    void startSongSearch();

    void backPressed(int position, String latestSongTrackName, String latestSongAlbumUrl, String latestMessage, long latestTimestamp, String frndId);

    void addSongToAlbumAdapter(SongModel song);
    void setBackGroundImage(String trackImageUrl);
    void setTrackTitle(String trackTitle);
    void setTrackArtist(String trackArtist);

    void showPendingChat();

}
