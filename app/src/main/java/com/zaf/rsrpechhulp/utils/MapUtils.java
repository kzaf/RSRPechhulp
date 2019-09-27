package com.zaf.rsrpechhulp.utils;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.zaf.rsrpechhulp.R;
import com.zaf.rsrpechhulp.activities.MainActivity;
import com.zaf.rsrpechhulp.activities.MapsActivity;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class MapUtils {

    static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    static final int MY_PERMISSIONS_REQUEST_PHONE = 98;

    private static GoogleMap mMap;
    private static LocationRequest mLocationRequest;
    private static List<Location> locationList;
    private static LatLng latLng = new LatLng(0, 0);
    private static boolean isFirstTime = true;
    private static String lastAddress;
    private static Marker mCurrLocationMarker;

    /**
     * Register the FusedLocationProviderClient
     * called every time the activity resumes {@see MapsActivity#onResume()}
     *
     * @param mapsActivity The required context
     * @return Returns an instance of FusedLocationProviderClient
     */
    public static FusedLocationProviderClient registerFusedLocationClient(MapsActivity mapsActivity) {
        return LocationServices.getFusedLocationProviderClient(mapsActivity);
    }

    /**
     * Set up the Google Map the first time the MapsActivity loads.
     * It is called inside {@link MapsActivity#onMapReady(GoogleMap)}
     *
     * @param googleMap The main class of the Google Maps SDK for Android
     * @param mFusedLocationClient Manages the underlying location technology and provides
     *                             a simple API so that you can specify requirements at a high level
     * @param mLocationCallback Used for receiving notifications from the FusedLocationProviderApi
     *                          when the device location has changed or can no longer be determined.
     * @param mapsActivity The context
     */
    public static void setUpMap(
            GoogleMap googleMap, FusedLocationProviderClient mFusedLocationClient,
            LocationCallback mLocationCallback, MapsActivity mapsActivity) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        getLocationUpdates(mFusedLocationClient, mapsActivity, mLocationCallback);

    }

    /**
     * Gets location updates via the FusedLocationProviderClient
     * It is called inside :
     * {@link #setUpMap(GoogleMap, FusedLocationProviderClient, LocationCallback, MapsActivity)}
     * {@link #checkPermissionRequestCode(
     * MapsActivity, int, int[],FusedLocationProviderClient, LocationCallback)}
     *
     * @param mFusedLocationClient Manages the underlying location technology and provides
     *                             a simple API so that you can specify requirements at a high level
     * @param mapsActivity The context needed for checkSelfPermission
     * @param mLocationCallback Used for receiving notifications from the FusedLocationProviderApi
     *                          when the device location has changed or can no longer be determined.
     */
    private static void getLocationUpdates(
            FusedLocationProviderClient mFusedLocationClient,
            MapsActivity mapsActivity, LocationCallback mLocationCallback) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    mapsActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                mFusedLocationClient.requestLocationUpdates(
                        mLocationRequest, mLocationCallback, Looper.myLooper());
            } else {
                PermissionCheck.checkLocationPermission(mapsActivity);
            }
        }
        else {
            mFusedLocationClient.requestLocationUpdates(
                    mLocationRequest, mLocationCallback, Looper.myLooper());
        }

        mMap.setMyLocationEnabled(false);
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(mapsActivity));
    }

    /**
     * Gets the location via the LocationCallback
     *
     * @param mapsActivity The context needed for the {@link AddressObtainTask}
     * @return The LocationCallback. Used for receiving notifications from
     *         the FusedLocationProviderApi when the device location has changed
     *         or can no longer be determined.
     */
    public static LocationCallback getLocation(final MapsActivity mapsActivity) {

        return new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                locationList = locationResult.getLocations();

                if (locationList.size() > 0 ) {
                    if(mMap == null) return;

                    Location location = locationList.get(locationList.size() - 1);
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    if (mCurrLocationMarker != null){
                        mCurrLocationMarker.remove();
                    }

                    mCurrLocationMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .anchor(0.5f, 1.0f)
                            .infoWindowAnchor(0.5f, -0.2f)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker)));

                    mCurrLocationMarker.setTitle(lastAddress);
                    mCurrLocationMarker.showInfoWindow();

                    new AddressObtainTask(mapsActivity, mapsActivity).execute(latLng);

                    if (isFirstTime){ // Do not move the camera after the first time of locating
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                        isFirstTime = false;
                    }

                }

            }

        };
    }

    /**
     * Checks the permission request code when the user clicks the allow button on
     * the permission dialog in {@link MapsActivity#onRequestPermissionsResult}
     *
     * @param mapsActivity The context
     * @param requestCode The request code (location or network)
     * @param grantResults An array to check if the respective permissions are already granted.
     *                     If it is not empty, the permissions are granted already.
     * @param mFusedLocationClient Manages the underlying location technology and provides
     *                             a simple API so that you can specify requirements at a high level
     * @param mLocationCallback Used for receiving notifications from the FusedLocationProviderApi
     *                          when the device location has changed or can no longer be determined.
     */
    public static void checkPermissionRequestCode(
            MapsActivity mapsActivity, int requestCode, @NonNull int[] grantResults,
            FusedLocationProviderClient mFusedLocationClient, LocationCallback mLocationCallback) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:

                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(mapsActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {

                        MapUtils.getLocationUpdates(
                                mFusedLocationClient, mapsActivity, mLocationCallback);

                    }
                } else {
                    mapsActivity.finish();
                }
                break;

            case MY_PERMISSIONS_REQUEST_PHONE: {
                if (grantResults.length > 0 && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {

                    Utilities.dialIfAvailable(mapsActivity, mapsActivity.getString(R.string.phone));
                }
            }
        }
    }

    /**
     * Sets the current fetched address from {@link MapsActivity#onAddressObtained}
     * to the Marker's info window
     *
     * @param address The current address fetched from {@link MapsActivity#onAddressObtained}
     */
    public static void setUpMarkerAddress(@NonNull String address) {

        if(mCurrLocationMarker != null) {
            lastAddress = address;
            mCurrLocationMarker.setTitle(address);
            mCurrLocationMarker.showInfoWindow();
        }
    }

    /**
     * Makes a call to a specific number
     * It makes a call only when the app is running on a phone
     * Called inside {@link MapsActivity#onCallButtonClick(View)}
     *
     * @param mapsActivity The context
     */
    public static void callButtonClick(final MapsActivity mapsActivity) {
        // It is tablet
        if (mapsActivity.findViewById(R.id.call_button) == null){
            if(PermissionCheck.checkPhonePermission(mapsActivity)){
                Utilities.dialIfAvailable(mapsActivity, mapsActivity.getString(R.string.phone));
            }
        } else { // It is a phone
            final Button callButton = mapsActivity.findViewById(R.id.call_button);
            final RelativeLayout frame = mapsActivity.findViewById(R.id.bel_nu_dialog);
            callButton.setVisibility(View.GONE);
            frame.setVisibility(View.VISIBLE);

            // If the call pop-up is open find its views
            if (frame.getVisibility() == View.VISIBLE){
                final Button frameCloseButton = mapsActivity.findViewById(R.id.bel_nu_close_button);
                final Button belNuButton = mapsActivity.findViewById(R.id.bel_nu_button);
                frameCloseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callButton.setVisibility(View.VISIBLE);
                        frame.setVisibility(View.GONE);
                    }
                });
                belNuButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(PermissionCheck.checkPhonePermission(mapsActivity)){
                            Utilities.dialIfAvailable(mapsActivity, mapsActivity.getString(R.string.phone));
                        }
                    }
                });
            }
        }
    }

    /**
     * Starts Main Activity when a button is clicked using (FLAG_ACTIVITY_CLEAR_TOP)
     *
     * If the activity being launched is already running in the current task, then instead
     * of launching a new instance of that activity, all of the other activities on top of it
     * will be closed and this Intent will be delivered to the (now on top) old activity as
     * a new Intent.
     * Called inside {@link MapsActivity#onBackButtonClick(View)}
     *
     * @param mapsActivity The context
     */
    public static void backButtonClick(final MapsActivity mapsActivity) {
        Button back = mapsActivity.findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mapsActivity.getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mapsActivity.startActivity(intent);
            }
        });
    }
}
