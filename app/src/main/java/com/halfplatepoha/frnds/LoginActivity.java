package com.halfplatepoha.frnds;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        FirebaseAuth.AuthStateListener, OnCompleteListener<AuthResult>{

    private FirebaseAuth mAuth;

    @Bind(R.id.btnLogin) Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(this);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin: {
                mAuth.signInAnonymously().addOnCompleteListener(this, this);
            }
            break;
        }
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
    public void onComplete(@NonNull Task<AuthResult> task) {
        Toast.makeText(this, task.isSuccessful() ? "Sign in success" : "Sign in unsuccessful", Toast.LENGTH_SHORT).show();
    }
}
