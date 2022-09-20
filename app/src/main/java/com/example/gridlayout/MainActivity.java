package com.example.gridlayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

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
        for (int i = 0; i < cell_value.size(); i ++) {
            TextView t = (TextView) cell_tvs.get(i);
            t.setText(String.valueOf(cell_value.get(i)));
            t.setTextColor(Color.GRAY);
        }

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
        }

        if (!mining) {
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
            }
        }

////        tv.setText(String.valueOf(i)+String.valueOf(j));
////        tv.setText(String.valueOf(n));
//        if (mining && running) {
//            if (isBomb(tv)) {
//                // This is an L – send the user home.
//                // Reveal all bombs.
//                TextView clicked = tv;
//                Iterator<Integer> it = bombs.iterator();
//                while(it.hasNext()) {
//                    tv = cell_tvs.get(it.next());
//                    tv.setText(R.string.mine); // How to set to mine?
//                    tv.setBackgroundColor(Color.GRAY);
//                }
//                clicked.setBackgroundColor(Color.RED);
//                bombed = true;
//                running = false;
//            }
//            else bfs(tv);
//        }
//        else if (!mining && running){
//            // Set and display a flag and up the flag counter.
//            if (flagged.get(n)) {
//                tv.setText("");
//                if(isBomb(tv)) {
//                    bombsToFlag++;
//                }
//                flagged.set(n, false);
//            }
//            else {
//                tv.setText(R.string.flag);
//                if(isBomb(tv)) {
//                    bombsToFlag--;
//                }
//                flagged.set(n, true);
//            }
//
//        }
//
//        if (bombed){
//            // You lose
//        }
//        else if (!running){
//            // You win
//        }
    }


    public Boolean isBomb(View view) {
        TextView tv = (TextView) view;
        int n = findIndexOfCellTextView(tv);
        if (bombs.contains(n)) return true;
        else return false;
    }
//    public Boolean isClean(View view) {
//        TextView tv = (TextView) view;
//        int n = findIndexOfCellTextView(tv);
//        int i = n/COLUMN_COUNT; // Deriving row index.
//        int j = n%COLUMN_COUNT; // Deriving column index.
//
//        int bombCount = 0;
//        if (i > 0 && j > 0) {
//            int a = findIndexOfCellTextView(tv) - 9;
//            TextView two = cell_tvs.get(a);
//            if (isBomb(two) || flagged.get(a)) bombCount++;
//        }
//        if (j > 0) {
//            int b = findIndexOfCellTextView(tv) - 8;
//            TextView two = cell_tvs.get(b);
//            if (isBomb(two) || flagged.get(b)) bombCount++;
//        }
//        if (i > 0 && j < 7) {
//            int c = findIndexOfCellTextView(tv) - 7;
//            TextView two = cell_tvs.get(c);
//            if (isBomb(two) || flagged.get(c)) bombCount++;
//        }
//        if (j > 0) {
//            int d = findIndexOfCellTextView(tv) - 1;
//            TextView two = cell_tvs.get(d);
//            if (isBomb(two) || flagged.get(d)) bombCount++;
//        }
//        if (j < 7) {
//            int e = findIndexOfCellTextView(tv) + 1;
//            TextView two = cell_tvs.get(e);
//            if (isBomb(two) || flagged.get(e)) bombCount++;
//        }
//        if (i < 9 && j > 0) {
//            int f = findIndexOfCellTextView(tv) + 7;
//            TextView two = cell_tvs.get(f);
//            if (isBomb(two) || flagged.get(f)) bombCount++;
//        }
//        if (i < 9) {
//            int g = findIndexOfCellTextView(tv) + 8;
//            TextView two = cell_tvs.get(g);
//            if (isBomb(two) || flagged.get(g)) bombCount++;
//        }
//        if (i < 9 && j < 7) {
//            int h = findIndexOfCellTextView(tv) + 9;
//            TextView two = cell_tvs.get(h);
//            if (isBomb(two) || flagged.get(h)) bombCount++;
//        }
//
//        if (bombCount == 0) return true;
//        else return false;
//    }

    public void bfs(View view) {
        TextView tv = (TextView) view;
        ArrayList<TextView> queue = new ArrayList<TextView>();
        queue.add(tv);

        while (queue.size() !=0) {
            TextView current = queue.remove(0);
            int index = findIndexOfCellTextView(current);

            // If current is a neighbor of a bomb, we reveal it and return
            if (cell_value.get(index) > 0) {
                current.setBackgroundColor(Color.LTGRAY);
                current.setText(cell_value.get(index));
                current.setTextColor(Color.GRAY);
                return;
            }
            // If current is empty, add neighbors
            else {
                ArrayList<Integer> neighbors = new ArrayList<Integer>();
                neighbors.add(-7);
                neighbors.add(7);
                neighbors.add(-1);
                neighbors.add(1);

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
