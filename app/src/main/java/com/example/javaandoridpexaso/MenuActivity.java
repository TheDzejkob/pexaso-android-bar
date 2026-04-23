package com.example.javaandoridpexaso;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button btnStartGame = findViewById(R.id.btnStartGame);
        Button btnLeaderboard = findViewById(R.id.btnLeaderboard);

        btnStartGame.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, MainActivity.class);
            startActivity(intent);
        });

        btnLeaderboard.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, LeaderboardActivity.class);
            startActivity(intent);
        });
    }
}
