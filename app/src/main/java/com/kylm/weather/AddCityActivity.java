package com.kylm.weather;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.kylm.weather.commons.RxBus;
import com.kylm.weather.model.CityInfoBean;
import com.kylm.weather.model.RefreshEvent;
import com.kylm.weather.widget.DividerItemDecoration;
import com.kylm.weather.widget.SideLetterBar;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import xyz.sahildave.widget.SearchViewLayout;

public class AddCityActivity extends AppCompatActivity implements SearchViewLayout.SearchListener,
        SearchViewLayout.OnToggleAnimationListener,
        SearchViewLayout.SearchBoxListener {

    public static final String TAG = AddCityActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.search_view_container)
    SearchViewLayout searchBar;
    @BindView(R.id.rcv_city_list)
    RecyclerView rcvCityList;
    @BindView(R.id.tv_letter_overlay)
    TextView tvLetterOverlay;
    @BindView(R.id.letterbar)
    SideLetterBar letterBar;

    CityHeadersAdapter cityAdapter;
    PopularCityAdapter popularCityAdapter;
    Realm realm;
    SearchStaticRecyclerFragment fragment;

    SharedPreferences preference;
    boolean move = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);

        ButterKnife.bind(this);

        preference = PreferenceManager.getDefaultSharedPreferences(this);

        searchBar.setHint(getString(R.string.hint_input_city_name));
        searchBar.setOnToggleAnimationListener(this);
        searchBar.setSearchBoxListener(this);
        searchBar.setSearchListener(this);
        searchBar.handleToolbarAnimation(toolbar);

        fragment = SearchStaticRecyclerFragment.newInstance(null);
        searchBar.setExpandedContentFragment(this, fragment);

        realm = Realm.getDefaultInstance();
        RealmResults<CityInfoBean> results = realm.where(CityInfoBean.class).findAllSorted("fullPinyin");
        Iterator<CityInfoBean> cityInfoBeanIterator = results.iterator();
        cityAdapter = new CityHeadersAdapter(this, Lists.newArrayList(cityInfoBeanIterator), true);
        cityAdapter.add(0, new CityInfoBean(getString(R.string.city_located), null));
        cityAdapter.add(1, new CityInfoBean(getString(R.string.city_popular), "0"));
        cityAdapter.setListOnItemClickListener(new CityRecyclerViewAdapter.ListOnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                addCity(cityAdapter, position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        popularCityAdapter = new PopularCityAdapter(this);
        popularCityAdapter.setListOnItemClickListener(new CityRecyclerViewAdapter.ListOnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                addCity(popularCityAdapter, position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        cityAdapter.setPopularCityAdapter(popularCityAdapter);

        rcvCityList.setAdapter(cityAdapter);


        rcvCityList.setLayoutManager(new LinearLayoutManager(this));
        rcvCityList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        // Add the sticky headers decoration
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(cityAdapter);
        rcvCityList.addItemDecoration(headersDecor);
        rcvCityList.addOnScrollListener(new RecyclerViewListener());

        letterBar.setOverlay(tvLetterOverlay);
        letterBar.setOnLetterChangedListener(new SideLetterBar.OnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter) {
                int position = cityAdapter.getLetterPosition(letter);
                moveToPosition(position);
            }
        });
    }

    private void addCity(CityRecyclerViewAdapter adapter, int position) {
        String cityId = adapter.getItem(position).getId();
        RefreshEvent event = new RefreshEvent();
        //根据id查询城市
        RealmResults<CityInfoBean> results = realm.where(CityInfoBean.class)
                .equalTo("id", cityId)
                .findAll();

        //具体城市
        if (results.size() > 0) {
            event.setObject(results.first());
        } else {//定位
            event.setObject(adapter.getItem(position));
        }

        event.setType(RefreshEvent.ADD_CITY);
        RxBus.getDefault().send(event);

        finish();
    }

    private void moveToPosition(int position) {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) rcvCityList.getLayoutManager();
        //先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
        int firstItem = linearLayoutManager.findFirstVisibleItemPosition();
        int lastItem = linearLayoutManager.findLastVisibleItemPosition();
        //然后区分情况
        if (position <= firstItem) {
            //当要置顶的项在当前显示的第一个项的前面时
            rcvCityList.scrollToPosition(position);
        } else if (position <= lastItem) {
            //当要置顶的项已经在屏幕上显示时
            int top = rcvCityList.getChildAt(position - firstItem).getTop();
            rcvCityList.scrollBy(0, top - cityAdapter.getHeaderHeightPix());
        } else {
            //当要置顶的项在当前显示的最后一项的后面时
            rcvCityList.scrollToPosition(position);
            //这里这个变量是用在RecyclerView滚动监听里面的
            move = true;
            mIndex = position;
        }
    }


    @Override
    public void onFinished(String searchKeyword) {

    }

    @Override
    public void onStart(boolean expanding) {
//        if (expanding) {
//            letterBar.setVisibility(View.GONE);
//            rcvCityList.setVisibility(View.GONE);
//            rcvSearchResult.setVisibility(View.VISIBLE);
//        } else {
//            letterBar.setVisibility(View.VISIBLE);
//            rcvCityList.setVisibility(View.VISIBLE);
//            rcvSearchResult.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onFinish(boolean expanded) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        Log.d(TAG, "beforeTextChanged: " + s + "," + start + "," + count + "," + after);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.d(TAG, "onTextChanged: " + s + "," + start + "," + before + "," + count);
        ArrayList<CityInfoBean> cityList = new ArrayList<>();
        if (!TextUtils.isEmpty(s)) {
            //先按中文和首字母缩写匹配
            RealmResults<CityInfoBean> results = realm
                    .where(CityInfoBean.class)
                    .contains("city", s.toString())
                    .or()
                    .contains("headerPinyin", s.toString())
                    .findAll();
            cityList.addAll(Lists.newArrayList(results.iterator()));

            //再按全拼音匹配
            results = realm.where(CityInfoBean.class)
                    .contains("fullPinyin", s.toString())
                    .not()
                    .contains("headerPinyin", s.toString())
                    .findAll();
            cityList.addAll(Lists.newArrayList(results.iterator()));

        }
        fragment.refresh(cityList);
    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.d(TAG, "afterTextChanged: " + s);
    }

    int mIndex = 0;
    class RecyclerViewListener extends RecyclerView.OnScrollListener{
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            //在这里进行第二次滚动（最后的100米！）
            if (move ){
                move = false;
                //获取要置顶的项在当前屏幕的位置，mIndex是记录的要置顶项在RecyclerView中的位置
                int firstVisibleIndex = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                int n = mIndex - firstVisibleIndex;
                if ( 0 <= n && n < recyclerView.getChildCount()){
                    //获取要置顶的项顶部离RecyclerView顶部的距离
                    int top = recyclerView.getChildAt(n).getTop();
                    //最后的移动
                    recyclerView.scrollBy(0, top - cityAdapter.getHeaderHeightPix());
                }
            }
        }
    }
}
