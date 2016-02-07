package com.borisenkoda.weathertest.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.borisenkoda.weathertest.R;
import com.borisenkoda.weathertest.databinding.ItemCityLvBinding;
import com.borisenkoda.weathertest.databinding.ScreenListCityBinding;
import com.borisenkoda.weathertest.helpers.Easy;
import com.borisenkoda.weathertest.net.City;
import com.borisenkoda.weathertest.net.ServerApi;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by BDA on 06.02.2016.
 */
public abstract class BaseCityListFragment extends BaseFragment {
    @Inject
    ServerApi api;
    @Inject
    StorIOSQLite storIOSQLite;

    protected AdapterLv adapterLv;
    protected final List<City> cities = new ArrayList<>();
    protected ScreenListCityBinding binding;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Easy.logsV();
        initSubscribeCities();
        binding.lv.setOnItemClickListener(getOnItemClickListener());
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Easy.logsV();
        binding = DataBindingUtil.inflate(inflater, R.layout.screen_list_city, container, false);
        updateListView();
        return binding.getRoot();
    }

    protected void updateListView() {
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
            if (cities == null) return 0;
            return cities.size();
        }

        @Override
        public Object getItem(int i) {
            return cities.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final ItemCityLvBinding itemBinding;
            if (view == null) {
                itemBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.item_city_lv, viewGroup, false);
                view = itemBinding.getRoot();
                view.setTag(itemBinding);
            } else {
                itemBinding = (ItemCityLvBinding) view.getTag();
            }
            itemBinding.setCity(cities.get(i));
            return view;
        }
    }

    protected void putCity(City city) {
        storIOSQLite
                .put()
                .object(city)
                .prepare().executeAsBlocking();
    }

    protected void putCities(List<City> cities) {
        storIOSQLite
                .put()
                .objects(cities)
                .prepare().executeAsBlocking();
    }

    protected void removeCity(City city) {
        storIOSQLite
                .delete()
                .object(city)
                .prepare().executeAsBlocking();
    }

    protected abstract void initSubscribeCities();

    protected abstract AdapterView.OnItemClickListener getOnItemClickListener();
}
