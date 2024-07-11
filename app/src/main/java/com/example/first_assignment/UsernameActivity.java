package com.example.first_assignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class UsernameActivity extends AppCompatActivity {

    private EditText usernameInput;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);

        usernameInput = findViewById(R.id.username_input);
        submitButton = findViewById(R.id.submit_button);

        submitButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            if (username.isEmpty()) {
                Toast.makeText(UsernameActivity.this, "Please enter a username", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(UsernameActivity.this, homePage_activity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            }
        });
    }
}
