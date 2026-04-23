package com.example.javaandoridpexaso;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LeaderboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        ListView lvLeaderboard = findViewById(R.id.lvLeaderboard);
        Button btnBack = findViewById(R.id.btnBack);

        SharedPreferences prefs = getSharedPreferences("PexesoPrefs", MODE_PRIVATE);
        String leaderboardData = prefs.getString("leaderboard", "");

        List<String> formattedEntries = new ArrayList<>();
        if (!leaderboardData.isEmpty()) {
            String[] entries = leaderboardData.split(",");
            for (int i = 0; i < entries.length; i++) {
                String[] parts = entries[i].split("\\|");
                if (parts.length == 2) {
                    int totalSeconds = Integer.parseInt(parts[0]);
                    String date = parts[1];
                    
                    int minutes = totalSeconds / 60;
                    int seconds = totalSeconds % 60;
                    
                    String formatted = String.format(Locale.getDefault(), 
                        "%d. místo - %02d:%02d\n(%s)", 
                        (i + 1), minutes, seconds, date);
                    formattedEntries.add(formatted);
                }
            }
        } else {
            formattedEntries.add("Zatím žádné výsledky");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, formattedEntries);
        lvLeaderboard.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
    }
}
