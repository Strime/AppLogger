package com.balram.applocker.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balram.applocker.R;
import com.balram.applocker.tools.Tools;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import rm.com.clocks.ClockDrawable;
import rm.com.clocks.Stroke;

/**
 * Created by GSA13442 on 28/02/2017.
 */

public class EventAdapter extends  CursorRecyclerViewAdapter<EventAdapter.EventViewHolder> {
    private Context ctx;
    private ToolChangedListener listener;
    public EventAdapter(Context context, Cursor cursor, ToolChangedListener l) {
        super(context, cursor);
        this.ctx = context;
        this.listener = l;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        PieChart pieChart;
        BarChart barChart;
        TextView titleCardView;
        TextView textExplanation;
        FloatingActionButton fabTools;

        EventAdapter parent;

        boolean isPie;
        boolean isShownByDuration;

        EventViewHolder(View view, final EventAdapter p) {
            super(view);
            parent = p;
            cv = (CardView) view.findViewById(R.id.cardview);
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isPie = !isPie;
                    parent.notifyDataSetChanged();
                }
            });
            pieChart = (PieChart) view.findViewById(R.id.pie_chart);
            barChart = (BarChart) view.findViewById(R.id.bar_chart);

            titleCardView = (TextView) view.findViewById(R.id.title_card);
            textExplanation = (TextView) view.findViewById(R.id.text_explanation);

            pieChart = Tools.configurePieChart(pieChart);
            barChart = Tools.configureBarChart(barChart);

            fabTools = (FloatingActionButton) view.findViewById(R.id.fabTools);
            fabTools.setEnabled(true);
            fabTools.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isShownByDuration = !isShownByDuration;
                    parent.onToolChanged(isShownByDuration);
                }
            });

            isPie = true;
            isShownByDuration = true;

            updateUI();
        }

        void updateUI() {
            pieChart.setVisibility(isPie?View.VISIBLE:View.GONE);
            barChart.setVisibility(isPie?View.GONE:View.VISIBLE);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textExplanation.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, isPie ? R.id.pie_chart : R.id.bar_chart);
            textExplanation.setText(isShownByDuration ? "Time in second spend on each applications" : "Number of time you opened each applications");

        }
    }

    private void onToolChanged(boolean isShownByDuration) {
        this.listener.onToolChanged(isShownByDuration);
    }

    public void setClock(ImageView iv, Timestamp start, Timestamp end, long duration) {

        Calendar startCal = Calendar.getInstance();
        startCal.setTimeZone(TimeZone.getDefault());
        startCal.setTimeInMillis(start.getTime());
        Calendar endCal = Calendar.getInstance();
        endCal.setTimeZone(TimeZone.getDefault());
        endCal.setTimeInMillis(end.getTime());


        ClockDrawable clock = ClockDrawable.builder(ctx)
                .hours(startCal.get(Calendar.HOUR_OF_DAY))                 // initial time hours
                .minutes(startCal.get(Calendar.MINUTE))                    // initial time minutes
                .withColor(Color.DKGRAY)                                    // set icon color
                .withFrameWidth(Stroke.REGULAR)                             // set frame width
                .withPointerWidth(Stroke.THIN)                              // set pointer width
                .withDuration(200L*1+duration)                                         // set animation duration in millis (600L by default)
                .withInterpolator(new DecelerateInterpolator())             // set animation interpolator (default is OverShootInterpolator)
                .into(iv);                                                  // attach the Drawable you built to ImageView and returns Drawable
    }

    public String getCorrectTime(Calendar c, int type) {
        return formatTime(c.get(type));
    }

    public String formatTime(int time) {
        return time < 10 ? "0"+time:""+time;
    }


    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_layout, parent, false);
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
        if(viewHolder.isPie) {
            ArrayList<PieEntry> entries = viewHolder.isShownByDuration ? Tools.getEntriesFromCursorTime(cursor) : Tools.getEntriesFromCursorOcc(cursor);
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
            if(viewHolder.isShownByDuration) {
                Tools.getEntriesFromCursorTime(labels,entries, cursor);
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

            // undo all highlights
            viewHolder.barChart.highlightValues(null);

            // update pie chart
            viewHolder.barChart.invalidate();
            viewHolder.barChart.animateXY(500,500);
        }


        viewHolder.titleCardView.setText("Navigation into applications");

        /*setClock(viewHolder.clock, ts_begin, ts_end, d);

        viewHolder.appNameTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow("event_name")));
        viewHolder.durationTextView.setText(String.format("%s (%s)",startAt,duration));*/
    }


    public interface ToolChangedListener {
        public void onToolChanged(boolean isShownByDuration);
    }
}
