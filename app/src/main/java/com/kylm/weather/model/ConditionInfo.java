package com.kylm.weather.model;

import java.util.List;

/**
 * Created by kangyi on 2016/7/29.
 */
public class ConditionInfo {

    /**
     * status : ok
     * cond_info : [CondInfoBeann, ...]
     */

    private String status;
    /**
     * code : 100
     * txt : 晴
     * txt_en : Sunny/Clear
     * icon : http://files.heweather.com/cond_icon/100.png
     */

    private List<ConditionInfoBean> cond_info;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ConditionInfoBean> getCond_info() {
        return cond_info;
    }

    public void setCond_info(List<ConditionInfoBean> cond_info) {
        this.cond_info = cond_info;
    }

}
