package com.kylm.weather.commons;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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

    private static WeatherService service;

    public static WeatherService getService(final Context context) {
        if (service == null) {
            Interceptor interceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    if (!NetworkUtil.isNetworkConnected(context)) {
                        request = request.newBuilder()
                                .cacheControl(CacheControl.FORCE_CACHE)
                                .build();
                    }

                    Response response = chain.proceed(request);

                    if (NetworkUtil.isNetworkConnected(context)) {
                        int maxAge = 60; // 有网络时,设置缓存超时时间1分钟
                        response.newBuilder()
                                .header("Cache-Control", "public, max-age=" + maxAge)
                                .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                                .build();
                    } else {
                        int maxStale = 60 * 60 * 24 * 28; // 无网络时，设置超时为4周
                        response.newBuilder()
                                .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                                .removeHeader("Pragma")
                                .build();
                    }
                    return response;
                }
            };

            //设置缓存路径
            File httpCacheDirectory = new File(context.getCacheDir(), "responses");
            //设置缓存 20M
            Cache cache = new Cache(httpCacheDirectory, 20 * 1024 * 1024);

            //创建OkHttpClient，并添加拦截器和缓存代码
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .cache(cache).build();

            //创建retrofit，把OkHttpClient对象写入
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            service = retrofit.create(WeatherService.class);
        }
        return service;
    }

}
