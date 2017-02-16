package com.halfplatepoha.frnds.detail.presenter;

import com.halfplatepoha.frnds.BasePresenter;
import com.halfplatepoha.frnds.detail.IDetailsConstants;

/**
 * Created by surajkumarsau on 16/02/17.
 */

public interface SongDetailPresenter extends BasePresenter {
    void onNewIntent(String frndId);
    void dataFromBundle(int position, String fbId, String source, String frndId,
                        String currentUserPlaceholder,
                        String musicChatMessage);

    void dataFromNotification(String frndName, String trackId, String trackTitle,
                              String trackUrl, String trackImageUrl, String trackArtist, String message,
                              String type, String timestamp);

    void onNotificationBroadcast(String trackId,
                                 int messageType,
                                 String messageBody,
                                 String frndId,
                                 String trackUrl,
                                 String trackTitle,
                                 long timestamp);

    void onSongSearchResultReceived(long trackId,
                               String trackUrl, String trackImageUrl,
                               String trackTitle, String trackArtist);

    void dataFromChat();
    void onBackPress();
    void onSendButtonClicked(String message, long timestamp);
    void onSongBroadCastReceived(@IDetailsConstants.CurrentSongStatusType int status);

    void onChatRowPlayClick(String trackId, String trackUrl, String trackMessage, int position);

    void onFocusChange(boolean isFocus);

    void onAddMusicClicked();
}
