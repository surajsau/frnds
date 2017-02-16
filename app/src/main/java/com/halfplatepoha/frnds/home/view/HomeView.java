package com.halfplatepoha.frnds.home.view;

import com.halfplatepoha.frnds.BaseView;

/**
 * Created by surajkumarsau on 16/02/17.
 */

public interface HomeView extends BaseView {
    void initReceivers();
    void registerReceivers();
    void unregisterReceivers();

    void setupViewPager();

    void initAnimations();

    void setPlayerAlbumUrl(String albumImageUrl);
    void setPlayerTrackName(String trackName);
    void setPlayerFriendName(String frndName);
}
