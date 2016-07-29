package com.kylm.weather;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.kylm.weather.model.HeWeather;
import com.kylm.weather.presenter.WeatherPresenter;

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

    private WeatherPresenter presenter;

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
        StringBuilder builder = new StringBuilder();
        builder.append(weather.getBasic().getCity())
                .append("-")
                .append(weather.getBasic().getCnty())
                .append("\n");

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
}
