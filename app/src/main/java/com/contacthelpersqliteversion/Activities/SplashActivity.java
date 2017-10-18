package com.contacthelpersqliteversion.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.maxim_ozarovskiy.contacthelperapp.R;


public class SplashActivity extends AppCompatActivity  {

    ImageView splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        splash = (ImageView) findViewById(R.id.splash);
        Animation splashAnimation = AnimationUtils.loadAnimation(this, R.anim.flashrotate);
        splash.startAnimation(splashAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startMainActivity();
            }

            private void startMainActivity() {
                Intent intent = new Intent(getApplicationContext(), ContactListActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);
    }
}
