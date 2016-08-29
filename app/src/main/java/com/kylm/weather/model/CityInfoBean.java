package com.kylm.weather.model;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;

public class CityInfoBean extends RealmObject implements Parcelable {
    /**
     * city : 南子岛
     * cnty : 中国
     * id : CN101310230
     * lat : 11.26
     * lon : 114.20
     * prov : 海南
     */
    private String city;
    private String cnty;
    private String id;
    private String lat;
    private String lon;
    private String prov;
    private String fullPinyin;
    private String headerPinyin;

    public CityInfoBean() {

    }

    public CityInfoBean(String city) {
        this.city = city;
    }

    protected CityInfoBean(Parcel in) {
        city = in.readString();
        cnty = in.readString();
        id = in.readString();
        lat = in.readString();
        lon = in.readString();
        prov = in.readString();
        fullPinyin = in.readString();
        headerPinyin = in.readString();
    }

    public static final Creator<CityInfoBean> CREATOR = new Creator<CityInfoBean>() {
        @Override
        public CityInfoBean createFromParcel(Parcel in) {
            return new CityInfoBean(in);
        }

        @Override
        public CityInfoBean[] newArray(int size) {
            return new CityInfoBean[size];
        }
    };

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCnty() {
        return cnty;
    }

    public void setCnty(String cnty) {
        this.cnty = cnty;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public String getFullPinyin() {
        return fullPinyin;
    }

    public void setFullPinyin(String fullPinyin) {
        this.fullPinyin = fullPinyin;
    }

    public String getHeaderPinyin() {
        return headerPinyin;
    }

    public void setHeaderPinyin(String headerPinyin) {
        this.headerPinyin = headerPinyin;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(city);
        dest.writeString(cnty);
        dest.writeString(id);
        dest.writeString(lat);
        dest.writeString(lon);
        dest.writeString(prov);
        dest.writeString(fullPinyin);
        dest.writeString(headerPinyin);
    }
}