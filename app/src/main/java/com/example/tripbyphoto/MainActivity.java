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

import com.example.tripbyphoto.adapter.RecyclerViewAdapter;
import com.example.tripbyphoto.adapter.RecyclerViewGridAdapter;
import com.example.tripbyphoto.utils.AppConsts;
import com.example.tripbyphoto.utils.ConnectionHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    protected RecyclerViewGridAdapter mGridAdapter;
    protected RecyclerView mImageGrid;
    protected RecyclerViewAdapter mAdapter;
    protected RecyclerView mRecyclerView;
    RecyclerViewAdapter.OnItemClickListener onItemClickListener = (View view, int position, String name) -> {

        //TODO передать эти данные в Layout и подвинуть RV вправо!
        // mLayoutManager.smoothScrollToPosition(RV, mAdapter.getItemCount());
        // layoutManager.scrollToPositionWithOffset(position, 0); for moving on the top :)

    };
    private ArrayList<Double> mLocationLatitude = new ArrayList<>();
    private ArrayList<Double> mLocationLongitude = new ArrayList<>();
    private ArrayList<String> mImagePaths = new ArrayList<>();
    RecyclerViewGridAdapter.OnItemClickListener onItemClickListenerGrid = (View view, int position, String name) -> {
        if (!ConnectionHelper.isOnline(this)) {
            Toast.makeText(getApplicationContext(), R.string.internet_warning, Toast.LENGTH_LONG).show();
            return;
        } else {
            Log.d(AppConsts.LOG_CHECK, "Item clicked successfully! On position " + position);
            Intent i = new Intent(this, this.getClass());
            finish();
            this.startActivity(i);
            Intent intent = new Intent(MainActivity.this, FullImageActivity.class);
            intent.setAction(android.content.Intent.ACTION_SEND);
            String path = mImagePaths.get(position);
            String latitude = String.valueOf(mLocationLatitude.get(position));
            String longitude = String.valueOf(mLocationLongitude.get(position));
            intent.putExtra(AppConsts.INTENT_IMAGE_URI, path);
            intent.putExtra(AppConsts.INTENT_LATITUDE, latitude);
            intent.putExtra(AppConsts.INTENT_LONGITUDE, longitude);
            startActivity(intent);

            //TODO передать эти данные в Layout и подвинуть RV вправо!
            // mLayoutManager.smoothScrollToPosition(RV, mAdapter.getItemCount());
            // layoutManager.scrollToPositionWithOffset(position, 0); for moving on the top :)
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

        mImageGrid = findViewById(R.id.rv_grid);
        mImageGrid.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        mImageGrid.setVerticalScrollBarEnabled(true);
        mImageGrid.setHorizontalScrollBarEnabled(false);
        mGridAdapter = new RecyclerViewGridAdapter(MainActivity.this, mImagePaths, mLocationLatitude, mLocationLongitude, onItemClickListenerGrid);
        mImageGrid.setAdapter(mGridAdapter);
        if (BuildConfig.DEBUG)
            Log.d(AppConsts.LOG_CHECK, "Item count is - " + mGridAdapter.getItemCount());

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        mRecyclerView.setVerticalScrollBarEnabled(false);
        mRecyclerView.setHorizontalScrollBarEnabled(false);
        mAdapter = new RecyclerViewAdapter(MainActivity.this, mImagePaths, mImageGrid, onItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
        if (BuildConfig.DEBUG)
            Log.d(AppConsts.LOG_CHECK, "Item count is - " + mGridAdapter.getItemCount());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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


