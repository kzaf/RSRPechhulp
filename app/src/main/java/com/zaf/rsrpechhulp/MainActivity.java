package com.zaf.rsrpechhulp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.zaf.rsrpechhulp.models.MainActivityInteractor;
import com.zaf.rsrpechhulp.models.MainActivityInteractorImpl;
import com.zaf.rsrpechhulp.presenter.MainActivityPresenter;
import com.zaf.rsrpechhulp.utils.Utilities;
import com.zaf.rsrpechhulp.view.MainActivityView;

public class MainActivity extends AppCompatActivity implements MainActivityView {

    MainActivityInteractor interactor; // Model
    private MainActivityPresenter presenter; // Presenter

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

        interactor = new MainActivityInteractorImpl(); // Model

        presenter = new MainActivityPresenter(interactor); // Presenter
        presenter.bind(this); // Presenter
    }

    @Override
    protected void onDestroy() {
        presenter.unbind();
        super.onDestroy();
    }

    /**
     * Called when the main button is clicked
     * Starts the MapsActivity
     * @param view View is required when calling from XML as it holds the OnClickListener
     */
    @Override
    public void startMapsActivity(View view) { // View
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
