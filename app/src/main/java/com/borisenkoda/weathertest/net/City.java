package com.borisenkoda.weathertest.net;


import android.os.Parcel;
import android.os.Parcelable;

import com.borisenkoda.weathertest.database.tables.CitiesTable;
import com.borisenkoda.weathertest.helpers.ParcelPacker;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

/**
 * Created by BDA on 31.01.2016.
 */
@StorIOSQLiteType(table = "cities")
public class City implements Parcelable{
    @StorIOSQLiteColumn(name = CitiesTable.COLUMN_ID, key = true)
    public int id;
    @StorIOSQLiteColumn(name = CitiesTable.NAME)
    public String name;
    @StorIOSQLiteColumn(name = CitiesTable.COUNTRY)
    public String country;
    @StorIOSQLiteColumn(name = CitiesTable.DESCRIPTION_WEATHER)
    public String weatherDescription;
    @StorIOSQLiteColumn(name = CitiesTable.WEATHER_TEMP)
    public Double weatherTemp;
    @StorIOSQLiteColumn(name = CitiesTable.ICON)
    public String icon;
    public Forecast forecast;

    public City() {

    }

    public City(int id, String name, String country) {
        this.id = id;
        this.name = name;
        this.country = country;
    }

    public void updateFromCurrentWeather(CurrentWeather currentWeather) {
        if (currentWeather == null || currentWeather.id == null) return;
        id = currentWeather.id;
        name = currentWeather.name;
        if (currentWeather.sys != null){
            country = currentWeather.sys.country;
        }
        if (currentWeather.main!=null){
            weatherTemp = currentWeather.main.temp;
        }
        if (currentWeather.weather!=null && currentWeather.weather.size()>0){
            weatherDescription = currentWeather.weather.get(0).description;
            icon = currentWeather.weather.get(0).icon;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        ParcelPacker.writeToParcel(parcel, this);
    }

    public static final Creator<City> CREATOR = ParcelPacker.getCreator(City.class);
}
