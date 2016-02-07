package com.borisenkoda.weathertest.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.borisenkoda.weathertest.R;
import com.borisenkoda.weathertest.database.tables.CitiesTable;
import com.borisenkoda.weathertest.helpers.Easy;
import com.borisenkoda.weathertest.net.City;
import com.borisenkoda.weathertest.net.CurrentWeather;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import static rx.android.schedulers.AndroidSchedulers.mainThread;

/**
 * Created by BDA on 31.01.2016.
 */
public class CityListFragment extends BaseCityListFragment {


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final City city = cities.get(position);
                PopupMenu menu = new PopupMenu(getContext(), view);
                menu.inflate(R.menu.menu_remove);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.remove) {
                            removeCity(city);
                        }
                        return true;
                    }
                });
                menu.show();

                return true;
            }
        });
    }

    @Override
    protected AdapterView.OnItemClickListener getOnItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final City city = cities.get(position);
                PopupMenu menu = new PopupMenu(getContext(), view);
                menu.inflate(R.menu.menu_city);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.action_update) {
                            updateCity(city);
                        } else if (id == R.id.action_forecast_3 || id == R.id.action_forecast_7) {
                            getFragmentStack().push(new ForecastFragment().setCity(city).setCount(id == R.id.action_forecast_3 ? 3 : 7));
                        }
                        return true;
                    }
                });
                menu.show();


            }
        };
    }

    private void updateCity(final City city) {
        api.getCurrentWeatherForId(city.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        Easy.logD("onUnsubscribe");
                    }
                })
                .subscribe(new ViewSubscription<CurrentWeather>() {
                    @Override
                    public void onCompleted() {
                        Easy.logD("onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        Easy.logE(e.getMessage());
                    }

                    @Override
                    public void onNext(CurrentWeather currentWeather) {
                        Easy.logD(currentWeather);
                        city.updateFromCurrentWeather(currentWeather);
                        putCity(city);
                        updateListView();
                        Toast.makeText(getContext(), currentWeather.main.temp + "", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void initSubscribeCities() {
        storIOSQLite
                .get()
                .listOfObjects(City.class)
                .withQuery(CitiesTable.QUERY_ALL)
                .prepare()
                .asRxObservable()
                .observeOn(mainThread())
                .subscribe(new ViewSubscription<List<City>>() {
                    @Override
                    public void onCompleted() {
                        Easy.logD("onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Easy.logE(e.getMessage());
                    }

                    @Override
                    public void onNext(List<City> nextCities) {
                        Easy.logD("cities.size()=" + nextCities.size());
                        cities.clear();
                        cities.addAll(nextCities);
                        if (nextCities.isEmpty()) {
                            updateCity(new City(498817, "Санкт-Петербург", "Россия"));
                            updateCity(new City(524901, "Москва", "Россия"));
                        } else {
                            updateListView();
                        }
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            getFragmentStack().push(new CitySearchFragment());
            return true;
        }
        return false;
    }
}
