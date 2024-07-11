package com.example.first_assignment;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.first_assignment.Fragments.ListFragment;
import com.example.first_assignment.Fragments.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class RecordsActivity extends AppCompatActivity implements ListFragment.OnRecordSelectedListener {

    public  static final String EXTRA_RECENT_SCORE = "EXTRA_RECENT_SCORE" ;
    private TextView recentScoreTextView;
    private RecordManager recordManager;
    private MapFragment mapFragment;

    private MaterialButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        initializeViews();
        recordManager = new RecordManager(this);
        displayRecentScore();

        loadFragments();
    }

    private void initializeViews() {
        recentScoreTextView = findViewById(R.id.recent_score_text_view);
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> navigateToHomePage());
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

    private void loadFragments() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ListFragment listFragment = new ListFragment();


        mapFragment = new MapFragment();


        // Show fragments
        getSupportFragmentManager().beginTransaction().add(R.id.list_container, listFragment).commit();
        mapFragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.map_container, mapFragment).commit();
    }

    @Override
    public void onRecordSelected(Record record) {
        if (mapFragment != null && mapFragment.isAdded()) {
            mapFragment.updateMapLocation(record.getLatitude(), record.getLongitude(), "Record Location");
        } else {
            Toast.makeText(this, "Map is not ready yet.", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToHomePage() {
        Intent intent = new Intent(this, homePage_activity.class);
        startActivity(intent);
        finish();
    }
}
