package com.example.tripbyphoto;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.example.tripbyphoto.fragment.FragmentTwo;
import com.example.tripbyphoto.utils.AppConsts;

public class HelpCallClass implements CallBackClass.MyCallBack {

    @Override
    public void callBackCall(String path, String latitude, String longitude) {
        FragmentTwo mFrg = FragmentTwo.newInstance(1, path, latitude, longitude);
        Bundle mArg = new Bundle();
        mArg.putString(AppConsts.INTENT_IMAGE_URI, path);
        mArg.putString(AppConsts.INTENT_LATITUDE, latitude);
        mArg.putString(AppConsts.INTENT_LONGITUDE, longitude);
        mFrg.setArguments(mArg);
        Log.d(AppConsts.LOG_CHECK + "...", "callBack HelpClass with Bundle");
    }

    @Override
    public void callBackToggle(ViewPager viewPager) {
        viewPager.setCurrentItem(1, true);
        Log.d(AppConsts.LOG_CHECK + "...", "callBack HelpClass with setItem");
    }
}
