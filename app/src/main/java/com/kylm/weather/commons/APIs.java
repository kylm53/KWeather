package com.kylm.weather.commons;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kanyi on 2016/7/28.
 */
public final class APIs {
    public static final String KEY = "3c85fe013f9f42049999ca4fe81ac854";
    public static final String BASE_URL = "https://api.heweather.com/x3/";

    public static final String SEARCH_TYPE_ALL_CHINA = "allchina";
    public static final String SEARCH_TYPE_HOT_WORLD = "hotworld";
    public static final String SEARCH_TYPE_ALL_WORLD = "allworld";


    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();

    public static WeatherService service = retrofit.create(WeatherService.class);
}
