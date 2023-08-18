package com.example.ninesquaregridview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "tag";
    private String mPassword;
    private NineSquareGridView gv;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gv = findViewById(R.id.gv);
        gv.setLockPatternListener(new NineSquareGridView.LockPatternListener() {
            @Override
            public void unLock(@NonNull String password) {
                if (!mPassword.equals(password)) gv.showSelectError();
                else gv.postDelayed(()->gv.clearSelect(),1000);
            }

            @Override
            public void lock(@NonNull String password) {
                Log.d(TAG, "lock: " + password);
                mPassword = password;
                gv.postDelayed(() ->
                    gv.clearSelect(), 1000);
            }
        });
    }
}