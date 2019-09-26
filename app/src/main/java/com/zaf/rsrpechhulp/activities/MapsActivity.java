package com.zaf.rsrpechhulp.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.zaf.rsrpechhulp.R;
import com.zaf.rsrpechhulp.receivers.ConnectionBroadcastReceiver;
import com.zaf.rsrpechhulp.utils.AddressObtainTask;
import com.zaf.rsrpechhulp.utils.MapUtils;
import com.zaf.rsrpechhulp.utils.Utilities;

import java.util.concurrent.locks.ReentrantLock;

import static com.zaf.rsrpechhulp.utils.PermissionAlertDialogUtils.checkGPSAndInternetAvailability;
import static com.zaf.rsrpechhulp.utils.PermissionCheck.checkPhonePermission;
import static com.zaf.rsrpechhulp.utils.Utilities.dialIfAvailable;

/**
 * Google’s LocationServices API is the one which is actually used to access device location.
 * To access these services the app needs to connect to Google Play Services.
 * With FusedLocationProviderApi it was our responsibility to initiate and manage the connection.
 */
public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, AddressObtainTask.Callback {

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private ReentrantLock addressObtainedLock;
    public AlertDialog lastAlertDialog;

    private BroadcastReceiver connectionStateReceiver = new ConnectionBroadcastReceiver(this);

    /**
     * A ReentrantLock is owned by the thread last successfully locking,
     * but not yet unlocking it. A thread invoking lock will return,
     * successfully acquiring the lock, when the lock is not owned by another thread.
     * The method will return immediately if the current thread already owns the lock.
     *
     * See <a href="https://developer.android.com/reference/java/util/concurrent/locks/ReentrantLock">ReentrantLock</a>
     *
     * @param savedInstanceState Default parameter for onCreate method.
     *                     It can be passed back to onCreate if the activity needs to be recreated
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment
                = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        addressObtainedLock = new ReentrantLock();
    }

    /**
     * Check connectivity status on Activity start
     * Register the Broadcast Receivers for the connection check
     */
    @Override
    protected void onResume() {
        super.onResume();

        lastAlertDialog = checkGPSAndInternetAvailability(lastAlertDialog, MapsActivity.this);
        Utilities.registerReceivers(this, connectionStateReceiver);

        mLocationCallback = MapUtils.getLocation(this);

    }

    /**
     * Stop location updates when Activity is no longer active
     * Unregister the Broadcast Receivers when the app is on background
     */
    @Override
    public void onPause() {
        super.onPause();

        if (mFusedLocationClient != null)
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);

        this.unregisterReceiver(connectionStateReceiver);
    }

    /**
     * Once an instance of this interface is set on a MapFragment or MapView object,
     * the onMapReady(GoogleMap) method is triggered when the map is ready to be used
     * and provides a non-null instance of GoogleMap.
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapUtils.setUpMap(googleMap, mFusedLocationClient, mLocationCallback, this);
    }

    /**
     * Called when the user selects allow or deny on a permission window
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        MapUtils.checkPermissionRequestCode(
                this, requestCode, grantResults, mFusedLocationClient, mLocationCallback);
    }

    /**
     * Called when bounded AddressObtainTask obtains address
     * @see AddressObtainTask
     * @param address The address coming as a result from the AddressObtainTask class onPostExecute
     *                Set this address as a title to the marker
     */
    @Override
    public void onAddressObtained(@NonNull String address) {

        addressObtainedLock.lock();
        MapUtils.setUpMarkerAddress(address);
        addressObtainedLock.unlock();
    }

    /**
     * Called when the user clicks the button to make the call from the MapActivity
     * It makes a call only when the app is running on a phone
     * @param view View is required when calling from XML as it holds the OnClickListener
     */
    public void onCallButtonClick(View view){

        // It is tablet
        if (findViewById(R.id.call_button) == null){
            if(checkPhonePermission(MapsActivity.this)){
                dialIfAvailable(MapsActivity.this, getString(R.string.phone));
            }
        } else { // It is a phone
            final Button callButton = findViewById(R.id.call_button);
            final RelativeLayout frame = findViewById(R.id.bel_nu_dialog);
            callButton.setVisibility(View.GONE);
            frame.setVisibility(View.VISIBLE);

            // If the call pop-up is open find its views
            if (frame.getVisibility() == View.VISIBLE){
                final Button frameCloseButton = findViewById(R.id.bel_nu_close_button);
                final Button belNuButton = findViewById(R.id.bel_nu_button);
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
                        if(checkPhonePermission(MapsActivity.this)){
                            dialIfAvailable(MapsActivity.this, getString(R.string.phone));
                        }
                    }
                });
            }
        }
    }

    /**
     * Called when the back button in MapsActivity toolbar is pressed
     * @param view View is required when calling from XML as it holds the OnClickListener
     */
    public void onBackButtonClick(View view) {

        Button back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}