package com.strime.applogger.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.strime.applogger.R;
import com.strime.applogger.tools.Tools;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

/**
 * Created by GSA13442 on 28/02/2017.
 */

public class AppEventAdapter extends  CursorRecyclerViewAdapter<AppEventAdapter.EventViewHolder> {
    private final ClickOnChartListener listener;
    private Context ctx;
    private boolean isPie = true;
    private boolean isShownByDuration = true;

    public AppEventAdapter(Context context, Cursor cursor, ClickOnChartListener listener) {
        super(context, cursor);
        this.ctx = context;
        this.listener = listener;
    }

    public boolean isShownByDuration() {
        return isShownByDuration;
    }

    public void setShownByDuration(boolean shownByDuration) {
        isShownByDuration = shownByDuration;
    }

    public boolean isPie() {
        return isPie;
    }

    public void setPie(boolean pie) {
        isPie = pie;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        PieChart pieChart;
        BarChart barChart;

        AppEventAdapter parent;

        EventViewHolder(View view, final AppEventAdapter p) {
            super(view);
            parent = p;

            pieChart = (PieChart) view.findViewById(R.id.pie_chart);
            barChart = (BarChart) view.findViewById(R.id.bar_chart);

            pieChart = Tools.configurePieChart(pieChart);
            barChart = Tools.configureBarChart(barChart);

            updateUI();
        }

        void updateUI() {
            pieChart.setVisibility(parent.isPie() ? View.VISIBLE : View.GONE);
            barChart.setVisibility(parent.isPie() ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public EventViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chart_layout, parent, false);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickOnChar();
            }
        });

        EventViewHolder evh = new EventViewHolder(v, this);
        return evh;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public void onBindViewHolder(EventViewHolder viewHolder, Cursor cursor) {
        viewHolder.updateUI();
        if(isPie) {

            ArrayList<PieEntry> entries = new ArrayList<>();
            if(isShownByDuration) {
                Tools.getEntriesFromCursorTime(cursor, entries);
            } else {
                Tools.getEntriesFromCursorOcc(cursor, entries);
            }
            PieDataSet dataSet = new PieDataSet(entries, "");

            Tools.configureDataSet(dataSet);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new DefaultValueFormatter(0));
            data.setValueTextSize(11f);
            data.setValueTextColor(Color.WHITE);

            viewHolder.pieChart.setData(data);

            // undo all highlights
            viewHolder.pieChart.highlightValues(null);

            // update pie chart
            viewHolder.pieChart.invalidate();
            viewHolder.pieChart.animateY(500);
        } else {
            ArrayList<String> labels = new ArrayList<>();
            ArrayList<BarEntry> entries = new ArrayList<>();
            if(isShownByDuration) {
                Tools.getEntriesFromCursorTime(labels, entries, cursor);
            } else {
                Tools.getEntriesFromCursorOcc(labels, entries, cursor);
            }
            BarDataSet dataSet = new BarDataSet(entries, "");
            Tools.configureDataSet(dataSet);

            BarData data = new BarData(dataSet);
            data.setValueFormatter(new DefaultValueFormatter(0));
            data.setValueTextSize(11f);

            data.setValueTextColor(Color.BLACK);

            viewHolder.barChart.setData(data);
            viewHolder.barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
            viewHolder.barChart.getXAxis().setLabelCount(labels.size());

            // undo all highlights
            viewHolder.barChart.highlightValues(null);

            // update pie chart
            viewHolder.barChart.invalidate();
            viewHolder.barChart.animateXY(500,500);
        }


        //viewHolder.titleCardView.setText("Navigation into applications");
    }


    public interface ClickOnChartListener {
        void onClickOnChar();
    }

}
