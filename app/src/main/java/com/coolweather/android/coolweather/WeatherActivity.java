package com.coolweather.android.coolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.coolweather.gson.Forecast;
import com.coolweather.android.coolweather.gson.Weather;
import com.coolweather.android.coolweather.util.HttpUtil;
import com.coolweather.android.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView scrollWeather;
    private TextView tvTitleCity;
    private TextView tvUpdateTime;
    private TextView tvDegree;
    private TextView tvWeatherInfo;
    private LinearLayout llForecast;
    private TextView tvAqi;
    private TextView tvPm25;
    private TextView tvComfort;
    private TextView tvCarWash;
    private TextView tvSport;
    private ImageView ivBingPic;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
            由于是 Android5.0 以上才支持的，所以我们加一个判断
            接着我们拿到当前活动的 DecorView，在调用 setSystemUiVisibility() 方法，
            来改变系统 UI 的显示

         */
        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            //这里设置的参数的意思是表示活动的布局会显示在状态栏上面
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //将状态栏设置成透明色
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView(){
        scrollWeather = (ScrollView) findViewById(R.id.scrollview_weather);
        tvTitleCity = (TextView) findViewById(R.id.tv_title_city);
        tvUpdateTime = (TextView) findViewById(R.id.tv_update_time);
        tvDegree = (TextView) findViewById(R.id.tv_degree);
        tvWeatherInfo = (TextView) findViewById(R.id.tv_weather_info);
        llForecast = (LinearLayout) findViewById(R.id.ll_forecast);
        tvAqi = (TextView) findViewById(R.id.tv_aqi);
        tvPm25 = (TextView) findViewById(R.id.tv_pm25);
        tvComfort = (TextView) findViewById(R.id.tv_comfort);
        tvCarWash = (TextView) findViewById(R.id.tv_car_wash);
        tvSport = (TextView) findViewById(R.id.tv_sport);

        ivBingPic = (ImageView) findViewById(R.id.iv_bing_pic);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        //有缓存，直接解析天气数据
        if(!TextUtils.isEmpty(weatherString)){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        }else{
            //无缓存从网络上获取
            String weatherId = getIntent().getStringExtra("weather_id");
            //注意，请求数据的时候先将 ScrollView 隐藏掉，否则空数据的界面看上去很奇怪
            scrollWeather.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }

        String bingPic = prefs.getString("bing_pic",null);
        //从缓存中读取，如果没有，就访问服务器
        if(!TextUtils.isEmpty(bingPic)){
            Glide.with(this).load(bingPic).into(ivBingPic);
        }else{
            loadBingPic();
        }
    }

    /**
     * 从服务端获取背景图片
     */
    private void loadBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic).apply();
                runOnUiThread(() -> Glide.with(WeatherActivity.this).load(bingPic).into(ivBingPic));
            }

            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
    }

    /**
     * 根据天气id从服务器上获取对应的天气信息
     */
    private void requestWeather(String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(() -> {
                    if(weather != null && "ok".equals(weather.getStatus())){
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                        editor.putString("weather",responseText);
                        editor.apply();
                        showWeatherInfo(weather);
                    }else{
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show());
            }
        });
        /*
            注意，在每次请求天气信息的时候也调用一下获取图片的方法，
            这样在每次请求天气的时候就会同时刷新背景图片
         */
        loadBingPic();
    }

    /**
     * 展示天气
     */
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "°C";
        String weatherInfo = weather.now.more.info;
        tvTitleCity.setText(cityName);
        tvUpdateTime.setText(updateTime);
        tvDegree.setText(degree);
        tvWeatherInfo.setText(weatherInfo);
        llForecast.removeAllViews();
        /*
            这里处理每天的天气信息，在循环中动态加载 forecast_item.xml 布局并设置相应的数据，然后添加到
            父布局当中，设置完成后要把 ScrollView 变得可见
         */
        for(Forecast forecast : weather.forecastList){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, llForecast, false);
            TextView tvDate = view.findViewById(R.id.tv_date);
            TextView tvInfo = view.findViewById(R.id.tv_info);
            TextView tvMax = view.findViewById(R.id.tv_max);
            TextView tvMin = view.findViewById(R.id.tv_min);
            tvDate.setText(forecast.date);
            tvInfo.setText(forecast.more.info);
            tvMax.setText(forecast.temperature.max);
            tvMin.setText(forecast.temperature.min);
            llForecast.addView(view);
        }
        if(weather.aqi != null){
            tvAqi.setText(weather.aqi.city.aqi);
            tvPm25.setText(weather.aqi.city.pm25);
        }

        String comfort = "舒适度："+weather.suggestion.comfort.info;
        String carWash = "洗车指数："+weather.suggestion.carWash.info;
        String sport = "运动建议："+weather.suggestion.sport.info;
        tvComfort.setText(comfort);
        tvCarWash.setText(carWash);
        tvSport.setText(sport);
        scrollWeather.setVisibility(View.VISIBLE);
    }
}
