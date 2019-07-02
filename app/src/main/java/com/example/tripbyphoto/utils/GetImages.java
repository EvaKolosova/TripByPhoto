package com.example.tripbyphoto.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.example.tripbyphoto.BuildConfig;

import java.util.ArrayList;

public class GetImages {
    protected Context mContext;
    private ArrayList<String> mImagePaths = new ArrayList<>();
    private ArrayList<Double> mLocationLatitude = new ArrayList<>();
    private ArrayList<Double> mLocationLongitude = new ArrayList<>();
    private Cursor cursor;

    public GetImages(Context context) {
        mContext = context;
        ContentResolver contentResolver = context.getContentResolver();
        cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
    }

    public ArrayList<String> getImagesPaths(boolean flag) {
        if (cursor != null) {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                try {
                    //filter for images without location-data
                    if (!cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.LATITUDE)).isEmpty()) {
                        String image = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        mImagePaths.add(image);
                        if (BuildConfig.DEBUG) Log.d(AppConsts.LOG_CHECK, image);
                    }
                } catch (NullPointerException e) {
                    if (BuildConfig.DEBUG)
                        Log.d(AppConsts.LOG_ERROR, cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                }
            }
            if (flag == true) cursor.close();
        }
        return mImagePaths;
    }

    public ArrayList<Double> getImagesLatitude(boolean flag) {
        if (cursor != null) {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                try {
                    Double data_latitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.LATITUDE)));
                    mLocationLatitude.add(data_latitude);

                    String image = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    if (BuildConfig.DEBUG) Log.d(AppConsts.LOG_CHECK, image);
                } catch (NullPointerException e) {
                    if (BuildConfig.DEBUG)
                        Log.d(AppConsts.LOG_ERROR, cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                }
            }
            if (flag == true) cursor.close();
        }
        return mLocationLatitude;
    }

    public ArrayList<Double> getImagesLongitude(boolean flag) {
        if (cursor != null) {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                try {
                    Double data_longitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE)));
                    mLocationLongitude.add(data_longitude);

                    String image = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    if (BuildConfig.DEBUG) Log.d(AppConsts.LOG_CHECK, image);
                } catch (NullPointerException e) {
                    if (BuildConfig.DEBUG)
                        Log.d(AppConsts.LOG_ERROR, cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                }
            }
            if (flag == true) cursor.close();
        }
        return mLocationLongitude;
    }
}
