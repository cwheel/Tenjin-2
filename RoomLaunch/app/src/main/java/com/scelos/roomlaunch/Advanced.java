package com.scelos.roomlaunch;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;

import org.json.JSONObject;

import java.util.HashMap;

public class Advanced extends Activity implements TenjinRoomDelegate {
    private TenjinRoom room;
    private final int SEND_THRESHOLD = 40;

    private SeekBar r1;
    private SeekBar g1;
    private SeekBar b1;
    private SeekBar w1;
    private SeekBar r2;
    private SeekBar g2;
    private SeekBar b2;
    private SeekBar w2;
    private SeekBar w3;

    private Switch complementarySync;
    private boolean complementaryLock = false;
    private Switch bedSync;
    private Switch musicSync;

    @Override
    public void roomLightProxyAuthSuccess() {}

    @Override
    public void roomLightProxyAuthFailure() {}

    @Override
    public void roomAlarmsUpdate(JSONObject resp) {}

    @Override
    public void roomLightContextUpdated(HashMap context) {
        r1.setProgress((int) context.get("red1"));
        g1.setProgress((int) context.get("green1"));
        b1.setProgress((int) context.get("blue1"));
        w1.setProgress((int) context.get("white1"));
        r2.setProgress((int) context.get("red2"));
        g2.setProgress((int) context.get("green2"));
        b2.setProgress((int) context.get("blue2"));
        w2.setProgress((int) context.get("white2"));
        w3.setProgress((int) context.get("white3"));

        r1.setEnabled(true);
        g1.setEnabled(true);
        b1.setEnabled(true);
        w1.setEnabled(true);
        r2.setEnabled(true);
        g2.setEnabled(true);
        b2.setEnabled(true);
        w2.setEnabled(true);
        w3.setEnabled(true);
    }

    @Override
    protected void onResume() {
        room.fetchLightingContext(this);

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);

        r1.setEnabled(false);
        g1.setEnabled(false);
        b1.setEnabled(false);
        w1.setEnabled(false);
        r2.setEnabled(false);
        g2.setEnabled(false);
        b2.setEnabled(false);
        w2.setEnabled(false);
        w3.setEnabled(false);

        r1.setProgress(sharedPreferences.getInt("r1", 0));
        g1.setProgress(sharedPreferences.getInt("g1", 0));
        b1.setProgress(sharedPreferences.getInt("b1", 0));
        w1.setProgress(sharedPreferences.getInt("w1", 0));
        r2.setProgress(sharedPreferences.getInt("r2", 0));
        g2.setProgress(sharedPreferences.getInt("g2", 0));
        b2.setProgress(sharedPreferences.getInt("b2", 0));
        w2.setProgress(sharedPreferences.getInt("w2", 0));
        w3.setProgress(sharedPreferences.getInt("w3", 0));

        bedSync.setChecked(sharedPreferences.getBoolean("bedSync", false));
        complementarySync.setChecked(sharedPreferences.getBoolean("bedSync", false));
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("r1", r1.getProgress());
        editor.putInt("g1", g1.getProgress());
        editor.putInt("b1", b1.getProgress());
        editor.putInt("w1", w1.getProgress());
        editor.putInt("r2", r2.getProgress());
        editor.putInt("g2", g2.getProgress());
        editor.putInt("b2", b2.getProgress());
        editor.putInt("w2", w2.getProgress());
        editor.putInt("w3", w3.getProgress());

        editor.putBoolean("bedSync", bedSync.isChecked());
        editor.putBoolean("complementarySync", complementarySync.isChecked());
        editor.commit();

        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_advanced);

        room = new TenjinRoom(this);
        room.fetchLightingContext(this);

        r1 = (SeekBar)findViewById(R.id.r1);
        g1 = (SeekBar)findViewById(R.id.g1);
        b1 = (SeekBar)findViewById(R.id.b1);
        w1 = (SeekBar)findViewById(R.id.w1);
        r2 = (SeekBar)findViewById(R.id.r2);
        g2 = (SeekBar)findViewById(R.id.g2);
        b2 = (SeekBar)findViewById(R.id.b2);
        w2 = (SeekBar)findViewById(R.id.w2);
        w3 = (SeekBar)findViewById(R.id.w3);

        bedSync = (Switch)findViewById(R.id.bedSync);
        complementarySync = (Switch)findViewById(R.id.complementarySync);
        bedSync.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //Update the 2nd Light Strip to the same values as the first
                    r2.setProgress(r1.getProgress());
                    g2.setProgress(g1.getProgress());
                    b2.setProgress(b1.getProgress());
                    w2.setProgress(w1.getProgress());

                    try {
                        room.setRGBWLight(TenjinRoom.rgbw2, r1.getProgress(), g1.getProgress(), b1.getProgress(), w1.getProgress());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //Disable Complementary when the lights are in Sync Mode
                complementarySync.setEnabled(!isChecked);
            }
        });
        complementarySync.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    updateComplementary(1);
                }
                //Disable Sync Lights as it conflicts with Complementary Colors
                bedSync.setEnabled(!isChecked);
            }
        });
        r1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int val = 0;
            int lastVal = 0;

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    if (bedSync.isChecked()) {
                        room.setLight(TenjinRoom.red, val);
                    } else if (complementarySync.isChecked()){
                        updateComplementary(1);
                    } else {
                        room.setLight(TenjinRoom.red1, val);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                lastVal = val;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                val = progress;

                if (bedSync.isChecked()) {
                    r2.setProgress(val);
                }

                if (Math.abs(val - lastVal) > SEND_THRESHOLD) {
                    try {
                        if (bedSync.isChecked()) {
                            room.setLight(TenjinRoom.red, val);
                        }else if (complementarySync.isChecked() && !complementaryLock){
                            updateComplementary(1);
                        } else {
                            room.setLight(TenjinRoom.red1, val);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        g1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int val = 0;
            int lastVal = 0;

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    if (bedSync.isChecked()) {
                        room.setLight(TenjinRoom.green, val);
                    } else if (complementarySync.isChecked()){
                        updateComplementary(1);
                    }else {
                        room.setLight(TenjinRoom.green1, val);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                lastVal = val;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                val = progress;

                if (bedSync.isChecked()) {
                    g2.setProgress(val);
                }

                if (Math.abs(val - lastVal) > SEND_THRESHOLD) {
                    try {
                        if (bedSync.isChecked()) {
                            room.setLight(TenjinRoom.green, val);
                        } else if (complementarySync.isChecked()&& !complementaryLock){
                            updateComplementary(1);
                        }else {
                            room.setLight(TenjinRoom.green1, val);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        b1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int val = 0;
            int lastVal = 0;

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    if (bedSync.isChecked()) {
                        room.setLight(TenjinRoom.blue, val);
                    }else if (complementarySync.isChecked()){
                        updateComplementary(1);
                    } else {
                        room.setLight(TenjinRoom.blue1, val);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                lastVal = val;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                val = progress;

                if (bedSync.isChecked()) {
                    b2.setProgress(val);
                }

                if (Math.abs(val - lastVal) > SEND_THRESHOLD) {
                    try {
                        if (bedSync.isChecked()) {
                            room.setLight(TenjinRoom.blue, val);
                        }else if (complementarySync.isChecked() && !complementaryLock){
                            updateComplementary(1);
                        } else {
                            room.setLight(TenjinRoom.blue1, val);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        w1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int val = 0;
            int lastVal = 0;

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    if (bedSync.isChecked()) {
                        room.setLight(TenjinRoom.whiteBeds, val);
                    } else {
                        room.setLight(TenjinRoom.white1, val);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                lastVal = val;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                val = progress;

                if (bedSync.isChecked()) {
                    w2.setProgress(val);
                }

                if (Math.abs(val - lastVal) > SEND_THRESHOLD) {
                    try {
                        if (bedSync.isChecked()) {
                            room.setLight(TenjinRoom.whiteBeds, val);
                        } else {
                            room.setLight(TenjinRoom.white1, val);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        r2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int val = 0;
            int lastVal = 0;

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    if (complementarySync.isChecked() ) {
                        updateComplementary(2);
                    }else if (!bedSync.isChecked()) {
                        room.setLight(TenjinRoom.red2, val);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                lastVal = val;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                val = progress;

                if (bedSync.isChecked()) {
                    r1.setProgress(val);
                }

                if (Math.abs(val - lastVal) > SEND_THRESHOLD) {
                    try {
                        if (complementarySync.isChecked() && !complementaryLock) {
                            updateComplementary(2);
                        }else if (!bedSync.isChecked()) {
                            room.setLight(TenjinRoom.red2, val);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        g2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int val = 0;
            int lastVal = 0;

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    if (complementarySync.isChecked()) {
                        updateComplementary(2);
                    }else if (!bedSync.isChecked()) {
                        room.setLight(TenjinRoom.green2, val);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                lastVal = val;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                val = progress;

                if (bedSync.isChecked()) {
                    g1.setProgress(val);
                }

                if (Math.abs(val - lastVal) > SEND_THRESHOLD) {
                    try {
                        if (complementarySync.isChecked() && !complementaryLock) {
                            updateComplementary(2);
                        }else if (!bedSync.isChecked()) {
                            room.setLight(TenjinRoom.green2, val);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        b2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int val = 0;
            int lastVal = 0;

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    if (complementarySync.isChecked()) {
                        updateComplementary(2);
                    }else if (!bedSync.isChecked()) {
                        room.setLight(TenjinRoom.blue2, val);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                lastVal = val;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                val = progress;

                if (bedSync.isChecked()) {
                    b1.setProgress(val);
                }

                if (Math.abs(val - lastVal) > SEND_THRESHOLD) {
                    try {
                        if (complementarySync.isChecked() && !complementaryLock) {
                            updateComplementary(2);
                        }else if (!bedSync.isChecked()) {
                            room.setLight(TenjinRoom.blue2, val);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        w2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int val = 0;
            int lastVal = 0;

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    if (!bedSync.isChecked()) {
                        room.setLight(TenjinRoom.white2, val);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                lastVal = val;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                val = progress;

                if (bedSync.isChecked()) {
                    w1.setProgress(val);
                }

                if (Math.abs(val - lastVal) > SEND_THRESHOLD) {
                    try {
                        if (!bedSync.isChecked()) {
                            room.setLight(TenjinRoom.white2, val);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        w3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int val = 0;
            int lastVal = 0;

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    room.setLight(TenjinRoom.whiteMain, val);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                lastVal = val;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                val = progress;

                if (Math.abs(val - lastVal) > SEND_THRESHOLD) {
                    try {
                        room.setLight(TenjinRoom.whiteMain, val);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private void updateComplementary(int strip) throws IllegalArgumentException{
        if(strip == 1){
            int[] stripOne = new int[3];
            stripOne[0] = r1.getProgress();
            stripOne[1] = g1.getProgress();
            stripOne[2] = b1.getProgress();
            int[] stripTwo = complementary(stripOne);
            complementaryLock = true;
            r2.setProgress(stripTwo[0]);
            g2.setProgress(stripTwo[1]);
            b2.setProgress(stripTwo[2]);
            try {
                room.setRGBWLight(TenjinRoom.rgbw2, stripTwo[0], stripTwo[1], stripTwo[2], w2.getProgress());
                room.setRGBWLight(TenjinRoom.rgbw1, stripOne[0], stripOne[1], stripOne[2], w1.getProgress());

            } catch (Exception e) {
                e.printStackTrace();
            }
            complementaryLock = false;

        }else if(strip == 2){
            int[] stripTwo = new int[3];
            stripTwo[0] = r2.getProgress();
            stripTwo[1] = g2.getProgress();
            stripTwo[2] = b2.getProgress();
            int[] stripOne = complementary(stripTwo);
            complementaryLock = true;
            r1.setProgress(stripOne[0]);
            g1.setProgress(stripOne[1]);
            b1.setProgress(stripOne[2]);
            try {
                room.setRGBWLight(TenjinRoom.rgbw2, stripTwo[0], stripTwo[1], stripTwo[2], w2.getProgress());
                room.setRGBWLight(TenjinRoom.rgbw1, stripOne[0], stripOne[1], stripOne[2], w1.getProgress());

            } catch (Exception e) {
                e.printStackTrace();
            }
            complementaryLock = false;
        }else{
            throw new IllegalArgumentException("Only Strips 1 and 2 are supported");
        }
    }
    private int[] complementary (int[] orig){
        float[] hsbvals = new float[3];
        Color.RGBToHSV(orig[0], orig[1], orig[2], hsbvals);
        hsbvals[0] = (float) ((hsbvals[0] + 180) % 360);
        int[] rgb = new int[3];
        int compositeColor = Color.HSVToColor(hsbvals);
        rgb[0] = Color.red(compositeColor);
        rgb[1] = Color.green(compositeColor);
        rgb[2] = Color.blue(compositeColor);
        return rgb;
    }
}
