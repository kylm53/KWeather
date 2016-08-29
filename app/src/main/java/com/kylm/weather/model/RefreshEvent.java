package com.kylm.weather.model;

/**
 * Created by ky on 2016/8/22.
 */
public class RefreshEvent {
    public static final int LOCATE_COMPLETE = 0x00;
    public static final int ADD_CITY = 0x01;
    public static final int DELETE_CITY = 0x02;
    public static final int GET_WEATHER = 0x03;

    private int type;

    private Object object;

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

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
