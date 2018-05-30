package com.zhqz.faces.ui.faceCamera;

import com.zhqz.faces.data.MvpClient;
import com.zhqz.faces.ui.base.Presenter;

import javax.inject.Inject;

public class FaceCameraPresenter implements Presenter<FaceCameraMvpView> {

    private FaceCameraMvpView fcMvpView;

    private MvpClient mvpClient;

    @Inject
    public FaceCameraPresenter(MvpClient client) {
        this.mvpClient = client;
    }

    @Override
    public void attachView(FaceCameraMvpView mvpView) {
        fcMvpView = mvpView;
    }

    @Override
    public void detachView() {
        fcMvpView = null;
    }


}
