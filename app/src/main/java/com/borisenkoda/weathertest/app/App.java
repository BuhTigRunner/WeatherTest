package com.borisenkoda.weathertest.app;

import android.app.Application;

import com.borisenkoda.weathertest.helpers.Dagger2Helper;


public class App extends Application {


    private static AppComponent appComponent;

    public static AppComponent getAppComponent() {
        return appComponent;
    }

    public static void inject(Object target) {
        Dagger2Helper.inject(AppComponent.class, appComponent, target);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

}
