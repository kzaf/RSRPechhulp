package com.zaf.rsrpechhulp.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.zaf.rsrpechhulp.R;
import com.zaf.rsrpechhulp.receivers.LocationBroadcastReceiver;
import com.zaf.rsrpechhulp.receivers.NetworkBroadcastReceiver;
import com.zaf.rsrpechhulp.utils.AddressObtainTask;
import com.zaf.rsrpechhulp.utils.CustomInfoWindowAdapter;
import com.zaf.rsrpechhulp.utils.Utils;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, AddressObtainTask.Callback {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int MY_PERMISSIONS_REQUEST_PHONE = 98;
    private GoogleMap mMap;
    Button back;
    Marker mCurrLocationMarker;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    FusedLocationProviderClient mFusedLocationClient;
    LocationCallback mLocationCallback;
    ReentrantLock addressObtainedLock;
    LatLng latLng = new LatLng(0, 0);
    MarkerOptions markerOptions;
    public AlertDialog lastAlertDialog;
    // Broadcast receiver to check the GPS state
    private BroadcastReceiver gpsSwitchStateReceiver = new LocationBroadcastReceiver(this);
    // Broadcast receiver to check the Network state
    private BroadcastReceiver networkSwitchStateReceiver = new NetworkBroadcastReceiver(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        toolbarOptions();

        // Google’s LocationServices API is the one which is actually used to access device location.
        // To access these services the app needs to connect to Google Play Services.
        // With FusedLocationProviderApi it was our responsibility to initiate and manage the connection.
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // A ReentrantLock is owned by the thread last successfully locking,
        // but not yet unlocking it. A thread invoking lock will return,
        // successfully acquiring the lock, when the lock is not owned by another thread.
        // The method will return immediately if the current thread already owns the lock.
        /**
         * See <a href="https://developer.android.com/reference/java/util/concurrent/locks/ReentrantLock">ReentrantLock</a>
         */
        addressObtainedLock = new ReentrantLock();

    }

    private void toolbarOptions() {
        back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    // This method is called when the user selects allow or deny on a permission window
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            // If request is cancelled, the result arrays are empty.
            case MY_PERMISSIONS_REQUEST_LOCATION:
                // permission was granted!
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    // If permission accepted check if the permission is in the manifest
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {
                        // If permission is in the manifest load the map
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(false);
                    }
                } else {
                    // If permission denied close the Map Activity
                    finish();
                }
            break;

            case MY_PERMISSIONS_REQUEST_PHONE: {

                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {

                    // If phone permission accepted, do the call
                    Utils.dialIfAvailable(MapsActivity.this, getString(R.string.phone));
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
        // Unregister the Broadcast Receivers when the app is on background
        this.unregisterReceiver(gpsSwitchStateReceiver);
        this.unregisterReceiver(networkSwitchStateReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check connectivity status on Activity start
        lastAlertDialog = Utils.checkGPSAndInternetAvailability(lastAlertDialog, MapsActivity.this);
        locationCallback();

        // Register the GPS receiver
        IntentFilter filterGps = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        filterGps.addAction(Intent.ACTION_PROVIDER_CHANGED);
        this.registerReceiver(gpsSwitchStateReceiver, filterGps);

        // Register the Network receiver
        IntentFilter filterNetwork = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filterNetwork.addAction(Intent.ACTION_PROVIDER_CHANGED);
        this.registerReceiver(networkSwitchStateReceiver, filterNetwork);
    }

    // Once an instance of this interface is set on a MapFragment or MapView object,
    // the onMapReady(GoogleMap) method is triggered when the map is ready to be used
    // and provides a non-null instance of GoogleMap.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setCompassEnabled(false);

        mLocationRequest = new LocationRequest();
        // Set the interval in which you want to get locations (two seconds interval)
        mLocationRequest.setInterval(2000);
        // If a location is available sooner you can get it
        // (i.e. another app is using the location services).
        mLocationRequest.setFastestInterval(2000);
        // Application wants high accuracy location,
        // thus it should create a location request with PRIORITY_HIGH_ACCURACY
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Check the android version to be API V23 (Marshmallow) and on  to show the permission
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                // Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback,
                        Looper.myLooper());
                mMap.setMyLocationEnabled(false);

            } else {
                // Request Location Permission
                mMap.setMyLocationEnabled(false);
                Utils.checkLocationPermission(MapsActivity.this);
            }
        }
        else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback,
                    Looper.myLooper());
            mMap.setMyLocationEnabled(false);

        }
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));
    }

    private void locationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                // Get a list with locations
                List<Location> locationList = locationResult.getLocations();

                // If the list is not empty
                if (locationList.size() > 0) {

                    //The last location in the list is the newest
                    Location location = locationList.get(locationList.size() - 1);

                    // Replace the last location wit hte new one
                    mLastLocation = location;

                    // Remove the old marker from the map to add the new
                    if (mCurrLocationMarker != null)
                        mCurrLocationMarker.remove();

                    // Set the coordinates to a new LatLng object
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    if(mMap == null)
                        return;

                    // Set custom location marker
                    markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.anchor(0.5f, 1.0f);
                    markerOptions.infoWindowAnchor(0.5f, -0.2f);
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker));
                    mCurrLocationMarker = mMap.addMarker(markerOptions);
                    mCurrLocationMarker.showInfoWindow();

                    //move map camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

                    // Obtain the address name to display
                    new AddressObtainTask(MapsActivity.this, MapsActivity.this).execute(latLng);
                }
            }
        };
    }

    // Called when bounded AddressObtainTask obtains address
    @Override
    public void onAddressObtained(@NonNull String address) {
        addressObtainedLock.lock();
        if(mCurrLocationMarker != null) {
            mCurrLocationMarker.setTitle(address);
            mCurrLocationMarker.showInfoWindow();
        }
        addressObtainedLock.unlock();
    }

    // This method is called when the user clicks the Bel RSR nu button to make the call
    public void onCallButtonClick(View view){
        // It is tablet
        if (findViewById(R.id.call_button) == null){
            if(Utils.checkPhonePermission(MapsActivity.this)){
                Utils.dialIfAvailable(MapsActivity.this, getString(R.string.phone));
            }
        } else { // It is a phone
            final Button callButton = findViewById(R.id.call_button);
            final RelativeLayout frame = findViewById(R.id.bel_nu_dialog);
            callButton.setVisibility(View.GONE); // Hide the Bel RSR nu button
            frame.setVisibility(View.VISIBLE); // Show the frame with the Bel nu button

            // If the Bel nu frame is open find its views
            // and set the onClick callback actions for the buttons of the frame
            if (frame.getVisibility() == View.VISIBLE){
                Button frameCloseButton = findViewById(R.id.bel_nu_close_button);
                Button belNuButton = findViewById(R.id.bel_nu_button);
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
                        if(Utils.checkPhonePermission(MapsActivity.this)){
                            Utils.dialIfAvailable(MapsActivity.this, getString(R.string.phone));
                        }
                    }
                });
            }
        }
    }




}