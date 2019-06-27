package com.example.tripbyphoto.map;

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

import com.example.tripbyphoto.R;
import com.example.tripbyphoto.utils.AppConsts;
import com.example.tripbyphoto.utils.ConnectionHelper;
import com.example.tripbyphoto.utils.GeocoderHelper;
import com.google.gson.JsonObject;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.BuildConfig;
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

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity {
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    private static final long MIN_TIME_BW_UPDATES = 1;
    private static final int REQUEST_CODE_AUTOCOMPLETE_1 = 1;
    private static final int REQUEST_CODE_AUTOCOMPLETE_2 = 2;
    protected MapboxMap mMapboxMap;
    protected String mPlaceName, mCountryName;
    protected boolean FLAG_CONNECTION_STATUS_ACTIVE = true;
    private String mPlaceNameClick, mCountryNameClick;
    private boolean isGPSEnabled = false, isNetworkEnabled = false, canGetLocation = false;
    private LocationManager mLocationManager;
    private Location mLocation;
    private Button mStartNavigationButton;
    private EditText tvDeparture, tvDestination;
    private Double mPhotoLatitude, mPhotoLongitude, mDeviceLatitude, mDeviceLongitude;
    private LocationComponent mLocationComponent;
    private MapView mMapView;
    private DirectionsRoute mCurrentRoute = null;
    private Point mOrigin, mDestination;
    private String mOriginName, mDestinationName, mSearchOriginName, mSearchDestinationName;
    private LatLng mPointOfDestination;
    private Context mContext;
    private NavigationMapRoute mNavigationMapRoute;
    private CameraPosition mPosition;
    private CarmenFeature mPhotoLocation, mDeviceLocation;
    private GridLayout glTopSheet;
    private Icon mIcon;
    private GeocoderHelper ghDevice, ghOnSearchResult, ghOnMapClick;
    private Geocoder mGeocoder;
    private Style mStyle;
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

        glTopSheet = findViewById(R.id.top_sheet);
        tvDeparture = findViewById(R.id.tv_departure);
        tvDestination = findViewById(R.id.tv_destination);
        mMapView = findViewById(R.id.map_view);
        mStartNavigationButton = findViewById(R.id.start_button);
        mContext = this;
        mGeocoder = new Geocoder(this, Locale.getDefault());

        TopSheetBehavior topSheetBehavior = TopSheetBehavior.from(glTopSheet);
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

        if (getIntent().hasExtra(AppConsts.MAP_LAT)) {
            String latitudeString = getIntent().getStringExtra(AppConsts.MAP_LAT);
            mPhotoLatitude = Double.parseDouble(latitudeString);
        }

        if (getIntent().hasExtra(AppConsts.MAP_LNG)) {
            String longitudeString = getIntent().getStringExtra(AppConsts.MAP_LNG);
            mPhotoLongitude = Double.parseDouble(longitudeString);
        }

        if (getIntent().hasExtra(AppConsts.MAP_PLACE_NAME)) {
            mPlaceName = getIntent().getStringExtra(AppConsts.MAP_PLACE_NAME);
        }

        if (getIntent().hasExtra(AppConsts.MAP_COUNTRY_NAME)) {
            mCountryName = getIntent().getStringExtra(AppConsts.MAP_COUNTRY_NAME);
        }
        Log.d(AppConsts.LOG_LOCATION, "PhotoLat " + mPhotoLatitude + ", PhotoLng " + mPhotoLongitude);
        mPointOfDestination = new LatLng(mPhotoLatitude, mPhotoLongitude);
        mPosition = new CameraPosition.Builder()
                .target(new LatLng(mPhotoLatitude, mPhotoLongitude))
                .zoom(2)
                .tilt(20)
                .build();
        Log.d(AppConsts.LOG_POSITION, mPosition.toString());

        mIcon = drawableToIcon(mContext, R.drawable.mapbox_marker_icon_default, Color.parseColor("#FF3C9AA4"));

        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(mapboxMap -> {
            mMapboxMap = mapboxMap;
            mMapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(mPosition));
            mMapboxMap.addMarker(new MarkerOptions().position(mPointOfDestination).setTitle(mPlaceName).setSnippet(mCountryName).icon(mIcon));
            mMapboxMap.setStyle(new Style.Builder().fromUrl(AppConsts.STYLE_URI), style -> {
                this.mStyle = style;
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
                mLocationComponent = mMapboxMap.getLocationComponent();
                mLocationComponent.activateLocationComponent(locationComponentActivationOptions);
                getLocation();
                mOrigin = Point.fromLngLat(mDeviceLongitude, mDeviceLatitude);
                mDestination = Point.fromLngLat(mPhotoLongitude, mPhotoLatitude);
                getRoute(style, mOrigin, mDestination);
                mMapView.addOnDidFinishLoadingStyleListener(() -> {
                    Log.i(AppConsts.LOG_CHECK, Integer.toString(R.string.log_check_msg_style_changed));
                    mMapboxMap.addMarker(new MarkerOptions().position(mPointOfDestination).setTitle(mPlaceName).setSnippet(mCountryName).icon(mIcon));
                });
                mMapView.addOnDidFinishLoadingMapListener(() -> {
                    Log.i(AppConsts.LOG_CHECK, Integer.toString(R.string.log_check_style_loaded));
                    mMapboxMap.addMarker(new MarkerOptions().position(mPointOfDestination).setTitle(mPlaceName).setSnippet(mCountryName).icon(mIcon));
                });

                mMapboxMap.addOnMapClickListener(point -> {
                    if (!ConnectionHelper.isOnline(this)) {
                        Toast.makeText(getApplicationContext(), R.string.internet_warning, Toast.LENGTH_LONG).show();
                        FLAG_CONNECTION_STATUS_ACTIVE = false;
                        return false;
                    } else if (!ConnectionHelper.isGPSon(mContext)) {
                        Toast.makeText(getApplicationContext(), R.string.GPS_warning, Toast.LENGTH_LONG).show();
                        FLAG_CONNECTION_STATUS_ACTIVE = false;
                        return false;
                    } else {
                        if (FLAG_CONNECTION_STATUS_ACTIVE == false && mCurrentRoute == null)
                            restartActivity();
                        if (FLAG_IS_ORIGIN_CHANGED) addMarkerOnMap(point, true, true);
                        else addMarkerOnMap(point, false, true);
                        return true;
                    }
                });

                LatLng point = new LatLng(mDeviceLatitude, mDeviceLongitude);
                ghDevice = new GeocoderHelper(mGeocoder, point);
                mOriginName = ghDevice.getPlaceFullName() + ", " + ghDevice.getCountryName();
                mDestinationName = mPlaceName + ", " + mCountryName;
                tvDeparture.setText(mOriginName);
                tvDestination.setText(mDestinationName);

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
        findViewById(R.id.fab_location_search_1).setOnClickListener(view -> {
            mStartNavigationButton.setVisibility(View.INVISIBLE);
            Intent intent = new PlaceAutocomplete.IntentBuilder()
                    .accessToken(Mapbox.getAccessToken())
                    .placeOptions(PlaceOptions.builder()
                            .backgroundColor(Color.parseColor("#FFEFE8E0"))
                            .limit(10)
                            .addInjectedFeature(mDeviceLocation)
                            .addInjectedFeature(mPhotoLocation)
                            .build(PlaceOptions.MODE_CARDS))
                    .build(MapsActivity.this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE_1);
        });

        findViewById(R.id.fab_location_search_2).setOnClickListener(view -> {
            mStartNavigationButton.setVisibility(View.INVISIBLE);
            Intent intent = new PlaceAutocomplete.IntentBuilder()
                    .accessToken(Mapbox.getAccessToken())
                    .placeOptions(PlaceOptions.builder()
                            .backgroundColor(Color.parseColor("#FFEFE8E0"))
                            .limit(10)
                            .addInjectedFeature(mDeviceLocation)
                            .addInjectedFeature(mPhotoLocation)
                            .build(PlaceOptions.MODE_CARDS))
                    .build(MapsActivity.this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE_2);
        });

        findViewById(R.id.fab_location_stability).setOnClickListener(view -> {
            mPosition = new CameraPosition.Builder()
                    .target(new LatLng(mDeviceLatitude, mDeviceLongitude))
                    .zoom(10)
                    .tilt(20)
                    .build();
            mMapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(mPosition));
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == MapsActivity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE_1) {
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
            mSearchOriginName = feature.text();
            tvDeparture.setText(mSearchOriginName);
            ghOnSearchResult = new GeocoderHelper(mGeocoder, mSearchOriginName);
            LatLng latlng = ghOnSearchResult.getLatlngFromPlaceName();
            mOrigin = Point.fromLngLat(latlng.getLongitude(), latlng.getLatitude());
            if (mSearchOriginName.equals(mOriginName)) {
                FLAG_IS_ORIGIN_CHANGED = false;
                if (FLAG_IS_DESTINATION_CHANGED) addMarkerOnMap(latlng, false, true);
                else addMarkerOnMap(latlng, false, false);
            } else {
                FLAG_IS_ORIGIN_CHANGED = true;
                if (FLAG_IS_DESTINATION_CHANGED) addMarkerOnMap(latlng, true, true);
                else addMarkerOnMap(latlng, true, false);
            }
            getRoute(mStyle, mOrigin, mDestination);
        } else if (resultCode == MapsActivity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE_2) {
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
            if (feature.placeName().equals(AppConsts.DEVICE_LOCATION)) {
                Toast toast = Toast.makeText(this, R.string.location_warning, Toast.LENGTH_LONG);
                toast.show();
                return;
            }
            mSearchDestinationName = feature.text();
            tvDestination.setText(mSearchDestinationName);
            ghOnSearchResult = new GeocoderHelper(mGeocoder, mSearchDestinationName);
            LatLng latlng = ghOnSearchResult.getLatlngFromPlaceName();
            mDestination = Point.fromLngLat(latlng.getLongitude(), latlng.getLatitude());
            if (mSearchDestinationName.equals(mDestinationName)) {
                FLAG_IS_DESTINATION_CHANGED = false;
                if (FLAG_IS_ORIGIN_CHANGED) addMarkerOnMap(latlng, true, false);
                else addMarkerOnMap(latlng, false, false);
            } else {
                FLAG_IS_DESTINATION_CHANGED = true;
                if (FLAG_IS_ORIGIN_CHANGED) addMarkerOnMap(latlng, true, true);
                else addMarkerOnMap(latlng, false, true);
            }
            getRoute(mStyle, mOrigin, mDestination);
        }
    }

    private void addUserLocations() {
        if (BuildConfig.DEBUG) {
            Log.d(AppConsts.LOG_LOCATION, "DeviceLocation: " + mDeviceLongitude + ", " + mDeviceLatitude);
            Log.d(AppConsts.LOG_LOCATION, "PhotoLocation: " + mPhotoLongitude + ", " + mPhotoLatitude);
            Log.d(AppConsts.LOG_NAMES, "OriginName: " + mOriginName);
            Log.d(AppConsts.LOG_NAMES, "DestinationName: " + mDestinationName);
        }

        mDeviceLocation = CarmenFeature.builder().text(mOriginName)
                .geometry(Point.fromLngLat(mDeviceLongitude, mDeviceLatitude))
                .placeName(AppConsts.DEVICE_LOCATION)
                .id(AppConsts.MAP_CONST)
                .properties(new JsonObject())
                .build();

        mPhotoLocation = CarmenFeature.builder().text(mDestinationName)
                .geometry(Point.fromLngLat(mPhotoLongitude, mPhotoLatitude))
                .placeName(AppConsts.PHOTO_LOCATION)
                .id(AppConsts.MAP_CONST)
                .properties(new JsonObject())
                .build();
    }

    private void addMarkerOnMap(@NonNull LatLng point, boolean flagOrigin, boolean flagDestination) {
        //adds marker with description
        ghOnMapClick = new GeocoderHelper(mGeocoder, point);
        mPlaceNameClick = ghOnMapClick.getPlaceFullName();
        mCountryNameClick = ghOnMapClick.getCountryName();
        if (!mMapboxMap.getMarkers().isEmpty()) {
            mMapboxMap.clear();
        }

        mMapboxMap.addMarker(new MarkerOptions().position(mPointOfDestination).setTitle(mPlaceName).setSnippet(mCountryName).icon(mIcon));

        if (mCountryNameClick != "")
            mMapboxMap.addMarker(new MarkerOptions().setTitle(mPlaceNameClick)
                    .setSnippet(mCountryNameClick)
                    .position(point));
        else
            mMapboxMap.addMarker(new MarkerOptions().setTitle(mPlaceNameClick).position(point));

        if (flagOrigin)
            mMapboxMap.addMarker(new MarkerOptions().position(new LatLng(mOrigin.latitude(), mOrigin.longitude())).setTitle(mSearchOriginName));
        if (flagDestination)
            mMapboxMap.addMarker(new MarkerOptions().position(new LatLng(mDestination.latitude(), mDestination.longitude())).setTitle(mSearchDestinationName));
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

            mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

            isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Log.v("isGPSEnabled", "= " + isGPSEnabled);

            isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.v("isNetworkEnabled", "= " + isNetworkEnabled);

            if (isGPSEnabled == false && isNetworkEnabled == false) {
                // no network provider is enabled
                Log.d(AppConsts.LOG_ERROR, "no provider for getting the device location");
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    mLocation = null;
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return null;
                    }
                    mLocationComponent.setLocationComponentEnabled(true);
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                    if (BuildConfig.DEBUG) Log.d(AppConsts.TAG_NETWORK, "Network");
                    if (mLocationManager != null) {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return null;
                        }
                        mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (mLocation != null) {
                            mDeviceLatitude = mLocation.getLatitude();
                            mDeviceLongitude = mLocation.getLongitude();
                        }
                    }
                }
                if (isGPSEnabled) {
                    mLocation = null;
                    mLocationComponent.setLocationComponentEnabled(true);
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                    Log.d("GPS Enabled", "GPS Enabled");
                    if (mLocationManager != null) {
                        mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (mLocation != null) {
                            mDeviceLatitude = mLocation.getLatitude();
                            mDeviceLongitude = mLocation.getLongitude();
                        }
                    }
                }
            }

            mLocationComponent.setCameraMode(CameraMode.TRACKING);
            mLocationComponent.setRenderMode(RenderMode.COMPASS);

            Log.d(AppConsts.LOG_LOCATION, "DeviceLat " + mDeviceLatitude + ", DeviceLng " + mDeviceLongitude);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mLocation;
    }

    public void startNavigationClick(View view) {
        if (!ConnectionHelper.isOnline(this)) {
            Toast.makeText(getApplicationContext(), R.string.internet_warning, Toast.LENGTH_LONG).show();
            FLAG_CONNECTION_STATUS_ACTIVE = false;
        } else if (!ConnectionHelper.isGPSon(mContext)) {
            Toast.makeText(getApplicationContext(), R.string.GPS_warning, Toast.LENGTH_LONG).show();
            FLAG_CONNECTION_STATUS_ACTIVE = false;
        } else {
            if (BuildConfig.DEBUG) Log.d(AppConsts.LOG_CHECK, "button is clicked");
            NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                    .directionsRoute(mCurrentRoute)
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

                        if (BuildConfig.DEBUG)
                            Log.d(AppConsts.TAG_DIRECTIONS, "@string/response_code" + response.code());
                        if (response.body() == null) {
                            if (BuildConfig.DEBUG)
                                Log.e(AppConsts.TAG_DIRECTIONS, "@string/msg_no_rotes_found_access");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            if (BuildConfig.DEBUG)
                                Log.e(AppConsts.TAG_DIRECTIONS, "@string/msg_no_routes_found");
                            Toast.makeText(mContext, "No routes found...",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mCurrentRoute = response.body().routes().get(0);
                        // Draw the route on the map
                        if (style.isFullyLoaded()) {
                            if (mNavigationMapRoute != null) {
                                mNavigationMapRoute.removeRoute();
                            } else {
                                mNavigationMapRoute = new NavigationMapRoute(null, mMapView, mMapboxMap);
                            }
                            mNavigationMapRoute.addRoute(mCurrentRoute);
                        }
                        mStartNavigationButton.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        if (BuildConfig.DEBUG)
                            Log.e(AppConsts.TAG_DIRECTIONS, "@string/error" + throwable.getMessage());
                    }
                });
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            mMapView.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.item1:
                if (BuildConfig.DEBUG)
                    Log.d(AppConsts.LOG_CHECK, String.valueOf(new Style.Builder().fromUrl(AppConsts.STYLE_URI)));
                mMapboxMap.setStyle(new Style.Builder().fromUrl(AppConsts.STYLE_URI));
                return true;
            case R.id.item2:
                if (BuildConfig.DEBUG) Log.d(AppConsts.LOG_CHECK, Style.MAPBOX_STREETS);
                mMapboxMap.setStyle(Style.MAPBOX_STREETS);
                return true;
            case R.id.item3:
                if (BuildConfig.DEBUG) Log.d(AppConsts.LOG_CHECK, Style.SATELLITE);
                mMapboxMap.setStyle(Style.SATELLITE);
                return true;
            case R.id.item4:
                if (BuildConfig.DEBUG) Log.d(AppConsts.LOG_CHECK, Style.SATELLITE_STREETS);
                mMapboxMap.setStyle(Style.SATELLITE_STREETS);
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