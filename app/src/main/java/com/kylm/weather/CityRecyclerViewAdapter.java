package com.kylm.weather;

import android.support.v7.widget.RecyclerView;

import com.kylm.weather.model.CityInfoBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Adapter holding a list of city beans of type CityInfoBean. Note that each item must be unique.
 */
public abstract class CityRecyclerViewAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {
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
}