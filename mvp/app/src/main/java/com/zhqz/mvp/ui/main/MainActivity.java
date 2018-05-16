package com.zhqz.mvp.ui.main;

import android.os.Bundle;

import com.zhqz.mvp.R;
import com.zhqz.mvp.ui.base.BaseActivity;
import javax.inject.Inject;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements MainMvpView{

    @Inject
    MainPresenter mMainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        activityComponent().inject(this);
        mMainPresenter.attachView(this);
        mMainPresenter.getchools();


    }

    @Override
    protected void onDestroy() {
        mMainPresenter.detachView();//？
        super.onDestroy();
    }

}
