package com.example.tripbyphoto.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionHelper {
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static boolean isGPSon(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return enabled;
    }
}
