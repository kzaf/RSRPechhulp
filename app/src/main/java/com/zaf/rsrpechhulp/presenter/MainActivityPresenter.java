package com.zaf.rsrpechhulp.presenter;

import com.zaf.rsrpechhulp.models.MainActivityInteractor;
import com.zaf.rsrpechhulp.view.MainActivityView;

public class MainActivityPresenter {

    private MainActivityInteractor interactor;
    private MainActivityView view;

    public MainActivityPresenter(MainActivityInteractor mainActivityInteractor) {
        this.interactor = mainActivityInteractor;
    }

    public void bind(MainActivityView view){
        this.view = view;
    }

    public void unbind(){
        view = null;
    }
}
