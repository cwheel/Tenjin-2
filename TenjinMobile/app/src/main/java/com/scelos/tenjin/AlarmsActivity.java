package com.scelos.tenjin;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TimePicker;

import com.melnykov.fab.FloatingActionButton;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;


public class AlarmsActivity extends ActionBarActivity implements TenjinRoomDelegate {
    private ListView alarmsView;
    private FloatingActionButton newAlarm;
    private TenjinRoom room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarms);

        getWindow().getDecorView().setBackgroundColor(Color.parseColor("#3e3a4f"));

        String[] test = {"7:25", "8:30", "9:45"};
        alarmsView = (ListView)findViewById(R.id.listView);
        newAlarm = (FloatingActionButton)findViewById(R.id.newAlarm);
        alarmsView.setAdapter(new AlarmsListAdapter(this, test));

        newAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker = new TimePickerDialog(AlarmsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                    }
                }, hour, minute, true);
                mTimePicker.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void roomLightContextUpdated(HashMap context) {

    }

    @Override
    public void roomLightProxyAuthSuccess() {

    }

    @Override
    public void roomLightProxyAuthFailure() {

    }

    @Override
    public void roomAlarmsUpdate(JSONObject resp) {

    }
}
