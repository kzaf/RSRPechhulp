package com.zaf.rsrpechhulp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.zaf.rsrpechhulp.MapsActivity;
import com.zaf.rsrpechhulp.utils.Utils;

public class NetworkBroadcastReceiver extends BroadcastReceiver {
    private MapsActivity mapsActivity;

    public NetworkBroadcastReceiver(MapsActivity mapsActivity) {
        this.mapsActivity = mapsActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (wifi.isAvailable() || mobile.isAvailable()) {
                mapsActivity.lastAlertDialog = Utils.checkGPSAndInternetAvailability(mapsActivity.lastAlertDialog, mapsActivity);
            } else {
                mapsActivity.lastAlertDialog = Utils.checkGPSAndInternetAvailability(mapsActivity.lastAlertDialog, mapsActivity);
            }
        }
    }
}
