package com.strime.applogger.interfaces;

import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.strime.applogger.adapter.EventNotifAdapter;

import rm.com.clocks.ClockImageView;

/**
 * Created by gsa13442 on 04/04/2017.
 */

public interface ToolChangedListener {
    void onToolChanged(boolean isShownByDuration);
    void animateClock(EventNotifAdapter.EventViewHolder evh, Integer h, Integer m);
}
