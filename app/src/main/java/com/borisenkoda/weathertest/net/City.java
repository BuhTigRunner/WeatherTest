package com.borisenkoda.weathertest.net;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by BDA on 31.01.2016.
 */
public class City extends RealmObject{
    @PrimaryKey
    private int id;
    private String name;
    private String country;
    private String weatherDescription;
    private Double weatherTemp;
    private String icon;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    public Double getWeatherTemp() {
        return weatherTemp;
    }

    public void setWeatherTemp(Double weatherTemp) {
        this.weatherTemp = weatherTemp;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public City() {

    }

    public City(int id, String name, String country) {
        this.id = id;
        this.name = name;
        this.country = country;
    }
}
