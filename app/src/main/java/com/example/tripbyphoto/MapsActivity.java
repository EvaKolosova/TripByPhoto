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

public class MapsActivity extends AppCompatActivity {
    protected MapboxMap mapboxMap;
    protected String placeName = "";
    private Button startNavigationButton;
    private LocationComponent locationComponent;
    private MapView mapView;
    private Double latitude, longitude;

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
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
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
                    }
                });
            }
        });

//        public void startNavigationClick(View view) {
//            Log.d("kolosova_checkInfo", "button is clicked 2");
//        }
        View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("kolosova_checkInfo", "button is clicked");
            }
        };
        startNavigationButton.setOnClickListener(buttonListener);
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        try {
            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            // Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());
        } catch (NullPointerException n) {
            n.printStackTrace();
        }

        try {
            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);
        } catch (SecurityException s) {
            s.getStackTrace();
        }

        // Set the component's camera mode
        locationComponent.setCameraMode(CameraMode.TRACKING);

        // Set the component's render mode
        locationComponent.setRenderMode(RenderMode.COMPASS);
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