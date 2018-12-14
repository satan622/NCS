package com.monad.noisecontrolsystem.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.monad.noisecontrolsystem.Model.ApiService;
import com.monad.noisecontrolsystem.Model.AverageModel;
import com.monad.noisecontrolsystem.Model.MyValueFormatter;
import com.monad.noisecontrolsystem.Model.MydataModel;
import com.monad.noisecontrolsystem.Model.TimeModel;
import com.monad.noisecontrolsystem.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyFragment extends Fragment implements SeekBar.OnSeekBarChangeListener,
        OnChartValueSelectedListener, View.OnClickListener
{
    private PieChart pieChart;
    private BarChart mChart;
    private View v;
    private Button temp;
    private ArrayList<PieEntry> yvalues;
    private MydataModel data;
    private ApiService service;
    private TextView info;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v =  inflater.inflate(R.layout.fragment_1, container, false);
        data = MydataModel.getInstance();
        info = (TextView) v.findViewById(R.id.info_text);
        setInfo();

        Log.i("firtNoise", data.getNoise() + "");
        Log.i("firstVibration", data.getVibration() + "");
        getAverage();
        getTime();
        return v;
    }


    private void getAverage() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://116.121.158.137:8000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ApiService.class);

        Call<AverageModel> loadSizeCall = service.getAverage(data.getRoom());

        loadSizeCall.enqueue(new Callback<AverageModel>() {
            @Override
            public void onResponse(Call<AverageModel> call, Response<AverageModel> response) {
                AverageModel responseData = response.body();
                if(responseData == null) {
                    Log.i("null", "null");
                } else {
                    setPiechart(responseData.getAverage(), responseData.getAverage2());
                }
            }

            @Override
            public void onFailure(Call<AverageModel> call, Throwable t) {
                Log.i("fail",t.getMessage());
            }
        });
    }
    private void getTime() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://116.121.158.137:8000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ApiService.class);

        Call<List<TimeModel>> loadSizeCall = service.getTime(data.getRoom());

        loadSizeCall.enqueue(new Callback<List<TimeModel>>() {
            @Override
            public void onResponse(Call<List<TimeModel>> call, Response<List<TimeModel>> response) {
                List<TimeModel> responseData = response.body();
                setLinechart(responseData);
            }

            @Override
            public void onFailure(Call<List<TimeModel>> call, Throwable t) {
                Log.i("fail",t.getMessage());
            }
        });
    }

    private void setInfo() {
        if(data.getNoise() < 10) {
            info.setText("우리집의 소음 지수는 '조용' 입니다");
        } else if(data.getNoise() >= 10 && data.getNoise() < 50) {
            info.setText("우리집의 소음 지수는 '보통' 입니다");
        } else if(data.getNoise() >= 50) {
            info.setText("우리집의 소음 지수는 '시끄러움' 입니다");
        }
        else {
            info.setText("우리집의 소음 지수는 " + data.getNoise()+ " 입니다");
        }
    }

    private void setPiechart(float num, float num2) {
        pieChart = (PieChart) v.findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);

        yvalues = new ArrayList<PieEntry>();
        yvalues.add(new PieEntry(num, "평균" + num));
        yvalues.add(new PieEntry(num2, "내 평균" + num2));

        PieDataSet set = new PieDataSet(yvalues,"");
        PieData data = new PieData(set);
        data.setValueFormatter(new PercentFormatter());
        pieChart.setData(data);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setTransparentCircleRadius(25f);
        pieChart.setHoleRadius(25f);

        set.setColors(ColorTemplate.PASTEL_COLORS);
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.DKGRAY);
        pieChart.setOnChartValueSelectedListener(this);

        pieChart.animateXY(1400, 1400);
    }

    private void setLinechart(List<TimeModel> result) {
        mChart = (BarChart) v.findViewById(R.id.line_chart);

        mChart.setBackgroundColor(Color.WHITE);


        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);

        mChart.getDescription().setEnabled(false);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setTextColor(Color.LTGRAY);
        xAxis.setTextSize(13f);
        xAxis.setLabelCount(5);
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f);

        YAxis left = mChart.getAxisLeft();
        left.setDrawLabels(false);
        left.setSpaceTop(25f);
        left.setSpaceBottom(25f);
        left.setDrawAxisLine(false);
        left.setDrawGridLines(false);
        left.setDrawZeroLine(true); // draw a zero line
        left.setZeroLineColor(Color.GRAY);
        left.setZeroLineWidth(0.7f);
        mChart.getAxisRight().setEnabled(false);
        mChart.getLegend().setEnabled(false);
        final List<Data> data = new ArrayList<>();
        float num = 0f;
        for (TimeModel model: result) {
            if(num > 5f)
                break;
            data.add(new Data(num, model.getNoise_data(), model.getCreate_date()));
            num+= 1f;

        }
        // THIS IS THE ORIGINAL DATA YOU WANT TO PLOT


        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return data.get(Math.min(Math.max((int) value, 0), data.size()-1)).xAxisValue;
            }
        });

        setData(data);

    }


    private void setData(List<Data> dataList) {

        ArrayList<BarEntry> values = new ArrayList<BarEntry>();
        List<Integer> colors = new ArrayList<Integer>();

        int green = Color.rgb(110, 190, 102);
        int red = Color.rgb(211, 74, 88);

        for (int i = 0; i < dataList.size(); i++) {

            Data d = dataList.get(i);
            BarEntry entry = new BarEntry(d.xValue, d.yValue);
            values.add(entry);

            // specific colors
            if (d.yValue >= 0)
                colors.add(red);
            else
                colors.add(green);
        }

        BarDataSet set;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set = (BarDataSet)mChart.getData().getDataSetByIndex(0);
            set.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set = new BarDataSet(values, "Values");
            set.setColors(colors);
            set.setValueTextColors(colors);

            BarData data = new BarData(set);
            data.setValueTextSize(13f);
            data.setValueFormatter(new ValueFormatter());
            data.setBarWidth(0.8f);

            mChart.setData(data);
            mChart.invalidate();
        }

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


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
        }
    }

    private class Data {

        public String xAxisValue;
        public float yValue;
        public float xValue;

        public Data(float xValue, float yValue, String xAxisValue) {
            this.xAxisValue = xAxisValue;
            this.yValue = yValue;
            this.xValue = xValue;
        }
    }

    private class ValueFormatter implements IValueFormatter
    {
        private DecimalFormat mFormat;

        public ValueFormatter() {
            mFormat = new DecimalFormat("######.0");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value);
        }
    }
}