package com.example.masterquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardView natureCard = findViewById(R.id.card_nature);
        CardView scienceCard = findViewById(R.id.card_science);
        CardView csCard = findViewById(R.id.card_computer_science);

        natureCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openQuizScreen("Nature");
            }
        });

        scienceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openQuizScreen("Science");
            }
        });

        csCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openQuizScreen("Computer Science");
            }
        });
    }

    private void openQuizScreen(String topic) {
        Intent intent = new Intent(MainActivity.this, QuizActivity.class);
        intent.putExtra("TOPIC", topic);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_history) {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}