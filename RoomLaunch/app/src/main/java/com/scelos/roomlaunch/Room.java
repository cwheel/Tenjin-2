package com.scelos.roomlaunch;

import com.scelos.roomlaunch.util.SystemUiHider;
import android.app.Activity;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */

public class Room extends Activity {
    private GestureDetectorCompat gDetect;
    private Typeface poiretFont;

    private TextClock clock;
    private TextView timeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_room);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Tenjin");
        wl.acquire();

        poiretFont = Typeface.createFromAsset(getAssets(), "poiret.ttf");

        gDetect = new GestureDetectorCompat(this, new GestureListener(this));
        timeText = (TextView)findViewById(R.id.timeText);
        clock = (TextClock)findViewById(R.id.clock);

        timeText.setTypeface(poiretFont);
        clock.setTypeface(poiretFont);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateWelcome();
            }
        }, 0, 1000*60*5);
    }

    @Override
    protected void onResume() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        super.onResume();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.gDetect.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void updateWelcome() {
        runOnUiThread(new Runnable() {
            public void run() {
                Date date = new Date();

                if (date.getHours() > 12 && date.getHours() < 17) {
                    timeText.setText("Good Afternoon");
                } else if (date.getHours() > 17 && date.getHours() < 24) {
                    timeText.setText("Good Evening");
                } else if (date.getHours() > 1 && date.getHours() < 12) {
                    timeText.setText("Good Morning");
                }
            }
        });
    }
}
