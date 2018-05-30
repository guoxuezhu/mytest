package com.zhqz.faces.ui.addFaces;


import com.zhqz.faces.data.HttpResult;
import com.zhqz.faces.data.MvpClient;
import com.zhqz.faces.data.model.FaceUser;
import com.zhqz.faces.ui.base.Presenter;
import com.zhqz.faces.ui.splash.SplashMvpView;
import com.zhqz.faces.utils.ELog;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class AddFacesPresenter implements Presenter<AddFacesMvpView> {
    private AddFacesMvpView addMvpView;

    private MvpClient mvpClient;

    @Inject
    public AddFacesPresenter(MvpClient client) {
        this.mvpClient = client;
    }

    @Override
    public void attachView(AddFacesMvpView mvpView) {
        addMvpView = mvpView;
    }

    @Override
    public void detachView() {
        addMvpView = null;
    }

    void getFaces(String cardNumber) {
        mvpClient.getfaceUsers(cardNumber)
                .subscribe(new Observer<HttpResult<List<FaceUser>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<List<FaceUser>> listHttpResult) {
                        ELog.i("========getEnterStudent===========onNext===" + listHttpResult.toString());
                        if (listHttpResult.getData() != null && listHttpResult.getData().size() != 0) {
                            addMvpView.showFaceUsers(listHttpResult.getData());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ELog.i("========getEnterStudent============onError===" + e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


}
