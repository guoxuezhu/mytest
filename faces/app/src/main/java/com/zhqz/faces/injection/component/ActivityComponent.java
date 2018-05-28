package com.zhqz.faces.injection.component;


import com.zhqz.faces.injection.PerActivity;
import com.zhqz.faces.injection.module.ActivityModule;
import com.zhqz.faces.ui.main.MainActivity;
import com.zhqz.faces.ui.splash.SplashActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(SplashActivity splashActivity);

    void inject(MainActivity mainActivity);
}

