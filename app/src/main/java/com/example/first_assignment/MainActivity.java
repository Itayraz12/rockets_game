package com.example.first_assignment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.first_assignment.buisnessLogic.GameManager;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private ArrayList<Record> recordsList;
    private MediaPlayer mediaPlayer;

    private AppCompatImageView[] hearts_img;
    private AppCompatImageView[] player_icon_cells;
    private AppCompatImageView[][] rockets_mat;
    private MaterialTextView score_label;

    private GameManager gameManager;
    private static final long DELAY = 1000L;
    private long startTime;
    private int playerPosition;

    private boolean timerOn = false;
    private Timer timer;
    private final String DEFAULT_REASON = "default";
    public static final String NO_COLLISION = "none";
    public static final String OBSTACLE_COLLISION = "obstacle";
    public static final String GOLD_COLLISION = "gold";
    public static final String CONTROL = "CONTROL_KEY";
    public static final String SPEED = "SPEED_KEY";

    public final String CHANGE_POINTS = "change_points";

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private SensorEventListener sensorEventListener;
    private Toast currentToast;
    private RecordManager recordManager;

    private LocationManager locationManager;
    private double currentLatitude;
    private double currentLongitude;

    private static final float SENSITIVITY_THRESHOLD = 4.0f; // Increased the threshold value

    private long lastSensorUpdateTime = 0;
    private static final long SENSOR_UPDATE_INTERVAL = 500; // 500 milliseconds debounce interval

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the LocationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Check for location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // Request location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, this);
        }

        // Initialize the MediaPlayer instance
        mediaPlayer = MediaPlayer.create(this, R.raw.hit_sound);

        findViews();

        // Retrieve the username from the Intent
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String speedSelection = intent.getStringExtra(SPEED);
        String controlSelection = intent.getStringExtra(CONTROL);

        gameManager = new GameManager(username,rockets_mat.length, rockets_mat[0].length);
        recordManager = new RecordManager(this);

        // Retrieve the selected options from the Intent



        // Configure the game settings based on the retrieved options
        configureGameSettings(speedSelection, controlSelection);

        // Load existing records
        recordsList = loadRecords();

        initViews();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, this);
                }
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void configureGameSettings(String speedSelection, String controlSelection) {
        if (speedSelection != null) {
            if (speedSelection.equals(getString(R.string.slow_option_string))) {
                gameManager.setSpeed(GameManager.SLOW_SPEED);
            } else if (speedSelection.equals(getString(R.string.fast_option_string))) {
                gameManager.setSpeed(GameManager.FAST_SPEED);
            }
        }

        if (controlSelection != null) {
            if (controlSelection.equals(getString(R.string.buttons_option_string))) {
                enableButtonControls();
            } else if (controlSelection.equals(getString(R.string.sensors_option_string))) {
                enableSensorControls();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTimer();
        // Register sensor listener if sensors are enabled
        if (sensorEventListener != null) {
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
        // Unregister sensor listener to save battery
        if (sensorEventListener != null) {
            sensorManager.unregisterListener(sensorEventListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release the MediaPlayer when the activity is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    private void findViews() {
        //score view
        score_label = findViewById(R.id.score_label);

        //hearts part
        hearts_img = new AppCompatImageView[]{
                findViewById(R.id.ic_heart1),
                findViewById(R.id.ic_heart2),
                findViewById(R.id.ic_heart3)
        };

        rockets_mat = new AppCompatImageView[][]{
                {
                        findViewById(R.id.obstacels_mat_cell_00),
                        findViewById(R.id.obstacels_mat_cell_01),
                        findViewById(R.id.obstacels_mat_cell_02),
                        findViewById(R.id.obstacels_mat_cell_03),
                        findViewById(R.id.obstacels_mat_cell_04)
                },
                {
                        findViewById(R.id.obstacels_mat_cell_10),
                        findViewById(R.id.obstacels_mat_cell_11),
                        findViewById(R.id.obstacels_mat_cell_12),
                        findViewById(R.id.obstacels_mat_cell_13),
                        findViewById(R.id.obstacels_mat_cell_14)
                },
                {
                        findViewById(R.id.obstacels_mat_cell_20),
                        findViewById(R.id.obstacels_mat_cell_21),
                        findViewById(R.id.obstacels_mat_cell_22),
                        findViewById(R.id.obstacels_mat_cell_23),
                        findViewById(R.id.obstacels_mat_cell_24)
                },
                {
                        findViewById(R.id.obstacels_mat_cell_30),
                        findViewById(R.id.obstacels_mat_cell_31),
                        findViewById(R.id.obstacels_mat_cell_32),
                        findViewById(R.id.obstacels_mat_cell_33),
                        findViewById(R.id.obstacels_mat_cell_34)
                },
                {
                        findViewById(R.id.obstacels_mat_cell_40),
                        findViewById(R.id.obstacels_mat_cell_41),
                        findViewById(R.id.obstacels_mat_cell_42),
                        findViewById(R.id.obstacels_mat_cell_43),
                        findViewById(R.id.obstacels_mat_cell_44)
                },
                {
                        findViewById(R.id.obstacels_mat_cell_50),
                        findViewById(R.id.obstacels_mat_cell_51),
                        findViewById(R.id.obstacels_mat_cell_52),
                        findViewById(R.id.obstacels_mat_cell_53),
                        findViewById(R.id.obstacels_mat_cell_54)
                },
                {
                        findViewById(R.id.obstacels_mat_cell_60),
                        findViewById(R.id.obstacels_mat_cell_61),
                        findViewById(R.id.obstacels_mat_cell_62),
                        findViewById(R.id.obstacels_mat_cell_63),
                        findViewById(R.id.obstacels_mat_cell_64)
                }
        };

        player_icon_cells = new AppCompatImageView[]{
                findViewById(R.id.player_leftmost),
                findViewById(R.id.player_left),
                findViewById(R.id.player_middle),
                findViewById(R.id.player_right),
                findViewById(R.id.player_rightmost)
        };
    }

    private void initViews() {
        ExtendedFloatingActionButton buttonLeft = findViewById(R.id.button_left);
        ExtendedFloatingActionButton buttonRight = findViewById(R.id.button_right);

        if (buttonLeft == null) {
            Log.e("MainActivity", "button Left is null");
        } else {
            buttonLeft.setOnClickListener(v -> change_player_position("left"));
        }

        if (buttonRight == null) {
            Log.e("MainActivity", "button Right is null");
        } else {
            buttonRight.setOnClickListener(v -> change_player_position("right"));
        }
    }

    private void change_player_position(String player_direction) {
        gameManager.movePlayerIcon(player_direction);
        refreshUI("other");
    }

    private void refreshUI(String reason) {
        // Checking if the game is over
        if (gameManager.isOutOfLife()) {
            stopTimer();

            // Ensure current latitude and longitude are up-to-date
            updateCurrentLocation();

            // Create a new record with current location
            Record newRecord = new Record(gameManager.getUserName(), gameManager.getScore(), currentLatitude, currentLongitude);
            Log.d("Record", newRecord.toString());

            // Load existing records
            recordsList = recordManager.loadRecords();

            // Add the new record to the list
            recordsList.add(newRecord);

            // Sort records based on score
            recordsList.sort((r1, r2) -> r2.getPoints() - r1.getPoints());

            // Trim the list to keep only the top 5 records
            if (recordsList.size() > 5) {
                recordsList = new ArrayList<>(recordsList.subList(0, 5));
            }

            // Save records to storage
            recordManager.saveRecords(recordsList);

            // Find the rank of the current record
            int rank = recordsList.indexOf(newRecord) + 1;

            // Navigate to RecordsActivity and pass rank and recent score
            Intent intent = new Intent(this, RecordsActivity.class);
            intent.putExtra("rank", rank);
            intent.putExtra(RecordsActivity.EXTRA_RECENT_SCORE, String.valueOf(gameManager.getScore()));
            startActivity(intent);
            finish(); // Finish current activity to prevent coming back with the back button
        } else {
            displayPlayerIcon();
            updateMatrixUI();
            // Check for collisions every refresh
            updateHeartsUI();
            if (reason.equals(CHANGE_POINTS)) {
                updateScore(DEFAULT_REASON);
            }
        }
    }

    private void updateCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                currentLatitude = lastKnownLocation.getLatitude();
                currentLongitude = lastKnownLocation.getLongitude();
            }
        }
    }

    private void updateHeartsUI() {
        boolean isCrushDetected = gameManager.detectCollisionAndAdjustStats();
        Log.d("Collision Detection", "Collision detected: " + isCrushDetected);
        if (isCrushDetected) {
            // Play the hit sound
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(0);
                } else {
                    mediaPlayer.start();
                }
            }

            toastAndVibrate("Hit detected! " + gameManager.getHits_count());
            hearts_img[gameManager.getHits_count() - 1].setVisibility(View.INVISIBLE);
        }
    }

    private void updateMatrixUI() {
        for (int i = 0; i < gameManager.getRows(); i++) {
            for (int j = 0; j < gameManager.getCols(); j++) {
                String curreCol = gameManager.getGame_mat()[i][j];
                if (curreCol.equals(gameManager.getNOTHING())) {
                    rockets_mat[i][j].setVisibility(View.INVISIBLE);
                } else if (curreCol.equals(gameManager.getObstacle())) {
                    rockets_mat[i][j].setImageResource(R.drawable.ic_rocket);
                    rockets_mat[i][j].setVisibility(View.VISIBLE);
                } else if (curreCol.equals(gameManager.getGOLD())) {
                    rockets_mat[i][j].setImageResource(R.drawable.money);
                    rockets_mat[i][j].setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void displayPlayerIcon() {
        playerPosition = gameManager.getPlayer_position();
        for (int i = 0; i < gameManager.getCols(); i++) {
            if (i == playerPosition) {
                player_icon_cells[i].setVisibility(View.VISIBLE);
            } else {
                player_icon_cells[i].setVisibility(View.INVISIBLE);
            }
        }
    }

    private void changeMatrixTime() {
        gameManager.matrixChangePeriod();
        refreshUI(CHANGE_POINTS);
    }

    private void startTimer() {
        if (!timerOn) {
            Log.d("Timer start", "Timer has been Started");
            startTime = System.currentTimeMillis();
            timerOn = true;
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> changeMatrixTime());
                }
            }, 0L, DELAY);
        }
    }

    private void stopTimer() {
        timerOn = false;
        Log.d("stopTimer", "stopTimer: Timer Stopped");
        timer.cancel();
    }

    private void toastAndVibrate(String text) {
        vibrate();
        showToast(text);
    }

    private void showToast(String message) {
        // Cancel the previous toast if it is still visible
        if (currentToast != null) {
            currentToast.cancel();
        }
        currentToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        currentToast.show();
    }

    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(750, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(750);
        }
    }

    private void enableButtonControls() {
        ExtendedFloatingActionButton buttonLeft = findViewById(R.id.button_left);
        ExtendedFloatingActionButton buttonRight = findViewById(R.id.button_right);

        buttonLeft.setVisibility(View.VISIBLE);
        buttonRight.setVisibility(View.VISIBLE);

        buttonLeft.setOnClickListener(v -> change_player_position("left"));
        buttonRight.setOnClickListener(v -> change_player_position("right"));

        stopSensorControls();
    }

    private void enableSensorControls() {
        // Hide button controls
        findViewById(R.id.button_left).setVisibility(View.GONE);
        findViewById(R.id.button_right).setVisibility(View.GONE);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastSensorUpdateTime > SENSOR_UPDATE_INTERVAL) {
                    float x = event.values[0];
                    if (x > SENSITIVITY_THRESHOLD) {
                        change_player_position("left");
                    } else if (x < -SENSITIVITY_THRESHOLD) {
                        change_player_position("right");
                    }
                    lastSensorUpdateTime = currentTime;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // You can handle changes in sensor accuracy if needed
            }
        };

        // Register the listener with the sensor manager
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    private void stopSensorControls() {
        if (sensorManager != null && sensorEventListener != null) {
            sensorManager.unregisterListener(sensorEventListener);
        }
    }

    private void updateScore(String collisionType) {
        if (collisionType.equals(GameManager.NO_COLLISION) || collisionType.equals(DEFAULT_REASON)) {
            gameManager.scoreTimeIncrease();
        }

        gameManager.scoreGoldIncrease();
        score_label.setText(String.valueOf(gameManager.getScore()));
    }

    private void saveRecords(List<Record> records) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(records);
        editor.putString("recordList", json);
        editor.apply();
    }

    private ArrayList<Record> loadRecords() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("recordList", null);
        Type type = new TypeToken<ArrayList<Record>>() {}.getType();
        ArrayList<Record> records = gson.fromJson(json, type);
        if (records == null) {
            records = new ArrayList<>();
        }
        return records;
    }
}
