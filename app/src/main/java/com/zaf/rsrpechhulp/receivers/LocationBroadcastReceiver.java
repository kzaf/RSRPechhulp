package com.zaf.rsrpechhulp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.zaf.rsrpechhulp.MapsActivity;
import com.zaf.rsrpechhulp.utils.Utils;

public class LocationBroadcastReceiver extends BroadcastReceiver {
    private MapsActivity mapsActivity;

    public LocationBroadcastReceiver(MapsActivity mapsActivity) {
        this.mapsActivity = mapsActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getAction())) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGpsEnabled || isNetworkEnabled) {
                mapsActivity.lastAlertDialog = Utils.checkGPSAndInternetAvailability(mapsActivity.lastAlertDialog, mapsActivity);
            } else {
                mapsActivity.lastAlertDialog = Utils.checkGPSAndInternetAvailability(mapsActivity.lastAlertDialog, mapsActivity);
            }
        }
    }
}
