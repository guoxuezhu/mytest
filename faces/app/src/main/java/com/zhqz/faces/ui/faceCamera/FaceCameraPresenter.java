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


    public void detectFace(int faceUserId, Bitmap bitmap) {
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
                            updataFaceDao(faceUserId, filePath);
                            for (int j = 0; j < features.size(); j++) {
                                facesDataDao.insert(new FacesData(filePath, features.get(j).mThumbnailPath, features.get(j).mFeatureIndex,
                                        features.get(j).mByteFeature, features.get(j).mFaceRect, features.get(j).mFeature));
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

    private void updataFaceDao(int faceUserId, String filePath) {
        mvpClient.updataface(faceUserId, filePath)
                .subscribe(new Observer<HttpResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult httpResult) {
                        ELog.i("============更新人脸======onNext=======" + httpResult.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        ELog.i("============更新人脸======onError=======" + e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
