package com.zaf.rsrpechhulp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.zaf.rsrpechhulp.R;
import com.zaf.rsrpechhulp.utils.Utilities;

public class MainActivity extends AppCompatActivity {

    /**
     * Activity's lifecycle method
     * When the activity starts, set the default theme so the Splash Screen to be replaced
     * @param savedInstanceState Default parameter for onCreate method.
     *                     It can be passed back to onCreate if the activity needs to be recreated
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utilities.mainActivityToolbarOptions(this);
    }

    /**
     * Called when the main button is clicked
     * Starts the MapsActivity
     * @param view View is required when calling from XML as it holds the OnClickListener
     */
    public void startMapsActivity(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

}
