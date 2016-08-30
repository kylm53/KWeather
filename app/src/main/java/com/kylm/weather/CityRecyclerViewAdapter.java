package com.kylm.weather;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kylm.weather.model.CityInfoBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Adapter holding a list of city beans of type CityInfoBean. Note that each item must be unique.
 */
public abstract class CityRecyclerViewAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {
    // Define a public click listener interface for items of the v7.RecyclerView which has no OnItemClickListener by default
    public interface ListOnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private ListOnItemClickListener mOnItemClickListener;

    public ListOnItemClickListener getListOnItemClickListener() {
        return mOnItemClickListener;
    }
    public void setListOnItemClickListener(ListOnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    private ArrayList<CityInfoBean> items = new ArrayList<CityInfoBean>();

    public CityRecyclerViewAdapter() {
        setHasStableIds(true);
    }

    public void add(CityInfoBean city) {
        items.add(city);
        notifyDataSetChanged();
    }

    public void add(int index, CityInfoBean city) {
        items.add(index, city);
        notifyDataSetChanged();
    }

    public void addAll(Collection<? extends CityInfoBean> collection) {
        if (collection != null) {
            items.addAll(collection);
            notifyDataSetChanged();
        }
    }

    public void addAll(CityInfoBean... items) {
        addAll(Arrays.asList(items));
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void remove(CityInfoBean city) {
        items.remove(city);
        notifyDataSetChanged();
    }

    public CityInfoBean getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        onBindItemViewHolder(holder, position);
        setOnItemClickListener(holder, position);
    }

    public abstract void onBindItemViewHolder(VH holder, int position);
    /**
     * 在onBindViewHolder方法中调用设置click listener
     */
    private void setOnItemClickListener(final VH holder, int position) {
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
}