package com.example.tripbyphoto;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.navigation.camera.Camera;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity {
    private static final String TAG = "DirectionsInfo";
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    private static final long MIN_TIME_BW_UPDATES = 1;
    private static final int REQUEST_CODE_AUTOCOMPLETE_1 = 1;
    private static final int REQUEST_CODE_AUTOCOMPLETE_2 = 2;
    protected MapboxMap myMapboxMap;
    protected String placeName, countryName;
    protected boolean FLAG_CONNECTION_STATUS_ACTIVE = true;
    private String placeNameClick, countryNameClick;
    private boolean isGPSEnabled = false, isNetworkEnabled = false, canGetLocation = false;
    private LocationManager locationManager;
    private Location location;
    private Button startNavigationButton;
    private EditText tvDeparture, tvDestination;
    private Double photoLatitude, photoLongitude, deviceLatitude, deviceLongitude;
    private LocationComponent locationComponent;
    private MapView mapView;
    private DirectionsRoute currentRoute = null;
    private Point origin, destination;
    private String originName, destinationName, searchOriginName, searchDestinationName;
    private LatLng pointOfDestination;
    private Context context;
    private NavigationMapRoute navigationMapRoute;
    private CameraPosition position;
    private CarmenFeature photoLocation, deviceLocation;
    private GridLayout llTopSheet;
    private Icon icon;
    private GetInfo getInfo = new GetInfo();
    private Geocoder geocoder;
    private Style style;
    private boolean FLAG_IS_ORIGIN_CHANGED = false;
    private boolean FLAG_IS_DESTINATION_CHANGED = false;


    public static Icon drawableToIcon(@NonNull Context context, @DrawableRes int id, @ColorInt int colorRes) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(context.getResources(), id, context.getTheme());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, colorRes);
        vectorDrawable.draw(canvas);
        return IconFactory.getInstance(context).fromBitmap(bitmap);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.access_token));

        setContentView(R.layout.activity_maps);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        llTopSheet = findViewById(R.id.top_sheet);
        tvDeparture = findViewById(R.id.tvDeparture);
        tvDestination = findViewById(R.id.tvDestination);
        mapView = findViewById(R.id.mapView);
        startNavigationButton = findViewById(R.id.startButton);
        context = this;
        geocoder = new Geocoder(this, Locale.getDefault());

        TopSheetBehavior topSheetBehavior = TopSheetBehavior.from(llTopSheet);
        topSheetBehavior.setState(TopSheetBehavior.STATE_COLLAPSED);
        topSheetBehavior.setState(TopSheetBehavior.STATE_EXPANDED);
        topSheetBehavior.setState(TopSheetBehavior.STATE_HIDDEN);
        topSheetBehavior.setPeekHeight(200);
        topSheetBehavior.setHideable(false);
        topSheetBehavior.setPeekHeight(40);
        topSheetBehavior.setTopSheetCallback(new TopSheetBehavior.TopSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View topSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View topSheet, float slideOffset) {
            }
        });

        if (getIntent().hasExtra("MAP_latitude")) {
            String latitudeString = getIntent().getStringExtra("MAP_latitude");
            photoLatitude = Double.parseDouble(latitudeString);
        }

        if (getIntent().hasExtra("MAP_longitude")) {
            String longitudeString = getIntent().getStringExtra("MAP_longitude");
            photoLongitude = Double.parseDouble(longitudeString);
        }

        if (getIntent().hasExtra("MAP_place_name")) {
            placeName = getIntent().getStringExtra("MAP_place_name");
        }

        if (getIntent().hasExtra("MAP_country_name")) {
            countryName = getIntent().getStringExtra("MAP_country_name");
        }
        Log.d("kolosova_checkLocation", "PLat " + photoLatitude + ", PLng " + photoLongitude);
        pointOfDestination = new LatLng(photoLatitude, photoLongitude);
        position = new CameraPosition.Builder()
                .target(new LatLng(photoLatitude, photoLongitude))
                .zoom(2)
                .tilt(20)
                .build();
        Log.d("kolosova_checkPosition", position.toString());

        icon = drawableToIcon(context, R.drawable.mapbox_marker_icon_default, Color.parseColor("#FF3C9AA4"));

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(mapboxMap -> {
            myMapboxMap = mapboxMap;
            myMapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
            myMapboxMap.addMarker(new MarkerOptions().position(pointOfDestination).setTitle(placeName).setSnippet(countryName).icon(icon));
            myMapboxMap.setStyle(new Style.Builder().fromUrl("mapbox://styles/evakolosova/cjw68gr1o1s921cr087ywkqll"), style -> {
                this.style = style;
                int blue = Color.parseColor("#FF4A8FE1");
                LocationComponentOptions locationComponentOptions = LocationComponentOptions.builder(MapsActivity.this)
                        .foregroundTintColor(blue)
                        .backgroundTintColor(Color.WHITE)
                        .bearingTintColor(blue)
                        .accuracyAlpha(1f)
                        .accuracyColor(blue)
                        .build();
                LocationComponentActivationOptions locationComponentActivationOptions = LocationComponentActivationOptions.builder(MapsActivity.this, style)
                        .locationComponentOptions(locationComponentOptions)
                        .build();
                locationComponent = myMapboxMap.getLocationComponent();
                locationComponent.activateLocationComponent(locationComponentActivationOptions);
                getLocation();
                origin = Point.fromLngLat(deviceLongitude, deviceLatitude);
                destination = Point.fromLngLat(photoLongitude, photoLatitude);
                getRoute(style, origin, destination);
                mapView.addOnDidFinishLoadingStyleListener(() -> {
                    Log.i("kolosova_checkInfo", "map's style has changed");
                    myMapboxMap.addMarker(new MarkerOptions().position(pointOfDestination).setTitle(placeName).setSnippet(countryName).icon(icon));
                });
                mapView.addOnDidFinishLoadingMapListener(() -> {
                    Log.i("kolosova_checkInfo", "map has loaded");
                    myMapboxMap.addMarker(new MarkerOptions().position(pointOfDestination).setTitle(placeName).setSnippet(countryName).icon(icon));
                });

                myMapboxMap.addOnMapClickListener(point -> {
                    if (!GetInfo.isOnline(this)) {
                        Toast.makeText(getApplicationContext(),
                                "No Internet connection! Please, turn on wi-fi or mobile data for information loading!", Toast.LENGTH_LONG).show();
                        FLAG_CONNECTION_STATUS_ACTIVE = false;
                        return false;
                    } else if (!GetInfo.isGPSon(context)) {
                        Toast.makeText(getApplicationContext(), "No GPS connection! Please, activate device location!", Toast.LENGTH_LONG).show();
                        FLAG_CONNECTION_STATUS_ACTIVE = false;
                        return false;
                    } else {
                        if (FLAG_CONNECTION_STATUS_ACTIVE == false && currentRoute == null)
                            restartActivity();
                        if (FLAG_IS_ORIGIN_CHANGED) addMarkerOnMap(point, true, true);
                        else addMarkerOnMap(point, false, true);
                        return true;
                    }
                });

                LatLng point = new LatLng(deviceLatitude, deviceLongitude);
                originName = getInfo.getPlaceFullName(geocoder, point) + ", " + getInfo.getCountryName(geocoder, point);
                destinationName = placeName + ", " + countryName;
                tvDeparture.setText(originName);
                tvDestination.setText(destinationName);

                if (style.isFullyLoaded()) {
                    initSearchFab();
                    addUserLocations();
                }

//                for checking TopSheetBehavior on the testActivity
//                myMapboxMap.addOnMapLongClickListener(point -> {
//                    Intent intent = new Intent(MapsActivity.this, testActivity.class);
//                    intent.setAction(Intent.ACTION_SEND);
//                    startActivity(intent);
//                    return false;
//                });
            });
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // API 21
            tvDestination.setShowSoftInputOnFocus(false);
            tvDeparture.setShowSoftInputOnFocus(false);
        } else { // API 11-20
            tvDestination.setTextIsSelectable(true);
            tvDeparture.setTextIsSelectable(true);

        }
    }

    private void initSearchFab() {
        findViewById(R.id.fab_location_search1).setOnClickListener(view -> {
            startNavigationButton.setVisibility(View.INVISIBLE);
            Intent intent = new PlaceAutocomplete.IntentBuilder()
                    .accessToken(Mapbox.getAccessToken())
                    .placeOptions(PlaceOptions.builder()
                            .backgroundColor(Color.parseColor("#FFEFE8E0"))
                            .limit(10)
                            .addInjectedFeature(deviceLocation)
                            .addInjectedFeature(photoLocation)
                            .build(PlaceOptions.MODE_CARDS))
                    .build(MapsActivity.this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE_1);
        });

        findViewById(R.id.fab_location_search2).setOnClickListener(view -> {
            startNavigationButton.setVisibility(View.INVISIBLE);
            Intent intent = new PlaceAutocomplete.IntentBuilder()
                    .accessToken(Mapbox.getAccessToken())
                    .placeOptions(PlaceOptions.builder()
                            .backgroundColor(Color.parseColor("#FFEFE8E0"))
                            .limit(10)
                            .addInjectedFeature(deviceLocation)
                            .addInjectedFeature(photoLocation)
                            .build(PlaceOptions.MODE_CARDS))
                    .build(MapsActivity.this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE_2);
        });

        findViewById(R.id.fab_location_stability).setOnClickListener(view -> {
            position = new CameraPosition.Builder()
                    .target(new LatLng(deviceLatitude, deviceLongitude))
                    .zoom(10)
                    .tilt(20)
                    .build();
            myMapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == MapsActivity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE_1) {
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
            searchOriginName = feature.text();
            tvDeparture.setText(searchOriginName);
            LatLng latlng = getInfo.getLatlngFromPlaceName(geocoder, searchOriginName);
            origin = Point.fromLngLat(latlng.getLongitude(), latlng.getLatitude());
            if (searchOriginName.equals(originName)) {
                FLAG_IS_ORIGIN_CHANGED = false;
                if (FLAG_IS_DESTINATION_CHANGED) addMarkerOnMap(latlng, false, true);
                else addMarkerOnMap(latlng, false, false);
            } else {
                FLAG_IS_ORIGIN_CHANGED = true;
                if (FLAG_IS_DESTINATION_CHANGED) addMarkerOnMap(latlng, true, true);
                else addMarkerOnMap(latlng, true, false);
            }
            getRoute(style, origin, destination);
        } else if (resultCode == MapsActivity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE_2) {
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
            if (feature.placeName().equals("Device location")) {
                Toast toast = Toast.makeText(this, "You cannot choose Device Location for destination!", Toast.LENGTH_LONG);
                toast.show();
                return;
            }
            searchDestinationName = feature.text();
            tvDestination.setText(searchDestinationName);
            LatLng latlng = getInfo.getLatlngFromPlaceName(geocoder, searchDestinationName);
            destination = Point.fromLngLat(latlng.getLongitude(), latlng.getLatitude());
            if (searchDestinationName.equals(destinationName)) {
                FLAG_IS_DESTINATION_CHANGED = false;
                if (FLAG_IS_ORIGIN_CHANGED) addMarkerOnMap(latlng, true, false);
                else addMarkerOnMap(latlng, false, false);
            } else {
                FLAG_IS_DESTINATION_CHANGED = true;
                if (FLAG_IS_ORIGIN_CHANGED) addMarkerOnMap(latlng, true, true);
                else addMarkerOnMap(latlng, false, true);
            }
            getRoute(style, origin, destination);
        }
    }

    private void addUserLocations() {
        Log.d("kolosova_checkLocation", "DL " + deviceLongitude + ", " + deviceLatitude);
        Log.d("kolosova_checkLocation", "PL " + photoLongitude + ", " + photoLatitude);
        Log.d("kolosova_checkNames", "ON is " + originName);
        Log.d("kolosova_checkNames", "DN is " + destinationName);

        deviceLocation = CarmenFeature.builder().text(originName)
                .geometry(Point.fromLngLat(deviceLongitude, deviceLatitude))
                .placeName("Device location")
                .id("mapbox-sf")
                .properties(new JsonObject())
                .build();

        photoLocation = CarmenFeature.builder().text(destinationName)
                .geometry(Point.fromLngLat(photoLongitude, photoLatitude))
                .placeName("Photo location")
                .id("mapbox-sf")
                .properties(new JsonObject())
                .build();
    }

    private void addMarkerOnMap(@NonNull LatLng point, boolean flagOrigin, boolean flagDestination) {
        //adds marker with description
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        placeNameClick = getInfo.getPlaceFullName(geocoder, point);
        countryNameClick = getInfo.getCountryName(geocoder, point);
        if (!myMapboxMap.getMarkers().isEmpty()) {
            myMapboxMap.clear();
        }

        myMapboxMap.addMarker(new MarkerOptions().position(pointOfDestination).setTitle(placeName).setSnippet(countryName).icon(icon));

        if (countryNameClick != "")
            myMapboxMap.addMarker(new MarkerOptions().setTitle(placeNameClick)
                    .setSnippet(countryNameClick)
                    .position(point));
        else
            myMapboxMap.addMarker(new MarkerOptions().setTitle(placeNameClick).position(point));


        //if(originName.equals(placeNameClick))

        if (flagOrigin)
            myMapboxMap.addMarker(new MarkerOptions().position(new LatLng(origin.latitude(), origin.longitude())).setTitle(searchOriginName));
        if (flagDestination)
            myMapboxMap.addMarker(new MarkerOptions().position(new LatLng(destination.latitude(), destination.longitude())).setTitle(searchDestinationName));
    }

    public Location getLocation() {
        try {
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            };

            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Log.v("isGPSEnabled", "= " + isGPSEnabled);

            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.v("isNetworkEnabled", "= " + isNetworkEnabled);

            if (isGPSEnabled == false && isNetworkEnabled == false) {
                // no network provider is enabled
                Log.d("kolosova_error", "no provider for getting the device location");
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    location = null;
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return null;
                    }
                    locationComponent.setLocationComponentEnabled(true);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return null;
                        }
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            deviceLatitude = location.getLatitude();
                            deviceLongitude = location.getLongitude();
                        }
                    }
                }
                if (isGPSEnabled) {
                    location = null;
                    locationComponent.setLocationComponentEnabled(true);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                    Log.d("GPS Enabled", "GPS Enabled");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            deviceLatitude = location.getLatitude();
                            deviceLongitude = location.getLongitude();
                        }
                    }
                }
            }

            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);

            Log.d("kolosova_checkLocation", "DLat " + deviceLatitude + ", DLng " + deviceLongitude);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    public void startNavigationClick(View view) {
        if (!GetInfo.isOnline(this)) {
            Toast.makeText(getApplicationContext(),
                    "No Internet connection! Please, turn on wi-fi or mobile data for information loading!", Toast.LENGTH_LONG).show();
            FLAG_CONNECTION_STATUS_ACTIVE = false;
        } else if (!GetInfo.isGPSon(context)) {
            Toast.makeText(getApplicationContext(), "No GPS connection! Please, activate device location!", Toast.LENGTH_LONG).show();
            FLAG_CONNECTION_STATUS_ACTIVE = false;
        } else {
            Log.d("kolosova_checkInfo", "button is clicked");
            NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                    .directionsRoute(currentRoute)
                    //.shouldSimulateRoute(true) //for checking routeNavigationFunctions
                    .build();
            NavigationLauncher.startNavigation(MapsActivity.this, options);
        }
    }

    private void getRoute(@NonNull final Style style, Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .alternatives(true)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        System.out.println(call.request().url().toString());

                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            Toast.makeText(context, "No routes found...",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        currentRoute = response.body().routes().get(0);
                        // Draw the route on the map
                        if (style.isFullyLoaded()) {
                            if (navigationMapRoute != null) {
                                navigationMapRoute.removeRoute();
                            } else {
                                navigationMapRoute = new NavigationMapRoute(null, mapView, myMapboxMap);
                            }
                            navigationMapRoute.addRoute(currentRoute);
                        }
                        startNavigationButton.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.item1:
                Log.d("kolosova_checkInfo", String.valueOf(new Style.Builder().fromUrl("mapbox://styles/evakolosova/cjw68gr1o1s921cr087ywkqll")));
                myMapboxMap.setStyle(new Style.Builder().fromUrl("mapbox://styles/evakolosova/cjw68gr1o1s921cr087ywkqll"));
                return true;
            case R.id.item2:
                Log.d("kolosova_checkInfo", Style.MAPBOX_STREETS);
                myMapboxMap.setStyle(Style.MAPBOX_STREETS);
                return true;
            case R.id.item3:
                Log.d("kolosova_checkInfo", Style.SATELLITE);
                myMapboxMap.setStyle(Style.SATELLITE);
                return true;
            case R.id.item4:
                Log.d("kolosova_checkInfo", Style.SATELLITE_STREETS);
                myMapboxMap.setStyle(Style.SATELLITE_STREETS);
                return true;
            default:
                onBackPressed();
                return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public void restartActivity() {
        Intent mIntent = getIntent();
        finish();
        startActivity(mIntent);
    }
}