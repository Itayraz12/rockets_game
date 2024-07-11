package com.example.first_assignment;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;

@SuppressLint("CustomSplashScreen")
public class LottieSplashActivity extends AppCompatActivity {
    private LottieAnimationView lottie_LOTTIE_lottie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottie_splash);

        findViews();
        lottie_LOTTIE_lottie.resumeAnimation();
        lottie_LOTTIE_lottie.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
                // No action needed
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                transactToUsernameActivity();
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {
                // No action needed
            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {
                // No action needed
            }
        });
    }

    private void transactToUsernameActivity() {
        startActivity(new Intent(this, UsernameActivity.class));
        finish();
    }

    private void findViews() {
        lottie_LOTTIE_lottie = findViewById(R.id.lottie_LOTTIE_lottie);
    }
}
