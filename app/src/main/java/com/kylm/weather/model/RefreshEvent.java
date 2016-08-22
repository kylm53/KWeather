package com.kylm.weather.model;

/**
 * Created by ky on 2016/8/22.
 */
public class RefreshEvent {
    public static final int ADD_CITY = 0x00;
    public static final int DELETE_CITY = 0x01;
    public static final int GET_WEATHER = 0x02;

    private int type;

    private CityInfoBean city;

    public RefreshEvent() {

    }

    public RefreshEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public CityInfoBean getCity() {
        return city;
    }

    public void setCity(CityInfoBean city) {
        this.city = city;
    }
}
