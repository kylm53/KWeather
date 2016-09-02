package com.kylm.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.google.common.collect.Lists;
import com.kylm.weather.commons.RxBus;
import com.kylm.weather.model.CityInfoBean;
import com.kylm.weather.model.RefreshEvent;
import com.kylm.weather.presenter.WeatherPresenter;
import com.kylm.weather.widget.DividerItemDecoration;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;

import java.util.ArrayList;
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
import rx.Subscription;
import rx.functions.Action1;

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

    ImageButton btnAddCity;

    RecyclerView selectedCity;
    SelectedCityRecyclerAdapter selectedCityRecyclerAdapter;
    RecyclerTouchListener onTouchListener;

    List<CityInfoBean> cities;
    CityPagerAdapter pagerAdapter;
    Realm realm;
    SharedPreferences preference;
    Set<String> cityIds;

    Subscription rxSubscription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        preference = PreferenceManager.getDefaultSharedPreferences(this);

        btnAddCity = (ImageButton) navigationView.getHeaderView(0).findViewById(R.id.iv_add_city);
        btnAddCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCityActivity.class);
                startActivity(intent);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        selectedCity = (RecyclerView) navigationView.getHeaderView(0).findViewById(R.id.rcv_selected_city);
        selectedCityRecyclerAdapter = new SelectedCityRecyclerAdapter();
        selectedCity.setAdapter(selectedCityRecyclerAdapter);
        selectedCity.setLayoutManager(new LinearLayoutManager(this));
        selectedCity.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        onTouchListener = new RecyclerTouchListener(this, selectedCity);
        onTouchListener.setSwipeable(true);
        onTouchListener.setSwipeOptionViews(R.id.delete)
                .setSwipeable(R.id.rowFG, R.id.rowBG, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, int position) {
                        if (viewID == R.id.delete) {
                            // Do something
                            Snackbar.make(selectedCity, "删除 \"" + cities.get(position).getCity() + "\"", Snackbar.LENGTH_SHORT).show();

                            cityIds.remove(cities.get(position).getId());
                            preference.edit()
                                    .putStringSet(KEY_CITY_IDS, cityIds)
                                    .apply();
                            cities.remove(position);
                            refresh();
                        }
                    }
                });
        onTouchListener.setLongClickable(true, new RecyclerTouchListener.OnRowLongClickListener() {
            @Override
            public void onRowLongClicked(int position) {
                if (position != 0) {
                    onTouchListener.openSwipeOptions(position);
                }
            }
        });
        onTouchListener.setClickable(new RecyclerTouchListener.OnRowClickListener() {
            @Override
            public void onRowClicked(int position) {
                viewPager.setCurrentItem(position, true);
                drawer.closeDrawer(GravityCompat.START);
            }

            @Override
            public void onIndependentViewClicked(int independentViewID, int position) {

            }
        });

        selectedCity.addOnItemTouchListener(onTouchListener);


        WeatherPresenter presenter = new WeatherPresenter(null, this);
        presenter.getCondition();
        presenter.getCityList();

        realm = Realm.getDefaultInstance();

        initCities();
        pagerAdapter = new CityPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        indicator.setViewPager(viewPager);

        ForecastApplication.getApplication().setLocationCallback(new ForecastApplication.LocationCallback() {
            @Override
            public void onLocationCallback(BDLocation location) {
                String district = location.getDistrict();
                district = district.substring(0, district.length() - 1);
                RealmResults<CityInfoBean> results = realm.where(CityInfoBean.class)
                        .beginsWith("city", district)
                        .findAll();
                if (results != null && results.size() > 0) {
                    CityInfoBean locatedCity = results.first();
                    cities.set(0, locatedCity);
                    refresh();
//                    ((CityForecastFragment) pagerAdapter.getItem(0)).refresh(locatedCity);
                }
            }
        });
        ForecastApplication.getApplication().mLocationClient.requestLocation();
        //刷新界面
        rxSubscription = RxBus.getDefault()
                .toObservable(RefreshEvent.class)
                .subscribe(new Action1<RefreshEvent>() {
                    @Override
                    public void call(RefreshEvent refreshEvent) {
                        switch (refreshEvent.getType()) {
                            case RefreshEvent.ADD_CITY:
                                CityInfoBean cityNew = (CityInfoBean) refreshEvent.getObject();
                                //不是已添加城市或定位城市
                                if (!(cities.contains(cityNew) || cityNew.getId() == null)) {
                                    addCity(cityNew);
                                    refresh();
                                }
                                int cityIndex;
                                //定位城市
                                if (cityNew.getId() == null) {
                                    cityIndex  = 0;
                                } else {
                                    cityIndex = cities.indexOf(cityNew);
                                }

                                viewPager.setCurrentItem(cityIndex, true);
                                break;
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        System.out.println(throwable.getMessage());
                    }
                });
    }

    private void addCity(CityInfoBean city) {
        Set<String> citySet = preference.getStringSet(MainActivity.KEY_CITY_IDS, null);
        if (citySet == null) {
            citySet = new HashSet<>();
        }

        citySet.add(city.getId());
        preference.edit().putStringSet(MainActivity.KEY_CITY_IDS, citySet).apply();
        cities.add(city);
    }

    public void refresh() {
        pagerAdapter.notifyDataSetChanged();
        selectedCityRecyclerAdapter.notifyDataSetChanged();
        indicator.setViewPager(viewPager);
    }

    private void initCities() {
        cities = new ArrayList<>();
        cities.add(new CityInfoBean("正在定位", null));
        cityIds = preference.getStringSet(KEY_CITY_IDS, null);
        if (cityIds != null && cityIds.size() > 0) {
            Iterator<String> iterator = cityIds.iterator();
            RealmQuery<CityInfoBean> query = realm.where(CityInfoBean.class);
            boolean isFirst = true;
            while (iterator.hasNext()) {
                String id = iterator.next();
                if (!isFirst) {
                    query.or();
                } else {
                    isFirst = false;
                }
                query.equalTo("id", id);
            }
            RealmResults<CityInfoBean> results = query.findAll();
            Iterator<CityInfoBean> cityInfoBeanIterator = results.iterator();
            cities.addAll(Lists.newArrayList(cityInfoBeanIterator));
        }
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
//        if (id == R.id.action_add) {
//            return true;
//        }

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
        if (!rxSubscription.isUnsubscribed()) {
            rxSubscription.unsubscribe();
        }
    }

    class CityPagerAdapter extends FragmentPagerAdapter {

        FragmentManager mFragmentManager;

        public CityPagerAdapter(FragmentManager fm) {
            super(fm);
            this.mFragmentManager = fm;
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

        public int getItemPosition(Object item) {
            CityForecastFragment fragment = (CityForecastFragment)item;

            CityInfoBean cityInfo = fragment.getCityInfo();
            int position = cities.indexOf(cityInfo);

            if (position >= 0) {
                return position;
            } else {
                return POSITION_NONE;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            List<Fragment> fragmentsList = mFragmentManager.getFragments();
            if (fragmentsList != null && position <= (fragmentsList.size() - 1)) {
                CityForecastFragment fragment = (CityForecastFragment) fragmentsList.get(position);
                CityInfoBean city = cities.get(position);
                //If the current data of the fragment changed, set the new data
                if (!city.equals(fragment.getCityInfo())) {
                    fragment.refresh(city);
                }
            } else {
                //No fragment instance available for this index, create a new fragment by calling getItem() and show the data.
            }

            return super.instantiateItem(container, position);
        }
    }


    class SelectedCityRecyclerAdapter extends RecyclerView.Adapter<SelectedCityRecyclerAdapter.CityViewHolder> {

        @Override
        public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.seleced_city_row, null);
            return new CityViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CityViewHolder holder, int position) {
            CityInfoBean city = cities.get(position);
            holder.tvCity.setText(city.getCity());
            if (position == 0) {
                Drawable drawable = getResources().getDrawable(R.drawable.ic_locate);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
                holder.tvCity.setCompoundDrawables(drawable, null, null, null);
            } else {
                holder.tvCity.setCompoundDrawables(null, null, null, null);
            }
        }

        @Override
        public int getItemCount() {
            return cities == null ? 0 : cities.size();
        }

        class CityViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tv_city)
            TextView tvCity;

            public CityViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
