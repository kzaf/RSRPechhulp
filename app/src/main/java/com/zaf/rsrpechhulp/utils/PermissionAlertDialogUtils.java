package com.zaf.rsrpechhulp.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import com.zaf.rsrpechhulp.R;

public class PermissionAlertDialogUtils {

    private static final String LOCATION = "location";
    private static final String NETWORK = "network";

    /**
     * Checks if GPS or Network is available, if not shows the respective dialog,
     * if yes, hides the dialog
     * @param lastAlertDialog The Alert Dialog that exists in the Activity and it's either
     *                        showing or hiding.
     *                        The new Alert Dialog will be assigned to this and it will be
     *                        returned to the Activity.
     * @param activity The activity that hosts the Alert Dialog
     * @return An Alert Dialog according to the location or network connectivity.
     *         If both are connected, hides the last Alert Dialog.
     */
    public static AlertDialog checkGPSAndInternetAvailability(
            AlertDialog lastAlertDialog, final Activity activity) {

        if(!checkLocationConnectivity(activity))
            (lastAlertDialog = alertNoLocationOrNetworkConnectivity(activity, LOCATION)).show();
        else if(!checkNetworkConnectivity(activity)){
            (lastAlertDialog = alertNoLocationOrNetworkConnectivity(activity, NETWORK)).show();
        }
        else {
            if(lastAlertDialog != null && lastAlertDialog.isShowing())
                lastAlertDialog.hide();
        }

        return lastAlertDialog;
    }

    /**
     * Builds an AlertDialog with information about disabled connections.
     * It starts - location or network - source settings on positive button
     * @param activity The Activity where the dialog will be hosted
     * @param connectivity The type of connectivity to which the Dialog will be shown
     * @return Returns an alert dialog according to the connectivity
     */
    private static android.app.AlertDialog alertNoLocationOrNetworkConnectivity(
            final Activity activity, String connectivity) {

        int alertMessage;
        int alertTitle;
        final String action;

        if (connectivity.equals(LOCATION)){
            alertMessage = R.string.error_gps_disabled_message;
            alertTitle = R.string.error_gps_disabled_title;
            action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        }else{
            alertMessage = R.string.error_network_message;
            alertTitle = R.string.error_network_title;
            action = Settings.ACTION_WIFI_SETTINGS;
        }

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.
                Builder(activity);

        builder.setMessage(alertMessage)
                .setTitle(alertTitle)
                .setCancelable(false)
                .setPositiveButton(R.string.error_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        activity.startActivity(new Intent(action));
                    }
                })
                .setNegativeButton(R.string.all_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        activity.finish();
                    }
                });
        return builder.create();
    }

    /**
     * Checks if GPS location provider is enabled
     * @param context The activity (context)
     * @return True or false according to the connectivity status
     */
    private static boolean checkLocationConnectivity(Context context) {
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
    private static boolean checkNetworkConnectivity(Context context) {

        final NetworkInfo activeNetworkInfo = ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        return activeNetworkInfo != null
                && activeNetworkInfo.isConnectedOrConnecting();
    }


}
