package com.zaf.rsrpechhulp.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.zaf.rsrpechhulp.activities.MapsActivity;
import com.zaf.rsrpechhulp.R;

import static com.zaf.rsrpechhulp.activities.MapsActivity.MY_PERMISSIONS_REQUEST_LOCATION;

public class Utils {
    // To highlight the hyperlink in the dialog
    public static void setClickableHighLightedText(final TextView tv, String textToHighlight, final View.OnClickListener onClickListener) {
        String tvt = tv.getText().toString();
        int ofe = tvt.indexOf(textToHighlight, 0);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                if (onClickListener != null) onClickListener.onClick(textView);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(tv.getContext().getResources().getColor(R.color.colorPrimary));
                ds.setUnderlineText(true);
            }
        };
        SpannableString wordToSpan = new SpannableString(tv.getText());
        for (int ofs = 0; ofs < tvt.length() && ofe != -1; ofs = ofe + 1) {
            ofe = tvt.indexOf(textToHighlight, ofs);
            if (ofe == -1)
                break;
            else {
                wordToSpan.setSpan(clickableSpan, ofe, ofe + textToHighlight.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(wordToSpan, TextView.BufferType.SPANNABLE);
                tv.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }

    public static void checkLocationPermission(final MapsActivity mapsActivity) {
        if (ContextCompat.checkSelfPermission(mapsActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(mapsActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Shows an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(mapsActivity)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, " +
                                "please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(mapsActivity,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(mapsActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    // Checks if GPS location provider is enabled
    public static boolean checkGPSEnabled(Context context) {
        final LocationManager locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);

        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    // Checks if currently active network is connected or connecting to Internet
    public static boolean checkInternetConnectivity(Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


     // Builds an AlertDialog with information about disabled GPS location provider.
     // It starts location source settings on positive button
    public static AlertDialog alertGpsDisabled(final Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.error_gps_disabled_message)
                .setTitle(R.string.error_gps_disabled_title)
                .setCancelable(false)
                .setPositiveButton(R.string.error_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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

    // Builds an AlertDialog with information about disabled WIFI provider.
    // It starts WIFI source settings on positive button
    public static AlertDialog alertNoInternet(final Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.error_network_message)
                .setTitle(R.string.error_network_title)
                .setCancelable(false)
                .setPositiveButton(R.string.error_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        activity.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
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

    public static AlertDialog checkGPSAndInternetAvailability(AlertDialog lastAlertDialog, final Activity activity) {
        if(!checkGPSEnabled(activity))
            (lastAlertDialog = alertGpsDisabled(activity)).show();
        else if(!checkInternetConnectivity(activity)){
            (lastAlertDialog = alertNoInternet(activity)).show();
        }
        else {
            if(isActiveAlertDialog(lastAlertDialog))
                lastAlertDialog.hide();
        }

        return lastAlertDialog;
    }

    public static boolean isActiveAlertDialog(AlertDialog lastAlertDialog) {
        return lastAlertDialog != null && lastAlertDialog.isShowing();
    }

}
