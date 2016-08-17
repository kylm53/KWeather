package com.kylm.weather;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.kylm.weather.model.CityInfoBean;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class AddCityActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener {

    @BindView(R.id.searchBar)
    MaterialSearchBar searchBar;
    @BindView(R.id.rcv_city_list)
    RecyclerView rcvCityList;

    CityRecyclerViewAdapter adapter;
    List<CityInfoBean> cityList;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);

        ButterKnife.bind(this);

        searchBar.setOnSearchActionListener(this);
        adapter = new CityRecyclerViewAdapter();

        realm = Realm.getDefaultInstance();
        RealmResults<CityInfoBean>  results = realm.where(CityInfoBean.class).findAll();
        Iterator<CityInfoBean> cityInfoBeanIterator = results.iterator();
        cityList = new ArrayList<>();
        cityList.addAll(Lists.newArrayList(cityInfoBeanIterator));

        rcvCityList.setAdapter(adapter);
        rcvCityList.setLayoutManager(new LinearLayoutManager(this));
        rcvCityList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {

    }

    @Override
    public void onSearchConfirmed(CharSequence text) {

    }

    @Override
    public void onSpeechIconSelected() {

    }

    @Override
    public boolean onSearchRequested() {
        return super.onSearchRequested();
    }

    @Override
    public boolean onSearchRequested(SearchEvent searchEvent) {
        return super.onSearchRequested(searchEvent);
    }


    class CityRecyclerViewAdapter extends RecyclerView.Adapter<CityRecyclerViewAdapter.CityViewHolder> {

        @Override
        public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.seleced_city_row, null);
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
            @BindView(android.R.id.text1)
            TextView tvCity;

            public CityViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

}
