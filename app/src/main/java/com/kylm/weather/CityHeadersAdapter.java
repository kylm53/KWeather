package com.kylm.weather;

import android.content.Context;
import android.graphics.Color;
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

public class CityHeadersAdapter extends CityRecyclerViewAdapter<CityHeadersAdapter.CityViewHolder>
        implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    // Define a public click listener interface for items of the v7.RecyclerView which has no OnItemClickListener by default
    public interface ListOnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private ListOnItemClickListener mOnItemClickListener;
    public void setListOnItemClickListener(ListOnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    private HashMap<String, Integer> letterIndex = new HashMap<>();
    private int headerHeigthInPix = 0;
    private boolean showIndexHeader = true;
    private Context context;

    public CityHeadersAdapter(Context context, Collection<? extends CityInfoBean> cities, boolean showHeader) {
        addAll(cities);
        this.showIndexHeader = showHeader;
        this.context = context;

        letterIndex.put("定位", 0);
        letterIndex.put("热门", 1);
        for (int index = 0; index < getItemCount(); index++) {
            char letter = getItem(index).getHeaderPinyin().charAt(0);
            String keyLetter = String.valueOf(letter).toUpperCase();
            String previousLetter = index >= 1 ? String.valueOf(getItem(index - 1).getHeaderPinyin().charAt(0)).toUpperCase() : "";
            if (!TextUtils.equals(keyLetter, previousLetter)) {
                letterIndex.put(keyLetter, index + 2);
            }

            headerHeigthInPix = showHeader ? Utils.dp2Px(context, 26) : 0;
        }
    }

    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.city_list_item, null);
        return new CityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CityViewHolder holder, int position) {
        CityInfoBean city = getItem(position);
        holder.tvCity.setText(city.getCity());
        holder.tvCity.setTextColor(Color.BLACK);

        // Click event called here
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, pos);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemLongClick(holder.itemView, pos);
                    return false;
                }
            });
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

    public int getLetterPosition(String letter) {
        Integer integer = letterIndex.get(letter);
        return integer == null ? -1 : integer;
    }

    public int getHeaderHeightPix() {
        return headerHeigthInPix;
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