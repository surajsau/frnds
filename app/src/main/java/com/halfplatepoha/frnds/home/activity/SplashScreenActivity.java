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
import com.halfplatepoha.frnds.db.ChatDAO;
import com.halfplatepoha.frnds.login.activity.LoginActivity;
import com.halfplatepoha.frnds.models.fb.InstalledFrnds;

import java.io.IOException;

import io.realm.Realm;

public class SplashScreenActivity extends AppCompatActivity implements ChatDAO.OnTransactionCompletedListener{

    private Realm mRealm;

    private FirebaseAuth mAuth;

    private ChatDAO helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null) {
            mRealm = Realm.getDefaultInstance();
            helper = new ChatDAO(mRealm);
            helper.setOnTransactionCompletedListener(this);

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
                        helper.updateChatList(frnds, IDbConstants.UPDATE_FRND_LIST_TRANSACTION_ID);
                    }  catch (JsonMappingException e) {
                        e.printStackTrace();
                    } catch (JsonParseException e) {
                        e.printStackTrace();
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    //TODO: error case
                }
            }
        }).executeAsync();
    }

    private void startHomeActivity() {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        startActivity(homeIntent);
    }

    @Override
    public void onTransactionComplete(int transcationId) {
        switch (transcationId) {
            case IDbConstants.UPDATE_FRND_LIST_TRANSACTION_ID:
                startHomeActivity();
                break;
        }
    }
}
