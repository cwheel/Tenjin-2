package com.scelos.tenjin;


import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Locale;

public class AlarmsListAdapter extends BaseAdapter {
    private JSONObject alarms;
    Context context;
    int [] imageId;
    private Typeface robotoFont;
    private static LayoutInflater inflater = null;
    private TenjinRoom room;


    public AlarmsListAdapter(Activity activity, JSONObject alarmsSource, TenjinRoom roomSource) {
        alarms = alarmsSource;
        context = activity;
        room = roomSource;

        robotoFont = Typeface.createFromAsset(activity.getAssets(), "roboto_light.ttf");
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        if (alarms == null) {
            System.out.println("0");
            return 0;
        } else {
            System.out.println(alarms.length());
            return alarms.length();
        }
    }

    @Override
    public Object getItem(int position) {
        if (alarms == null) {
            return null;
        }

        Iterator<?> keys = alarms.keys();
        int pos = 0;

        while (keys.hasNext()) {
            String key = (String)keys.next();

            try {
                if (alarms.get(key) instanceof JSONObject && pos == position) {
                    return alarms.get(key);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            pos++;
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getAlarmTitle(int position) {
        Iterator<?> keys = alarms.keys();
        int pos = 0;

        while (keys.hasNext()) {
            String key = (String)keys.next();

            try {
                if (alarms.get(key) instanceof JSONObject && pos == position) {
                    return key;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            pos++;
        }

        return null;
    }

    public class AlarmRow {
        TextView alarmTime;
        Switch alarmEnabled;
        TextView alarmDesc;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        AlarmRow row = new AlarmRow();

        View rowView;
        rowView = inflater.inflate(R.layout.alarm, parent, false);
        row.alarmEnabled = (Switch)rowView.findViewById(R.id.alarmEnabled);
        row.alarmTime = (TextView)rowView.findViewById(R.id.alarmTime);
        row.alarmDesc = (TextView)rowView.findViewById(R.id.alarmDesc);

        row.alarmTime.setTypeface(robotoFont);

        Date alarmDate = new Date();
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);
            alarmDate = format.parse(((JSONObject)getItem(position)).getString("date"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        row.alarmTime.setText(alarmDate.getHours() + ":" + alarmDate.getMinutes());

        DateFormat dateFormat = new SimpleDateFormat("EEEE, d MMM yyyy");
        row.alarmDesc.setText(dateFormat.format(alarmDate));

        if (alarmDate.before(new Date())) {
            row.alarmEnabled.setChecked(false);
        } else {
            row.alarmEnabled.setChecked(true);
        }

        row.alarmEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    room.validateAlarm(getAlarmTitle(position));
                } else {
                    room.invalidateAlarm(getAlarmTitle(position));
                }
            }
        });

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return rowView;
    }

}