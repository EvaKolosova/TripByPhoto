package com.example.tripbyphoto;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

public class testActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        LinearLayout llTopSheet = findViewById(R.id.top_sheet);
        TopSheetBehavior topSheetBehavior = TopSheetBehavior.from(llTopSheet);
        topSheetBehavior.setState(TopSheetBehavior.STATE_COLLAPSED);
        topSheetBehavior.setState(TopSheetBehavior.STATE_EXPANDED);
        topSheetBehavior.setState(TopSheetBehavior.STATE_HIDDEN);
        topSheetBehavior.setPeekHeight(200);
        topSheetBehavior.setHideable(false);
        topSheetBehavior.setPeekHeight(30);
        topSheetBehavior.setTopSheetCallback(new TopSheetBehavior.TopSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View topSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View topSheet, float slideOffset) {
            }
        });
    }
}
