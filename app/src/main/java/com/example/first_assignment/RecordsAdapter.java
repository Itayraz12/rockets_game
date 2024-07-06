package com.example.first_assignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RecordsAdapter extends ArrayAdapter<Record> {

    public RecordsAdapter(Context context, ArrayList<Record> records) {
        super(context, 0, records);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Record record = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.record_item, parent, false);
        }

        TextView pointsTextView = convertView.findViewById(R.id.record_points);
        TextView latLonTextView = convertView.findViewById(R.id.record_lat_lon);

        pointsTextView.setText(String.valueOf(record.getPoints()));
        latLonTextView.setText("Location: " + record.getLatitude() + ", " + record.getLongitude());

        return convertView;
    }
}
