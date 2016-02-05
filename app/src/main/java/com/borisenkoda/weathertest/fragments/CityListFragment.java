package com.borisenkoda.weathertest.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.borisenkoda.weathertest.R;
import com.borisenkoda.weathertest.databinding.ItemCityLvBinding;
import com.borisenkoda.weathertest.databinding.ScreenListCityBinding;
import com.borisenkoda.weathertest.helpers.Easy;
import com.borisenkoda.weathertest.net.City;
import com.borisenkoda.weathertest.net.CurrentWeather;
import com.borisenkoda.weathertest.net.ServerApi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by BDA on 31.01.2016.
 */
public class CityListFragment extends BaseFragment {
    @Inject
    ServerApi api;
    ScreenListCityBinding binding;
    private AdapterLv adapterLv;
    private Realm realm;
    RealmResults<City> citiesRealm;

    private void initCities() {
        citiesRealm = realm.where(City.class).findAll();
        if (citiesRealm.isEmpty()){
            realm.beginTransaction();
            realm.copyToRealm(new City(498817,"Санкт-Петербург","Россия"));
            realm.copyToRealm(new City(524901,"Москва","Россия"));
            realm.commitTransaction();
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Easy.logsV();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Easy.logsV();
        realm = Realm.getDefaultInstance();
        binding = DataBindingUtil.inflate(inflater, R.layout.screen_list_city, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Easy.logsV();
        updateList();
        binding.lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                api.getCurrentWeather(citiesRealm.get(position).getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnUnsubscribe(new Action0() {
                            @Override
                            public void call() {
                                Easy.logD("onUnsubscribe");
                            }
                        })
                        .subscribe(new FragmentSubscription<CurrentWeather>() {
                            @Override
                            public void onCompleted() {
                                Easy.logD("onCompleted");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Easy.logE(e.getMessage());
                            }

                            @Override
                            public void onNext(CurrentWeather currentWeather) {
                                Easy.logD(currentWeather);
                                City city = new City();
                                city.setId(currentWeather.id);
                                city.setName(currentWeather.name);
                                city.setCountry(currentWeather.sys.country);
                                city.setWeatherTemp(currentWeather.main.temp);
                                city.setWeatherDescription(currentWeather.weather.get(0).description);
                                city.setIcon(currentWeather.weather.get(0).icon);
                                if (realm!=null){
                                    realm.beginTransaction();
                                    realm.copyToRealmOrUpdate(city);
                                    realm.commitTransaction();
                                }
                                updateList();
                                Toast.makeText(getActivity(), currentWeather.main.temp+"", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    public void onStart() {
        Easy.logsV();

        super.onStart();
        realm = Realm.getDefaultInstance();
        updateList();
    }

    @Override
    public void onStop() {
        Easy.logsV();

        super.onStop();
        realm.close();
    }

    private void updateList() {
        if (citiesRealm==null) initCities();
        if (binding == null) return;
        if (binding.lv.getAdapter() == null) {
            adapterLv = new AdapterLv();
            binding.lv.setAdapter(adapterLv);
        } else {
            adapterLv.notifyDataSetChanged();
        }
    }

    private class AdapterLv extends BaseAdapter {
        @Override
        public int getCount() {
            if (citiesRealm == null) return 0;
            return citiesRealm.size();
        }

        @Override
        public Object getItem(int i) {
            return citiesRealm.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final ItemCityLvBinding itemBinding;
            if (view == null) {
                itemBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.item_city_lv, viewGroup, false);
                view = itemBinding.getRoot();
                view.setTag(itemBinding);
            } else {
                itemBinding = (ItemCityLvBinding) view.getTag();
            }
            itemBinding.setCity(citiesRealm.get(i));
            return view;
        }
    }
}
