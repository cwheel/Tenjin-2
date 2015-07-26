package com.scelos.roomlaunch;

import android.app.Activity;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {
    private LightController lc;
    private Activity activity;
    private int lightState = 0;

    private final int SWIPE_DISTANCE_THRESHOLD = 50;
    private final int SWIPE_VELOCITY_THRESHOLD = 50;
    private final int MAX_SWIPE = 1024;

    private final int MAX_LIGHT = 255;
    private final int MIN_LIGHT = 0;

    public GestureListener(Activity activ) {
        activity = activ;
        lc = new LightController(activ);
    }

    @Override
    public boolean onDown(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e2, MotionEvent e1, float velocityX, float velocityY) {
        float distanceX = e2.getX() - e1.getX();
        float distanceY = e2.getY() - e1.getY();

        if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
            if (distanceX > 0) {
                lightState = lightState - Math.abs(Math.round((MAX_LIGHT * distanceX) / MAX_SWIPE));
            } else {
                lightState = lightState + Math.abs(Math.round((MAX_LIGHT * distanceX) / MAX_SWIPE));
            }

            if (lightState > MAX_LIGHT) lightState = MAX_LIGHT;
            if (lightState < MIN_LIGHT) lightState = MIN_LIGHT;

            try {
                lc.setLight(LightController.superWhiteAll, lightState);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        } else if (Math.abs(distanceY) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
            if (distanceY > 0) {
                try {
                    lc.setLight(LightController.superWhiteAll, MAX_LIGHT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    lc.setLight(LightController.superWhiteAll, MIN_LIGHT );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Intent i = new Intent(activity.getApplicationContext(), Advanced.class);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(i);

        super.onLongPress(e);
    }
}