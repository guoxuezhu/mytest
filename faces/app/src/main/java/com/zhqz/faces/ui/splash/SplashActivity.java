package com.zhqz.faces.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.zhqz.faces.MvpApplication;
import com.zhqz.faces.R;
import com.zhqz.faces.data.DbDao.FacesDataDao;
import com.zhqz.faces.ui.base.BaseActivity;
import com.zhqz.faces.ui.main.MainActivity;
import com.zhqz.faces.utils.ELog;
import com.zhqz.faces.utils.FileSizeUtil;
import com.zhqz.faces.utils.faceUtil.FaceDBAsyncTask;
import com.zhqz.faces.utils.faceUtil.FaceSearchManager;
import com.zhqz.faces.utils.faceUtil.LicenseResultListener;
import com.zhqz.faces.utils.faceUtil.PrepareLicenseAyncTask;
import com.zhqz.faces.utils.faceUtil.ResultListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity implements SplashMvpView, LicenseResultListener {

    @Inject
    SplashPresenter mSplashPresenter;

    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        activityComponent().inject(this);
        mSplashPresenter.attachView(this);
        FileSizeUtil.createFile();

        // 执行授权申请线程，Face SDK的其他接口调用都需要在该线程成功完成之后
        // execute the license task，other api should be called after the task
        // completes successfully
        PrepareLicenseAyncTask licenseAyncTask = new PrepareLicenseAyncTask(this, this);
        licenseAyncTask.execute();


//        mHandler = new Handler();
//        mHandler.postDelayed(() -> {
//            setTiaozhuan();
//            mHandler = null;
//        }, 1000);

    }


    private void setTiaozhuan() {
        if (mSplashPresenter.hasValidUserStored()) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();

    }


    @Override
    protected void onDestroy() {
        mSplashPresenter.detachView();//？
        super.onDestroy();
    }
//
//    @Override
//    public void onSuccess(Integer result) {
//        ELog.i("======facesDataDao.loadAll().size()==========" + result);
//        setTiaozhuan();
//    }
//
//    @Override
//    public void onFailed(String errorMessage) {
//        Toast.makeText(this, "出错啦:" + errorMessage, Toast.LENGTH_LONG).show();
//    }

    @Override
    public void onLicenseInitSuccess() {
        // 初始化人脸搜索
        // initialize for face searching
        try {
            FaceSearchManager.getInstance().init(this, FileSizeUtil.getModelPath(this, "M_Verify_MIMICG2_Common_3.17.0_v1.model"));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "出错啦: init error.", Toast.LENGTH_LONG).show();
            return;
        }
        FacesDataDao facesDataDao = MvpApplication.getDaoSession().getFacesDataDao();
        ELog.i("=====检测到====facesDataDao===" + facesDataDao.loadAll().toString());
        setTiaozhuan();
//
//        List<String> imageList = new ArrayList<>();
//        imageList.add("/storage/emulated/0/zhqz/FacesImage/aaa.jpg");
//        imageList.add("/storage/emulated/0/zhqz/FacesImage/bbb.jpg");
//        imageList.add("/storage/emulated/0/zhqz/FacesImage/ccc.jpg");
//
//        if (imageList.size() != 0) {
//            FaceDBAsyncTask task = new FaceDBAsyncTask(this, imageList, this);
//            task.execute();
//        }
    }

    @Override
    public void onLicenseInitFailed(String errorMessage) {
        Toast.makeText(this, "出错啦:" + errorMessage, Toast.LENGTH_LONG).show();
    }


}
