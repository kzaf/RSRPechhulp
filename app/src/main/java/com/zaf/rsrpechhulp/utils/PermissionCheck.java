package com.zaf.rsrpechhulp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.zaf.rsrpechhulp.activities.MapsActivity;

class PermissionCheck {

    /**
     * Checks if the GPS permission has been accepted
     * @param mapsActivity The host Activity that holds the dialog
     */
    static void checkLocationPermission(final MapsActivity mapsActivity) {
        if (ContextCompat.checkSelfPermission(mapsActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(mapsActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MapUtils.MY_PERMISSIONS_REQUEST_LOCATION );

        }
    }

    /**
     * Checks if the Phone permission has been accepted
     * permission is automatically granted on sdk < 23 upon installation
     * @param mapsActivity The host Activity that holds the dialog
     */
    static boolean checkPhonePermission(Activity mapsActivity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(mapsActivity,
                    android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(mapsActivity,
                        new String[]{Manifest.permission.CALL_PHONE},
                        MapUtils.MY_PERMISSIONS_REQUEST_PHONE);
                return false;
            }
        }
        else {
            return true;
        }
    }
}
