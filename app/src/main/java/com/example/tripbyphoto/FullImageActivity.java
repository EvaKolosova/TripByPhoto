package com.example.tripbyphoto;

import android.content.Intent;
import android.content.res.Configuration;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.Locale;

public class FullImageActivity extends AppCompatActivity {
    private Double latitude, longitude;
    private ImageView imageView;
    private String uriString, placeName, countryName;
    private TextView textViewLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView = findViewById(R.id.imageView);

        if (getIntent().hasExtra("imageUri")) {
            uriString = getIntent().getStringExtra("imageUri");
            Uri uri = Uri.parse(uriString);
            imageView.setImageURI(uri);
        }

        textViewLocation = findViewById(R.id.textViewLocation);
        if (getIntent().hasExtra("latitude")) {
            String latitudeString = getIntent().getStringExtra("latitude");
            latitude = Double.parseDouble(latitudeString);
            textViewLocation.append(" " + latitudeString);
        }

        textViewLocation.append(", Longitude: ");
        if (getIntent().hasExtra("longitude")) {
            String longitudeString = getIntent().getStringExtra("longitude");
            longitude = Double.parseDouble(longitudeString);
            textViewLocation.append(longitudeString);
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        GetInfo getInfo = new GetInfo();
        LatLng point = new LatLng(latitude, longitude);
        countryName = getInfo.getCountryName(geocoder, point);
        placeName = getInfo.getPlaceFullName(geocoder, point);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            textViewLocation.append(", " + placeName);
        else textViewLocation.append("\n " + placeName);

        Integer textLength = textViewLocation.getText().length();
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (textLength > 75) {
                textViewLocation.getLayoutParams().height = 130;
            }
        } else {
            if (textLength > 40 && textLength < 80) {
                textViewLocation.getLayoutParams().height = 130;
            } else if (textLength > 80 && textLength < 120) {
                textViewLocation.getLayoutParams().height = 205;
            } else {
                textViewLocation.getLayoutParams().height = 280;
            }
        }

        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(FullImageActivity.this, MapsActivity.class);
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra("MAP_latitude", String.valueOf(latitude));
            intent.putExtra("MAP_longitude", String.valueOf(longitude));
            intent.putExtra("MAP_place_name", placeName);
            intent.putExtra("MAP_country_name", countryName);
            startActivity(intent);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}