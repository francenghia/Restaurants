package com.example.customer.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.customer.R;

public class SplashActivity extends AppCompatActivity {

    private TextView txtWelcome,txtAnim;
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        txtWelcome=findViewById(R.id.txtWelcome);
        txtAnim=findViewById(R.id.txtAnim);

        animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.push_right);
        txtWelcome.setAnimation(animation);

        animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.push_left);
        txtAnim.setAnimation(animation);

        Handler handler = new Handler();
        final Intent mIntent = new Intent(this, MainActivity.class);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(mIntent);
                finish();
            }
        },3500);
    }
}
