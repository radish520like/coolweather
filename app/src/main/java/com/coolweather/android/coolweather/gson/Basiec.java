package com.coolweather.android.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 *
 */

public class Basiec {

    @SerializedName("city")
    public String cityName;//城市名称
    @SerializedName("id")
    public String weatherId;//城市对应的天气id

    public Update update;

    public class Update{

        @SerializedName("loc")
        public String updateTime;

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public Update getUpdate() {
        return update;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }
}
