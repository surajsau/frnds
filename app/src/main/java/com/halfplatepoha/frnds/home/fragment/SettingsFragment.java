package com.halfplatepoha.frnds.home.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.halfplatepoha.frnds.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by surajkumarsau on 30/09/16.
 */
public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @OnClick(R.id.btnLogout)
    public void logout() {
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();

    }
}
