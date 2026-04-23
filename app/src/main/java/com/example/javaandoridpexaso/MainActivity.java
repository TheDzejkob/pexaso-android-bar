package com.example.javaandoridpexaso;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private TextView tvTime, tvBestTime;
    private Button btnReset;

    private List<String> cardValues;
    private Button[] buttons = new Button[16];
    private int firstCardIndex = -1;
    private int secondCardIndex = -1;
    private boolean isProcessing = false;

    private int secondsElapsed = 0;
    private int bestTime = 0;
    private int pairsFound = 0;
    private boolean timerRunning = false;

    private SharedPreferences prefs;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (timerRunning) {
                secondsElapsed++;
                tvTime.setText("Čas: " + formatTime(secondsElapsed));
                timerHandler.postDelayed(this, 1000);
            }
        }
    };

    private final String[] EMOJIS = {
            "🍎", "🍌", "🍇", "🍓", "🍒", "🍍", "🍉", "🥝"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridLayout = findViewById(R.id.gridLayout);
        tvTime = findViewById(R.id.tvTime);
        tvBestTime = findViewById(R.id.tvBestTime);
        btnReset = findViewById(R.id.btnReset);

        prefs = getSharedPreferences("PexesoPrefs", MODE_PRIVATE);
        bestTime = prefs.getInt("bestTime", 0);
        tvBestTime.setText("Nejlepší čas: " + (bestTime == 0 ? "--:--" : formatTime(bestTime)));

        btnReset.setOnClickListener(v -> startNewGame());

        setupGame();
    }

    private void setupGame() {
        cardValues = new ArrayList<>();
        for (String emoji : EMOJIS) {
            cardValues.add(emoji);
            cardValues.add(emoji);
        }
        Collections.shuffle(cardValues);

        gridLayout.removeAllViews();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int padding = 60;
        int cellSize = (screenWidth - padding) / 4;

        for (int i = 0; i < 16; i++) {
            final int index = i;
            Button button = new Button(this);
            
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = cellSize - 10;
            params.height = cellSize - 10;
            params.setMargins(8, 8, 8, 8);
            button.setLayoutParams(params);

            button.setText("");
            button.setTextSize(32);
            button.setBackgroundColor(Color.parseColor("#DDDDDD"));
            button.setOnClickListener(v -> onCardClicked(index));
            buttons[index] = button;
            gridLayout.addView(button);
        }
    }

    private void onCardClicked(int index) {
        if (isProcessing || buttons[index].getVisibility() != View.VISIBLE || index == firstCardIndex) {
            return;
        }

        if (!timerRunning && pairsFound < 8) {
            timerRunning = true;
            timerHandler.postDelayed(timerRunnable, 1000);
        }

        buttons[index].setText(cardValues.get(index));
        buttons[index].setBackgroundColor(Color.WHITE);

        if (firstCardIndex == -1) {
            firstCardIndex = index;
        } else {
            secondCardIndex = index;
            isProcessing = true;
            checkMatch();
        }
    }

    private void checkMatch() {
        if (cardValues.get(firstCardIndex).equals(cardValues.get(secondCardIndex))) {
            new Handler().postDelayed(() -> {
                buttons[firstCardIndex].setVisibility(View.INVISIBLE);
                buttons[secondCardIndex].setVisibility(View.INVISIBLE);
                
                resetSelection();
                pairsFound++;
                if (pairsFound == 8) {
                    endGame();
                }
            }, 500);
        } else {
            new Handler().postDelayed(() -> {
                buttons[firstCardIndex].setText("");
                buttons[firstCardIndex].setBackgroundColor(Color.parseColor("#DDDDDD"));
                buttons[secondCardIndex].setText("");
                buttons[secondCardIndex].setBackgroundColor(Color.parseColor("#DDDDDD"));
                resetSelection();
            }, 800);
        }
    }

    private void resetSelection() {
        firstCardIndex = -1;
        secondCardIndex = -1;
        isProcessing = false;
    }

    private void startNewGame() {
        stopTimer();
        secondsElapsed = 0;
        pairsFound = 0;
        tvTime.setText("Čas: 00:00");
        resetSelection();
        setupGame();
    }

    private void stopTimer() {
        timerRunning = false;
        timerHandler.removeCallbacks(timerRunnable);
    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    private void saveTimeToLeaderboard(int time) {
        String currentDate = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(new Date());
        String entry = time + "|" + currentDate;

        String leaderboardData = prefs.getString("leaderboard", "");
        List<String> entries = new ArrayList<>();
        if (!leaderboardData.isEmpty()) {
            String[] split = leaderboardData.split(",");
            for (String s : split) {
                entries.add(s);
            }
        }
        entries.add(entry);

        // Sort by time (the number before '|')
        Collections.sort(entries, (e1, e2) -> {
            int t1 = Integer.parseInt(e1.split("\\|")[0]);
            int t2 = Integer.parseInt(e2.split("\\|")[0]);
            return Integer.compare(t1, t2);
        });
        
        // Keep top 10
        if (entries.size() > 10) {
            entries = entries.subList(0, 10);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < entries.size(); i++) {
            sb.append(entries.get(i));
            if (i < entries.size() - 1) sb.append(",");
        }
        prefs.edit().putString("leaderboard", sb.toString()).apply();
    }

    private void endGame() {
        stopTimer();
        saveTimeToLeaderboard(secondsElapsed);
        
        String resultMsg = "Gratulujeme! Váš čas: " + formatTime(secondsElapsed);
        
        if (bestTime == 0 || secondsElapsed < bestTime) {
            bestTime = secondsElapsed;
            prefs.edit().putInt("bestTime", bestTime).apply();
            tvBestTime.setText("Nejlepší čas: " + formatTime(bestTime));
            resultMsg += "\nNový nejlepší čas!";
        }

        new AlertDialog.Builder(this)
                .setTitle("Konec hry!")
                .setMessage(resultMsg + "\nChcete hrát znovu?")
                .setPositiveButton("Ano", (dialog, which) -> startNewGame())
                .setNegativeButton("Menu", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }
}
