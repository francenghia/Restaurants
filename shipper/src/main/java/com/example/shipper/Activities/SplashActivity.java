package com.example.shipper.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shipper.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        final Intent mIntent = new Intent(this, MainActivity.class);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(mIntent);
                finish();
            }
        },2000);
    }
}
