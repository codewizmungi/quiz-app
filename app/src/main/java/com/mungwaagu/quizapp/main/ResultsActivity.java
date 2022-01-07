package com.mungwaagu.quizapp.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mungwaagu.quizapp.R;

public class ResultsActivity extends AppCompatActivity {

    int score = 0, totalQuestions = 0;
    String category;

    TextView result, take_quiz_again, message;
    CardView take_new_quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);


        result = findViewById(R.id.result);
        take_quiz_again = findViewById(R.id.take_quiz_again);
        take_new_quiz = findViewById(R.id.take_new_quiz);
        message = findViewById(R.id.message_text);

        Bundle extras = getIntent().getExtras();
        category = extras.getString("category");
        score = extras.getInt("score");
        totalQuestions = extras.getInt("totalQuestions");

        String message_string = "You have completed the " + category + " QUIZ";

        String result_string = String.valueOf(score) + " / " + String.valueOf(totalQuestions);

        result.setText(result_string);
        message.setText(message_string);

        take_new_quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResultsActivity.this.finish();
            }
        });

        take_quiz_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultsActivity.this, QuizActivity.class);
                intent.putExtra("quizCategory", category);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed()
    {
        // code here to show dialog
        super.onBackPressed();
        this.finish();;

    }

}