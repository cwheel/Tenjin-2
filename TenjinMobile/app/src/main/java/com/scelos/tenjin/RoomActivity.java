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

import org.json.JSONObject;

import java.util.HashMap;


public class RoomActivity extends ActionBarActivity implements TenjinRoomDelegate {
    private final int lightOn = 255;

    private ColorPicker picker;
    private SaturationBar saturationBar;
    private ValueBar valueBar;

    private Switch leftBed;
    private Switch rightBed;
    private Switch desk;

    private TenjinRoom room;

    private String username;
    private String password;

    @Override
    public void roomLightContextUpdated(HashMap context) {
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
    public void roomLightProxyAuthSuccess() {
        room.fetchLightingContext(this);
    }

    @Override
    public void roomLightProxyAuthFailure() {
        returnToLogin();
    }

    @Override
    public void roomAlarmsUpdate(JSONObject resp) {

    }

    @Override
    protected void onResume() {
        room = new TenjinRoom(this, Config.srv, username, password);
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        getWindow().getDecorView().setBackgroundColor(Color.parseColor("#3e3a4f"));

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

        username = this.getIntent().getStringExtra("username");
        password = this.getIntent().getStringExtra("password");
        room = new TenjinRoom(this, Config.srv, username, password);

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
                        room.setLight(TenjinRoom.superWhite1, lightOn);
                    } else {
                        room.setLight(TenjinRoom.superWhite1, 0);
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
                        room.setLight(TenjinRoom.superWhite2, lightOn);
                    } else {
                        room.setLight(TenjinRoom.superWhite2, 0);
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
                        room.setLight(TenjinRoom.whiteMain, lightOn);
                    } else {
                        room.setLight(TenjinRoom.whiteMain, 0);
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
            i.putExtra("username", username);
            i.putExtra("password", password);
            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            this.startActivity(i);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void pickerChanged() {
        try {
            room.setLight(TenjinRoom.blue, Color.blue(picker.getColor()));
            room.setLight(TenjinRoom.red, Color.red(picker.getColor()));
            room.setLight(TenjinRoom.green, Color.green(picker.getColor()));
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
