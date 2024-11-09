package com.example.masterquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class ScoreSummaryActivity extends AppCompatActivity {

    private TextView scorePercentageTextView;
    private TextView correctAnswersTextView;
    private Button playAgainButton;
    private Button viewHistoryButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_summary);

        scorePercentageTextView = findViewById(R.id.score_percentage_text);
        correctAnswersTextView = findViewById(R.id.correct_answers_text);
        playAgainButton = findViewById(R.id.play_again_button);
        viewHistoryButton = findViewById(R.id.view_history_button);
        db = FirebaseFirestore.getInstance();

        int score = getIntent().getIntExtra("SCORE", 0);
        int totalQuestions = getIntent().getIntExtra("TOTAL_QUESTIONS", 0);
        int correctAnswers = getIntent().getIntExtra("CORRECT_ANSWERS", 0);

        double scorePercentage = ((double) correctAnswers / totalQuestions) * 100;
        scorePercentageTextView.setText("Score: " + String.format("%.2f", scorePercentage) + "%");
        correctAnswersTextView.setText("Correct Answers: " + correctAnswers + "/" + totalQuestions);

        saveScoreToHistory(scorePercentage, correctAnswers);

        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScoreSummaryActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        viewHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScoreSummaryActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void saveScoreToHistory(double scorePercentage, int correctAnswers) {
        Map<String, Object> history = new HashMap<>();
        history.put("scorePercentage", scorePercentage);
        history.put("correctAnswers", correctAnswers);
        history.put("date", System.currentTimeMillis());

        db.collection("history").add(history);
    }
}