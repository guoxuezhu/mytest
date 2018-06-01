package com.zhqz.hikjiankong.injection.component;

import android.app.Application;
import android.content.Context;


import com.zhqz.hikjiankong.data.MvpClient;
import com.zhqz.hikjiankong.data.db.AppDbModule;
import com.zhqz.hikjiankong.injection.ApplicationContext;
import com.zhqz.hikjiankong.injection.module.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, AppDbModule.class})
public interface ApplicationComponent {

    void inject(Application application);

    @ApplicationContext
    Context context();

    Application application();

    MvpClient mvpClient();

}
