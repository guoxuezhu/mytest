package com.zhqz.hikjiankong.ui.splash;

import com.zhqz.hikjiankong.data.MvpClient;
import com.zhqz.hikjiankong.ui.base.Presenter;

import javax.inject.Inject;

public class SplashPresenter implements Presenter<SplashMvpView> {
    private SplashMvpView sMvpView;

    private MvpClient mvpClient;

    @Inject
    public SplashPresenter(MvpClient client) {
        this.mvpClient = client;
    }

    @Override
    public void attachView(SplashMvpView mvpView) {
        sMvpView = mvpView;
    }

    @Override
    public void detachView() {
        sMvpView = null;
    }

    public boolean hasValidUserStored() {
        return mvpClient.loadUserIfAvailble();
    }
}
