package com.coolweather.android.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * 城市
 */

public class City extends DataSupport {

    private int id;
    private String cityName;//城市名字
    private int cityCode;//城市代号
    private int provinceId;//当前城市所属省份的id

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", cityName='" + cityName + '\'' +
                ", cityCode=" + cityCode +
                ", provinceId=" + provinceId +
                '}';
    }
}
