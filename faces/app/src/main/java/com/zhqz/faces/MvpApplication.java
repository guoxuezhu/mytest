package com.zhqz.faces;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.zhqz.faces.data.DbDao.DaoMaster;
import com.zhqz.faces.data.DbDao.DaoSession;
import com.zhqz.faces.data.db.SharePreferenceUtil;
import com.zhqz.faces.injection.component.ApplicationComponent;
import com.zhqz.faces.injection.component.DaggerApplicationComponent;
import com.zhqz.faces.injection.module.ApplicationModule;


public class MvpApplication extends MultiDexApplication {
    private ApplicationComponent mApplicationComponent;

    public static MvpApplication get(Context context) {
        return (MvpApplication) context.getApplicationContext();
    }

    public static SharePreferenceUtil prefs;
    public static Context context;
    public static DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        setupComponent();
        prefs = new SharePreferenceUtil(this, "faceSaveDates");

        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(getApplicationContext(), "faces.db", null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        daoSession = daoMaster.newSession();

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

    public static DaoSession getDaoSession() {
        return daoSession;
    }
}
