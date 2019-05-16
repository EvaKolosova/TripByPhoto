package com.example.tripbyphoto;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    protected RecyclerViewGridAdapter adapter;
    protected RecyclerView imagegrid;
    private String path, latitude, longitude;
    private ArrayList<Double> locationLatitude = new ArrayList<>();// list of latitude & longitude
    private ArrayList<Double> locationLongitude = new ArrayList<>();// list of latitude & longitude
    private ArrayList<String> f = new ArrayList<>();// list of files paths
        RecyclerViewGridAdapter.OnItemClickListener onItemClickListener = (View view, int position, String name)-> {
            Intent intent = new Intent(MainActivity.this, FullImageActivity.class);
            intent.setAction(android.content.Intent.ACTION_SEND);
            path = f.get(position);
            latitude = String.valueOf(locationLatitude.get(position));
            longitude = String.valueOf(locationLongitude.get(position));
            intent.putExtra("imageUri", path);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            startActivity(intent);
        };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        SpannableString s = new SpannableString("My Title");
//        s.setSpan(new TypefaceSpan("@font/fredericka_the_great"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setTitle(s);

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if(cursor!=null){
            while (cursor.moveToNext()) {
                try {
                    //filter for images without location-data
                    Double data_latitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.LATITUDE)));
                    locationLatitude.add(data_latitude);
                    Double data_longitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE)));
                    locationLongitude.add(data_longitude);

                    String image = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    f.add(image);

                    Log.d("kolosova_checkInfo", image);
                }
                catch (NullPointerException e) {
                    Log.d("kolosova_errorInfo", cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                }
            }
            cursor.close();
        }
        imagegrid = findViewById(R.id.rvGridRecycler);
        imagegrid.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        adapter = new RecyclerViewGridAdapter(MainActivity.this, f, locationLatitude, locationLongitude, onItemClickListener);
        imagegrid.setAdapter(adapter);
    }

    @Override
    public void onBackPressed(){
        //эмулируется нажатие на HOME, сворачивая приложение
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
}


