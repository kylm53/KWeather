package com.kylm.weather;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.kylm.weather.model.CityInfoBean;
import com.kylm.weather.presenter.WeatherPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.viewpager_cities)
    ViewPager viewPager;
    @BindView(R.id.indicator)
    CircleIndicator indicator;

    List<CityInfoBean> cities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.getBackground().setAlpha(0);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        WeatherPresenter presenter = new WeatherPresenter(null);
        presenter.getCondition();

        //test
        cities = new ArrayList<>();
        CityInfoBean bean = new CityInfoBean();
        bean.setCity("北京");
        bean.setCnty("中国");
        bean.setId("CN101010100");
        bean.setLat("");
        bean.setLon("");
        bean.setProv("北京");
        cities.add(bean);

        CityInfoBean bean2 = new CityInfoBean();
        bean2.setCity("海淀");
        bean2.setCnty("中国");
        bean2.setId("CN101010200");
        bean2.setLat("");
        bean2.setLon("");
        bean2.setProv("北京");
        cities.add(bean2);
        /////////////////////////
        viewPager.setAdapter(new CityAdapter(getSupportFragmentManager()));
        indicator.setViewPager(viewPager);
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


    class CityAdapter extends FragmentPagerAdapter {

        public CityAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return cities == null ? 0 : cities.size();
        }

        @Override
        public Fragment getItem(int position) {
            CityInfoBean cityInfoBean = cities.get(position);
            return CityForecastFragment.newInstance(cityInfoBean);
        }
    }

}
