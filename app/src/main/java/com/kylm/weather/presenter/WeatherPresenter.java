package com.kylm.weather.presenter;

import com.kylm.weather.MainView;
import com.kylm.weather.commons.APIs;
import com.kylm.weather.model.CityInfo;
import com.kylm.weather.model.CityInfoBean;
import com.kylm.weather.model.ConditionInfo;
import com.kylm.weather.model.ConditionInfoBean;
import com.kylm.weather.model.HeWeather;

import java.util.List;

import io.realm.Realm;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by kangyi on 2016/7/29.
 */
public class WeatherPresenter {
    private MainView view;

    public WeatherPresenter(MainView view) {
        this.view = view;
    }

    public void getCityList() {
        Realm realm = Realm.getDefaultInstance();
        if (realm.where(CityInfoBean.class).findAll().isEmpty()) {
            Observable<CityInfo> cityInfoObservable = APIs.service.getCityList(APIs.TYPE_ALL_CHINA, APIs.KEY);
            cityInfoObservable.subscribeOn(Schedulers.io())
                    .subscribe(new Action1<CityInfo>() {
                        @Override
                        public void call(CityInfo cityInfo) {
                            //realm 在创建线程使用
                            Realm realm = Realm.getDefaultInstance();
                            List<CityInfoBean> cities = cityInfo.getCity_info();
                            realm.beginTransaction();
                            for (CityInfoBean city : cities) {
                                System.out.println(city.getId() + ":" +city.getCity());

                                realm.copyToRealm(city);
                            }
                            realm.commitTransaction();
                            realm.close();
                        }
                    });
        }
        realm.close();
    }

    public void getCondition() {
        Realm realm = Realm.getDefaultInstance();

        if (realm.where(ConditionInfoBean.class).findAll().isEmpty()) {
            Observable<ConditionInfo> conditionInfoObservable = APIs.service.getCondition(APIs.TYPE_CONDITION_ALL_CONDITONS, APIs.KEY);
            conditionInfoObservable.subscribeOn(Schedulers.io())
                    .subscribe(new Action1<ConditionInfo>() {
                        @Override
                        public void call(ConditionInfo conditionInfo) {

                            //realm 在创建线程使用
                            Realm realm = Realm.getDefaultInstance();
                            List<ConditionInfoBean> conditions = conditionInfo.getCond_info();
                            realm.beginTransaction();
                            for (ConditionInfoBean condition : conditions) {
                                System.out.println(condition.getCode() + ":"
                                        + condition.getTxt() + "\n"
                                        + condition.getIcon());

                                realm.copyToRealm(condition);
                            }
                            realm.commitTransaction();
                            realm.close();
                        }
                    });
        }
        realm.close();

    }

    public void getWeahter(String cityId) {
        Observable<HeWeather> heWeatherObservable = APIs.service.getWeather(cityId, APIs.KEY);
        heWeatherObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HeWeather>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(HeWeather weather) {
                        if (weather != null) {
                            HeWeather.WeatherBean weatherInfo = weather.getWeather().get(0);
                            if (APIs.STATUS_OK.equalsIgnoreCase(weatherInfo.getStatus())) {
                                if (view != null) {
                                    view.showWeatherInfo(weatherInfo);
                                }
                            }
                        }
                    }
                });
    }

}
