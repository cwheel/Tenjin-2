package com.scelos.tenjin;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AlarmItems extends BaseAdapter {
    String [] alarms;
    Context context;
    int [] imageId;
    private static LayoutInflater inflater = null;


    public AlarmItems(Activity activity, String[] alarmsSource) {
        alarms = alarmsSource;
        context = activity;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return alarms.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class AlarmRow
    {
        TextView alarmName;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        AlarmRow row = new AlarmRow();

        View rowView;
        rowView = inflater.inflate(R.layout.alarm, parent, false);
        row.alarmName = (TextView) rowView.findViewById(R.id.alarmTitle);
        row.alarmName.setText(alarms[position]);



        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return rowView;
    }

}