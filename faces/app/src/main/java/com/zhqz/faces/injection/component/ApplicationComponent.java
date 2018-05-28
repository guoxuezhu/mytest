package com.zhqz.faces.injection.component;

import android.app.Application;
import android.content.Context;

import com.zhqz.faces.data.MvpClient;
import com.zhqz.faces.data.db.AppDbModule;
import com.zhqz.faces.injection.ApplicationContext;
import com.zhqz.faces.injection.module.ApplicationModule;

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
