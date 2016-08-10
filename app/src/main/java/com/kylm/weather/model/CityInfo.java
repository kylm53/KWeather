package com.kylm.weather.model;

import java.util.List;

/**
 * Created by kanyi on 2016/7/28.
 */
public class CityInfo {

    /**
     * city_info : [CityInfoBean,......]
     * status : ok
     */

    private String status;

    private List<CityInfoBean> city_info;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CityInfoBean> getCity_info() {
        return city_info;
    }

    public void setCity_info(List<CityInfoBean> city_info) {
        this.city_info = city_info;
    }

}
