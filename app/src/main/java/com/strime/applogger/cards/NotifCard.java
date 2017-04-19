package com.strime.applogger.cards;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.CloseableIterator;
import com.strime.applogger.R;
import com.strime.applogger.adapter.NotificationAdapter;
import com.strime.applogger.database.sqlHelper;
import com.strime.applogger.model.Event;
import com.strime.applogger.model.Horaire;
import com.strime.applogger.model.Notif;
import com.strime.applogger.stub.StubSql;

import java.sql.SQLException;
import java.util.ArrayList;

import rm.com.clocks.ClockDrawable;
import rm.com.clocks.ClockImageView;
import rm.com.clocks.Stroke;

/**
 * Created by gsa13442 on 06/04/2017.
 */

public class NotifCard extends RelativeLayout  {

    private sqlHelper mHelper;

    private TextView _title;
    private ClockImageView _clock;
    private RecyclerView _rvNotif;
    private TextView _time;
    private NotificationAdapter mNotifAdapter;
    private FloatingActionButton _fabPlay;

    public NotifCard(Context context) {
        super(context);
        loadAttributes(context);
    }

    public NotifCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        loadAttributes(context);
    }

    public NotifCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadAttributes(context);
    }

    private void loadAttributes(Context context) {
        View.inflate(context, R.layout.card_notif, this);
        _title = (TextView) findViewById(R.id.title_card_notif);
        _clock = (ClockImageView) findViewById(R.id.clocks);
        ClockDrawable.builder(context)
                .hours(00)                 // initial time hours
                .minutes(00)                    // initial time minutes
                .withColor(Color.DKGRAY)                                    // set icon color
                .withFrameWidth(Stroke.REGULAR)                             // set frame width
                .withPointerWidth(Stroke.THIN)// set pointer width
                .into(_clock);
        _time = (TextView) findViewById(R.id.textViewTime);
        _time.setText("00:00");
        _rvNotif = (RecyclerView) findViewById(R.id.rvNotif);
        LinearLayoutManager llm_notif = new LinearLayoutManager(context);
        _rvNotif.setLayoutManager(llm_notif);

        _fabPlay = (FloatingActionButton) findViewById(R.id.fabPlay);
        _fabPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mNotifAdapter.startAnimation();
                } catch (Exception e) {
                    e.printStackTrace();
                    refreshAdapter();

                }
            }
        });
    }

    public void updateUI(boolean serviceRunning) {
        setVisibility(serviceRunning ? VISIBLE : GONE);

        if(serviceRunning) {
            refreshAdapter();
        }
    }

    public void refreshAdapter()  {

        ArrayList<Horaire> horaires = new ArrayList<>();

        CloseableIterator<Notif> iterator = null;
        try {

            iterator = getHelper().getNotifIterator();
            while (iterator.hasNext()) {
                horaires.addAll(iterator.next().getsHoraires());
            }
            if(!horaires.isEmpty()) {
                mNotifAdapter = new NotificationAdapter(getContext(), _clock, _time, horaires);
                _rvNotif.setAdapter(mNotifAdapter);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public sqlHelper getHelper() {
        if(mHelper == null){
            mHelper = OpenHelperManager.getHelper(getContext(), sqlHelper.class);
        }
        return mHelper;
    }
}
