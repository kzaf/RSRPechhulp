package com.zaf.rsrpechhulp.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;

import com.zaf.rsrpechhulp.R;

public class AlertDialogUtils {

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
    public static AlertDialog alertCheckConnectivityAvailability(
            AlertDialog lastAlertDialog, final Activity activity) {

        if(!ConnectivityCheck.checkLocationConnectivity(activity))
            (lastAlertDialog = alertNoConnectivity(activity, LOCATION)).show();
        else if(!ConnectivityCheck.checkNetworkConnectivity(activity)){
            (lastAlertDialog = alertNoConnectivity(activity, NETWORK)).show();
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
    private static android.app.AlertDialog alertNoConnectivity(
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

        return new android.app.AlertDialog.Builder(activity).setMessage(alertMessage)
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
                }).create();
    }

    /**
     * Creates and shows an Alert Dialog for the privacy policy information
     * Contains a hyperlink which when it's clicked, opens the website in a browser
     * @param activity The activity that hosts the Dialog
     */
    static void alertPrivacyPolicy(final Activity activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        @SuppressLint("InflateParams")
        View customLayout = Utilities.setPrivacyDialogLayout(activity);

        builder.setCancelable(false);
        builder.setView(customLayout);
        builder.setPositiveButton(activity.getResources().getString(
                R.string.privacy_dialog_confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }



}
