package com.zhqz.faces;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

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

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        setupComponent();
        prefs = new SharePreferenceUtil(this, "faceSaveDates");


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
