package com.balram.applocker.tools;

import android.database.Cursor;
import android.graphics.Color;

import com.balram.applocker.R;
import com.github.bassaer.library.MDColor;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by GSA13442 on 31/03/2017.
 */

public class Tools {
    private static final int PIE_CHART_MAX_ELEM = 4;
    public static final int[] REAL_MATERIAL_COLORS = {MDColor.RED_500, MDColor.BLUE_500, MDColor.TEAL_500, MDColor.YELLOW_500, MDColor.DEEP_ORANGE_500, MDColor.BROWN_500};


            /*
            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(TimeZone.getDefault());

            Timestamp ts_begin = Timestamp.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("inserted_time")));
            Timestamp ts_end = Timestamp.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("end_time")));

            long begin = ts_begin.getTime();

            cal.setTimeInMillis(begin);
            String startAt = String.format("%s:%s:%s", getCorrectTime(cal, Calendar.HOUR_OF_DAY), getCorrectTime(cal, Calendar.MINUTE), getCorrectTime(cal, Calendar.SECOND));
            long end = ts_end.getTime();
            cal.setTimeInMillis(end);
            startAt += String.format(" --> %s:%s:%s", getCorrectTime(cal, Calendar.HOUR_OF_DAY), getCorrectTime(cal, Calendar.MINUTE), getCorrectTime(cal, Calendar.SECOND));
            long d = TimeUnit.MILLISECONDS.toSeconds(end - begin);
            String duration = formatTime((int) d) + "s";
            */


    /**
     * PIE CHART
     */
    public static PieChart configurePieChart(PieChart pieChart) {
        pieChart.setTouchEnabled(false);
        // configure pie chart
        pieChart.setUsePercentValues(false);
        Description desc = new Description();
        desc.setText("");
        pieChart.setDescription(desc);
        // enable hole and configure
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(R.color.black);
        pieChart.setHoleRadius(7);
        pieChart.setTransparentCircleRadius(10);
        pieChart.setDrawEntryLabels(false);

        // enable rotation of the chart by touch
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setClickable(false);
        return pieChart;
    }

    public static ArrayList<PieEntry> getEntriesFromCursorTime(Cursor cursor) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        int i=0;
        do {
            long begin = cursor.getInt(cursor.getColumnIndexOrThrow("totalInsert"));
            long end = cursor.getInt(cursor.getColumnIndexOrThrow("totalEnd"));
            long duration = TimeUnit.MILLISECONDS.toSeconds(end - begin);

            entries.add(new PieEntry(duration,cursor.getString(cursor.getColumnIndex("event_name"))));
            i++;
        } while(cursor.moveToNext() && i< PIE_CHART_MAX_ELEM);
        return entries;
    }


    public static ArrayList<PieEntry> getEntriesFromCursorOcc(Cursor cursor) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        int i=0;
        do {
            entries.add(new PieEntry(cursor.getInt(cursor.getColumnIndex("nbOcc")),cursor.getString(cursor.getColumnIndex("event_name"))));
            i++;
        } while(cursor.moveToNext() && i< PIE_CHART_MAX_ELEM);
        return entries;
    }


    public static void configureDataSet(PieDataSet dataSet) {
        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors
        dataSet.setColors(REAL_MATERIAL_COLORS);
    }

    /**
     * BAR CHART
     */
    public static BarChart configureBarChart(BarChart barChart) {
        barChart.setTouchEnabled(false); //DISABLE INTERACTIONS
        barChart.getLegend().setEnabled(false);   // Hide the legend

        barChart.setPinchZoom(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(false);

        barChart.getDescription().setText("");
        barChart.setDrawGridBackground(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setLabelRotationAngle(90.0f);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1.0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter());

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setDrawLabels(false);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setAxisMinimum(0);
        leftAxis.setTextSize(15);
        leftAxis.setGranularity(1.0f);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setValueFormatter(new DefaultAxisValueFormatter(0));


        return barChart;
    }

    public static void configureDataSet(BarDataSet dataSet) {
        dataSet.setDrawIcons(false);
        dataSet.setIconsOffset(new MPPointF(0, 40));

        // add a lot of colors
        dataSet.setColors(REAL_MATERIAL_COLORS);
    }

    public static void getEntriesFromCursorTime(ArrayList<String> labels, ArrayList<BarEntry> entries, Cursor cursor) {
        do {

            long begin = cursor.getInt(cursor.getColumnIndexOrThrow("totalInsert"));
            long end = cursor.getInt(cursor.getColumnIndexOrThrow("totalEnd"));
            long duration = TimeUnit.MILLISECONDS.toSeconds(end - begin);

            String appName = cursor.getString(cursor.getColumnIndex("event_name"));
            labels.add(appName);
            entries.add(new BarEntry(labels.indexOf(appName),duration));
        } while(cursor.moveToNext());
    }

    public static void getEntriesFromCursorOcc(ArrayList<String> labels, ArrayList<BarEntry> entries, Cursor cursor) {
        do {
            String appName = cursor.getString(cursor.getColumnIndex("event_name"));
            labels.add(appName);
            entries.add(new BarEntry(labels.indexOf(appName),cursor.getInt(cursor.getColumnIndex("nbOcc"))));
        } while(cursor.moveToNext());
    }
}
