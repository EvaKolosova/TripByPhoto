package com.example.tripbyphoto.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripbyphoto.R;
import com.example.tripbyphoto.map.MapsActivity;
import com.example.tripbyphoto.utils.AppConsts;
import com.example.tripbyphoto.utils.ConnectionHelper;
import com.example.tripbyphoto.utils.GeocoderHelper;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.Locale;

public class FragmentFullImage extends Fragment {
    private static final String MY_NUM_KEY = "numOfPage";
    protected String mUriString, mPlaceName, mCountryName;
    protected TextView tvLocation;
    protected Double mLatitude, mLongitude;
    protected Context mContext;
    protected View view;
    private ImageView mImageView;

    public static FragmentFullImage newInstance(int numOfPage, String uriString, String latitude, String longitude) {
        FragmentFullImage fragment = new FragmentFullImage();
        Bundle args = new Bundle();
        args.putInt(MY_NUM_KEY, numOfPage);
        args.putString(AppConsts.INTENT_IMAGE_URI, uriString);
        args.putString(AppConsts.INTENT_LATITUDE, latitude);
        args.putString(AppConsts.INTENT_LONGITUDE, longitude);
        fragment.setArguments(args);
        return fragment;
    }

    public static FragmentFullImage newInstance(int numOfPage) {
        FragmentFullImage fragment = new FragmentFullImage();
        Bundle args = new Bundle();
        args.putInt(MY_NUM_KEY, numOfPage);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity().getApplicationContext();

        view = inflater.inflate(R.layout.layout_full_image, container, false);
        mImageView = view.findViewById(R.id.iv_full_image);

        Log.d(AppConsts.LOG_CHECK + "FR2", "onCreateView");

        setData();

        mImageView.setOnClickListener(v -> {
            if (!ConnectionHelper.isOnline(mContext)) {
                Toast.makeText(mContext, R.string.internet_warning, Toast.LENGTH_LONG).show();
                return;
            } else if (!ConnectionHelper.isGPSon(mContext)) {
                Toast.makeText(mContext, R.string.GPS_warning, Toast.LENGTH_LONG).show();
                return;
            } else {
                Intent intent = new Intent(mContext, MapsActivity.class);
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(AppConsts.MAP_LAT, String.valueOf(mLatitude));
                intent.putExtra(AppConsts.MAP_LNG, String.valueOf(mLongitude));
                intent.putExtra(AppConsts.MAP_PLACE_NAME, mPlaceName);
                intent.putExtra(AppConsts.MAP_COUNTRY_NAME, mCountryName);
                startActivity(intent);
            }
        });
        return view;
    }

    public void setData() {
        if (this.getArguments().getString(AppConsts.INTENT_IMAGE_URI) != null) {
            mUriString = this.getArguments().getString(AppConsts.INTENT_IMAGE_URI);
            mLatitude = Double.parseDouble(this.getArguments().getString(AppConsts.INTENT_LATITUDE));
            mLongitude = Double.parseDouble(this.getArguments().getString(AppConsts.INTENT_LONGITUDE));

            Uri uri = Uri.parse(mUriString);
            mImageView.setImageURI(uri);

            String locationTextValue = String.format(getString(R.string.location_text_template), mLatitude, mLongitude);
            tvLocation = view.findViewById(R.id.tv_location);
            tvLocation.setText(locationTextValue);

            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            LatLng point = new LatLng(mLatitude, mLongitude);
            GeocoderHelper geocoderHelper = new GeocoderHelper(geocoder, point);
            mCountryName = geocoderHelper.getCountryName();
            mPlaceName = geocoderHelper.getPlaceFullName();

            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE)
                tvLocation.append(", " + mPlaceName);
            else tvLocation.append("\n " + mPlaceName);
            if (mCountryName != "") tvLocation.append(", " + mCountryName);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setData();
    }
}