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
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.zaf.rsrpechhulp.activities.MapsActivity;
import com.zaf.rsrpechhulp.R;

import static com.zaf.rsrpechhulp.activities.MapsActivity.MY_PERMISSIONS_REQUEST_LOCATION;
import static com.zaf.rsrpechhulp.activities.MapsActivity.MY_PERMISSIONS_REQUEST_PHONE;

public class Utils {
    // To highlight the hyperlink in the dialog
    public static void setClickableHighLightedText(final TextView tv, String textToHighlight,
                                                   final View.OnClickListener onClickListener) {
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
                wordToSpan.setSpan(clickableSpan, ofe, ofe +
                        textToHighlight.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(wordToSpan, TextView.BufferType.SPANNABLE);
                tv.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }

    // Check if the GPS permission has been accepted
    public static void checkLocationPermission(final MapsActivity mapsActivity) {
        if (ContextCompat.checkSelfPermission(mapsActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(mapsActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(mapsActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    // Check if the Phone permission has been accepted
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
    public static boolean checkGPSEnabled(Context context) {
        final LocationManager locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);

        return locationManager != null &&
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
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
                        activity.startActivity(new Intent(Settings.
                                ACTION_LOCATION_SOURCE_SETTINGS));
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

    // Checks if GPS or Network is available, if not shows the respective dialog,
    // if yes, hides the dialog
    public static AlertDialog checkGPSAndInternetAvailability(AlertDialog lastAlertDialog,
                                                              final Activity activity) {
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

    // Checks if the alert dialog in MapsActivity is displayed or not
    private static boolean isActiveAlertDialog(AlertDialog lastAlertDialog) {
        return lastAlertDialog != null && lastAlertDialog.isShowing();
    }

    // dialIfAvailable method used to starts dialer if available with given phone number
    public static void dialIfAvailable(Context context, String phoneNumber) {
        // ACTION_CALL directly calls the number instead of CALL_PHONE
        // where first it displays the number in the dialer
        Intent dialIntent = new Intent(Intent.ACTION_CALL);
        dialIntent.setData(Uri.parse("tel:" + phoneNumber));
        //check if exists activity that can be started with dialIntent
        if (context.getPackageManager().queryIntentActivities(dialIntent, 0).size() > 0) {
            context.startActivity(dialIntent);
        }
    }

}
