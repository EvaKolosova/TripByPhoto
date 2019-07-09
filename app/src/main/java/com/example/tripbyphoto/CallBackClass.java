package com.example.tripbyphoto;

import android.support.v4.view.ViewPager;

public class CallBackClass {
    public MyCallBack callback;

    public void registerCallBack(MyCallBack callback) {
        this.callback = callback;
    }

    public interface MyCallBack {
        void callBackCall(String path, String latitude, String longitude);

        void callBackToggle(ViewPager viewPager);
    }
}