package com.zhqz.faces.ui.faceCamera;

import android.graphics.Bitmap;

import com.zhqz.faces.MvpApplication;
import com.zhqz.faces.data.DbDao.FacesDataDao;
import com.zhqz.faces.data.HttpResult;
import com.zhqz.faces.data.MvpClient;
import com.zhqz.faces.data.model.FaceFeature;
import com.zhqz.faces.data.model.FacesData;
import com.zhqz.faces.data.model.School;
import com.zhqz.faces.exception.ClientRuntimeException;
import com.zhqz.faces.ui.base.Presenter;
import com.zhqz.faces.utils.ELog;
import com.zhqz.faces.utils.FileSizeUtil;
import com.zhqz.faces.utils.faceUtil.FaceSearchManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.schedulers.Schedulers;

public class FaceCameraPresenter implements Presenter<FaceCameraMvpView> {

    private FaceCameraMvpView fcMvpView;

    private MvpClient mvpClient;

    private Scheduler.Worker detectFaceWorker = Schedulers.io().createWorker();
    private FacesDataDao facesDataDao;

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


    public void detectFace(int faceUserId, String faceUserName, String faceUserSex, Bitmap bitmap) {
        facesDataDao = MvpApplication.getDaoSession().getFacesDataDao();
        if (detectFaceWorker.isDisposed()) {
            detectFaceWorker = Schedulers.io().createWorker();
        }
        detectFaceWorker.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    List<FaceFeature> features = FaceSearchManager.getInstance().getImageFeatures("", null, bitmap);

                    if (features == null && features.size() == 0) {
                        ELog.i("=====检测人脸失败===没有人脸===");
                    } else {
                        String filePath = FileSizeUtil.saveBitmap(bitmap);
                        if (filePath != null) {
                            for (int j = 0; j < features.size(); j++) {
                                facesDataDao.insert(new FacesData(faceUserName, faceUserSex, filePath, features.get(j).mFaceRect, features.get(j).mFeature));

                                updataFaceDao(faceUserId, features.get(j).mFaceRect, features.get(j).mFeature, filePath);
                            }
                            ELog.i("=====检测人脸==facesDataDao====" + facesDataDao.loadAll().toString());
                            ELog.i("=====检测人脸==facesDataDao====" + facesDataDao.loadAll().size());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, 1, TimeUnit.MILLISECONDS);
    }

    private void updataFaceDao(int faceUserId, String mFaceRect, String mFeature, String filePath) {
        mvpClient.updataface(faceUserId, mFaceRect, mFeature, filePath)
                .subscribe(new Observer<HttpResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult httpResult) {
                        ELog.i("============更新人脸======onNext=======" + httpResult.toString());
                        if (httpResult.code.equals("200")) {
                            fcMvpView.updataFaceOK();
                        } else {
                            fcMvpView.updataFaceNO("更新人脸失败，检查原因");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ELog.i("============更新人脸======onError=======" + e.toString());
                        fcMvpView.updataFaceNO(e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
