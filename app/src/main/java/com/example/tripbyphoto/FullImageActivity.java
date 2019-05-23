package com.example.tripbyphoto;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FullImageActivity extends AppCompatActivity {
    protected Double latitude, longitude;
    private ImageView imageView;
    private TextView textViewLocation;
    private String uriString, placeName;

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
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            placeName = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        textViewLocation.append("\n " + placeName);

        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(FullImageActivity.this, MapsActivity.class);
                intent.setAction(android.content.Intent.ACTION_SEND);
                intent.putExtra("MAP_latitude", String.valueOf(latitude));
                intent.putExtra("MAP_longitude", String.valueOf(longitude));
                intent.putExtra("MAP_place_name", placeName);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}