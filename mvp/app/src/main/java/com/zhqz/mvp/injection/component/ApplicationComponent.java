package com.zhqz.mvp.injection.component;

import android.app.Application;
import android.content.Context;

import com.zhqz.mvp.data.MvpClient;
import com.zhqz.mvp.data.db.AppDbModule;
import com.zhqz.mvp.injection.ApplicationContext;
import com.zhqz.mvp.injection.module.ApplicationModule;

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
