package com.example.first_assignment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class RecordsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ListView recordsListView;
    private MaterialButton backButton;
    private TextView recentScoreTextView;
    private RecordsAdapter adapter;

    public static final String EXTRA_RECENT_SCORE = "EXTRA_RECENT_SCORE";
    private RecordManager recordManager;
    private GoogleMap googleMap;

    private static final LatLng DEFAULT_LOCATION = new LatLng(32.1151332652287, 34.81798588795976);

    private Record pendingRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        initializeViews();
        recordManager = new RecordManager(this); // Ensure RecordManager is initialized here
        loadRecordData();
        displayRecentScore();

        // Initialize the map
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.map_container, mapFragment);
        fragmentTransaction.commit();
        mapFragment.getMapAsync(this);
    }

    private void displayRecentScore() {
        Intent intent = getIntent();
        String recentScore = intent.getStringExtra(EXTRA_RECENT_SCORE);
        if (recentScore != null) {
            recentScoreTextView.setText(getString(R.string.recent_score_text, recentScore));
            recentScoreTextView.setVisibility(View.VISIBLE);
        } else {
            recentScoreTextView.setVisibility(View.GONE);
        }
    }

    private void initializeViews() {
        recordsListView = findViewById(R.id.records_list_view);
        backButton = findViewById(R.id.back_button);
        recentScoreTextView = findViewById(R.id.recent_score_text_view);

        backButton.setOnClickListener(v -> navigateToHomePage());
    }

    private void loadRecordData() {
        List<Record> records = recordManager.loadRecords();
        if (records != null && !records.isEmpty()) {
            // Trim the list to keep only the top 5 records
            if (records.size() > 5) {
                records = new ArrayList<>(records.subList(0, 5));
                // Save the trimmed list back to storage
                recordManager.saveRecords(records);
            }

            Log.d("RecordsActivity", "Records loaded: " + records.size());
            for (Record record : records) {
                Log.d("RecordsActivity", record.toString());
            }
            adapter = new RecordsAdapter(this, new ArrayList<>(records));
            recordsListView.setAdapter(adapter);

            recordsListView.setOnItemClickListener((parent, view, position, id) -> {
                Record selectedRecord = (Record) parent.getItemAtPosition(position);
                if (googleMap != null) {
                    updateMap(selectedRecord);
                } else {
                    pendingRecord = selectedRecord;
                    Log.d("RecordsActivity", "GoogleMap is not ready yet. Record stored for later update.");
                }
            });
        } else {
            Log.d("RecordsActivity", "No records found.");
            showToast();
        }
    }

    private void navigateToHomePage() {
        Intent intent = new Intent(this, homePage_activity.class);
        startActivity(intent);
        finish();
    }

    private void showToast() {
        Toast.makeText(this, "No records found.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        // Set default location
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15));
        googleMap.addMarker(new MarkerOptions().position(DEFAULT_LOCATION).title("Default Location"));

        // Update the map with the pending record if there is one
        if (pendingRecord != null) {
            updateMap(pendingRecord);
            pendingRecord = null;
        }
    }

    private void updateMap(Record record) {
        if (googleMap != null) {
            LatLng recordLocation = new LatLng(record.getLatitude(), record.getLongitude());
            Log.d("RecordsActivity", "Updating map with record location: " + recordLocation.toString());
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(recordLocation).title("Record Location"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(recordLocation, 15));
        } else {
            Log.d("RecordsActivity", "GoogleMap is not ready yet.");
        }
    }
}
