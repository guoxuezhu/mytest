package com.zhqz.mvp.injection.component;



import com.zhqz.mvp.injection.PerActivity;
import com.zhqz.mvp.injection.module.ActivityModule;
import com.zhqz.mvp.ui.main.MainActivity;
import com.zhqz.mvp.ui.splash.SplashActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(SplashActivity splashActivity);

    void inject(MainActivity mainActivity);
}

