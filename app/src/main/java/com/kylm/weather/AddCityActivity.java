package com.kylm.weather;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.kylm.weather.model.CityInfoBean;
import com.kylm.weather.widget.DividerItemDecoration;
import com.kylm.weather.widget.SideLetterBar;

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
    @BindView(R.id.rcv_search_result)
    RecyclerView rcvSearchResult;
    @BindView(R.id.tv_letter_overlay)
    TextView tvLetterOverlay;
    @BindView(R.id.letterbar)
    SideLetterBar letterBar;

    CityRecyclerViewAdapter adapter;
    ArrayList<CityInfoBean> cityList;
    Realm realm;
    SearchStaticRecyclerFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);

        ButterKnife.bind(this);

        searchBar.setHint(getString(R.string.hint_input_city_name));
        searchBar.setOnToggleAnimationListener(this);
        searchBar.setSearchBoxListener(this);
        searchBar.setSearchListener(this);
        searchBar.handleToolbarAnimation(toolbar);

        adapter = new CityRecyclerViewAdapter();

        realm = Realm.getDefaultInstance();
        RealmResults<CityInfoBean> results = realm.where(CityInfoBean.class).findAll();
        Iterator<CityInfoBean> cityInfoBeanIterator = results.iterator();
        cityList = new ArrayList<>();
        cityList.addAll(Lists.newArrayList(cityInfoBeanIterator));

        fragment = SearchStaticRecyclerFragment.newInstance(null);
        searchBar.setExpandedContentFragment(this, fragment);

        rcvCityList.setAdapter(adapter);
        rcvCityList.setLayoutManager(new LinearLayoutManager(this));
        rcvCityList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        letterBar.setOverlay(tvLetterOverlay);
        letterBar.setOnLetterChangedListener(new SideLetterBar.OnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter) {
            }
        });
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
            RealmResults<CityInfoBean> results = realm
                    .where(CityInfoBean.class)
                    .contains("city", s.toString())
                    .or()
                    .contains("fullPinyin", s.toString())
                    .or()
                    .contains("headerPinyin", s.toString())
                    .findAll();
            Iterator<CityInfoBean> cityInfoBeanIterator = results.iterator();
            cityList = new ArrayList<>();
            cityList.addAll(Lists.newArrayList(cityInfoBeanIterator));
        }
        fragment.refresh(cityList);
    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.d(TAG, "afterTextChanged: " + s);
    }

    class CityRecyclerViewAdapter extends RecyclerView.Adapter<CityRecyclerViewAdapter.CityViewHolder> {

        @Override
        public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.city_list_item, null);
            return new CityViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CityViewHolder holder, int position) {
            CityInfoBean city = cityList.get(position);
            holder.tvCity.setText(city.getCity());
            holder.tvCity.setTextColor(Color.BLACK);
        }

        @Override
        public int getItemCount() {
            return cityList == null ? 0 : cityList.size();
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
