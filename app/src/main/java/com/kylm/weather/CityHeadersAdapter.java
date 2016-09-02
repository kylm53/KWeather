package com.kylm.weather;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kylm.weather.commons.Utils;
import com.kylm.weather.model.CityInfoBean;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.Collection;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CityHeadersAdapter extends CityRecyclerViewAdapter<RecyclerView.ViewHolder>
        implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    private PopularCityAdapter popularCityAdapter;

    private HashMap<String, Integer> letterIndex = new HashMap<>();
    private int headerHeightInPix = 0;
    private boolean showIndexHeader = true;
    private Context context;

    public CityHeadersAdapter(Context context, Collection<? extends CityInfoBean> cities, boolean showHeader) {
        if (cities != null) {
            addAll(cities);
        }
        this.showIndexHeader = showHeader;
        this.context = context;

        letterIndex.put(context.getString(R.string.locate), 0);
        letterIndex.put(context.getString(R.string.popular), 1);
        for (int index = 0; index < getItemCount(); index++) {
            char letter = getItem(index).getHeaderPinyin().charAt(0);
            String keyLetter = String.valueOf(letter).toUpperCase();
            String previousLetter = index >= 1 ? String.valueOf(getItem(index - 1).getHeaderPinyin().charAt(0)).toUpperCase() : "";
            if (!TextUtils.equals(keyLetter, previousLetter)) {
                letterIndex.put(keyLetter, index + 2);
            }

            headerHeightInPix = showHeader ? Utils.dp2Px(context, 26) : 0;
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0:
            case 2:
                view = inflater.inflate(R.layout.city_list_item, parent, false);
                holder = new CityViewHolder(view);
                break;
            case 1:

                view = inflater.inflate(R.layout.city_popluar, parent, false);
                holder = new PopularCityViewHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case 0:
            case 2:
                CityViewHolder cityViewHolder = (CityViewHolder) holder;
                CityInfoBean city = getItem(position);
                StringBuilder builder = new StringBuilder();
                builder.append(city.getCity());
                if (position > 1) {
                    builder.append(", ")
                            .append(city.getProv()).append(", ")
                            .append(city.getCnty());
                }

                //定位
                if (position == 0 && city.getId() == null) {
                    Drawable drawable = context.getResources().getDrawable(R.drawable.ic_locate);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
                    cityViewHolder.tvCity.setCompoundDrawables(drawable, null, null, null);
                } else {
                    cityViewHolder.tvCity.setCompoundDrawables(null, null, null, null);
                }

                cityViewHolder.tvCity.setText(builder.toString());

                break;
            case 1:
                break;
        }
    }


    @Override
    public long getHeaderId(int position) {
        if (showIndexHeader) {
            if (position == 0) {
                return 0;
            } else if (position == 1) {
                return 1;
            } else {
                return getItem(position).getHeaderPinyin().charAt(0);
            }
        }
        //不显示index header
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_header, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;
        String header;
        if (position <= 1) {
            header = getItem(position).getCity();
        } else {
            header = String.valueOf(getItem(position).getHeaderPinyin().charAt(0)).toUpperCase();
        }
        textView.setText(header);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && getItem(position).getCity().equalsIgnoreCase(
                context.getString(R.string.city_located))) {
            return 0;
        } else if (position == 1 && getItem(position).getCity().equalsIgnoreCase(
                context.getString(R.string.city_popular))) {
            return 1;
        } else {
            return 2;
        }

    }

    public int getLetterPosition(String letter) {
        Integer integer = letterIndex.get(letter);
        return integer == null ? -1 : integer;
    }

    public int getHeaderHeightPix() {
        return headerHeightInPix;
    }

    /**
     * 设置热门城市的adapter
     */
    public void setPopularCityAdapter(PopularCityAdapter adapter) {
        popularCityAdapter = adapter;
    }

    class CityViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_city)
        TextView tvCity;

        public CityViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class PopularCityViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rcv_city_popular)
        RecyclerView rcvPopluarCity;

        public PopularCityViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            rcvPopluarCity.setAdapter(popularCityAdapter);
        }
    }

    //popular city

}