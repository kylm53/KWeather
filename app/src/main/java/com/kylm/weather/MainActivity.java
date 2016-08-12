package com.kylm.weather;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.baidu.location.BDLocation;
import com.google.common.collect.Lists;
import com.kylm.weather.model.CityInfoBean;
import com.kylm.weather.presenter.WeatherPresenter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String KEY_CITY_IDS = "cityIds";

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
    CityAdapter pagerAdapter;
    Realm realm;

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
        presenter.getCityList();

        //test
        final Set<String> citySet = new HashSet<>();
        citySet.add("CN101010100");
        citySet.add("CN101010200");

        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putStringSet(KEY_CITY_IDS, citySet)
                .commit();
        /////////////////////////
        initCities();
        pagerAdapter = new CityAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        indicator.setViewPager(viewPager);

        ((ForecastApplication)getApplication()).setLocationCallback(new ForecastApplication.LocationCallback() {
            @Override
            public void onLocationCallback(BDLocation location) {
                String district = location.getDistrict();
                district = district.substring(0, district.length() - 1);
                RealmResults<CityInfoBean> results = realm.where(CityInfoBean.class)
                        .beginsWith("city", district)
                        .findAll();
                if (results.size() > 0) {
                    CityInfoBean locatedCity = results.first();
                    cities.set(0, locatedCity);
//                    pagerAdapter.notifyDataSetChanged();
                    ((CityForecastFragment) pagerAdapter.getItem(0)).refresh(locatedCity);
                }
            }
        });
    }

    private void initCities() {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> cityIds = preference.getStringSet(KEY_CITY_IDS, null);
        Iterator<String> iterator = cityIds.iterator();
        realm = Realm.getDefaultInstance();
        RealmQuery<CityInfoBean> query = realm.where(CityInfoBean.class);
        while (iterator.hasNext()) {
            String id = iterator.next();
            query.equalTo("id", id).or();
        }
        RealmResults<CityInfoBean> results = query.findAll();
        Iterator<CityInfoBean> cityInfoBeanIterator = results.iterator();
        cities = Lists.newArrayList(cityInfoBeanIterator);
        cities.add(0, new CityInfoBean());
//        pagerAdapter.notifyDataSetChanged();
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
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
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
