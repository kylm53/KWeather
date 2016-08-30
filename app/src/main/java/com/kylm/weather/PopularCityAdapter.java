package com.kylm.weather;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kylm.weather.model.CityInfoBean;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kangyi on 2016/8/30.
 */
public class PopularCityAdapter extends CityRecyclerViewAdapter<PopularCityAdapter.PopularViewHolder> {

    private Context context;
    public PopularCityAdapter(Context context) {
        this.context = context;

        add(new CityInfoBean("北京", "CN101010100"));
        add(new CityInfoBean("上海", "CN101020100"));
        add(new CityInfoBean("广州", "CN101280101"));
        add(new CityInfoBean("深圳", "CN101280601"));
        add(new CityInfoBean("杭州", "CN101210101"));
        add(new CityInfoBean("天津", "CN101030100"));
        add(new CityInfoBean("重庆", "CN101040100"));
        add(new CityInfoBean("南京", "CN101190101"));
        add(new CityInfoBean("沈阳", "CN101070101"));
        add(new CityInfoBean("武汉", "CN101200101"));
        add(new CityInfoBean("长沙", "CN101250101"));
        add(new CityInfoBean("长春", "CN101060101"));
    }
    @Override
    public PopularViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.city_list_item, parent, false);
        return new PopularViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(PopularViewHolder holder, int position) {
        final CityInfoBean city = getItem(position);
        holder.tvCity.setText(city.getCity());
    }

    class PopularViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_city)
        TextView tvCity;

        public PopularViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            tvCity.setGravity(Gravity.CENTER);
        }
    }
}
