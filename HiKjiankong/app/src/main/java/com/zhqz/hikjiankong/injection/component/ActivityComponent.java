package com.zhqz.hikjiankong.injection.component;




import com.zhqz.hikjiankong.injection.PerActivity;
import com.zhqz.hikjiankong.injection.module.ActivityModule;
import com.zhqz.hikjiankong.ui.main.MainActivity;
import com.zhqz.hikjiankong.ui.splash.SplashActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(SplashActivity splashActivity);

    void inject(MainActivity mainActivity);
}

