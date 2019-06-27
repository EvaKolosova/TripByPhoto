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

import com.example.tripbyphoto.utils.AppConsts;
import com.example.tripbyphoto.utils.ConnectionHelper;
import com.example.tripbyphoto.utils.GeocoderHelper;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.Locale;

public class FullImageActivity extends AppCompatActivity {
    private Double mLatitude, mLongitude;
    private ImageView mImageView;
    private String mUriString, mPlaceName, mCountryName;
    private TextView tvLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mImageView = findViewById(R.id.iv_full_image);

        if (getIntent().hasExtra(AppConsts.INTENT_IMAGE_URI)) {
            mUriString = getIntent().getStringExtra(AppConsts.INTENT_IMAGE_URI);
            Uri uri = Uri.parse(mUriString);
            mImageView.setImageURI(uri);
        }

        tvLocation = findViewById(R.id.tv_location);
        if (getIntent().hasExtra(AppConsts.INTENT_LATITUDE)) {
            String latitudeString = getIntent().getStringExtra(AppConsts.INTENT_LATITUDE);
            mLatitude = Double.parseDouble(latitudeString);
        }

        if (getIntent().hasExtra(AppConsts.INTENT_LONGITUDE)) {
            String longitudeString = getIntent().getStringExtra(AppConsts.INTENT_LONGITUDE);
            mLongitude = Double.parseDouble(longitudeString);
        }
        String locationTextValue = String.format(getString(R.string.location_text_template), mLatitude, mLongitude);
        tvLocation.setText(locationTextValue);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        LatLng point = new LatLng(mLatitude, mLongitude);
        GeocoderHelper geocoderHelper = new GeocoderHelper(geocoder, point);
        mCountryName = geocoderHelper.getCountryName();
        mPlaceName = geocoderHelper.getPlaceFullName();

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            tvLocation.append(", " + mPlaceName);
        else tvLocation.append("\n " + mPlaceName);
        if (mCountryName != "") tvLocation.append(", " + mCountryName);

        Context context = this;

        mImageView.setOnClickListener(v -> {
            if (!ConnectionHelper.isOnline(this)) {
                Toast.makeText(getApplicationContext(), R.string.internet_warning, Toast.LENGTH_LONG).show();
                return;
            } else if (!ConnectionHelper.isGPSon(context)) {
                Toast.makeText(getApplicationContext(), R.string.GPS_warning, Toast.LENGTH_LONG).show();
                return;
            } else {
                Intent intent = new Intent(FullImageActivity.this, MapsActivity.class);
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(AppConsts.MAP_LAT, String.valueOf(mLatitude));
                intent.putExtra(AppConsts.MAP_LNG, String.valueOf(mLongitude));
                intent.putExtra(AppConsts.MAP_PLACE_NAME, mPlaceName);
                intent.putExtra(AppConsts.MAP_COUNTRY_NAME, mCountryName);
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