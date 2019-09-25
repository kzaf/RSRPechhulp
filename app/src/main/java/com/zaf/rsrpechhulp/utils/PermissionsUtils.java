package com.zaf.rsrpechhulp.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.zaf.rsrpechhulp.activities.MapsActivity;

import static com.zaf.rsrpechhulp.activities.MapsActivity.MY_PERMISSIONS_REQUEST_LOCATION;
import static com.zaf.rsrpechhulp.activities.MapsActivity.MY_PERMISSIONS_REQUEST_PHONE;
import static com.zaf.rsrpechhulp.utils.PermissionAlertDialog.alertNoLocationOrNetworkConnectivity;

public class PermissionsUtils {

    private static final String LOCATION = "location";
    private static final String NETWORK = "network";

    /**
     * Checks if the GPS permission has been accepted
     * @param mapsActivity the host Activity that hold the dialog
     */
    public static void checkLocationPermission(final MapsActivity mapsActivity) {
        if (ContextCompat.checkSelfPermission(mapsActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(mapsActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION );

        }
    }

    /**
     * Checks if the Phone permission has been accepted
     * @param mapsActivity the host Activity that hold the dialog
     */
    public static boolean checkPhonePermission(Activity mapsActivity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(mapsActivity,
                    android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(mapsActivity,
                        new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_PHONE);
                return false;
            }
        }
        //permission is automatically granted on sdk < 23 upon installation
        else {
            return true;
        }
    }

    // Checks if GPS location provider is enabled
    private static boolean checkGPSEnabled(Context context) {
        final LocationManager locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);

        return locationManager != null &&
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    // Checks if currently active network is connected or connecting to Internet
    private static boolean checkInternetConnectivity(Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    // Checks if GPS or Network is available, if not shows the respective dialog,
    // if yes, hides the dialog
    public static AlertDialog checkGPSAndInternetAvailability(AlertDialog lastAlertDialog,
                                                              final Activity activity) {
        if(!checkGPSEnabled(activity))
            (lastAlertDialog = alertNoLocationOrNetworkConnectivity(activity, LOCATION)).show();
        else if(!checkInternetConnectivity(activity)){
            (lastAlertDialog = alertNoLocationOrNetworkConnectivity(activity, NETWORK)).show();
        }
        else {
            if(isActiveAlertDialog(lastAlertDialog))
                lastAlertDialog.hide();
        }

        return lastAlertDialog;
    }

    // Checks if the alert dialog in MapsActivity is displayed or not
    private static boolean isActiveAlertDialog(AlertDialog lastAlertDialog) {
        return lastAlertDialog != null && lastAlertDialog.isShowing();
    }

}
