package com.xchange_place.traxists.traxists;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

/**
 *
 * An activity that displays a splash screen for 1.5 seconds while the Traxist logo is
 * spun in a single, counterclockwise rotation.
 *
 * Created by Ryan Fletcher on 7/30/2015.
 */
public class SplashActivity extends Activity {

    private ImageView splashLogo;
    private final int SPLASH_DISPLAY_LENGTH = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        splashLogo = (ImageView) findViewById(R.id.splash_logo);

        // New handler to start the MainActivity and close
        // this splash screen after SPLASH_DISPLAY_LENGTH ms.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

        // define the RotateAnimation
        RotateAnimation rotate = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF,
                1.0f);

        // call the RotateAnimation to run
        rotate.setDuration(SPLASH_DISPLAY_LENGTH - 50);
        rotate.setRepeatCount(-1);
        splashLogo.setAnimation(rotate);
    }
}

