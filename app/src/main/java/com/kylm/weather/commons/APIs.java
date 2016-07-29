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

    //查询城市代码类型
    public static final String TYPE_ALL_CHINA = "allchina";
    public static final String TYPE_HOT_WORLD = "hotworld";
    public static final String TYPE_ALL_WORLD = "allworld";

    //查询天气代码类型
    public static final String TYPE_CONDITION_ALL_CONDITONS = "allcond";


    /**
     * 接口正常
     */
    public static final String STATUS_OK = "ok";

    /**
     * 错误的用户 key
     */
    public static final String STATUS_INVALID_KEY = "invalid key";

    /**
     * 未知城市
     */
    public static final String STATUS_UNKNOWN_CITY = "unknown city";

    /**
     * 超过访问次数
     */
    public static final String STATUS_NO_MORE_REQUESTS = "no more requests";

    /**
     * 没有访问权限
     */
    public static final String STATUS_PERMISSION_DENIED = "permission denied";

    /**
     * 服务无响应或超时
     */
    public static final String STATUS_ANR = "anr";


    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();

    public static WeatherService service = retrofit.create(WeatherService.class);

}
