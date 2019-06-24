package com.example.tripbyphoto;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.Locale;

public class FullImageActivity extends AppCompatActivity {
    private Double latitude, longitude;
    private ImageView imageView;
    private String uriString, placeName, countryName;
    private TextView tvLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView = findViewById(R.id.ivFullImage);

        if (getIntent().hasExtra("imageUri")) {
            uriString = getIntent().getStringExtra("imageUri");
            Uri uri = Uri.parse(uriString);
            imageView.setImageURI(uri);
        }

        tvLocation = findViewById(R.id.tvLocation);
        if (getIntent().hasExtra("latitude")) {
            String latitudeString = getIntent().getStringExtra("latitude");
            latitude = Double.parseDouble(latitudeString);
            tvLocation.append(" " + latitudeString);
        }

        tvLocation.append(", Longitude: ");
        if (getIntent().hasExtra("longitude")) {
            String longitudeString = getIntent().getStringExtra("longitude");
            longitude = Double.parseDouble(longitudeString);
            tvLocation.append(longitudeString);
        }

        GetInfo getInfo = new GetInfo();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        LatLng point = new LatLng(latitude, longitude);
        countryName = getInfo.getCountryName(geocoder, point);
        placeName = getInfo.getPlaceFullName(geocoder, point);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            tvLocation.append(", " + placeName);
        else tvLocation.append("\n " + placeName);
        if (countryName != "") tvLocation.append(", " + countryName);

        Integer textLength = tvLocation.getText().length();
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (textLength > 82) {
                tvLocation.getLayoutParams().height *= 2;
            }
        } else {
            if (textLength > 42 && textLength < 84) {
                tvLocation.getLayoutParams().height *= 2;
            } else if (textLength > 84 && textLength < 126) {
                tvLocation.getLayoutParams().height *= 3;
            } else {
                tvLocation.getLayoutParams().height *= 4;
            }
        }

        Context context = this;

        imageView.setOnClickListener(v -> {
            if (!GetInfo.isOnline(this)) {
                Toast.makeText(getApplicationContext(), "No Internet connection! Please, turn on wi-fi or mobile data for information loading!", Toast.LENGTH_LONG).show();
                return;
            } else if (!GetInfo.isGPSon(context)) {
                Toast.makeText(getApplicationContext(), "No GPS connection! Please, activate device location!", Toast.LENGTH_LONG).show();
                return;
            } else {
                Intent intent = new Intent(FullImageActivity.this, MapsActivity.class);
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra("MAP_latitude", String.valueOf(latitude));
                intent.putExtra("MAP_longitude", String.valueOf(longitude));
                intent.putExtra("MAP_place_name", placeName);
                intent.putExtra("MAP_country_name", countryName);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(FullImageActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        super.onBackPressed();
    }
}