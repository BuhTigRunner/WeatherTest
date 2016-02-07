package com.borisenkoda.weathertest.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.borisenkoda.weathertest.R;
import com.borisenkoda.weathertest.databinding.ScreenForecastBinding;
import com.borisenkoda.weathertest.helpers.Easy;
import com.borisenkoda.weathertest.net.City;
import com.borisenkoda.weathertest.net.Forecast;
import com.borisenkoda.weathertest.net.ServerApi;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by BDA on 07.02.2016.
 */
public class ForecastFragment extends BaseFragment {
    @Inject
    ServerApi api;
    @Inject
    StorIOSQLite storIOSQLite;

    City city;
    private ScreenForecastBinding binding;
    private int count = 3;


    public ForecastFragment setCity(City city) {
        getArguments().putParcelable("city", city);
        return this;
    }

    public ForecastFragment setCount(int count) {
        this.count = count;
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (city == null && savedInstanceState != null && savedInstanceState.containsKey("city")) {
            city = savedInstanceState.getParcelable("city");
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("city", city);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.screen_forecast, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (city == null) {
            reload();
        } else {
            binding.setCity(city);
        }
        binding.tvForecast.setText(getString(count == 3 ? R.string.forecast_3 : R.string.forecast_7));
    }

    private void reload() {
        final City city = getArguments().getParcelable("city");

        api.getForecastDaily(city.id, count)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ViewSubscription<Forecast>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        updateCity(city);
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(Forecast forecast) {
                        city.forecast = forecast;
                        storIOSQLite
                                .put()
                                .object(city)
                                .prepare()
                                .executeAsBlocking();
                        updateCity(city);
                        Easy.logD(forecast.list.size());
                    }
                });
    }

    private void updateCity(City city) {
        this.city = city;
        binding.setCity(city);
    }
}
