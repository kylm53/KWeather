package com.kylm.weather;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by kkk on 2016/8/8.
 */
public class ForecastApplication extends Application {
    private static ForecastApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this)
                .name("forecast.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }

    public static ForecastApplication getApplication() {
        return application;
    }

}
