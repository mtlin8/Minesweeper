package com.example.gridlayout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Results extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Intent intent = getIntent();
        int seconds = intent.getIntExtra("time", 0);
        Boolean game_won = intent.getBooleanExtra("game_won", false);
        TextView results = (TextView) findViewById(R.id.resultsMessage);
        if (game_won) {
            results.setText("You won!\nYou took " + String.valueOf(seconds) + " seconds .\nGood job!");
        }
        else {
            results.setText("You lost.\n" + String.valueOf(seconds) + " seconds passed.\nNice try!");
        }


    }

    public void resetButton(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}