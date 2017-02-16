package com.halfplatepoha.frnds.db;

import com.halfplatepoha.frnds.FrndsLog;
import com.halfplatepoha.frnds.db.models.Chat;
import com.halfplatepoha.frnds.db.models.Message;
import com.halfplatepoha.frnds.db.models.Song;
import com.halfplatepoha.frnds.db.models.User;
import com.halfplatepoha.frnds.models.fb.InstalledFrnds;
import com.halfplatepoha.frnds.network.BaseSubscriber;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by surajkumarsau on 25/09/16.
 */
public class ChatDAO {

    private Realm mRealm;

    private OnTransactionCompletedListener mTransactionCompletedListener;

    public ChatDAO(Realm realm) {
        mRealm = realm;
    }

    public void setOnTransactionCompletedListener(OnTransactionCompletedListener transactionCompletedListener) {
        mTransactionCompletedListener = transactionCompletedListener;
    }

    public void insertIntoChatList(final Chat chat) {
        final String frndId = chat.getFrndId();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if(getChatWithFrndId(frndId) == null)
                    realm.insert(chat);
            }
        });
    }

    public void insertMessageToChat(String chatId, Message message) {
        try{
            Chat chatResult = getChatWithFrndId(chatId);

            mRealm.beginTransaction();
            Message msg = mRealm.copyToRealm(message);
            chatResult.getFrndMessages().add(msg);
            chatResult.setFrndLastMessage(msg);
            chatResult.setFrndLastMessageTimestamp(msg.getMsgTimestamp());
            mRealm.commitTransaction();
        }catch (Exception e) {
            FrndsLog.e("Message Transaction cancelled : " + e.getMessage());
            mRealm.cancelTransaction();
        }
    }

    public void insertSongToChat(String chatId, Song song) {
        try {
            Chat chatResult = getChatWithFrndId(chatId);
            User user = mRealm.where(User.class).findFirst();

            mRealm.beginTransaction();
            Song sng = mRealm.copyToRealm(song);
            user.getUserFavSongs().add(song);
            chatResult.getFrndSongs().add(sng);
            mRealm.commitTransaction();
        } catch (Exception e) {
            FrndsLog.e("Song Transaction cancelled : " + e.getMessage());
            mRealm.cancelTransaction();
        }
    }

    public Chat getChatWithFrndId(String frndId) {
        return mRealm.where(Chat.class).equalTo(IDbConstants.FRND_ID_KEY, frndId).findFirst();
    }

    public void updateChatList(InstalledFrnds installedFrnds, final int transactionId) {
        Observable.just(installedFrnds)
                .filter(new Func1<InstalledFrnds, Boolean>() {
                    @Override
                    public Boolean call(InstalledFrnds installedFrnds) {
                        return installedFrnds != null
                                && installedFrnds.getData() != null
                                && installedFrnds.getData().size() > 0;
                    }
                })
                .map(new Func1<InstalledFrnds, ArrayList<InstalledFrnds.Frnd>>() {
                    @Override
                    public ArrayList<InstalledFrnds.Frnd> call(InstalledFrnds installedFrnds) {
                        return installedFrnds.getData();
                    }
                })
                .flatMap(new Func1<ArrayList<InstalledFrnds.Frnd>, Observable<InstalledFrnds.Frnd>>() {
                    @Override
                    public Observable<InstalledFrnds.Frnd> call(ArrayList<InstalledFrnds.Frnd> frnds) {
                        return Observable.from(frnds);
                    }
                })
                .filter(new Func1<InstalledFrnds.Frnd, Boolean>() {
                    @Override
                    public Boolean call(InstalledFrnds.Frnd frnd) {
                        if(frnd != null)
                            return (mRealm.where(Chat.class)
                                    .equalTo(IDbConstants.FRND_ID_KEY, frnd.getId())
                                    .findFirst() == null);
                        return false;
                    }
                })
                .subscribe(new BaseSubscriber<InstalledFrnds.Frnd>() {
                    @Override
                    public void onObjectReceived(InstalledFrnds.Frnd frnd) {
                        Chat chat = new Chat();
                        chat.setFrndId(frnd.getId());
                        chat.setFrndName(frnd.getName());
                        chat.setFrndImageUrl(frnd.getPicture().getData().getUrl());

                        insertIntoChatList(chat);
                    }

                    @Override
                    public void onCompleted() {
                        mTransactionCompletedListener.onTransactionComplete(transactionId);
                    }
                });
    }

    public interface OnTransactionCompletedListener {
        void onTransactionComplete(int transcationId);
    }

    public void updateSong(Song sng) {
        try {
            Song song = mRealm.where(Song.class)
                    .equalTo(IDbConstants.FRND_ID_KEY, sng.getFrndId())
                    .equalTo(IDbConstants.SONG_TIME_STAMP_KEY, sng.getSongTimestamp()).findFirst();
            mRealm.beginTransaction();
            song.setSongImgUrl(sng.getSongImgUrl());
            song.setSongArtist(sng.getSongArtist());
            mRealm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            mRealm.cancelTransaction();
        }
    }

    public boolean doesSongExist(String frndId, long timestamp) {
        return (mRealm.where(Song.class)
                .equalTo(IDbConstants.FRND_ID_KEY, frndId)
                .equalTo(IDbConstants.SONG_TIME_STAMP_KEY, timestamp)
                .count() == 1);
    }

    public boolean doesMessageExist(String frndId, long timestamp) {
        return (mRealm.where(Message.class)
                .equalTo(IDbConstants.FRND_ID_KEY, frndId)
                .equalTo(IDbConstants.MSG_TIME_STAMP_KEY, timestamp)
                .count() == 1);
    }

    public RealmResults<Song> getAllSongs() {
        User result = mRealm.where(User.class).findFirst();
        if(result != null)
            return result.getUserFavSongs().sort(IDbConstants.SONG_TIME_STAMP_KEY, Sort.DESCENDING);
        return null;
    }

    public RealmResults<Chat> getAllChats() {
        return mRealm.where(Chat.class).findAll();
    }

    public void updateChatPosition(Chat chat, int position) {
        try {
            mRealm.beginTransaction();
            chat.setFrndPosition(position);
            mRealm.commitTransaction();
        } catch (Exception e) {
            mRealm.cancelTransaction();
        }
    }

    public void close() {

    }
}
