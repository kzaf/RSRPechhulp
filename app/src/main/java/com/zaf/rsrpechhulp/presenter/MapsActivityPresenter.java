package com.zaf.rsrpechhulp.presenter;

import com.zaf.rsrpechhulp.models.MapsActivityInteractor;
import com.zaf.rsrpechhulp.view.MapsActivityView;

public class MapsActivityPresenter {

    private MapsActivityInteractor interactor;
    private MapsActivityView view;

    public MapsActivityPresenter(MapsActivityInteractor interactor) {
        this.interactor = interactor;
    }

    public void bind(MapsActivityView view){
        this.view = view;
    }

    public void unbind(){
        view = null;
    }
}
