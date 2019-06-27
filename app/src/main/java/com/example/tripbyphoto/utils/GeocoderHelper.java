package com.example.tripbyphoto.utils;

import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.mapbox.mapboxsdk.BuildConfig;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.IOException;
import java.util.List;

public class GeocoderHelper {

    protected Address mAddrFromName;
    private Address mAddrFromLocation;

    public GeocoderHelper(Geocoder geocoder, @NonNull LatLng point) {
        mAddrFromLocation = findAddressFromLocation(geocoder, point);
    }

    public GeocoderHelper(Geocoder geocoder, @NonNull String placeName) {
        mAddrFromName = findAddressFromName(geocoder, placeName);
    }

    public static Address findAddressFromLocation(Geocoder geocoder, @NonNull LatLng point) {
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(point.getLatitude(), point.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address resultAddr = null;
        if (!addresses.isEmpty()) {
            if (addresses.get(0) != null) resultAddr = addresses.get(0);
            if (BuildConfig.DEBUG) {
                Log.d(AppConsts.LOG_CHECK, resultAddr.toString());
            }
        }
        return resultAddr;
    }

    public static Address findAddressFromName(Geocoder geocoder, @NonNull String placeName) {
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(placeName, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address resultAddr = null;
        if (!addresses.isEmpty()) {
            resultAddr = addresses.get(0);
        }
        return resultAddr;
    }

    public String getCountryName() {
        return mAddrFromLocation != null ? mAddrFromLocation.getCountryName() : null;
    }

    public String getPlaceFullName() {
        String placeName = "";
        if (mAddrFromLocation != null) {
            if (mAddrFromLocation.getThoroughfare() != null && !mAddrFromLocation.getThoroughfare().equals("Unnamed Road")) {
                placeName += mAddrFromLocation.getThoroughfare();
                if (BuildConfig.DEBUG) Log.d(AppConsts.LOG_CHECK, placeName);
                if ((mAddrFromLocation.getLocality() != null) || mAddrFromLocation.getAdminArea() != null || mAddrFromLocation.getFeatureName() != null)
                    placeName += ", ";
            }
            if (mAddrFromLocation.getFeatureName() != null && !mAddrFromLocation.getFeatureName().equals("Unnamed Road")) {
                placeName += mAddrFromLocation.getFeatureName();
                if (BuildConfig.DEBUG) Log.d(AppConsts.LOG_CHECK, placeName);
                if ((mAddrFromLocation.getLocality() != null) || mAddrFromLocation.getAdminArea() != null)
                    placeName += ", ";
            }
            if (mAddrFromLocation.getLocality() != null) {
                placeName += mAddrFromLocation.getLocality();
                if (BuildConfig.DEBUG) Log.d(AppConsts.LOG_CHECK, placeName);
                if (mAddrFromLocation.getAdminArea() != null)
                    placeName += ", ";
            }
            if (mAddrFromLocation.getAdminArea() != null) {
                placeName += mAddrFromLocation.getAdminArea();
                if (BuildConfig.DEBUG) Log.d(AppConsts.LOG_CHECK, placeName);
            }
        }
        return placeName;
    }

    public LatLng getLatlngFromPlaceName() {
        if (mAddrFromName != null) {
            Double longitude = mAddrFromName.getLongitude();
            Double latitude = mAddrFromName.getLatitude();
            LatLng latLng = new LatLng(latitude, longitude);

            return latLng;
        }
        return null;
    }
}
