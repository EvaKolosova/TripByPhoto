package com.example.tripbyphoto;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Scroller;

import com.example.tripbyphoto.fragment.FragmentFullImage;
import com.example.tripbyphoto.utils.AppConsts;

import java.lang.reflect.Field;

public class HelpCallClass implements CallBackClass.MyCallBack {

    @Override
    public void callBackCall(String path, String latitude, String longitude) {
        FragmentFullImage mFrg = FragmentFullImage.newInstance(1, path, latitude, longitude);
        Bundle mArg = new Bundle();
        mArg.putString(AppConsts.INTENT_IMAGE_URI, path);
        mArg.putString(AppConsts.INTENT_LATITUDE, latitude);
        mArg.putString(AppConsts.INTENT_LONGITUDE, longitude);
        mFrg.setArguments(mArg);
        if (BuildConfig.DEBUG) {
            Log.d(AppConsts.LOG_CHECK + "...", "callBack HelpClass with Bundle");
        }
    }

    @Override
    public void callBackToggle(ViewPager viewPager, Context context) {

        Field mScroller;
        try {
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            Scroller scroller = new Scroller(context, new DecelerateInterpolator(0.01f));
            mScroller.set(viewPager, scroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        viewPager.setCurrentItem(1, true);

        if (BuildConfig.DEBUG) {
            Log.d(AppConsts.LOG_CHECK + "...", "callBack HelpClass with setItem");
        }
    }

    @Override
    public void callBackVisibility(ImageView imageView, boolean flag) {
        if (flag == true) {
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.INVISIBLE);
        }
    }
}
