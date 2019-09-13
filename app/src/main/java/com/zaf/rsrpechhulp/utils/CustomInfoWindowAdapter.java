package com.zaf.rsrpechhulp.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.zaf.rsrpechhulp.R;

// Provides views for customized rendering of info windows.
/**
 * See <a href="https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.InfoWindowAdapter">GoogleMap.InfoWindowAdapter</a>
 */
public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private LayoutInflater layoutInflater;

    public CustomInfoWindowAdapter(Activity activity) {
        this.layoutInflater = activity.getLayoutInflater();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        // Inflate the custom info window layout
        View view = layoutInflater.inflate(R.layout.layout_maps_info_window, null);
        // Get the title that has been set to the marker object to display
        ((TextView) view.findViewById(R.id.txv_maps_info_window)).setText(marker.getTitle());
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
