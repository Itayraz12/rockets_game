package com.example.first_assignment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class RecordsAdapter extends ArrayAdapter<Record> {

    public RecordsAdapter(Context context, ArrayList<Record> records) {
        super(context, 0, records);
    }

    @NonNull
    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Record record = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.record_item, parent, false);
        }

        TextView pointsTextView = convertView.findViewById(R.id.record_points);
        TextView userNameTextView = convertView.findViewById(R.id.user_name);

        assert record != null;
        pointsTextView.setText("SCORE : " + String.valueOf(record.getPoints()));
        userNameTextView.setText("USER NAME : " + record.getUserName());

        return convertView;
    }
}
