package com.example.tripbyphoto;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.tripbyphoto.adapter.RecyclerViewGridAdapter;
import com.example.tripbyphoto.adapter.ViewPagerAdapter;
import com.example.tripbyphoto.utils.AppConsts;
import com.example.tripbyphoto.utils.ConnectionHelper;
import com.example.tripbyphoto.utils.GetImages;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private GetImages getImages;
    protected ViewPagerAdapter mAdapter;
    protected ViewPager mPager;
    protected RecyclerViewGridAdapter mGridAdapter;
    protected RecyclerView mImageGrid;
    private ArrayList<Double> mLocationLatitude = new ArrayList<>();
    private ArrayList<Double> mLocationLongitude = new ArrayList<>();
    private ArrayList<String> mImagePaths = new ArrayList<>();
    RecyclerViewGridAdapter.OnItemClickListener onItemClickListenerGrid = (View view, int position, String name) -> {
//        if (!ConnectionHelper.isOnline(this)) {
//            Toast.makeText(getApplicationContext(), R.string.internet_warning, Toast.LENGTH_LONG).show();
//            return;
//        } else {
//            Log.d(AppConsts.LOG_CHECK, "Item clicked successfully! On position " + position);
//            Intent i = new Intent(this, this.getClass());
//            finish();
//            this.startActivity(i);
//            Intent intent = new Intent(MainActivity.this, FullImageActivity.class);
//            intent.setAction(android.content.Intent.ACTION_SEND);
//            String path = mImagePaths.get(position);
//            String latitude = String.valueOf(mLocationLatitude.get(position));
//            String longitude = String.valueOf(mLocationLongitude.get(position));
//            intent.putExtra(AppConsts.INTENT_IMAGE_URI, path);
//            intent.putExtra(AppConsts.INTENT_LATITUDE, latitude);
//            intent.putExtra(AppConsts.INTENT_LONGITUDE, longitude);
//            startActivity(intent);
//
//            //TODO передать эти данные в Layout и подвинуть RV вправо!
//            // mLayoutManager.smoothScrollToPosition(RV, mAdapter.getItemCount());
//            // layoutManager.scrollToPositionWithOffset(position, 0); for moving on the top :)
//        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPager = findViewById(R.id.viewPager);
        mPager.setAdapter(mAdapter);

//        getImages = new GetImages(this);
//        mImagePaths = getImages.getImagesPaths(false);
//        mLocationLatitude = getImages.getImagesLatitude(false);
//        mLocationLongitude = getImages.getImagesLongitude(true);

//        mImageGrid = findViewById(R.id.rv_grid);
//        mImageGrid.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
//        mImageGrid.setVerticalScrollBarEnabled(true);
//        mImageGrid.setHorizontalScrollBarEnabled(false);
//        mGridAdapter = new RecyclerViewGridAdapter(MainActivity.this, mImagePaths, mLocationLatitude, mLocationLongitude, onItemClickListenerGrid);
//        mImageGrid.setAdapter(mGridAdapter);
//        if (BuildConfig.DEBUG)
//            Log.d(AppConsts.LOG_CHECK, "Item count is - " + mGridAdapter.getItemCount());

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


