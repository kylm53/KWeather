package com.kylm.weather;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by kkk on 2016/8/8.
 */
public class ForecastApplication extends Application {
    private static Realm realm;
    private static ForecastApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public Realm getRealm() {
        if (realm == null) {
            RealmConfiguration config = new RealmConfiguration.Builder(this)
                    .name("forecast.realm")
                    .build();
            // Use the config
            realm = Realm.getInstance(config);
        }
        return realm;
    }

    public static ForecastApplication getApplication() {
        return application;
    }

}
