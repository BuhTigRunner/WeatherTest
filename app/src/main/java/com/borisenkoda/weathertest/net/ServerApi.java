package com.borisenkoda.weathertest.net;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by BDA on 04.02.2016.
 */
public interface ServerApi {
    String API_ENDPOINT = "http://api.openweathermap.org/data/2.5/";
    String API_KEY = "faddf7b83f3b59d117303478903c2615";


    // ---------------------------------------  general  -----------------------------------------
    @GET("weather")
    Observable<CurrentWeather> getCurrentWeather(@Query("id") int id);
}
