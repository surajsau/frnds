package com.halfplatepoha.frnds;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by surajkumarsau on 16/02/17.
 */

public class BaseActivity extends AppCompatActivity implements BaseView {

    @Override
    public void showShortToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
