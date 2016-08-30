package com.kylm.weather;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.kylm.weather.commons.RxBus;
import com.kylm.weather.model.CityInfoBean;
import com.kylm.weather.model.RefreshEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SearchStaticRecyclerFragment extends Fragment {

    SharedPreferences preference;
    CityHeadersAdapter adapter;

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

        adapter = new CityHeadersAdapter(getActivity(), null, false);
        adapter.setListOnItemClickListener(new CityHeadersAdapter.ListOnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Set<String> citySet = preference.getStringSet(MainActivity.KEY_CITY_IDS, null);
                if (citySet == null) {
                    citySet = new HashSet<>();
                }

                citySet.add(adapter.getItem(position).getId());
                preference.edit().putStringSet(MainActivity.KEY_CITY_IDS, citySet).apply();
                RefreshEvent event = new RefreshEvent(RefreshEvent.ADD_CITY);
                event.setObject(adapter.getItem(position));
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
        adapter.clear();
        adapter.addAll(cities);
        adapter.notifyDataSetChanged();
    }
}