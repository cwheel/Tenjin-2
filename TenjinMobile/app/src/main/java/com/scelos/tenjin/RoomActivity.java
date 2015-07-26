package com.scelos.tenjin;

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


public class RoomActivity extends ActionBarActivity {
    private ColorPicker picker;
    private SaturationBar saturationBar;
    private ValueBar valueBar;

    private Switch leftBed;
    private Switch rightBed;
    private Switch desk;

    private LightController lc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        picker = (ColorPicker)findViewById(R.id.picker);
        saturationBar = (SaturationBar)findViewById(R.id.saturationbar);
        valueBar = (ValueBar)findViewById(R.id.valuebar);

        leftBed = (Switch)findViewById(R.id.leftBed);
        rightBed = (Switch)findViewById(R.id.leftBed);
        desk = (Switch)findViewById(R.id.mainDesk);

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

            }
        });

        rightBed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        desk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void pickerChanged() {
        System.out.println(Color.red(picker.getColor()) + " " + Color.green(picker.getColor()) + " " + Color.blue(picker.getColor()));
    }
}
