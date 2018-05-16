package com.zhqz.mvp.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.zhqz.mvp.R;
import com.zhqz.mvp.ui.base.BaseActivity;
import com.zhqz.mvp.ui.main.MainActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity implements SplashMvpView {

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
        mHandler = new Handler();
        mHandler.postDelayed(() -> {
            setTiaozhuan();
            mHandler = null;
        }, 1000);

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

}
