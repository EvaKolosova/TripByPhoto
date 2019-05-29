package com.example.tripbyphoto;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.map.NavigationMapboxMap;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity {
    private static final String TAG = "DirectionsInfo";
    protected MapboxMap mapboxMap;
    protected String placeName = "";
    private Button startNavigationButton;
    private Double latitude, longitude, deviceLatitude, deviceLongitude;
    private LocationComponent locationComponent;
    private MapView mapView;
    // variables for calculating and drawing a route
    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;
    private NavigationMapboxMap map;

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

        mapView = findViewById(R.id.mapView);
        startNavigationButton = findViewById(R.id.startButton);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                mapboxMap.setStyle(new Style.Builder().fromUrl("mapbox://styles/evakolosova/cjw68gr1o1s921cr087ywkqll"), new Style.OnStyleLoaded() { // Style.MAPBOX_STREETS
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
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

                        locationComponent = mapboxMap.getLocationComponent();
                        locationComponent.activateLocationComponent(locationComponentActivationOptions);
                        enableLocationComponent(style);
                        style.addImage("marker-icon-id", BitmapFactory.decodeResource(MapsActivity.this.getResources(), R.drawable.mapbox_marker_icon_default));

                        GeoJsonSource geoJsonSource = new GeoJsonSource("source-id", Feature.fromGeometry(Point.fromLngLat(longitude, latitude)));

                        style.addSource(geoJsonSource);

                        SymbolLayer symbolLayer = new SymbolLayer("layer-id", "source-id");
                        symbolLayer.withProperties(PropertyFactory.iconImage("marker-icon-id"));
                        style.addLayer(symbolLayer);

                        addDestinationIconSymbolLayer(style);

                        Point originPoint = Point.fromLngLat(deviceLongitude, deviceLatitude);
                        Point destinationPoint = Point.fromLngLat(longitude, latitude);

                        getRoute(originPoint, destinationPoint);
                        startNavigationButton.setEnabled(true);

//                        mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
//                            @Override
//                            public boolean onMapClick(@NonNull LatLng point){
//                                Point originPoint = Point.fromLngLat(deviceLongitude, deviceLatitude);
//                                Point destinationPoint = Point.fromLngLat(longitude, latitude);
//
//                                getRoute(originPoint, destinationPoint);
//                                startNavigationButton.setEnabled(true);
//                                return true;
//                            }
//                        });
                    }
                });
            }
        });
    }

    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                PropertyFactory.iconImage("destination-icon-id"),
                PropertyFactory.iconAllowOverlap(true),
                PropertyFactory.iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        try {
            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);
            deviceLatitude = locationComponent.getLastKnownLocation().getLatitude();
            deviceLongitude = locationComponent.getLastKnownLocation().getLongitude();
        } catch (SecurityException s) {
            s.getStackTrace();
        }

        // Set the component's camera mode
        locationComponent.setCameraMode(CameraMode.TRACKING);

        // Set the component's render mode
        locationComponent.setRenderMode(RenderMode.COMPASS);
    }

    public void startNavigationClick(View view) {
        Log.d("kolosova_checkInfo", "button is clicked");

        //functions for creating a map road
        //startNavigationButton.setBackgroundResource(R.color.colorAbsolutelyWhite);
        boolean simulateRoute = true;
        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                .directionsRoute(currentRoute)
                .shouldSimulateRoute(simulateRoute)
                .build();
        // Call this method with Context from within an Activity
        NavigationLauncher.startNavigation(MapsActivity.this, options);
    }

    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .alternatives(true)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        currentRoute = response.body().routes().get(0);

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
//                            try {
                                navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap);
//                            } catch (NullPointerException ex) {
//                                Log.i(TAG, "NullPointerEx in new NavigationMapRoute");
//                            }
                        }
                        try {
                            navigationMapRoute.addRoute(currentRoute);
                        } catch (NullPointerException e) {
                            Log.i(TAG, "NullPointerEx in navigationMapRoute.addRoute");
                        }
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
        mapView.onSaveInstanceState(outState);
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
        onBackPressed();
        return true;
    }
}