package com.scelos.roomlaunch;

import com.scelos.roomlaunch.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Switch;

import java.util.HashMap;

public class Advanced extends Activity implements LightControllerCallback{
    private LightController lc;
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

    private Switch bedSync;
    private Switch musicSync;

    @Override
    public void lightControllerContextUpdated(HashMap context) {
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
        lc.fetchContext(this);

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
        musicSync.setChecked(sharedPreferences.getBoolean("musicSync", false));

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
        editor.putBoolean("musicSync", musicSync.isChecked());
        editor.commit();

        super.onBackPressed();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        System.out.println("da");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_advanced);

        lc = new LightController(this);
        lc.fetchContext(this);

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
        musicSync= (Switch)findViewById(R.id.musicSync);

        r1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int val = 0;
            int lastVal = 0;

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    if (bedSync.isChecked()) {
                        lc.setLight(LightController.red, val);
                    } else {
                        lc.setLight(LightController.red1, val);
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
                            lc.setLight(LightController.red, val);
                        } else {
                            lc.setLight(LightController.red1, val);
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
                        lc.setLight(LightController.green, val);
                    } else {
                        lc.setLight(LightController.green1, val);
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
                            lc.setLight(LightController.green, val);
                        } else {
                            lc.setLight(LightController.green1, val);
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
                        lc.setLight(LightController.blue, val);
                    } else {
                        lc.setLight(LightController.blue1, val);
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
                            lc.setLight(LightController.blue, val);
                        } else {
                            lc.setLight(LightController.blue1, val);
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
                        lc.setLight(LightController.whiteBeds, val);
                    } else {
                        lc.setLight(LightController.white1, val);
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
                            lc.setLight(LightController.whiteBeds, val);
                        } else {
                            lc.setLight(LightController.white1, val);
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
                    if (!bedSync.isChecked()) {
                        lc.setLight(LightController.red2, val);
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
                        if (!bedSync.isChecked()) {
                            lc.setLight(LightController.red2, val);
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
                    if (!bedSync.isChecked()) {
                        lc.setLight(LightController.green2, val);
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
                        if (!bedSync.isChecked()) {
                            lc.setLight(LightController.green2, val);
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
                    if (!bedSync.isChecked()) {
                        lc.setLight(LightController.blue2, val);
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
                        if (!bedSync.isChecked()) {
                            lc.setLight(LightController.blue2, val);
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
                        lc.setLight(LightController.white2, val);
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
                            lc.setLight(LightController.white2, val);
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
                    lc.setLight(LightController.whiteMain, val);
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
                        lc.setLight(LightController.whiteMain, val);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
