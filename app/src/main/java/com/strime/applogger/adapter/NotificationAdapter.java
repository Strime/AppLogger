package com.strime.applogger.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.strime.applogger.R;
import com.strime.applogger.model.Horaire;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import rm.com.clocks.ClockImageView;

/**
 * Created by gsa13442 on 05/04/2017.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotifViewHolder> {
    private final TextView time;
    private final ClockImageView clock;
    private final Context ctx;
    private ArrayList<Horaire> allHoraires;

    private long currentTimeStamp;
    private ArrayList<Horaire> horairesOnCurrentTimestamp = new ArrayList<>();

    public NotificationAdapter(Context ctx, ClockImageView clock, TextView time, ArrayList<Horaire> h) {
        this.clock = clock;
        this.time = time;
        this.allHoraires = h;
        this.ctx = ctx;
        Collections.sort(this.allHoraires, new HoraireComparator());
        setCurrentTimeStamp(this.allHoraires.get(0).timestamp);
    }

    private void setTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(currentTimeStamp);
        String h = cal.get(Calendar.HOUR_OF_DAY) < 10 ? "0"+cal.get(Calendar.HOUR_OF_DAY) : ""+cal.get(Calendar.HOUR_OF_DAY);
        String m = cal.get(Calendar.MINUTE) < 10 ? "0"+cal.get(Calendar.MINUTE) : ""+cal.get(Calendar.MINUTE);
        String s = cal.get(Calendar.SECOND) < 10 ? "0"+cal.get(Calendar.SECOND) : ""+cal.get(Calendar.SECOND);

        clock.animateToTime(Integer.valueOf(h),Integer.valueOf(m));
        time.setText(h+":"+m+":"+s);
    }

    @Override
    public NotifViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_view, parent, false);
        NotifViewHolder evh = new NotifViewHolder(view);
        return evh;
    }

    @Override
    public void onBindViewHolder(NotifViewHolder holder, int position) {
        Log.d("ITEM COUNT","!!!"+ horairesOnCurrentTimestamp.get(position).event.getAppName());
        String[] infos = horairesOnCurrentTimestamp.get(position).infos;

        holder.info.setText(infos[0] + " : "+infos[1]);
        holder.moreInfo.setText(infos[2]);

        setAnimation(holder.itemView, position);
    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position) {
        Animation animation = AnimationUtils.loadAnimation(ctx, android.R.anim.slide_in_left);
        viewToAnimate.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return horairesOnCurrentTimestamp.size();
    }

    public void startAnimation() throws Exception {
        setCurrentTimeStamp(getNextTimeStamp());
    }

    public void setCurrentTimeStamp(long currentTimeStamp) {
        this.currentTimeStamp = currentTimeStamp;
        horairesOnCurrentTimestamp.clear();
        for(Horaire h : allHoraires) {
            if(h.timestamp == currentTimeStamp) {
                horairesOnCurrentTimestamp.add(h);
            }
        }
        setTime();
        notifyDataSetChanged();
    }

    public long getNextTimeStamp() throws Exception {
        for(Horaire h : allHoraires) {
            if(h.timestamp > currentTimeStamp) {
                return h.timestamp;
            }
        }
        throw new Exception("Animation should be reset");
    }

    public static class NotifViewHolder extends RecyclerView.ViewHolder {
        TextView info;
        TextView moreInfo;

        public NotifViewHolder(View itemView) {
            super(itemView);
            info = (TextView) itemView.findViewById(R.id.title_notif);
            moreInfo = (TextView) itemView.findViewById(R.id.text_more_info);
        }
    }
}
