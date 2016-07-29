package com.kylm.weather;

import com.kylm.weather.model.HeWeather;

/**
 * Created by kanyi on 2016/7/29.
 */
public interface MainView<T> extends BaseView<T> {
    void showWeatherInfo(HeWeather.WeatherBean weather);
}
