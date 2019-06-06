package com.example.tripbyphoto;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.IOException;
import java.util.List;

import static android.support.v4.content.ContextCompat.getSystemService;

public class GetInfo {
    public String getCountryName(Geocoder geocoder, @NonNull LatLng point) {
        String countryName = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(point.getLatitude(), point.getLongitude(), 1);
            countryName = addresses.get(0).getCountryName();
            Log.d("kolosova_checkInfo", countryName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return countryName;
    }

    public String getPlaceFullName(Geocoder geocoder, @NonNull LatLng point) {
        String placeName = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(point.getLatitude(), point.getLongitude(), 1);
            if (addresses.get(0).getFeatureName() != null) {
                placeName += addresses.get(0).getFeatureName();
                Log.d("kolosova_checkInfo", placeName);
                if ((addresses.get(0).getLocality() != null) || addresses.get(0).getAdminArea() != null)
                    placeName += ", ";
            }
            if (addresses.get(0).getLocality() != null) {
                placeName += addresses.get(0).getLocality();
                Log.d("kolosova_checkInfo", placeName);
                if (addresses.get(0).getAdminArea() != null)
                    placeName += ", ";
            }
            if (addresses.get(0).getAdminArea() != null) {
                placeName += addresses.get(0).getAdminArea();
                Log.d("kolosova_checkInfo", placeName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return placeName;
    }

    public static boolean isOnline(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static boolean isGPSon(Context context)
    {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);;
        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return enabled;
    }
}
