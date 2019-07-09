package com.example.tripbyphoto;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

public class CallBackClass {
    public MyCallBack callback;

    public void registerCallBack(MyCallBack callback) {
        this.callback = callback;
    }

    public interface MyCallBack {
        void callBackCall(String path, String latitude, String longitude);

        void callBackToggle(ViewPager viewPager, Context context);

        void callBackVisibility(ImageView imageView, boolean flag);
    }
}