package com.example.first_assignment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class RecordsActivity extends AppCompatActivity {
    private ListView recordsListView;
    private MaterialButton backButton;
    private TextView recentScoreTextView;
    private RecordsAdapter adapter;

    public static final String EXTRA_RECENT_SCORE = "EXTRA_RECENT_SCORE";
    private RecordManager recordManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        initializeViews();
        recordManager = new RecordManager(this); // Ensure RecordManager is initialized here
        loadRecordData();
        displayRecentScore();
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
            Log.d("RecordsActivity", "Records loaded: " + records.size());
            for (Record record : records) {
                Log.d("RecordsActivity", record.toString());
            }
            adapter = new RecordsAdapter(this, new ArrayList<>(records));
            recordsListView.setAdapter(adapter);
        } else {
            Log.d("RecordsActivity", "No records found.");
            showToast("No records found.");
        }
    }

    private void navigateToHomePage() {
        Intent intent = new Intent(this, homePage_activity.class);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
