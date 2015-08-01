package com.scelos.tenjin;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TimePicker;

import com.melnykov.fab.FloatingActionButton;

import java.util.Calendar;


public class AlarmsActivity extends ActionBarActivity {
    private ListView alarmsView;
    private FloatingActionButton newAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarms);

        getWindow().getDecorView().setBackgroundColor(Color.parseColor("#3e3a4f"));

        String[] test = {"7:25", "8:30", "9:45"};
        alarmsView = (ListView)findViewById(R.id.listView);
        newAlarm = (FloatingActionButton)findViewById(R.id.newAlarm);
        alarmsView.setAdapter(new AlarmItems(this, test));

        newAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker = new TimePickerDialog(AlarmsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        System.out.println("here");
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
}
