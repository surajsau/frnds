package com.halfplatepoha.frnds.home.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.halfplatepoha.frnds.FrndsLog;
import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.TokenTracker;
import com.halfplatepoha.frnds.db.IDbConstants;
import com.halfplatepoha.frnds.db.models.Chat;
import com.halfplatepoha.frnds.login.activity.LoginActivity;
import com.halfplatepoha.frnds.models.InstalledFrnds;
import com.halfplatepoha.frnds.models.User;

import java.io.IOException;

import io.realm.Case;
import io.realm.Realm;

public class SplashScreenActivity extends AppCompatActivity {

    private Realm mRealm;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null) {
            mRealm = Realm.getDefaultInstance();
            getFriendsWhoInstalledApp();
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
                        updateFrndsList(frnds);
                        startHomeActivity();
                    }  catch (JsonMappingException e) {
                        e.printStackTrace();
                    } catch (JsonParseException e) {
                        e.printStackTrace();
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        }).executeAsync();
    }

    private void startHomeActivity() {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        startActivity(homeIntent);
    }

    private void updateFrndsList(final InstalledFrnds frnds) {
        if(frnds != null && frnds.getData() != null && !frnds.getData().isEmpty()) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for(InstalledFrnds.Frnd frnd : frnds.getData()) {
                        if(realm.where(Chat.class).equalTo(IDbConstants.FRND_ID_KEY, frnd.getId()).findFirst() == null) {
                            Chat chat = new Chat();
                            chat.setFrndId(frnd.getId());
                            chat.setFrndName(frnd.getName());
                            chat.setFrndImageUrl(frnd.getPicture().getData().getUrl());
                            realm.insert(chat);
                        }
                    }
                }
            });
        }
    }
}
