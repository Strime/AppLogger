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
import com.balram.applocker.interfaces.ToolChangedListener;
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
import rm.com.clocks.ClockImageView;
import rm.com.clocks.Stroke;

/**
 * Created by GSA13442 on 28/02/2017.
 */

public class EventNotifAdapter extends  CursorRecyclerViewAdapter<EventNotifAdapter.EventViewHolder> {
    private Context ctx;
    private ToolChangedListener listener;
    public EventNotifAdapter(Context context, Cursor cursor, ToolChangedListener l) {
        super(context, cursor);
        this.ctx = context;
        this.listener = l;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        ClockImageView clockImageView;
        TextView text;

        EventNotifAdapter parent;

        EventViewHolder(View view, final EventNotifAdapter p) {
            super(view);
            parent = p;
            cv = (CardView) view.findViewById(R.id.cardview);
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //parent.notifyDataSetChanged();
                }
            });

            clockImageView = (ClockImageView) view.findViewById(R.id.time_marker);

            text = (TextView) view.findViewById(R.id.text_explanation);

            updateUI();
        }

        void updateUI() {        }
    }

    public void initClock(ImageView iv, Timestamp start) {

        Calendar startCal = Calendar.getInstance();
        startCal.setTimeZone(TimeZone.getDefault());
        startCal.setTimeInMillis(start.getTime());

        ClockDrawable clock = ClockDrawable.builder(ctx)
                .hours(startCal.get(Calendar.HOUR_OF_DAY))                 // initial time hours
                .minutes(startCal.get(Calendar.MINUTE))                    // initial time minutes
                .withColor(Color.DKGRAY)                                    // set icon color
                .withFrameWidth(Stroke.REGULAR)                             // set frame width
                .withPointerWidth(Stroke.THIN)                              // set pointer width
                //.withDuration(200L*1+duration)                            // set animation duration in millis (600L by default)
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_notif_layout, parent, false);
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
        long minInserted = Long.MAX_VALUE;
        String sample ="";
        do {
            minInserted = Math.min(cursor.getLong(cursor.getColumnIndexOrThrow("inserted_time")),minInserted);
            sample = cursor.getString(cursor.getColumnIndexOrThrow("event_name"));
        } while(cursor.moveToNext());
        viewHolder.text.setText(sample);

        initClock(viewHolder.clockImageView, new Timestamp(minInserted));
    }

}
