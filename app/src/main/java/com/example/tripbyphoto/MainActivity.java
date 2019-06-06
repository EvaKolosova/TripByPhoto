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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    protected RecyclerViewGridAdapter adapter;
    protected RecyclerView imageGrid;
    private Toolbar toolbar;
    private String path, latitude, longitude;
    private ArrayList<Double> locationLatitude = new ArrayList<>();// list of latitude & longitude
    private ArrayList<Double> locationLongitude = new ArrayList<>();// list of latitude & longitude
    private ArrayList<String> imagePaths = new ArrayList<>();// list of files paths
    RecyclerViewGridAdapter.OnItemClickListener onItemClickListener = (View view, int position, String name) -> {
        if (!GetInfo.isOnline(this)) {
            Toast.makeText(getApplicationContext(),
                    "No Internet connection! Please, turn on wi-fi or mobile data for information loading!", Toast.LENGTH_LONG).show();
            return;
        } else {
            Intent i = new Intent(this, this.getClass());
            finish();
            this.startActivity(i);
            Intent intent = new Intent(MainActivity.this, FullImageActivity.class);
            intent.setAction(android.content.Intent.ACTION_SEND);
            path = imagePaths.get(position);
            latitude = String.valueOf(locationLatitude.get(position));
            longitude = String.valueOf(locationLongitude.get(position));
            intent.putExtra("imageUri", path);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
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
            while (cursor.moveToNext()) {
                try {
                    //filter for images without location-data
                    Double data_latitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.LATITUDE)));
                    locationLatitude.add(data_latitude);
                    Double data_longitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE)));
                    locationLongitude.add(data_longitude);

                    String image = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    imagePaths.add(image);
                    Log.d("kolosova_checkInfo", image);
                } catch (NullPointerException e) {
                    Log.d("kolosova_errorInfo", cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                }
            }
            cursor.close();
        }

        imageGrid = findViewById(R.id.rvGridRecycler);
        imageGrid.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        adapter = new RecyclerViewGridAdapter(MainActivity.this, imagePaths, locationLatitude, locationLongitude, onItemClickListener);
        imageGrid.setAdapter(adapter);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onBackPressed() {
        //эмулируется нажатие на HOME, сворачивая приложение
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!GetInfo.isOnline(this)) {
            Toast.makeText(getApplicationContext(),
                    "No Internet connection! Please, turn on wi-fi or mobile data for information loading!", Toast.LENGTH_LONG).show();
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


