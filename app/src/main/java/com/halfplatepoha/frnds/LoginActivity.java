package com.halfplatepoha.frnds;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.halfplatepoha.frnds.network.BaseSubscriber;
import com.halfplatepoha.frnds.network.clients.FrndsClient;
import com.halfplatepoha.frnds.network.models.request.RegisterGCMRequest;
import com.halfplatepoha.frnds.network.models.response.RegisterGCMResponse;
import com.halfplatepoha.frnds.network.servicegenerators.ClientGenerator;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener,
        FacebookCallback<LoginResult>, OnCompleteListener<AuthResult>{

    private static final String TAG = LoginActivity.class.getSimpleName();

    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;

    private FrndsClient mClient;

    private int gcmRetryCount = 5;

    @Bind(R.id.btnFbLogin) LoginButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        mClient = new ClientGenerator.Builder()
                                .setBaseUrl(IConstants.FRNDS_BASE_URL)
                                .setLoggingInterceptor()
                                .setClientClass(FrndsClient.class)
                                .buildClient();

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
            callRegisterGCMApi();
            startSearchScreenActivity();
        } else {
            Toast.makeText(this, "You've logged out", Toast.LENGTH_SHORT).show();
        }
    }

    private void startSearchScreenActivity() {
        Intent startScreenIntent = new Intent(this, SearchScreenActivity.class);
        startActivity(startScreenIntent);
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        FrndsLog.d(loginResult.getAccessToken().getToken());
        handleAccessToken(loginResult.getAccessToken());
    }

    private void handleAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        FrndsPreference.setInPref(IPrefConstants.FB_USER_ID, accessToken.getUserId());
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

    private void callRegisterGCMApi() {
        RegisterGCMRequest req = new RegisterGCMRequest();
        req.setDeviceId(FrndsPreference.getFromPref(IPrefConstants.FCM_REFRESH_TOKEN, ""));
        req.setFbId(FrndsPreference.getFromPref(IPrefConstants.FB_USER_ID, ""));

        mClient.updateGCM(req)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new BaseSubscriber<RegisterGCMResponse>() {
                    @Override
                    public void onObjectReceived(RegisterGCMResponse registerGCMResponse) {
                        if(!registerGCMResponse.isSuccessful() && gcmRetryCount > 0)
                            callRegisterGCMApi();
                        else
                            FrndsPreference.setInPref(IPrefConstants.FCM_REFRESH_TOKEN_REGISTERED, true);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
