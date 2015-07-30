package com.scelos.tenjin;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;


public class AlarmsActivity extends ActionBarActivity {
    private ListView alarmsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarms);

        String[] test = {"7:25", "8:30", "9:45"};
        alarmsView = (ListView)findViewById(R.id.listView);
        alarmsView.setAdapter(new AlarmItems(this, test));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alarms, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (item.getItemId() == R.id.action_newAlarm) {
            newAlarm();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void newAlarm() {
        System.out.println("hi");
    }
}
