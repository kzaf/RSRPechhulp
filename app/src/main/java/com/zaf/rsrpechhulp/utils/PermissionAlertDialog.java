package com.zaf.rsrpechhulp.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

import com.zaf.rsrpechhulp.R;

class PermissionAlertDialog {

    private static final String LOCATION = "location";

    /**
     * Builds an AlertDialog with information about disabled connections.
     * It starts location or network source settings on positive button
     * @param activity The Activity where the dialog will be displayed
     * @param connectivity The type of connectivity to which the Dialog will be shown
     * @return Returns an alert dialog according to the
     */
    static android.app.AlertDialog alertNoLocationOrNetworkConnectivity(final Activity activity, String connectivity) {
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

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
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
}
