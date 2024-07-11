package com.example.first_assignment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class homePage_activity extends AppCompatActivity {

    private MaterialButton playBtn;
    private MaterialButton recordsBtn;
    private RadioButton slowBtn;
    private RadioButton fastBtn;
    private RadioButton buttonsOption;
    private RadioButton sensorsOption;
    private RadioGroup speedGroup;
    private RadioGroup movementGroup;
    private String speedSelection;
    private String controlSelection;

    private TextView welcomeMessage;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        setupUI();
        configureListeners();
        displayWelcomeMessage(); // Display the welcome message
    }

    private void setupUI() {
        playBtn = findViewById(R.id.playButton);
        recordsBtn = findViewById(R.id.recordsButton);
        slowBtn = findViewById(R.id.slowRadioButton);
        fastBtn = findViewById(R.id.fastRadioButton);
        buttonsOption = findViewById(R.id.buttonsRadioButton);
        sensorsOption = findViewById(R.id.sensorsRadioButton);
        speedGroup = findViewById(R.id.gameSpeedRadioGroup);
        movementGroup = findViewById(R.id.movementRadioGroup);
        welcomeMessage = findViewById(R.id.welcome_message);

        // Set default values
        slowBtn.setChecked(true);
        speedSelection = getString(R.string.slow_option_string);
        buttonsOption.setChecked(true);
        controlSelection = getString(R.string.buttons_option_string);
    }

    private void displayWelcomeMessage() {
        username = getIntent().getStringExtra("username");
        if (username != null) {
            welcomeMessage.setText("Welcome, " + username + "!");
        } else {
            welcomeMessage.setText("Welcome!");
        }
    }

    private void configureListeners() {
        playBtn.setOnClickListener(v -> startGameActivity());
        recordsBtn.setOnClickListener(v -> openRecordsActivity());

        speedGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.slowRadioButton) {
                speedSelection = getString(R.string.slow_option_string);
            } else if (checkedId == R.id.fastRadioButton) {
                speedSelection = getString(R.string.fast_option_string);
            }
        });

        movementGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.buttonsRadioButton) {
                controlSelection = getString(R.string.buttons_option_string);
            } else if (checkedId == R.id.sensorsRadioButton) {
                controlSelection = getString(R.string.sensors_option_string);
            }
        });
    }

    private void startGameActivity() {
        Log.d("homePage_activity", "Selected Speed: " + speedSelection);
        Log.d("homePage_activity", "Selected Control: " + controlSelection);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.SPEED, speedSelection);
        intent.putExtra(MainActivity.CONTROL, controlSelection);
        intent.putExtra("username", username); // Pass the username to MainActivity
        startActivity(intent);
        finish();
    }

    private void openRecordsActivity() {
        Intent intent = new Intent(this, RecordsActivity.class);
        startActivity(intent);
        finish();
    }
}
