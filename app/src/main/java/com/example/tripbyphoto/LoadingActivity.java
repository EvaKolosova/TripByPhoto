package com.example.tripbyphoto;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class LoadingActivity extends AppCompatActivity {
    private static final int REQUEST_ACCESS = 110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ContextCompat.checkSelfPermission(LoadingActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(LoadingActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //permissions has already been granted
                    startActivity(new Intent(LoadingActivity.this, MainActivity.class));
                } else {
                    requestFewPermissions();
                }
            }
        }, 3500);
    }

    public void requestFewPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION
        }, REQUEST_ACCESS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ACCESS && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            Log.i("kolosova_permissionInfo", "Permissions Allowed! You can see photos from Gallery and use device's location.");
            startActivity(new Intent(LoadingActivity.this, MainActivity.class));
        } else {
            requestFewPermissions();
            Log.i("kolosova_permissionInfo", "Permissions Denied! You canNOT see photos from Gallery and use device's location.");
        }
    }
}
