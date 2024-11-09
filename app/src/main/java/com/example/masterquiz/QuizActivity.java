package com.example.masterquiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<Question> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;
    private CountDownTimer countDownTimer;

    private TextView questionTextView;
    private RadioGroup optionsGroup;
    private RadioButton option1, option2, option3, option4;
    private TextView scoreTextView;
    private TextView timerTextView;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        db = FirebaseFirestore.getInstance();

        questionTextView = findViewById(R.id.question_text);
        optionsGroup = findViewById(R.id.options_group);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        scoreTextView = findViewById(R.id.score_text);
        timerTextView = findViewById(R.id.timer_text);
        nextButton = findViewById(R.id.next_button);

        fetchQuestions();
    }

    private void fetchQuestions() {
        db.collection("questions")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            // Add random questions if the collection is empty
                            addRandomQuestions();
                        } else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Question question = document.toObject(Question.class);
                                questionList.add(question);
                            }
                            Collections.shuffle(questionList);
                            if (questionList.size() > 5) {
                                questionList = questionList.subList(0, 5);
                            }
                            displayQuestion();
                        }
                    } else {
                        Log.e("FirestoreError", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void addRandomQuestions() {
        List<Question> randomQuestions = new ArrayList<>();
        randomQuestions.add(new Question("What is the capital of Italy?", "Rome", "Paris", "Berlin", "Madrid", "Rome"));
        randomQuestions.add(new Question("What is the square root of 16?", "2", "4", "8", "16", "4"));
        randomQuestions.add(new Question("Who painted the Mona Lisa?", "Vincent van Gogh", "Pablo Picasso", "Leonardo da Vinci", "Claude Monet", "Leonardo da Vinci"));
        randomQuestions.add(new Question("What is the largest ocean on Earth?", "Atlantic Ocean", "Indian Ocean", "Arctic Ocean", "Pacific Ocean", "Pacific Ocean"));
        randomQuestions.add(new Question("What is the chemical symbol for gold?", "Au", "Ag", "Pb", "Fe", "Au"));

        for (Question question : randomQuestions) {
            db.collection("questions").add(question);
        }

        // Fetch questions again after adding
        fetchQuestions();
    }

    private void displayQuestion() {
        if (currentQuestionIndex < questionList.size()) {
            Question currentQuestion = questionList.get(currentQuestionIndex);
            questionTextView.setText(currentQuestion.getQuestion());
            option1.setText(currentQuestion.getOption1());
            option2.setText(currentQuestion.getOption2());
            option3.setText(currentQuestion.getOption3());
            option4.setText(currentQuestion.getOption4());

            optionsGroup.clearCheck();
            enableOptions(true);

            startTimer();
        } else {
            Intent intent = new Intent(QuizActivity.this, ScoreSummaryActivity.class);
            intent.putExtra("SCORE", score);
            intent.putExtra("TOTAL_QUESTIONS", questionList.size());
            intent.putExtra("CORRECT_ANSWERS", score); // assuming score is the number of correct answers
            startActivity(intent);
            finish();
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(15000, 1000) {
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("Time: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                timerTextView.setText("Time: 0");
                moveToNextQuestion();
            }
        }.start();
    }

    public void onOptionSelected(View view) {
        countDownTimer.cancel();
        enableOptions(false);

        RadioButton selectedOption = findViewById(optionsGroup.getCheckedRadioButtonId());
        Question currentQuestion = questionList.get(currentQuestionIndex);

        if (selectedOption != null && selectedOption.getText().equals(currentQuestion.getCorrectAnswer())) {
            score++;
            scoreTextView.setText("Score: " + score);
        }

        nextButton.setVisibility(View.VISIBLE);
    }

    public void onNextButtonClicked(View view) {
        moveToNextQuestion();
    }

    private void moveToNextQuestion() {
        currentQuestionIndex++;
        nextButton.setVisibility(View.GONE);
        displayQuestion();
    }

    private void enableOptions(boolean enable) {
        for (int i = 0; i < optionsGroup.getChildCount(); i++) {
            optionsGroup.getChildAt(i).setEnabled(enable);
        }
    }
}