package com.borisenkoda.weathertest.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.borisenkoda.weathertest.R;
import com.borisenkoda.weathertest.activity.MainActivity;
import com.borisenkoda.weathertest.net.City;
import com.borisenkoda.weathertest.net.CurrentWeather;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by BDA on 06.02.2016.
 */
public class CitySearchFragment extends BaseCityListFragment implements SearchView.OnQueryTextListener {

    private String query="";

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        final SearchView sv = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        sv.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, sv);
        sv.setOnQueryTextListener(this);
        sv.setIconifiedByDefault(true);
    }



    @Override
    public boolean onQueryTextSubmit(String query) {
        this.query = query;
        api.getCurrentWeatherForQ(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ViewSubscription<CurrentWeather>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(CurrentWeather currentWeather) {
                        City city = new City();
                        city.updateFromCurrentWeather(currentWeather);
                        addOrUpdateCity(city);
                        updateListView();
                    }
                });
        return true;
    }

    private void addOrUpdateCity(City city) {
        if (city == null) return;
        for (City cityI:cities){
            if (city.id == cityI.id){
                cities.remove(cityI);
                cities.add(0,city);
                return;
            }
        }
        cities.add(0,city);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        query = newText;
        return true;
    }

    @Override
    protected void initSubscribeCities() {

    }

    @Override
    protected AdapterView.OnItemClickListener getOnItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                PopupMenu menu = new PopupMenu(getActivity(), view);
                menu.inflate(R.menu.menu_add);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        putCity(cities.get(position));
                        cities.remove(position);
                        updateListView();
                        Toast.makeText(getContext(),R.string.add_city_info, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                menu.show();
            }
        };
    }
}
