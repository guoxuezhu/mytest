package com.zhqz.mvp;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.zhqz.mvp.data.db.SharePreferenceUtil;
import com.zhqz.mvp.injection.component.ApplicationComponent;
import com.zhqz.mvp.injection.component.DaggerApplicationComponent;
import com.zhqz.mvp.injection.module.ApplicationModule;


public class MvpApplication extends MultiDexApplication {
    private ApplicationComponent mApplicationComponent;

    public static MvpApplication get(Context context) {
        return (MvpApplication) context.getApplicationContext();
    }

    public static SharePreferenceUtil prefs;
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        setupComponent();
        prefs = new SharePreferenceUtil(this, "mvpSaveDates");


    }


    private void setupComponent() {
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        mApplicationComponent.inject(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public ApplicationComponent getComponent() {
        return mApplicationComponent;
    }


}
