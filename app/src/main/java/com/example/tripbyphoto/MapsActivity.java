package com.example.tripbyphoto;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.core.constants.Constants;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MapsActivity extends AppCompatActivity {
    private static final String TAG = "DirectionsInfo";
    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters  // The minimum distance to change Updates in meters
    private static final long MIN_TIME_BW_UPDATES = 1; // 1 minute  // The minimum time between updates in milliseconds
    protected MapboxMap myMapboxMap;
    protected String placeName, countryName;
    private String placeNameClick, countryNameClick;
    private boolean isGPSEnabled = false, isNetworkEnabled = false, canGetLocation = false;
    private LocationManager locationManager;
    private Location location;
    private Button startNavigationButton;
    private Double latitude, longitude, deviceLatitude, deviceLongitude;
    private LocationComponent locationComponent;
    private MapView mapView;
    private DirectionsRoute currentRoute = null;
    private MapboxDirections client;
    private Point origin, destination;
    private LatLng pointOfDestination;
    private Context context;
    private NavigationMapRoute navigationMapRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.access_token));

        setContentView(R.layout.activity_maps);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra("MAP_latitude")) {
            String latitudeString = getIntent().getStringExtra("MAP_latitude");
            latitude = Double.parseDouble(latitudeString);
        }

        if (getIntent().hasExtra("MAP_longitude")) {
            String longitudeString = getIntent().getStringExtra("MAP_longitude");
            longitude = Double.parseDouble(longitudeString);
        }

        if (getIntent().hasExtra("MAP_place_name")) {
            placeName = getIntent().getStringExtra("MAP_place_name");
        }

        if (getIntent().hasExtra("MAP_country_name")) {
            countryName = getIntent().getStringExtra("MAP_country_name");
        }
        pointOfDestination = new LatLng(latitude, longitude);
        mapView = findViewById(R.id.mapView);
        startNavigationButton = findViewById(R.id.startButton);
        context = this;

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(mapboxMap -> {
            myMapboxMap = mapboxMap;
            myMapboxMap.addMarker(new MarkerOptions().position(pointOfDestination).setTitle(countryName).setSnippet(placeName));
            myMapboxMap.setStyle(new Style.Builder().fromUrl("mapbox://styles/evakolosova/cjw68gr1o1s921cr087ywkqll"), style -> {
                int blue = Color.parseColor("#FF4A8FE1");
                float alpfa = 1f;
                LocationComponentOptions locationComponentOptions = LocationComponentOptions.builder(MapsActivity.this)
                        .foregroundTintColor(blue)
                        .backgroundTintColor(Color.WHITE)
                        .bearingTintColor(blue)
                        .accuracyAlpha(alpfa)
                        .accuracyColor(blue)
                        .build();
                LocationComponentActivationOptions locationComponentActivationOptions = LocationComponentActivationOptions.builder(MapsActivity.this, style)
                        .locationComponentOptions(locationComponentOptions)
                        .build();
                locationComponent = myMapboxMap.getLocationComponent();
                locationComponent.activateLocationComponent(locationComponentActivationOptions);
                getLocation();
                origin = Point.fromLngLat(deviceLongitude, deviceLatitude);
                destination = Point.fromLngLat(longitude, latitude);
                getRoute(style, origin, destination);
                mapView.addOnDidFinishLoadingStyleListener(() -> {
                    Log.i("kolosova_checkInfo", "map's style has changed");
                    myMapboxMap.addMarker(new MarkerOptions().position(pointOfDestination).setTitle(countryName).setSnippet(placeName));
                });
                mapView.addOnDidFinishLoadingMapListener(() -> {
                    Log.i("kolosova_checkInfo", "map has loaded");
                    myMapboxMap.addMarker(new MarkerOptions().position(pointOfDestination).setTitle(countryName).setSnippet(placeName));
                });
                myMapboxMap.addOnMapClickListener(point -> {
                    Log.d("kolosova_checkInfo", "inside");
                    if (!myMapboxMap.getMarkers().isEmpty()) {
                        myMapboxMap.clear();
                    }
                    addMarkerOnMap(point);
                    myMapboxMap.addMarker(new MarkerOptions().position(pointOfDestination).setTitle(countryName).setSnippet(placeName));
                    return true;
                });
            });
        });
    }

    private void addMarkerOnMap(@NonNull LatLng point) {
        //adds marker with description
        placeNameClick = "";
        countryNameClick = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(point.getLatitude(), point.getLongitude(), 1);
            if (addresses.get(0).getFeatureName() != null) {
                placeNameClick += addresses.get(0).getFeatureName();
                if ((addresses.get(0).getLocality() != null) || addresses.get(0).getAdminArea() != null)
                    placeNameClick += ", ";
            }
            if (addresses.get(0).getLocality() != null) {
                placeNameClick += addresses.get(0).getLocality();
                if (addresses.get(0).getAdminArea() != null)
                    placeNameClick += ", ";
            }
            if (addresses.get(0).getAdminArea() != null) {
                placeNameClick += addresses.get(0).getAdminArea();
            }
            countryNameClick = addresses.get(0).getCountryName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        myMapboxMap.addMarker(new MarkerOptions().setTitle(countryNameClick).setSnippet(placeNameClick).position(point));
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
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return null;
                    }
                    locationComponent.setLocationComponentEnabled(true);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                    if (location == null) {
                        locationComponent.setLocationComponentEnabled(true);
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                deviceLatitude = location.getLatitude();
                                deviceLongitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    public void startNavigationClick(View view) {
        Log.d("kolosova_checkInfo", "button is clicked");
        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                .directionsRoute(currentRoute)
                //.shouldSimulateRoute(true) //for checking routeNavigationFunctions
                .build();

        NavigationLauncher.startNavigation(MapsActivity.this, options);
    }

    private void getRoute(@NonNull final Style style, Point origin, Point destination) {
        // region NAVIGATION QUICK ROUTE
        // более быстрый вариант отрисовки пути, но это лишь синяя линия без учета пробок и она может отличаться от маршрута, который будет строиться для конечной навигации,
        // использование GeoJSON обьекта для рисования пути.
        // при тестирование на физическом нужно раскоменчивать :(
        /*client = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(getString(R.string.access_token))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                System.out.println(call.request().url().toString());

                Timber.d("Response code: " + response.code());
                if (response.body() == null) {
                    Timber.e("No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Timber.e("No routes found");
                    return;
                }

                currentRoute = response.body().routes().get(0);

                if (style.isFullyLoaded()) {
                    // Retrieve and update the source designated for showing the directions route
                    GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);

                    // Create a LineString with the directions route's geometry and
                    // reset the GeoJSON source for the route LineLayer source
                    if (source != null) {
                        Timber.d("onResponse: source != null");
                        source.setGeoJson(FeatureCollection.fromFeature(
                                Feature.fromGeometry(LineString.fromPolyline(currentRoute.geometry(), Constants.PRECISION_6))));
                    }
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Timber.e("Error: " + throwable.getMessage());
                Toast.makeText(MapsActivity.this, "Error: " + throwable.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });*/
        // endregion

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
        if (client != null) {
            client.cancelCall();
        }
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
}