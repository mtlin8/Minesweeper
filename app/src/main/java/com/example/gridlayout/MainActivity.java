package com.example.gridlayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private static final int COLUMN_COUNT = 8;

    // save the TextViews of all cells in an array, so later on,
    // when a TextView is clicked, we know which cell it is
    private ArrayList<TextView> cell_tvs;
    private HashSet<Integer> bombs = new HashSet<Integer>();
    private Queue<TextView> q = new LinkedList<>(); // Queue for DFS

    private Boolean mining = true; // Toggle between playing modes
    private Boolean running = false; // Game over?
    private Boolean bombed = false; // If true, turn smiley to frown and play again.

    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cell_tvs = new ArrayList<TextView>();

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
//        tv.setText(String.valueOf(i)+String.valueOf(j));
        tv.setText(String.valueOf(n));

        if (isBomb(tv)) {
            // Reveal all bombs.
            TextView clicked = tv;
            Iterator<Integer> it = bombs.iterator();
            while(it.hasNext()) {
                tv = cell_tvs.get(it.next());
                tv.setText("ð"); // How to set to mine?
                tv.setBackgroundColor(Color.LTGRAY);
            }
            clicked.setBackgroundColor(Color.RED);
            // Tell user they lost here. But how?
        }
        else {

            if (tv.getCurrentTextColor() == Color.GREEN) {
                tv.setTextColor(Color.GRAY);
                tv.setBackgroundColor(Color.LTGRAY);
            }
        }


    }

    public Boolean isBomb(View view) {
        TextView tv = (TextView) view;
        int n = findIndexOfCellTextView(tv);
        if (bombs.contains(n)) return true;
        else return false;
    }

    public void neighborBombers(View view) {
        // DFS for bombs
    }

    public void switchTool(View view){
        TextView tv = (TextView) view;
        if (mining) {
            // Set to flag
            mining = false;
            tv.setText("flag");
        }
        else {
            // Set to pick
            mining = true;
            tv.setText("pick");
        }
    }

    public void youWin(){
        // Send it over to .Results
    }

    public void youLose() {
        // Display bombs
        // Display reset button
        //
    }
}
