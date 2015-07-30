package com.scelos.tenjin;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;


public class RoomActivity extends ActionBarActivity implements LightControllerDelegate {
    private final int lightOn = 255;

    private ColorPicker picker;
    private SaturationBar saturationBar;
    private ValueBar valueBar;

    private Switch leftBed;
    private Switch rightBed;
    private Switch desk;

    private LightController lc;

    @Override
    public void lightControllerContextUpdated(HashMap context) {
        leftBed.setEnabled(true);
        rightBed.setEnabled(true);
        desk.setEnabled(true);
        picker.setEnabled(true);
        saturationBar.setEnabled(true);
        valueBar.setEnabled(true);

        if ((int)context.get("white3") == lightOn) {
            desk.setChecked(true);
        }

        if ((int)context.get("white1") == lightOn && (int)context.get("red1") == lightOn && (int)context.get("green1") == lightOn && (int)context.get("blue1") == lightOn) {
            leftBed.setChecked(true);
        }

        if ((int)context.get("white2") == lightOn && (int)context.get("red2") == lightOn && (int)context.get("green2") == lightOn && (int)context.get("blue2") == lightOn) {
            rightBed.setChecked(true);
        }

        picker.setColor(Color.argb(1, ((int)context.get("red1")+(int)context.get("red2"))/2, ((int)context.get("green1")+(int)context.get("green2"))/2, ((int)context.get("blue1")+(int)context.get("blue2"))/2));
    }

    @Override
    public void lightControllerProxyAuthSuccess() {
        lc.fetchContext(this);
    }

    @Override
    public void lightControllerProxyAuthFailure() {
        returnToLogin();
    }

    @Override
    protected void onResume() {
        lc = new LightController(this, Config.srv, this.getIntent().getStringExtra("username"), this.getIntent().getStringExtra("password"));
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        picker = (ColorPicker)findViewById(R.id.picker);
        saturationBar = (SaturationBar)findViewById(R.id.saturationbar);
        valueBar = (ValueBar)findViewById(R.id.valuebar);

        leftBed = (Switch)findViewById(R.id.leftBed);
        rightBed = (Switch)findViewById(R.id.rightBed);
        desk = (Switch)findViewById(R.id.mainDesk);

        leftBed.setEnabled(false);
        rightBed.setEnabled(false);
        desk.setEnabled(false);
        picker.setEnabled(false);
        saturationBar.setEnabled(false);
        valueBar.setEnabled(false);

        lc = new LightController(this, Config.srv, this.getIntent().getStringExtra("username"), this.getIntent().getStringExtra("password"));

        picker.addValueBar(valueBar);
        picker.addSaturationBar(saturationBar);
        picker.setShowOldCenterColor(false);

        valueBar.setOnValueChangedListener(new ValueBar.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                pickerChanged();
            }
        });

        saturationBar.setOnSaturationChangedListener(new SaturationBar.OnSaturationChangedListener() {
            int prevSat = 0;

            @Override
            public void onSaturationChanged(int saturation) {
                if (Math.abs(saturation - prevSat) > 10) {
                    prevSat = saturation;
                    pickerChanged();
                }
            }
        });

        picker.setOnColorSelectedListener(new ColorPicker.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                pickerChanged();
            }
        });

        leftBed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked) {
                        lc.setLight(LightController.superWhite1, lightOn);
                    } else {
                        lc.setLight(LightController.superWhite1, 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        rightBed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked) {
                        lc.setLight(LightController.superWhite2, lightOn);
                    } else {
                        lc.setLight(LightController.superWhite2, 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        desk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked) {
                        lc.setLight(LightController.whiteMain, lightOn);
                    } else {
                        lc.setLight(LightController.whiteMain, 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_room, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            returnToLogin();
            return true;
        }

        if (item.getItemId() == R.id.action_alarms) {
            Intent i = new Intent(this.getApplicationContext(), AlarmsActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            this.startActivity(i);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void pickerChanged() {
        try {
            lc.setLight(LightController.blue, Color.blue(picker.getColor()));
            lc.setLight(LightController.red, Color.red(picker.getColor()));
            lc.setLight(LightController.green, Color.green(picker.getColor()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void returnToLogin() {
        Intent i = new Intent(this.getApplicationContext(), LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        this.startActivity(i);

        finish();
    }
}
