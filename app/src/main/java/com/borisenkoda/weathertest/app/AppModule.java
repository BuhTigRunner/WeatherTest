package com.borisenkoda.weathertest.app;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.borisenkoda.weathertest.database.DBOpenHelper;
import com.borisenkoda.weathertest.database.tables.CitiesTable;
import com.borisenkoda.weathertest.net.City;
import com.borisenkoda.weathertest.net.CityStorIOSQLiteDeleteResolver;
import com.borisenkoda.weathertest.net.CityStorIOSQLiteGetResolver;
import com.borisenkoda.weathertest.net.CityStorIOSQLitePutResolver;
import com.borisenkoda.weathertest.net.Forecast;
import com.borisenkoda.weathertest.net.ServerApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class AppModule {

    private static final String PREFERENCES_NAME = "preferences";
    private App app;
    private Gson gson;


    AppModule(App app) {
        this.app = app;
        gson = new GsonBuilder()
                .create();
    }

    @Provides
    @Singleton
    App provideApp() {
        return app;
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return gson;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences() {
        return app.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }


    @Provides
    @Singleton
    ServerApi provideServerAPI() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().
                addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        HttpUrl url = request.url().newBuilder().
                                addQueryParameter("APPID", ServerApi.API_KEY).
                                addQueryParameter("lang", "ru").
                                addQueryParameter("units", "metric").
                                build();
                        request = request.newBuilder().url(url).build();
                        return chain.proceed(request);
                    }
                }).
                addInterceptor(httpLoggingInterceptor).
                build();

        return new Retrofit.Builder()
                .baseUrl(ServerApi.API_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build().create(ServerApi.class);

    }

    @Provides
    @Singleton
    StorIOSQLite provideStorIOSQLite() {
        return DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(new DBOpenHelper(app))
                .addTypeMapping(City.class, SQLiteTypeMapping.<City>builder().putResolver(new CityStorIOSQLitePutResolver(){

                    @NonNull
                    @Override
                    public ContentValues mapToContentValues(@NonNull City object) {

                        ContentValues values = super.mapToContentValues(object);
                        if(object.forecast != null){
                            values.put(CitiesTable.FORECAST, gson.toJson(object.forecast));
                        }
                        return values;
                    }
                })
                        .getResolver(new CityStorIOSQLiteGetResolver(){
                            @NonNull
                            @Override
                            public City mapFromCursor(@NonNull Cursor cursor) {
                                City result = super.mapFromCursor(cursor);
                                String forecastSource = cursor.getString(cursor.getColumnIndex(CitiesTable.FORECAST));
                                if(forecastSource != null) result.forecast = gson.fromJson(forecastSource, Forecast.class);
                                return result;
                            }
                        })
                        .deleteResolver(new CityStorIOSQLiteDeleteResolver()).build())
                .build();
    }


}
