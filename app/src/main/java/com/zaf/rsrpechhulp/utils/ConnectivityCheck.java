package com.zaf.rsrpechhulp.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

class ConnectivityCheck {

    /**
     * Checks if GPS location provider is enabled
     * @param context The activity (context)
     * @return True or false according to the connectivity status
     */
    static boolean checkLocationConnectivity(Context context) {
        final LocationManager locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);

        return locationManager != null &&
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Checks if currently active network is connected or connecting to Internet
     * @param context The activity (context)
     * @return True or false according to the connectivity status
     */
    static boolean checkNetworkConnectivity(Context context) {

        final NetworkInfo activeNetworkInfo = ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        return activeNetworkInfo != null
                && activeNetworkInfo.isConnectedOrConnecting();
    }

}
