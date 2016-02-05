package com.borisenkoda.weathertest.app;


import com.borisenkoda.weathertest.activity.MainActivity;
import com.borisenkoda.weathertest.fragments.CityListFragment;
import com.borisenkoda.weathertest.helpers.BindingHelper;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(MainActivity activity);
    void inject(CityListFragment fragment);
    void inject(BindingHelper helper);


}
