package com.example.tripbyphoto.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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
import com.example.tripbyphoto.utils.GetImages;

import java.util.ArrayList;

public class FragmentTwo extends Fragment {
    private static final String MY_NUM_KEY = "numOfPage";
    protected ArrayList<Double> mLocationLatitude = new ArrayList<>();
    protected ArrayList<Double> mLocationLongitude = new ArrayList<>();
    protected ArrayList<String> mImagePaths = new ArrayList<>();
    protected String mUriString, mPlaceName, mCountryName;
    protected TextView tvLocation;
    protected Double mLatitude, mLongitude;
    protected Context mContext;
    private GetImages getImages;
    private ImageView mImageView;

    public static FragmentTwo newInstance(int numOfPage) {
        FragmentTwo f = new FragmentTwo();
        Bundle args = new Bundle();
        args.putInt(MY_NUM_KEY, numOfPage);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO получение данных с первого фрагмента и их выгрузка
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_full_image, container, false);
        mImageView = view.findViewById(R.id.iv_full_image);

        Log.d(AppConsts.LOG_CHECK + "FR2", "onCreateView");
        mContext = container.getContext();

        getImages = new GetImages(mContext);
        mImagePaths = getImages.getImagesPaths(false);
        mLocationLatitude = getImages.getImagesLatitude(false);
        mLocationLongitude = getImages.getImagesLongitude(true);

        Uri uri = Uri.parse(mImagePaths.get(0));
        mImageView.setImageURI(uri);

//        if (getIntent().hasExtra(AppConsts.INTENT_IMAGE_URI)) {
//            mUriString = getIntent().getStringExtra(AppConsts.INTENT_IMAGE_URI);
//            Uri uri = Uri.parse(mUriString);
//            mImageView.setImageURI(uri);
//        }
//
//        tvLocation = view.findViewById(R.id.tv_location);
//        if (getIntent().hasExtra(AppConsts.INTENT_LATITUDE)) {
//            String latitudeString = getIntent().getStringExtra(AppConsts.INTENT_LATITUDE);
//            mLatitude = Double.parseDouble(latitudeString);
//        }
//
//        if (getIntent().hasExtra(AppConsts.INTENT_LONGITUDE)) {
//            String longitudeString = getIntent().getStringExtra(AppConsts.INTENT_LONGITUDE);
//            mLongitude = Double.parseDouble(longitudeString);
//        }
//        String locationTextValue = String.format(getString(R.string.location_text_template), mLatitude, mLongitude);
//        tvLocation.setText(locationTextValue);
//
//        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//        LatLng point = new LatLng(mLatitude, mLongitude);
//        GeocoderHelper geocoderHelper = new GeocoderHelper(geocoder, point);
//        mCountryName = geocoderHelper.getCountryName();
//        mPlaceName = geocoderHelper.getPlaceFullName();

//        int orientation = getResources().getConfiguration().orientation;
//        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
//            tvLocation.append(", " + mPlaceName);
//        else tvLocation.append("\n " + mPlaceName);
//        if (mCountryName != "") tvLocation.append(", " + mCountryName);


//        if (BuildConfig.DEBUG)
//            Log.d(AppConsts.LOG_CHECK, "Item count is - " + mGridAdapter.getItemCount());

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
}