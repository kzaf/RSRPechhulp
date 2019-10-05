package com.zaf.rsrpechhulp.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;

import com.zaf.rsrpechhulp.MapsActivity;
import com.zaf.rsrpechhulp.utils.AlertDialogUtils;

public class ConnectionBroadcastReceiver extends BroadcastReceiver {
    private MapsActivity hostActivity;

    public ConnectionBroadcastReceiver(Activity activity) {
        this.hostActivity = (MapsActivity) activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean isGpsEnabled = false;
        boolean isNetworkEnabled = false;

        boolean wifi = false;
        boolean mobile = false;

        if (intent.getAction() == null) return;

        if (intent.getAction().equals(LocationManager.PROVIDERS_CHANGED_ACTION)) {
            LocationManager locationManager = (
                    LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
            ConnectivityManager connMgr =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isAvailable();
            mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isAvailable();
        }

        if (isGpsEnabled || isNetworkEnabled || wifi || mobile) {
            hostActivity.lastAlertDialog = AlertDialogUtils.alertCheckConnectivityAvailability
                    (hostActivity.lastAlertDialog, hostActivity);
        } else {
            hostActivity.lastAlertDialog = AlertDialogUtils.alertCheckConnectivityAvailability
                    (hostActivity.lastAlertDialog, hostActivity);
        }

    }
}
