package com.example.tripbyphoto;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.tripbyphoto.adapter.RecyclerViewGridAdapter;
import com.example.tripbyphoto.utils.AppConsts;
import com.example.tripbyphoto.utils.ConnectionHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    protected RecyclerViewGridAdapter mAdapter;
    protected RecyclerView mImageGrid;
    private Toolbar mToolbar;
    private String mPath, mLatitude, mLongitude;
    private ArrayList<Double> mLocationLatitude = new ArrayList<>();
    private ArrayList<Double> mLocationLongitude = new ArrayList<>();
    private ArrayList<String> mImagePaths = new ArrayList<>();
    RecyclerViewGridAdapter.OnItemClickListener onItemClickListener = (View view, int position, String name) -> {
        if (!ConnectionHelper.isOnline(this)) {
            Toast.makeText(getApplicationContext(), R.string.internet_warning, Toast.LENGTH_LONG).show();
            return;
        } else {
            Intent i = new Intent(this, this.getClass());
            finish();
            this.startActivity(i);
            Intent intent = new Intent(MainActivity.this, FullImageActivity.class);
            intent.setAction(android.content.Intent.ACTION_SEND);
            mPath = mImagePaths.get(position);
            mLatitude = String.valueOf(mLocationLatitude.get(position));
            mLongitude = String.valueOf(mLocationLongitude.get(position));
            intent.putExtra(AppConsts.INTENT_IMAGE_URI, mPath);
            intent.putExtra(AppConsts.INTENT_LATITUDE, mLatitude);
            intent.putExtra(AppConsts.INTENT_LONGITUDE, mLongitude);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                try {
                    //filter for images without location-data
                    Double data_latitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.LATITUDE)));
                    mLocationLatitude.add(data_latitude);
                    Double data_longitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE)));
                    mLocationLongitude.add(data_longitude);

                    String image = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    mImagePaths.add(image);
                    Log.d(AppConsts.LOG_CHECK, image);
                } catch (NullPointerException e) {
                    Log.d(AppConsts.LOG_ERROR, cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                }
            }
            cursor.close();
        }

        mImageGrid = findViewById(R.id.rv_grid_recycler);
        mImageGrid.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        mAdapter = new RecyclerViewGridAdapter(MainActivity.this, mImagePaths, mLocationLatitude, mLocationLongitude, onItemClickListener);
        mImageGrid.setAdapter(mAdapter);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onBackPressed() {
        //эмулируется нажатие на HOME, сворачивая приложение
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
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


