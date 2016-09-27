package com.halfplatepoha.frnds.db;

import com.halfplatepoha.frnds.FrndsLog;
import com.halfplatepoha.frnds.db.models.Chat;
import com.halfplatepoha.frnds.db.models.Message;
import com.halfplatepoha.frnds.db.models.Song;
import com.halfplatepoha.frnds.models.fb.InstalledFrnds;
import com.halfplatepoha.frnds.network.BaseSubscriber;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;
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
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if(realm.where(Chat.class).equalTo(IDbConstants.FRND_ID_KEY, chat.getFrndId()).findFirst() == null)
                    realm.insert(chat);
            }
        });
    }

    public void insertMessageToChat(String chatId, Message message) {
        try{
            Chat chatResult = mRealm.where(Chat.class).equalTo(IDbConstants.FRND_ID_KEY, chatId).findFirst();

            mRealm.beginTransaction();
            chatResult.getFrndMessages().add(message);
            mRealm.commitTransaction();
        }catch (Exception e) {
            FrndsLog.e("Message Transaction cancelled : " + e.getMessage());
            mRealm.cancelTransaction();
        }
    }

    public void insertSongToChat(String chatId, Song song) {
        try {
            Chat chatResult = mRealm.where(Chat.class).equalTo(IDbConstants.FRND_ID_KEY, chatId).findFirst();

            mRealm.beginTransaction();
            chatResult.getFrndSongs().add(song);
            mRealm.commitTransaction();
        } catch (Exception e) {
            FrndsLog.e("Song Transaction cancelled : " + e.getMessage());
            mRealm.cancelTransaction();
        }
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

    public void close() {

    }
}
