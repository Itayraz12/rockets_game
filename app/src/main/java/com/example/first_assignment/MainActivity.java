package com.example.first_assignment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.first_assignment.buisnessLogic.GameManager;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private AppCompatImageView[] hearts_img;
    private AppCompatImageView[] player_icon_cells;
    private AppCompatImageView[][] rockets_mat;

   
    
    private GameManager gameManager;
    private static final long DELAY = 1000L;
    private long startTime;
    private int playerPosition;

    private boolean timerOn = false;

    private Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        // init game manager class
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
    
    
    //find the hearts images
    private void findViews() {
        hearts_img = new AppCompatImageView[]{
                findViewById(R.id.ic_heart1),
                findViewById(R.id.ic_heart2),
                findViewById(R.id.ic_heart3)
        };

        // Find obstacles matrix with 5 columns
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

        // Find the player icons (visible & invisible) with 5 columns
        player_icon_cells = new AppCompatImageView[]{
                findViewById(R.id.player_leftmost),
                findViewById(R.id.player_left),
                findViewById(R.id.player_middle),
                findViewById(R.id.player_right),
                findViewById(R.id.player_rightmost)
        };
    }


    private void initViews() {
        // Initialize buttons with null checks and logging
        ExtendedFloatingActionButton buttonLeft = findViewById(R.id.button_left);
        ExtendedFloatingActionButton buttonRight = findViewById(R.id.button_right);

        // Check if buttonLeft is null and log an error if it is
        if (buttonLeft == null) {
            Log.e("MainActivity", "button Left is null");
        } else {
            buttonLeft.setOnClickListener(v -> change_player_position("left"));
        }

        // Check if button Right is null and log an error if it is
        if (buttonRight == null) {
            Log.e("MainActivity", "button Right is null");
        } else {
            buttonRight.setOnClickListener(v -> change_player_position("right"));
        }
    }


    private void change_player_position(String direction) {
        gameManager.movePlayerIcon(direction);
        refreshUI();
    }

    private void refreshUI() {
        if (gameManager.isOutOfLife()) {
            Log.d("Game over", "You lose");
            stopTimer();
            restartGame();
            //showGameOverCountdownDialog();
            return;
        } else {
            displayPlayerIcon();
            updateMatrixUI();
            updateHeartsUI();
        }
    };

    private void updateHeartsUI(){
        boolean detectedHit = gameManager.detectCollisionAndAdjustStats();
        if (detectedHit){
            toastAndVibrate("hit detected! " + gameManager.getHits_count());
            hearts_img[gameManager.getHits_count() - 1].setVisibility(View.INVISIBLE);
        }

    }
    private void updateMatrixUI(){
        for (int i = 0; i < gameManager.getRows(); i++) {
            for (int j = 0; j < gameManager.getCols(); j++) {
                String currentCol = gameManager.getGame_mat()[i][j];
                if(currentCol.equals("none")){
                    rockets_mat[i][j].setVisibility(View.INVISIBLE);
                } else if (currentCol.equals(gameManager.getObstacle())) {
                    rockets_mat[i][j].setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void displayPlayerIcon(){
        playerPosition = gameManager.getPlayer_position();
        for (int i = 0; i < gameManager.getCols(); i++){
            if(i == playerPosition){
                player_icon_cells[i].setVisibility(View.VISIBLE);
            }
            else{
                player_icon_cells[i].setVisibility(View.INVISIBLE);
            }
        }
    }

    private void changeMatrixTime(){
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
            },0L,DELAY);
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

        v.vibrate(VibrationEffect.createOneShot(750, VibrationEffect.DEFAULT_AMPLITUDE));
    }


//    private void showGameOverCountdownDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Game Over");
//
//        // Create a TextView to display the countdown
//        final TextView message = new TextView(this);
//        message.setText("You have lost all your lives, game will restart in 3");
//        message.setTextSize(18);
//        message.setPadding(20, 20, 20, 20);
//        builder.setView(message);
//
//        // Create the AlertDialog
//        AlertDialog dialog = builder.create();
//        dialog.show();
//
//        // Create a CountDownTimer to update the countdown text and restart the game after 3 seconds
//        new CountDownTimer(3000, 1000) {

//            @Override
//            public void onTick(long millisUntilFinished) {
//                int secondsRemaining = (int) (millisUntilFinished / 1000);
//                message.setText("You have lost all your lives, game will restart in " + secondsRemaining);
//            }
//
//            @Override
//            public void onFinish() {
//                dialog.dismiss();
//                // Reset the game
//                gameManager.resetGame();
//                refreshUI();
//                startTimer();
//            }
//        }.start();
//    }


    private void restartGame() {
        // Reset game variables
        gameManager.resetGame();

        // Reset UI elements
        for (AppCompatImageView heart : hearts_img) {
            heart.setVisibility(View.VISIBLE);
        }

        // Reset player icons
        displayPlayerIcon();

        // Reset obstacle matrix
        updateMatrixUI();

        // Restart timer
        startTimer();
    }

}