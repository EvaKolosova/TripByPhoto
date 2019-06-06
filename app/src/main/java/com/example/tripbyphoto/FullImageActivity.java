package com.example.tripbyphoto;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

        GetInfo getInfo = new GetInfo();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
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
                textViewLocation.getLayoutParams().height *= 2;
            }
        } else {
            if (textLength > 40 && textLength < 80) {
                textViewLocation.getLayoutParams().height *= 2;
            } else if (textLength > 80 && textLength < 120) {
                textViewLocation.getLayoutParams().height *= 3;
            } else {
                textViewLocation.getLayoutParams().height *= 4;
            }
        }

        Context context = this;

        imageView.setOnClickListener(v -> {
            if (!GetInfo.isOnline(this)) {
                Toast.makeText(getApplicationContext(),"No Internet connection! Please, turn on wi-fi or mobile data for information loading!", Toast.LENGTH_LONG).show();
                return;
            } else if (!GetInfo.isGPSon(context)) {
                Toast.makeText(getApplicationContext(),"No GPS connection! Please, activate device location!", Toast.LENGTH_LONG).show();
                return;
            }
            else {
                    Intent intent = new Intent(FullImageActivity.this, MapsActivity.class);
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("MAP_latitude", String.valueOf(latitude));
                    intent.putExtra("MAP_longitude", String.valueOf(longitude));
                    intent.putExtra("MAP_place_name", placeName);
                    intent.putExtra("MAP_country_name", countryName);
                    startActivity(intent);
                }
        });
    }
}