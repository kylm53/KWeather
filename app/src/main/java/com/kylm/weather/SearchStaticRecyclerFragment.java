package com.kylm.weather;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kylm.weather.commons.RxBus;
import com.kylm.weather.model.CityInfoBean;
import com.kylm.weather.model.RefreshEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchStaticRecyclerFragment extends Fragment {

    SharedPreferences preference;
    List<CityInfoBean> cities = new ArrayList<>();
    ListViewAdapter adapter;

    public SearchStaticRecyclerFragment() {
        // Required empty public constructor
    }

    public static SearchStaticRecyclerFragment newInstance(ArrayList<CityInfoBean> cities) {
        SearchStaticRecyclerFragment fragment = new SearchStaticRecyclerFragment();
        Bundle data = new Bundle();
        data.putParcelableArrayList("cities", cities);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        preference = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search_result, container, false);
        RecyclerView recyclerView = ((RecyclerView) rootView.findViewById(R.id.search_static_recycler));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new ListViewAdapter(cities);
        adapter.setListOnItemClickListener(new ListOnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Set<String> citySet = preference.getStringSet(MainActivity.KEY_CITY_IDS, null);
                if (citySet == null) {
                    citySet = new HashSet<>();
                }

                citySet.add(cities.get(position).getId());
                preference.edit().putStringSet(MainActivity.KEY_CITY_IDS, citySet).apply();
                RefreshEvent event = new RefreshEvent(RefreshEvent.ADD_CITY);
                event.setCity(cities.get(position));
                RxBus.getDefault().send(event);

                getActivity().finish();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        return rootView;
    }

    public void refresh(ArrayList<CityInfoBean> cities) {
        this.cities.clear();
        this.cities.addAll(cities);
        adapter.notifyDataSetChanged();
    }

    // Define a public click listener interface for items of the v7.RecyclerView which has no OnItemClickListener by default
    public interface ListOnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.ListViewHolder> {
        public List<CityInfoBean> mStringList;

        public ListViewAdapter(List<CityInfoBean> stringList) {
            this.mStringList = stringList;
        }

        private ListOnItemClickListener mOnItemClickListener;

        public void setListOnItemClickListener(ListOnItemClickListener mOnItemClickListener) {
            this.mOnItemClickListener = mOnItemClickListener;
        }

        class ListViewHolder extends RecyclerView.ViewHolder {
            private final TextView mDetailText;

            public ListViewHolder(View itemView) {
                super(itemView);
                mDetailText = (TextView) itemView.findViewById(R.id.tv_city);
            }
        }

        @Override
        public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ListViewHolder(LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.city_list_item, parent, false));
        }

        @Override
        public int getItemCount() {
            return mStringList == null ? 0 : mStringList.size();
        }

        @Override
        public void onBindViewHolder(final ListViewHolder viewHolder, int position) {
            viewHolder.mDetailText.setText(mStringList.get(position).getCity());
            viewHolder.mDetailText.setTextColor(Color.BLACK);

            // Click event called here
            if (mOnItemClickListener != null) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = viewHolder.getLayoutPosition();
                        mOnItemClickListener.onItemClick(viewHolder.itemView, pos);
                    }
                });

                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int pos = viewHolder.getLayoutPosition();
                        mOnItemClickListener.onItemLongClick(viewHolder.itemView, pos);
                        return false;
                    }
                });
            }

        }
    }
}