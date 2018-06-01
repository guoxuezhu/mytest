package com.zhqz.hikjiankong.ui.main;

import android.os.Bundle;
import android.view.SurfaceView;

import com.zhqz.hikjiankong.R;
import com.zhqz.hikjiankong.ui.base.BaseActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements MainMvpView {

    @Inject
    MainPresenter mMainPresenter;

    @BindView(R.id.sf_VideoMonitor)
    SurfaceView m_osurfaceView;

    public final String ADDRESS = "192.168.31.105";
    public final int PORT = 8000;
    public final String USER = "admin";
    public final String PSD = "zhqz58612468";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        activityComponent().inject(this);
        mMainPresenter.attachView(this);
        //mMainPresenter.getchools();


    }

    @Override
    protected void onDestroy() {
        mMainPresenter.detachView();//？
        super.onDestroy();
    }

}
