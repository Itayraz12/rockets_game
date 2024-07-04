package com.example.first_assignment;

import android.Manifest;
import android.content.Context;
import com.google.gson.Gson;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.first_assignment.buisnessLogic.GameManager;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity  {
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the MediaPlayer instance
        mediaPlayer = MediaPlayer.create(this, R.raw.hit_sound);

        findViews();
        gameManager = new GameManager(rockets_mat.length, rockets_mat[0].length);
        initViews();
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

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release the MediaPlayer when the activity is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    private void findViews() {


        //score view
        score_label =findViewById(R.id.score_label);

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
        refreshUI();
    }

    private void refreshUI() {
        //checking if the game is over
        if (gameManager.isOutOfLife()) {
            Log.d("Game over", "You lose");
            stopTimer();
            insert_record();
            //restartGame();
            //showGameOverCountdownDialog();
            return;
        } else {
            displayPlayerIcon();
            updateMatrixUI();
            //check for collisions every refresh
            //String collisionType = gameManager.detectCollisionAndAdjustStats();
            updateHeartsUI();
            updateScore(DEFAULT_REASON);
        }
    }

    private void insert_record() {
        Gson gson = new Gson();
        //Add here logic
    }

    private void updateHeartsUI() {
        boolean isCrushDetected = gameManager.detectCollisionAndAdjustStats();
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
                }else if (curreCol.equals(gameManager.getGOLD())) {
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
        refreshUI();
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
        toast(text);
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(750, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(750);
        }
    }

    private void showGameOverCountdownDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Over");

        final TextView message = new TextView(this);
        message.setText("You have lost all your lives, game will restart in 3");
        message.setTextSize(18);
        message.setPadding(20, 20, 20, 20);
        builder.setView(message);

        AlertDialog dialog = builder.create();
        dialog.show();

        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsRemaining = (int) (millisUntilFinished / 1000);
                message.setText("You have lost all your lives, game will restart in " + secondsRemaining);
            }

            @Override
            public void onFinish() {
                dialog.dismiss();
                gameManager.resetGame();
                refreshUI();
                startTimer();
            }
        }.start();
    }

    private void updateScore(String collisionType) {
        if (collisionType.equals(GameManager.NO_COLLISION) ||
                collisionType.equals(DEFAULT_REASON) ) {
            gameManager.scoreTimeIncrease();
        }

                gameManager.scoreGoldIncrease();
        score_label.setText(String.valueOf(gameManager.getScore()));
    }




    private void restartGame() {
        gameManager.resetGame();

        for (AppCompatImageView heart : hearts_img) {
            heart.setVisibility(View.VISIBLE);
        }
        score_label.setText(0);
        displayPlayerIcon();
        updateMatrixUI();
        startTimer();
    }


}
