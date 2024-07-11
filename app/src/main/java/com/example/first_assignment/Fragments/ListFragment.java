package com.example.first_assignment.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.first_assignment.R;
import com.example.first_assignment.Record;
import com.example.first_assignment.RecordManager;
import com.example.first_assignment.RecordsAdapter;
import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    private ListView recordsListView;
    private OnRecordSelectedListener listener;
    private RecordsAdapter adapter;

    public interface OnRecordSelectedListener {
        void onRecordSelected(Record record);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnRecordSelectedListener) {
            listener = (OnRecordSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnRecordSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fregment, container, false);
        recordsListView = view.findViewById(R.id.records_list_view);
        loadRecordData();
        return view;
    }

    private void loadRecordData() {
        RecordManager recordManager = new RecordManager(getContext());
        List<Record> records = recordManager.loadRecords();
        if (records != null && !records.isEmpty()) {
            Log.d("ListFragment", "Records loaded: " + records.size());
            if (records.size() > 5) {
                records = new ArrayList<>(records.subList(0, 5));
                recordManager.saveRecords(records);
            }
            adapter = new RecordsAdapter(getContext(), new ArrayList<>(records));
            recordsListView.setAdapter(adapter);

            recordsListView.setOnItemClickListener((parent, view, position, id) -> {
                Record selectedRecord = (Record) parent.getItemAtPosition(position);
                Log.d("ListFragment", "Item clicked: " + selectedRecord.toString());
                if (listener != null) {
                    listener.onRecordSelected(selectedRecord);
                }
            });
        } else {
            Log.d("ListFragment", "No records found.");
            // Handle empty or null records
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
