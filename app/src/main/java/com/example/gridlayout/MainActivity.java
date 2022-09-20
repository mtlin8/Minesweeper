package com.example.gridlayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private static final int COLUMN_COUNT = 8;

    // save the TextViews of all cells in an array, so later on,
    // when a TextView is clicked, we know which cell it is
    private ArrayList<TextView> cell_tvs;
    private HashSet<Integer> bombs;
    private ArrayList<Integer> cell_value;
    private HashSet<TextView> visited;

    private Boolean mining = true; // Toggle between playing modes
    private Boolean running = true;
    private int seconds = 0;
    private Boolean bombed = false;
    private boolean game_won = false;

    private ArrayList<Boolean> flagged;
    private int bombsToFlag = 4;

    // Button goes in .Results.

    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Starts timer
        runTimer();

        cell_tvs = new ArrayList<TextView>();
        bombs = new HashSet<Integer>();
        cell_value = new ArrayList<Integer>();
        visited = new HashSet<TextView>();
        for (int i =0; i < 80; i ++) {
            cell_value.add(0);
        }
        flagged = new ArrayList<Boolean>();
        TextView btn = (TextView) findViewById(R.id.modeButton);
        // Method (2): Dynamically create cells
        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout01);
        for (int i = 0; i<=9; i++) {
            for (int j=0; j<=COLUMN_COUNT-1; j++) {
                TextView tv = new TextView(this);
                tv.setHeight( dpToPixel(32) );
                tv.setWidth( dpToPixel(32) );
                tv.setTextSize(20);
                tv.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                tv.setTextColor(Color.GREEN);
                tv.setBackgroundColor(Color.GREEN);
                tv.setOnClickListener(this::onClickTV);

                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                lp.setMargins(dpToPixel(2), dpToPixel(2), dpToPixel(2), dpToPixel(2));
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                grid.addView(tv, lp);

                cell_tvs.add(tv);
            }
        }

        // Setting up the bombs
        bombs.clear(); // Ensure no funny business.
        Random r =  new Random();
        while (bombs.size() < 4) {
            int nextMine = r.nextInt(79);
            if (!bombs.contains(nextMine)) bombs.add(nextMine);
        }

        // Bomb n
        for (Integer b: bombs) {
            cell_value.set(b, -1);
            ArrayList<Integer> neighbors = new ArrayList<Integer>();
            neighbors.add(-9);
            neighbors.add(-8);
            neighbors.add(-7);
            neighbors.add(-1);
            neighbors.add(1);
            neighbors.add(7);
            neighbors.add(8);
            neighbors.add(9);

            for (int num : neighbors) {
                if (b%8==0 && (num == -9 || num == -1 || num == 7)) {
                    continue;
                }
                if ((b + 1) % 8 == 0 && (num == -7 || num == 1 || num == 9)) {
                    continue;
                }
                if (b+num > -1 && b+num < 80) {
                    cell_value.set(b+num, cell_value.get(b+num)+1);;
                }
            }
        }
        for (Integer b: bombs) {
            cell_value.set(b, -1);
        }

        mining = true; // Set mining to be true.
        running = true;
    }

    private int findIndexOfCellTextView(TextView tv) {
        for (int n=0; n<cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }
        return -1;
    }

    public void onClickTV(View view){
        TextView tv = (TextView) view;
        int n = findIndexOfCellTextView(tv);
        int i = n/COLUMN_COUNT; // Deriving row index.
        int j = n%COLUMN_COUNT; // Deriving column index.

        // If game is over we go to results screen
        if (!running) {
            // Go to results
            Intent intent = new Intent(this, Results.class);
            intent.putExtra("time", seconds);
            intent.putExtra("game_won", game_won);
            startActivity(intent);
        }

        else if (!mining) {
            if (tv.getText() == getString(R.string.flag)) {
                tv.setText("");
                bombsToFlag += 1;
                TextView flagged = (TextView) findViewById(R.id.flaggedBombs);
                flagged.setText(String.valueOf(bombsToFlag));
            }
            else {
                tv.setText(getString(R.string.flag));
                bombsToFlag -= 1;
                TextView flagged = (TextView) findViewById(R.id.flaggedBombs);
                flagged.setText(String.valueOf(bombsToFlag));
            }
        }
        else {
            // If bomb
            if (cell_value.get(n) == -1) {
                TextView clicked = tv;
                Iterator<Integer> it = bombs.iterator();
                while(it.hasNext()) {
                    tv = cell_tvs.get(it.next());
                    tv.setText(R.string.mine); // How to set to mine?
                    tv.setBackgroundColor(Color.GRAY);
                }
                clicked.setBackgroundColor(Color.RED);
                bombed = true;
                running = false;
            }
            // If else, we add to visited and run bfs
            else {
                visited.add(tv);
                bfs(tv);
                if (visited.size() == 76) {
                    running = false;
                    game_won = true;
                }
            }
        }
    }

    public void bfs(View view) {
        TextView tv = (TextView) view;
        ArrayList<TextView> queue = new ArrayList<TextView>();
        queue.add(tv);

        while (!queue.isEmpty()) {
            TextView current = queue.remove(0);
            int index = findIndexOfCellTextView(current);

            // If current is flagged, we continue
            if (current.getText() == getString(R.string.flag)) {
                continue;
            }

            // If current is a neighbor of a bomb, we reveal it and return
            if (cell_value.get(index) >= 0) {
                current.setBackgroundColor(Color.LTGRAY);
                current.setText(String.valueOf(cell_value.get(index)));
                current.setTextColor(Color.GRAY);
                // Does not continue bfs from this square if is bomb neighbor
                if (cell_value.get(index) > 0) {
                    continue;
                }
            }
            // If current is empty, add neighbors
            ArrayList<Integer> neighbors = new ArrayList<Integer>();
            neighbors.add(-8);
            neighbors.add(8);
            neighbors.add(-1);
            neighbors.add(1);

            for (int n: neighbors) {
                int neighbor_index = index + n;
                if (neighbor_index < 0 || neighbor_index > 79) {
                    continue;
                }
                if ((index % 8 == 0) && (n == -1)) {
                    continue;
                }
                if ((index+1)%8 == 0 && (n == 1)) {
                    continue;
                }
                if (!visited.contains(cell_tvs.get(neighbor_index))) {
                    queue.add(cell_tvs.get(neighbor_index));
                    visited.add(cell_tvs.get(neighbor_index));
                }
            }

        }
    }

    public void switchTool(View view){
        TextView tv = (TextView) view;
        if (mining) {
            // Set to flag
            mining = false;
            tv.setText(getString(R.string.flag));
        }
        else {
            // Set to pick
            mining = true;
            tv.setText(getString(R.string.pick));
        }
    }

    public void runTimer() {
        TextView time = (TextView) findViewById(R.id.seconds);
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                time.setText(String.valueOf(seconds));
                if (running) {
                    seconds++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }
}
