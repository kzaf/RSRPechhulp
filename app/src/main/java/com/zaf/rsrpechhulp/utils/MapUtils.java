package com.zaf.rsrpechhulp.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.zaf.rsrpechhulp.R;
import com.zaf.rsrpechhulp.activities.MapsActivity;

import java.util.List;

import static com.zaf.rsrpechhulp.utils.PermissionCheck.checkLocationPermission;
import static com.zaf.rsrpechhulp.utils.Utilities.dialIfAvailable;

public class MapUtils {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int MY_PERMISSIONS_REQUEST_PHONE = 98;

    private static GoogleMap mMap;
    private static LocationRequest mLocationRequest;
    private static List<Location> locationList;
    private static LatLng latLng = new LatLng(0, 0);
    private static boolean isFirstTime = true;
    private static String lastAddress;
    private static Marker mCurrLocationMarker;

    /**
     * @param googleMap
     * @param mFusedLocationClient
     * @param mLocationCallback
     * @param mapsActivity
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
     * @param mFusedLocationClient
     * @param mapsActivity
     * @param mLocationCallback
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
                checkLocationPermission(mapsActivity);
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
     * @param mapsActivity
     * @return
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
     * @param mapsActivity
     * @param requestCode
     * @param grantResults
     * @param mFusedLocationClient
     * @param mLocationCallback
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

                    dialIfAvailable(mapsActivity, mapsActivity.getString(R.string.phone));
                }
            }
        }
    }


    /**
     * @param address
     */
    public static void setUpMarkerAddress(@NonNull String address) {

        if(mCurrLocationMarker != null) {
            lastAddress = address;
            mCurrLocationMarker.setTitle(address);
            mCurrLocationMarker.showInfoWindow();
        }
    }

}
