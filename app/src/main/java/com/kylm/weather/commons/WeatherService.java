package com.kylm.weather.commons;

import com.kylm.weather.model.CityInfo;
import com.kylm.weather.model.ConditionInfo;
import com.kylm.weather.model.HeWeather;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by kanyi on 2016/7/28.
 */
public interface WeatherService {

    @GET("citylist")
    Observable<CityInfo> getCityList(@Query("search") String searchType,
                                     @Query("key") String key);

    @GET("weather")
    Observable<HeWeather> getWeather(@Query("cityid") String cityid,
                                     @Query("key") String key);

    @GET("condition")
    Observable<ConditionInfo> getCondition(@Query("search") String searchType,
                                           @Query("key") String key);
}
