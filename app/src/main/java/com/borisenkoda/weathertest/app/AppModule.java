package com.borisenkoda.weathertest.app;


import android.content.Context;
import android.content.SharedPreferences;

import com.borisenkoda.weathertest.net.ServerApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;

import java.io.IOException;
import java.util.Date;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;

@Module
public class AppModule {

    private static final String PREFERENCES_NAME = "preferences";
    private App app;
    private Gson gson;


    AppModule(App app) {
        this.app = app;
        gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
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
                                addQueryParameter("units","metric").
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



}
