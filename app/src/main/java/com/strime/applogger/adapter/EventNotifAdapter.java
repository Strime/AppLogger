package com.strime.applogger.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.strime.applogger.R;
import com.strime.applogger.model.Event;
import com.strime.applogger.model.Horaire;
import com.strime.applogger.stub.StubSql;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.TimerTask;

import rm.com.clocks.ClockDrawable;
import rm.com.clocks.ClockImageView;
import rm.com.clocks.Stroke;


/**
 * Created by GSA13442 on 28/02/2017.
 */

public class EventNotifAdapter extends  CursorRecyclerViewAdapter<EventNotifAdapter.EventViewHolder> {
    private static final int refresh_int = 2000;

    private final ArrayList<Event> temp;
    private final Context ctx;

    private static ArrayList<Horaire> horaires = new ArrayList<>();

    public EventNotifAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        this.ctx = context;
        temp = StubSql.getStubNotifEvent();
        for (Event e : temp) {
            horaires.addAll(e.getsHoraires());
        }
        Collections.sort(horaires, new HoraireComparator());

    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        private final NotificationAdapter notificationAdapter;
        CardView cv;
        RecyclerView rvNotif;
        ClockImageView clock;
        TextView tvTime;

        private final Context appCtx;

        EventNotifAdapter parent;

        public ClockImageView getClockImageView() {
            return clock;
        }
        public TextView getTvTime() {
            return tvTime;
        }

        EventViewHolder(View view, Context appCtx, final EventNotifAdapter p) {
            super(view);
            this.appCtx = appCtx;
            parent = p;
            cv = (CardView) view.findViewById(R.id.cardview);
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //parent.notifyDataSetChanged();
                }
            });

            clock = (ClockImageView) view.findViewById(R.id.clocks);

            tvTime = (TextView) view.findViewById(R.id.textViewTime);
            rvNotif = (RecyclerView) view.findViewById(R.id.rvNotif);
            LinearLayoutManager llm_notif = new LinearLayoutManager(appCtx);
            rvNotif.setLayoutManager(llm_notif);
            notificationAdapter = new NotificationAdapter(horaires);
            rvNotif.setAdapter(notificationAdapter);
            //digitClock.startActionMode()
            updateUI();
        }

        void updateUI() {
            ClockDrawable.builder(parent.ctx)
                    .hours(00)                 // initial time hours
                    .minutes(00)                    // initial time minutes
                    .withColor(Color.DKGRAY)                                    // set icon color
                    .withFrameWidth(Stroke.REGULAR)                             // set frame width
                    .withPointerWidth(Stroke.THIN)// set pointer width
                    .into(clock);
        }
    }

    public String getCorrectTime(Calendar c, int type) {
        return formatTime(c.get(type));
    }

    public String formatTime(int time) {
        return time < 10 ? "0"+time:""+time;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notif_card_layout, parent, false);
        EventViewHolder evh = new EventViewHolder(v, ctx, this);
        return evh;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public void onBindViewHolder(final EventViewHolder viewHolder, Cursor cursor) {
        viewHolder.updateUI();
        /*long minInserted = Long.MAX_VALUE;
        String sample ="";
        do {
            minInserted = Math.min(cursor.getLong(cursor.getColumnIndexOrThrow("inserted_time")),minInserted);
            sample = cursor.getString(cursor.getColumnIndexOrThrow("event_name"));
        } while(cursor.moveToNext());
        viewHolder.tvTime.setText(sample);*/


        //new ExpectAnim().expect(viewHolder.tvTime).toBe(n)setDuration(d).setNow();

       /* final Timer t = new Timer();
        t.schedule(new MyTimerTask(viewHolder), refresh_int, refresh_int);*/
        Horaire h = horaires.remove(0);
        //listener.animateClock(viewHolder, h.hour,h.minute);
    }
    private class MyTimerTask extends TimerTask {
        private final EventViewHolder viewHolder;

        public MyTimerTask(EventViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }

        @Override
        public void run() {
            if(!horaires.isEmpty()) {
                Horaire h = horaires.remove(0);
               // listener.animateClock(viewHolder, h.hour,h.minute);
            }
        }
    }


}
