package com.kylm.weather;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kylm.weather.commons.DateUtil;
import com.kylm.weather.model.HeWeather;
import com.kylm.weather.presenter.WeatherPresenter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainView<WeatherPresenter> {


    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.tv_weather_content) TextView content;
    @BindView(R.id.tv_city) TextView city;
    @BindView(R.id.tv_condition) TextView condition;
    @BindView(R.id.tv_tmp) TextView currentTemperature;
    @BindView(R.id.tv_tmp_high) TextView highTemperature;
    @BindView(R.id.tv_tmp_low) TextView lowTemperature;
    @BindView(R.id.tv_wind) TextView wind;
    @BindView(R.id.tv_hum) TextView hum;
    @BindView(R.id.rcv_forecast) RecyclerView recyclerViewForcast;

    private WeatherPresenter presenter;
    List<HeWeather.WeatherBean.DailyForecastBean> dailyForecasts;
    ForecastAdapter mForecastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewForcast.setLayoutManager(linearLayoutManager);
//        recyclerViewForcast.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        mForecastAdapter = new ForecastAdapter();
        recyclerViewForcast.setAdapter(mForecastAdapter);
        recyclerViewForcast.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST));

        navigationView.setNavigationItemSelectedListener(this);

        presenter = new WeatherPresenter(this);
        presenter.getWeahter("CN101010100");
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void showWeatherInfo(HeWeather.WeatherBean weather) {
        dailyForecasts = weather.getDaily_forecast();
        mForecastAdapter.notifyDataSetChanged();

        city.setText(weather.getBasic().getCity());
        condition.setText(weather.getNow().getCond().getTxt());
        currentTemperature.setText(weather.getNow().getTmp());
        lowTemperature.setText(weather.getDaily_forecast().get(0).getTmp().getMin() + "°");
        highTemperature.setText(weather.getDaily_forecast().get(0).getTmp().getMax() + "°");
        wind.setText(weather.getNow().getWind().getDir() + " " + weather.getNow().getWind().getSc());
        hum.setText(weather.getNow().getHum() + "%");

        StringBuilder builder = new StringBuilder();

        //basic 城市基本信息
        builder.append(weather.getBasic().getCity())
                .append("-")
                .append(weather.getBasic().getCnty())
                .append("\n")
                .append("更新时间：")
                .append(weather.getBasic().getUpdate().getLoc())
                .append("\n\n");

        //aqi 空气质量指数
        builder.append("空气质量指数:").append(weather.getAqi().getCity().getAqi()).append("\n");
        builder.append("PM2.5:").append(weather.getAqi().getCity().getPm25()).append("\n");
        builder.append("PM10:").append(weather.getAqi().getCity().getPm10()).append("\n");
        builder.append("二氧化硫1小时平均值so2(ug/m³):").append(weather.getAqi().getCity().getSo2()).append("\n");
        builder.append("二氧化氮1小时平均值no2(ug/m³):").append(weather.getAqi().getCity().getSo2()).append("\n");
        builder.append("一氧化碳1小时平均值(ug/m³):").append(weather.getAqi().getCity().getCo()).append("\n");
        builder.append("臭氧1小时平均值(ug/m³):").append(weather.getAqi().getCity().getO3()).append("\n");
        builder.append("空气质量类别:").append(weather.getAqi().getCity().getQlty()).append("\n\n");

        //suggestion 生活指数
        builder.append("穿衣指数:").append(weather.getSuggestion().getDrsg().getBrf()).append("\n");
        builder.append("紫外线指数:").append(weather.getSuggestion().getUv().getBrf()).append("\n");
        builder.append("洗车指数:").append(weather.getSuggestion().getCw().getBrf()).append("\n");
        builder.append("旅游指数:").append(weather.getSuggestion().getTrav().getBrf()).append("\n");
        builder.append("感冒指数:").append(weather.getSuggestion().getFlu().getBrf()).append("\n");
        builder.append("运动指数:").append(weather.getSuggestion().getSport().getBrf()).append("\n\n");

        //now 实况天气
        builder.append("实况天气---").append("\n");
        builder.append("当前温度(摄氏度):").append(weather.getNow().getTmp()).append("\n");
        builder.append("体感温度:").append(weather.getNow().getFl()).append("\n");
        builder.append("风速(Kmph):").append(weather.getNow().getWind().getSpd()).append("\n");
        builder.append("风力等级:").append(weather.getNow().getWind().getSc()).append("\n");
        builder.append("风向(角度):").append(weather.getNow().getWind().getDeg()).append("\n");
        builder.append("风向(方向):").append(weather.getNow().getWind().getDir()).append("\n");
        builder.append("天气状况:").append(weather.getNow().getCond().getTxt()).append("\n");
        builder.append("降雨量(mm):").append(weather.getNow().getPcpn()).append("\n");
        builder.append("湿度(%):").append(weather.getNow().getHum()).append("\n");
        builder.append("气压:").append(weather.getNow().getPres()).append("\n");
        builder.append("能见度(km):").append(weather.getNow().getVis()).append("\n\n");

        //hourly_forecast 每小时天气预报
        builder.append("每小时天气预报---").append("\n");
        builder.append("当地日期和时间:").append(weather.getHourly_forecast().get(0).getDate()).append("\n");
        builder.append("当前温度(摄氏度):").append(weather.getHourly_forecast().get(0).getTmp()).append("\n");
        builder.append("风速(Kmph):").append(weather.getHourly_forecast().get(0).getWind().getSpd()).append("\n");
        builder.append("风力等级:").append(weather.getHourly_forecast().get(0).getWind().getSc()).append("\n");
        builder.append("风向(角度):").append(weather.getHourly_forecast().get(0).getWind().getDeg()).append("\n");
        builder.append("风向(方向):").append(weather.getHourly_forecast().get(0).getWind().getDir()).append("\n");
        builder.append("降水概率:").append(weather.getHourly_forecast().get(0).getPop()).append("\n");
        builder.append("湿度(%):").append(weather.getHourly_forecast().get(0).getHum()).append("\n");
        builder.append("气压:").append(weather.getHourly_forecast().get(0).getPres()).append("\n\n");

        //daily_forecast 天气预报
        builder.append("天气预报---").append("\n");
        List<HeWeather.WeatherBean.DailyForecastBean> dailyForecastList = weather.getDaily_forecast();
        for (HeWeather.WeatherBean.DailyForecastBean dailyForecastBean : dailyForecastList) {
            builder.append("时间：").append(dailyForecastBean.getDate()).append("\n");
            builder.append("日出时间：").append(dailyForecastBean.getAstro().getSr()).append("\n");
            builder.append("日落时间：").append(dailyForecastBean.getAstro().getSs()).append("\n");
            builder.append("白天：").append(dailyForecastBean.getCond().getTxt_d()).append("\n");
            builder.append("夜间：").append(dailyForecastBean.getCond().getTxt_n()).append("\n");
            builder.append("湿度(%)：").append(dailyForecastBean.getHum()).append("\n");
            builder.append("降雨量(mm)：").append(dailyForecastBean.getPcpn()).append("\n");
            builder.append("降水概率：").append(dailyForecastBean.getPop()).append("\n");
            builder.append("气压：").append(dailyForecastBean.getPres()).append("\n");
            builder.append("能见度(km)：").append(dailyForecastBean.getVis()).append("\n");
            builder.append("最高温度(摄氏度)：").append(dailyForecastBean.getTmp().getMax()).append("\n");
            builder.append("最低温度(摄氏度)：").append(dailyForecastBean.getTmp().getMin()).append("\n");
            builder.append("风速(Kmph)：").append(dailyForecastBean.getWind().getSpd()).append("\n");
            builder.append("风力等级：").append(dailyForecastBean.getWind().getSc()).append("\n");
            builder.append("风向(方向)：").append(dailyForecastBean.getWind().getDir()).append("\n\n");
        }
        content.setText(builder.toString());

    }


    @Override
    public void setPresenter(WeatherPresenter presenter) {
    }

    class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

        public ForecastAdapter() {
        }

        @Override
        public ForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.item_forecast, null);
            int screenWidth = getScreenWidth();
            int width = screenWidth / 3;
            view.setLayoutParams(new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ForecastViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ForecastViewHolder holder, int position) {
            HeWeather.WeatherBean.DailyForecastBean forecastBean = dailyForecasts.get(position);
            Date date = DateUtil.str2Date(forecastBean.getDate(), DateUtil.FORMAT_YMD);
            String week = DateUtil.getWeekOfDate(date);
            boolean isToday = DateUtil.isToday(date.getTime(), Calendar.getInstance().getTimeInMillis());
            holder.tvWeek.setText(isToday ? "今天" : week);
            holder.tvDatetime.setText(DateUtil.date2Str(date, "MM/dd"));
            holder.tvTemp.setText(forecastBean.getTmp().getMin() + "°/" + forecastBean.getTmp().getMax() + "°");
        }

        @Override
        public int getItemCount() {
            return dailyForecasts == null ? 0 : dailyForecasts.size();
        }

        class ForecastViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.tv_datetime) TextView tvDatetime;
            @BindView(R.id.tv_week) TextView tvWeek;
            @BindView(R.id.iv_forecast_image) ImageView ivForecast;
            @BindView(R.id.tv_tmp) TextView tvTemp;

            public ForecastViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

    public int getScreenWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }
}
