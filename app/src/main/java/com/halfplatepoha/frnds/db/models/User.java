package com.halfplatepoha.frnds.db.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by surajkumarsau on 21/09/16.
 */
public class User extends RealmObject {

    @PrimaryKey
    private String      userFbId;

    private String      userName;
    private RealmList<Song>     userFavSongs;

}
