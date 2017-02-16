package com.halfplatepoha.frnds.home.presenter;

import com.halfplatepoha.frnds.BasePresenter;

/**
 * Created by surajkumarsau on 16/02/17.
 */

public interface HomePresenter extends BasePresenter {

    void onNotificationBroadcast(String trackId,
                                 int messageType,
                                 String messageBody,
                                 String frndId,
                                 String trackUrl,
                                 String trackTitle,
                                 long timestamp);

    void onSongDetailResult(String albumUrl, String trackTitle, String frndName);
}
