package com.strime.applogger.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.strime.applogger.R;
import com.strime.applogger.model.Horaire;

import java.util.ArrayList;

/**
 * Created by gsa13442 on 05/04/2017.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotifViewHolder> {
    private final ArrayList<Horaire> horaires;

    public NotificationAdapter(ArrayList<Horaire> h) {
        this.horaires = h;
    }

    @Override
    public NotifViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_view, parent, false);
        view.setMinimumHeight(300);
        view.setMinimumWidth(300);
        NotifViewHolder evh = new NotifViewHolder(view);
        /*v.setVisibility(View.GONE);
        new ExpectAnim()
                .expect(v).toBe(alpha(1)).toAnimation().setDuration(5000).setNow();*/

        /*view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BLAAA","CLMICKEDEODKEOKD");
            }
        });*/


        return evh;
    }

    @Override
    public void onBindViewHolder(NotifViewHolder holder, int position) {
        Log.d("ITEM COUNT","!!!"+horaires.get(position).event.getAppName());

        holder.tvTitle.setText("blaa");
    }


    @Override
    public int getItemCount() {
        Log.d("ITEM COUNT","!!!"+horaires.size());
        return horaires.size();
    }

    public static class NotifViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        public NotifViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.title_card_app);
        }
    }
}
