package com.example.tripbyphoto;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class testActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        LinearLayout llBottomSheet = findViewById(R.id.bottom_sheet);

        /*TopSheetBehavior.from(llBottomSheet).setState(TopSheetBehavior.STATE_EXPANDED);
        TopSheetBehavior.from(llBottomSheet).setState(TopSheetBehavior.STATE_COLLAPSED);
        TopSheetBehavior.from(llBottomSheet).setState(TopSheetBehavior.STATE_HIDDEN);*/

        // настройка поведения нижнего экрана
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);

        // настройка состояний нижнего экрана
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        // настройка максимальной высоты
        bottomSheetBehavior.setPeekHeight(200);

        // настройка возможности скрыть элемент при свайпе вниз
        bottomSheetBehavior.setHideable(false);

        // настройка колбэков при изменениях
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }
}
