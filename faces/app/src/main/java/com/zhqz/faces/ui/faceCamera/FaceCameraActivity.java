package com.zhqz.faces.ui.faceCamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.Toast;

import com.sensetime.senseid.facepro.jniwrapper.library.FaceLibrary;
import com.zhqz.faces.R;
import com.zhqz.faces.ui.addFaces.AddFacesActivity;
import com.zhqz.faces.ui.base.BaseActivity;
import com.zhqz.faces.utils.ELog;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FaceCameraActivity extends BaseActivity implements FaceCameraMvpView, TakePictureSurfaceCallback.CallBack {

    @Inject
    FaceCameraPresenter mFaceCameraPresenter;

    @BindView(R.id.face_camera_surfaceview)
    SurfaceView surfaceView;
    private int faceUserId;
    private String faceUserName;
    private String faceUserSex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_camera);
        ButterKnife.bind(this);
        activityComponent().inject(this);
        mFaceCameraPresenter.attachView(this);

        faceUserId = getIntent().getIntExtra("faceUserId", 0);
        faceUserName = getIntent().getStringExtra("faceUserName");
        faceUserSex = getIntent().getStringExtra("faceUserSex");
        initListener();
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        SurfaceHolder holder = surfaceView.getHolder();
        holder.setFixedSize(1280, 720);
        holder.setKeepScreenOn(true);
        TakePictureSurfaceCallback.number = 100;
        holder.addCallback(new TakePictureSurfaceCallback(this));
    }

    @OnClick(R.id.image_ok)
    void image_ok() {
        TakePictureSurfaceCallback.number = 9;
    }

    @OnClick(R.id.back_img)
    void back_img() {
        tiaozhuanfinish();
    }

    private void tiaozhuanfinish() {
        startActivity(new Intent(FaceCameraActivity.this, AddFacesActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        tiaozhuanfinish();
    }

    @Override
    protected void onDestroy() {
        mFaceCameraPresenter.detachView();//？
        super.onDestroy();
    }

    @Override
    public void image(Bitmap bitmap) {
        ELog.i("==========bitmap===========" + bitmap);
        mFaceCameraPresenter.detectFace(faceUserId, faceUserName, faceUserSex, bitmap);
    }

    @Override
    public void updataFaceOK() {
        tiaozhuanfinish();
    }

    @Override
    public void updataFaceNO(String errorMsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FaceCameraActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
