package com.kylm.weather;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kylm.weather.commons.DateUtil;
import com.kylm.weather.commons.Utils;
import com.kylm.weather.model.CityInfoBean;
import com.kylm.weather.model.ConditionInfoBean;
import com.kylm.weather.model.HeWeather;
import com.kylm.weather.presenter.WeatherPresenter;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CityForecastFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CityForecastFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CityForecastFragment extends Fragment implements MainView<WeatherPresenter> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CITY_INFO = "city_info";

    // TODO: Rename and change types of parameters
    private CityInfoBean cityInfo;

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.tv_city) TextView city;
    @BindView(R.id.iv_condition) ImageView conditionIcon;
    @BindView(R.id.tv_condition) TextView condition;
    @BindView(R.id.tv_tmp) TextView currentTemperature;
    @BindView(R.id.tv_tmp_high) TextView highTemperature;
    @BindView(R.id.tv_tmp_low) TextView lowTemperature;
    @BindView(R.id.tv_wind) TextView wind;
    @BindView(R.id.tv_hum) TextView hum;
    @BindView(R.id.rcv_forecast) RecyclerView recyclerViewForcast;

    private WeatherPresenter presenter;
    private HeWeather.WeatherBean weather;
    List<HeWeather.WeatherBean.DailyForecastBean> dailyForecasts;
    ForecastAdapter mForecastAdapter;


    public CityForecastFragment() {
        // Required empty public constructor
    }

    public CityInfoBean getCityInfo() {
        return cityInfo;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param city
     * @return A new instance of fragment CityForecastFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CityForecastFragment newInstance(CityInfoBean city) {
        CityForecastFragment fragment = new CityForecastFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CITY_INFO, city);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cityInfo = getArguments().getParcelable(ARG_CITY_INFO);
            presenter = new WeatherPresenter(this, getContext());
            mForecastAdapter = new ForecastAdapter();
            refresh(cityInfo);
        }
    }

    public void refresh(CityInfoBean city) {
        if ((cityInfo != city)) {
            cityInfo = city;
        }
        String cityId = cityInfo.getId();
        if (!TextUtils.isEmpty(cityId)) {
            presenter.getWeahter(cityId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_city_forecast, container, false);
        ButterKnife.bind(this, view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewForcast.setLayoutManager(linearLayoutManager);
//        recyclerViewForcast.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));

        recyclerViewForcast.setAdapter(mForecastAdapter);
        recyclerViewForcast.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL_LIST));

        if (weather != null) {
            setWeather(weather);
        }
//          else {
//            refresh(cityInfo);
//        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
//        else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void setWeather(HeWeather.WeatherBean weather) {
        loadIcon(weather.getNow().getCond().getCode(), conditionIcon, R.drawable.unknown);
        city.setText(weather.getBasic().getCity());
        condition.setText(weather.getNow().getCond().getTxt());
        currentTemperature.setText(weather.getNow().getTmp());
        lowTemperature.setText(weather.getDaily_forecast().get(0).getTmp().getMin() + "°");
        highTemperature.setText(weather.getDaily_forecast().get(0).getTmp().getMax() + "°");
        wind.setText(weather.getNow().getWind().getDir() + " " + weather.getNow().getWind().getSc());
        hum.setText(weather.getNow().getHum() + "%");
    }

    @Override
    public void showWeatherInfo(HeWeather.WeatherBean weather) {
        this.weather = weather;
        dailyForecasts = weather.getDaily_forecast();
        mForecastAdapter.notifyDataSetChanged();

        setWeather(weather);

        StringBuilder builder = new StringBuilder();

        //basic 城市基本信息
        builder.append(weather.getBasic().getCity())
                .append("-")
                .append(weather.getBasic().getCnty())
                .append("\n")
                .append("更新时间：")
                .append(weather.getBasic().getUpdate().getLoc())
                .append("\n\n");

        //aqi 空气质量指数
        builder.append("空气质量指数:").append(weather.getAqi().getCity().getAqi()).append("\n");
        builder.append("PM2.5:").append(weather.getAqi().getCity().getPm25()).append("\n");
        builder.append("PM10:").append(weather.getAqi().getCity().getPm10()).append("\n");
        builder.append("二氧化硫1小时平均值so2(ug/m³):").append(weather.getAqi().getCity().getSo2()).append("\n");
        builder.append("二氧化氮1小时平均值no2(ug/m³):").append(weather.getAqi().getCity().getSo2()).append("\n");
        builder.append("一氧化碳1小时平均值(ug/m³):").append(weather.getAqi().getCity().getCo()).append("\n");
        builder.append("臭氧1小时平均值(ug/m³):").append(weather.getAqi().getCity().getO3()).append("\n");
        builder.append("空气质量类别:").append(weather.getAqi().getCity().getQlty()).append("\n\n");

        //suggestion 生活指数
        builder.append("穿衣指数:").append(weather.getSuggestion().getDrsg().getBrf()).append("\n");
        builder.append("紫外线指数:").append(weather.getSuggestion().getUv().getBrf()).append("\n");
        builder.append("洗车指数:").append(weather.getSuggestion().getCw().getBrf()).append("\n");
        builder.append("旅游指数:").append(weather.getSuggestion().getTrav().getBrf()).append("\n");
        builder.append("感冒指数:").append(weather.getSuggestion().getFlu().getBrf()).append("\n");
        builder.append("运动指数:").append(weather.getSuggestion().getSport().getBrf()).append("\n\n");

        //now 实况天气
        builder.append("实况天气---").append("\n");
        builder.append("当前温度(摄氏度):").append(weather.getNow().getTmp()).append("\n");
        builder.append("体感温度:").append(weather.getNow().getFl()).append("\n");
        builder.append("风速(Kmph):").append(weather.getNow().getWind().getSpd()).append("\n");
        builder.append("风力等级:").append(weather.getNow().getWind().getSc()).append("\n");
        builder.append("风向(角度):").append(weather.getNow().getWind().getDeg()).append("\n");
        builder.append("风向(方向):").append(weather.getNow().getWind().getDir()).append("\n");
        builder.append("天气状况:").append(weather.getNow().getCond().getTxt()).append("\n");
        builder.append("降雨量(mm):").append(weather.getNow().getPcpn()).append("\n");
        builder.append("湿度(%):").append(weather.getNow().getHum()).append("\n");
        builder.append("气压:").append(weather.getNow().getPres()).append("\n");
        builder.append("能见度(km):").append(weather.getNow().getVis()).append("\n\n");

        //hourly_forecast 每小时天气预报
        builder.append("每小时天气预报---").append("\n");
        builder.append("当地日期和时间:").append(weather.getHourly_forecast().get(0).getDate()).append("\n");
        builder.append("当前温度(摄氏度):").append(weather.getHourly_forecast().get(0).getTmp()).append("\n");
        builder.append("风速(Kmph):").append(weather.getHourly_forecast().get(0).getWind().getSpd()).append("\n");
        builder.append("风力等级:").append(weather.getHourly_forecast().get(0).getWind().getSc()).append("\n");
        builder.append("风向(角度):").append(weather.getHourly_forecast().get(0).getWind().getDeg()).append("\n");
        builder.append("风向(方向):").append(weather.getHourly_forecast().get(0).getWind().getDir()).append("\n");
        builder.append("降水概率:").append(weather.getHourly_forecast().get(0).getPop()).append("\n");
        builder.append("湿度(%):").append(weather.getHourly_forecast().get(0).getHum()).append("\n");
        builder.append("气压:").append(weather.getHourly_forecast().get(0).getPres()).append("\n\n");

        //daily_forecast 天气预报
        builder.append("天气预报---").append("\n");
        List<HeWeather.WeatherBean.DailyForecastBean> dailyForecastList = weather.getDaily_forecast();
        for (HeWeather.WeatherBean.DailyForecastBean dailyForecastBean : dailyForecastList) {
            builder.append("时间：").append(dailyForecastBean.getDate()).append("\n");
            builder.append("日出时间：").append(dailyForecastBean.getAstro().getSr()).append("\n");
            builder.append("日落时间：").append(dailyForecastBean.getAstro().getSs()).append("\n");
            builder.append("白天：").append(dailyForecastBean.getCond().getTxt_d()).append("\n");
            builder.append("夜间：").append(dailyForecastBean.getCond().getTxt_n()).append("\n");
            builder.append("湿度(%)：").append(dailyForecastBean.getHum()).append("\n");
            builder.append("降雨量(mm)：").append(dailyForecastBean.getPcpn()).append("\n");
            builder.append("降水概率：").append(dailyForecastBean.getPop()).append("\n");
            builder.append("气压：").append(dailyForecastBean.getPres()).append("\n");
            builder.append("能见度(km)：").append(dailyForecastBean.getVis()).append("\n");
            builder.append("最高温度(摄氏度)：").append(dailyForecastBean.getTmp().getMax()).append("\n");
            builder.append("最低温度(摄氏度)：").append(dailyForecastBean.getTmp().getMin()).append("\n");
            builder.append("风速(Kmph)：").append(dailyForecastBean.getWind().getSpd()).append("\n");
            builder.append("风力等级：").append(dailyForecastBean.getWind().getSc()).append("\n");
            builder.append("风向(方向)：").append(dailyForecastBean.getWind().getDir()).append("\n\n");
        }
        System.out.println(builder.toString());

    }

    @Override
    public void setPresenter(WeatherPresenter presenter) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void loadIcon(String code, ImageView imageView, int placeholder) {
        Realm realm = Realm.getDefaultInstance();
        if (realm.isEmpty()) {
            System.out.println("realm is empty");
        }
        RealmResults<ConditionInfoBean> results = realm.where(ConditionInfoBean.class)
                .equalTo("code", code)
                .findAll();
        if (!results.isEmpty()) {
            Picasso.with(getContext())
                    .load(results.first().getIcon())
                    .placeholder(placeholder)
                    .into(imageView);
            System.out.println(results.first().getIcon());
        } else {
            System.out.println("没有结果");
        }
        realm.close();
    }

    class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

        public ForecastAdapter() {
        }

        @Override
        public ForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.item_forecast, null);
            int screenWidth = Utils.getScreenWidth(getContext());
            int width = screenWidth / 3;
            view.setLayoutParams(new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ForecastViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ForecastViewHolder holder, int position) {
            HeWeather.WeatherBean.DailyForecastBean forecastBean = dailyForecasts.get(position);
            Date date = DateUtil.str2Date(forecastBean.getDate(), DateUtil.FORMAT_YMD);
            String week = DateUtil.getWeekOfDate(date);
            boolean isToday = DateUtil.isToday(date.getTime(), Calendar.getInstance().getTimeInMillis());
            holder.tvWeek.setText(isToday ? "今天" : week);
            holder.tvDatetime.setText(DateUtil.date2Str(date, "MM/dd"));
            holder.tvTemp.setText(forecastBean.getTmp().getMin() + "°/" + forecastBean.getTmp().getMax() + "°");

            String code = forecastBean.getCond().getCode_d();
            loadIcon(code, holder.ivForecast, R.drawable.unknown);
        }

        @Override
        public int getItemCount() {
            return dailyForecasts == null ? 0 : dailyForecasts.size();
        }

        class ForecastViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.tv_datetime)
            TextView tvDatetime;
            @BindView(R.id.tv_week)
            TextView tvWeek;
            //            @BindView(R.id.iv_forecast_image)
//            ImageView ivForecast;
            @BindView(R.id.iv_forecast_image)
            ImageView ivForecast;
            @BindView(R.id.tv_tmp)
            TextView tvTemp;

            public ForecastViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
