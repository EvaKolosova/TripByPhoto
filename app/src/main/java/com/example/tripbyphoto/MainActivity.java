package com.example.tripbyphoto;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.tripbyphoto.adapter.ViewPagerAdapter;
import com.example.tripbyphoto.utils.ConnectionHelper;

public class MainActivity extends AppCompatActivity { //implements CallBackClass.MyCallBack {
    protected ViewPagerAdapter mAdapter;
    protected ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPager = findViewById(R.id.viewPager);
        mPager.setAdapter(mAdapter);

//        boolean swipeEnabled = false;
//        ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                if (!swipeEnabled) {
//                    if (mPager.getAdapter().getCount() > 1) {
//                        mPager.setCurrentItem(1);
//                        mPager.setCurrentItem(0);
//                    }
//                }
//            }
//            public void onPageScrollStateChanged(int state) {
//            }
//
//            public void onPageSelected(int position) {
//            }
//        };
//        mPager.addOnPageChangeListener(onPageChangeListener);

        mPager.setOnTouchListener((v, event) -> {
            if (mPager.getCurrentItem() == 0) {
                mPager.setCurrentItem(1, false);
                mPager.setCurrentItem(0, false);
                return true;
            }
            return false;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onBackPressed() {
        //эмулируется нажатие на HOME, сворачивая приложение если мы на первой странице, иначе просто возврат на первую страницу
        if (mPager.getCurrentItem() == 0) {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
        } else {
            mPager.setCurrentItem(0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!ConnectionHelper.isOnline(this)) {
            Toast.makeText(getApplicationContext(), R.string.internet_warning, Toast.LENGTH_LONG).show();
            return;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}


