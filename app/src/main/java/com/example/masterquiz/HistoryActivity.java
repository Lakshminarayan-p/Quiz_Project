package com.example.masterquiz;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    private ListView historyListView;
    private FirebaseFirestore db;
    private List<String> historyList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyListView = findViewById(R.id.history_list_view);
        db = FirebaseFirestore.getInstance();
        historyList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyList);
        historyListView.setAdapter(adapter);

        fetchHistory();
    }

    private void fetchHistory() {
        db.collection("history")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            double scorePercentage = document.getDouble("scorePercentage");
                            long dateMillis = document.getLong("date");
                            Date date = new Date(dateMillis);
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                            String dateString = sdf.format(date);
                            String historyItem = "Score: " + String.format("%.2f", scorePercentage) + "%, Date: " + dateString;
                            historyList.add(historyItem);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}