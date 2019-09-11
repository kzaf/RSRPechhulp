package com.zaf.rsrpechhulp.Utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.zaf.rsrpechhulp.R;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private LayoutInflater layoutInflater;

    public CustomInfoWindowAdapter(Activity activity) {
        this.layoutInflater = activity.getLayoutInflater();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View view = layoutInflater.inflate(R.layout.layout_maps_info_window, null);
        ((TextView) view.findViewById(R.id.txv_maps_info_window)).setText(marker.getTitle());
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
