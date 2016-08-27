package com.halfplatepoha.frnds;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener,
        FacebookCallback<LoginResult>, OnCompleteListener<AuthResult>{

    private static final String TAG = LoginActivity.class.getSimpleName();

    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;

    @Bind(R.id.btnFbLogin) LoginButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();

        initFbButton();
    }

    private void initFbButton() {
        btnLogin.setReadPermissions("email", "public_profile", "user_friends");
        btnLogin.registerCallback(mCallbackManager, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null) {
            startSearchScreenActivity(user.getUid());
        } else {
            Toast.makeText(this, "You've logged out", Toast.LENGTH_SHORT).show();
        }
    }

    private void startSearchScreenActivity(String userId) {
        Intent startScreenIntent = new Intent(this, SearchScreenActivity.class);
        startScreenIntent.putExtra(IConstants.USER_ID, userId);
        startActivity(startScreenIntent);
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        FrndsLog.d(loginResult.getAccessToken().getUserId());
        handleAccessToken(loginResult.getAccessToken());
        registerGCM();
    }

    private void registerGCM() {

    }

    private void handleAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, this);
    }

    @Override
    public void onCancel() {
        FrndsLog.e("Cancelled");
    }

    @Override
    public void onError(FacebookException error) {
        FrndsLog.e(error.getMessage());
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if(!task.isSuccessful()) {
            FrndsLog.e("signInWithCredential " + task.getException());
        } else {
            FrndsLog.d("signInWithCredential " + task.isSuccessful());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
