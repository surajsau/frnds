package com.halfplatepoha.frnds.home.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.halfplatepoha.frnds.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileAndSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_and_settings);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnLogout)
    public void logout() {
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
    }
}
