package com.monad.noisecontrolsystem.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.monad.noisecontrolsystem.Model.Contributor;
import com.monad.noisecontrolsystem.Model.ApiService;
import com.monad.noisecontrolsystem.Model.ListModel;
import com.monad.noisecontrolsystem.Model.MydataModel;
import com.monad.noisecontrolsystem.Model.RankModel;
import com.monad.noisecontrolsystem.R;
import com.monad.noisecontrolsystem.Realm.ResponseModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RankFagment extends Fragment implements SeekBar.OnSeekBarChangeListener,
        OnChartValueSelectedListener
{
    private PieChart pieChart2;
    private View v;
    private TextView rank1, rank2, rank3;
    private ApiService service;
    private Spinner spinner;
    private int targetRoom;
    private Retrofit retrofit;
    private List<TextView> ranks;
    private List<String> test = new ArrayList<String>();

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v =  inflater.inflate(R.layout.fragment_2, container, false);
        test.clear();
        setLayout();
        loadList();
        setRetrofit();
        loadRank();

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    private void loadList() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://116.121.158.137:8000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.i("start","start");
        service = retrofit.create(ApiService.class);

        Call<List<ListModel>> loadSizeCall = service.listLoad();

        loadSizeCall.enqueue(new Callback<List<ListModel>>() {
            @Override
            public void onResponse(Call<List<ListModel>>  call, Response<List<ListModel>> response) {
                List<ListModel> list = response.body();
                for (ListModel obj: list) {
                    test.add(obj.getHo()+"");
                }
                setSpinner(test);

            }

            @Override
            public void onFailure(Call<List<ListModel>> call, Throwable t) {
                Log.i("start2","start2");
                Log.i("fail",t.getMessage());
            }
        });
    }

    private void setRetrofit() {
        retrofit = new Retrofit.Builder()
            .baseUrl("http://116.121.158.137:8000")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        service = retrofit.create(ApiService.class);
    }


    private void setLayout() {
        rank1 = (TextView) v.findViewById(R.id.rank_1);
        rank2 = (TextView) v.findViewById(R.id.rank_2);
        rank3 = (TextView) v.findViewById(R.id.rank_3);
        ranks = new ArrayList<>();
        ranks.add(rank1);
        ranks.add(rank2);
        ranks.add(rank3);
    }

    private void setSpinner(List<String> test){
        spinner = (Spinner)v.findViewById(R.id.spinner1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String str = (String) spinner.getSelectedItem();
                targetRoom = Integer.parseInt(str.replaceAll("\\D", ""));
                //loadChart(targetRoom);
                Log.i("select", "select");
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://116.121.158.137:8000")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                service = retrofit.create(ApiService.class);

                Call<ResponseModel> loadSizeCall = service.Load(targetRoom);

                loadSizeCall.enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        Log.i("asd", response.body().getVibration_data() + " ");
                        ResponseModel responseData = response.body();
                        setPiechart(responseData);

                        Log.i("Noise", responseData.getNoise() + "");
                        Log.i("Vibration", responseData.getVibration_data() + "");
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        Log.i("fail",t.getMessage());
                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        ArrayAdapter adapter = new ArrayAdapter(getActivity(),
                R.layout.spin, test );
        adapter.setDropDownViewResource(R.layout.spin);
        spinner.setAdapter(adapter);
    }


    private void loadRank() {
        Call<List<RankModel>> loadSizeCall = service.getRank();

        loadSizeCall.enqueue(new Callback<List<RankModel>>() {
            @Override
            public void onResponse(Call<List<RankModel>> call, Response<List<RankModel>> response) {
                List<RankModel> size = response.body();
                int i = 0;
                for (RankModel obj: size) {
                    if(i == 3)
                        break;
                    ranks.get(i).setText(obj.getHo()+"");
                    i++;
                }
            }

            @Override
            public void onFailure(Call<List<RankModel>> call, Throwable t) {
                Log.i("fail",t.getMessage());
            }
        });
    }

    private void setPiechart(ResponseModel model) {
        pieChart2 = (PieChart) v.findViewById(R.id.piechart2);
        pieChart2.setUsePercentValues(true);
        ArrayList<PieEntry> yvalues = new ArrayList<PieEntry>();
        yvalues.add(new PieEntry(model.getNoise(), "소음 : " + model.getNoise()));
        yvalues.add(new PieEntry(model.getVibration_data(), "진동 : " + model.getVibration_data()));

        PieDataSet dataSet = new PieDataSet(yvalues, targetRoom+"호 지수");

        ArrayList<String> xVals = new ArrayList<String>();

        xVals.add("소음");
        xVals.add("진동");

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        pieChart2.setData(data);

        pieChart2.setDrawHoleEnabled(true);
        pieChart2.setTransparentCircleRadius(25f);
        pieChart2.setHoleRadius(25f);

        dataSet.setColors(ColorTemplate.PASTEL_COLORS);
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.DKGRAY);
        pieChart2.setOnChartValueSelectedListener(this);
        pieChart2.animateXY(1400, 1400);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}