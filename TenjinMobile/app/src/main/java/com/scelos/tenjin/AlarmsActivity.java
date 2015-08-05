package com.scelos.tenjin;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TimePicker;

import com.melnykov.fab.FloatingActionButton;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


public class AlarmsActivity extends ActionBarActivity implements TenjinRoomDelegate {
    private ListView alarmsView;
    private FloatingActionButton newAlarm;
    private ProgressBar spinner;
    private TenjinRoom room;

    private JSONObject alarms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarms);

        getWindow().getDecorView().setBackgroundColor(Color.parseColor("#3e3a4f"));

        room = new TenjinRoom(this, Config.srv, this.getIntent().getStringExtra("username"), this.getIntent().getStringExtra("password"));

        alarmsView = (ListView)findViewById(R.id.listView);
        newAlarm = (FloatingActionButton)findViewById(R.id.newAlarm);
        spinner = (ProgressBar)findViewById(R.id.alarmsProgress);

        spinner.setIndeterminate(true);
        spinner.setVisibility(View.VISIBLE);

        newAlarm.attachToListView(alarmsView);

        newAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker = new TimePickerDialog(AlarmsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        Date occurenceToday = new Date();
                        occurenceToday.setHours(selectedHour);
                        occurenceToday.setMinutes(selectedMinute);

                        if (occurenceToday.before(new Date())) {
                            Calendar c = Calendar.getInstance();
                            c.setTime(occurenceToday);
                            c.add(Calendar.DATE, 1);
                            occurenceToday = c.getTime();
                        }

                        SimpleDateFormat jsFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
                        room.addRoomAlarm(String.valueOf(occurenceToday.toString().hashCode()), jsFormat.format(occurenceToday) + Config.timezone, "Today", TenjinRoom.audioAlarm);
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
        room.fetchRoomAlarms(this);
    }

    @Override
    public void roomLightProxyAuthFailure() {

    }

    @Override
    public void roomAlarmsUpdate(JSONObject resp) {
        spinner.setVisibility(View.GONE);
        alarmsView.setAdapter(new AlarmsListAdapter(this, resp, room));
    }
}
