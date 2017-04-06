package com.strime.applogger.cards;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.CloseableIterator;
import com.strime.applogger.App;
import com.strime.applogger.MainActivity;
import com.strime.applogger.R;
import com.strime.applogger.adapter.EventAdapter;
import com.strime.applogger.adapter.EventNotifAdapter;
import com.strime.applogger.database.sqlHelper;
import com.strime.applogger.interfaces.ManagerListener;
import com.strime.applogger.model.Event;

import java.sql.SQLException;

/**
 * Created by gsa13442 on 06/04/2017.
 */

public class AppCard extends RelativeLayout  {
    private TextView _title;
    private TextView _explainations;

    private RecyclerView _recyclerChart;
    private EventAdapter mAppAdapter;

    private FloatingActionButton _fab;

    private sqlHelper mHelper;

    public AppCard(Context context) {
        super(context);
        loadAttributes(context);
    }

    public AppCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        loadAttributes(context);
    }

    public AppCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadAttributes(context);
    }

    private void loadAttributes(Context context) {
        View.inflate(context, R.layout.card_event_app, this);
        _title = (TextView) findViewById(R.id.title_card_app);
        _explainations = (TextView) findViewById(R.id.text_explanation_app);
        _recyclerChart = (RecyclerView) findViewById(R.id.recycler_chart);
        LinearLayoutManager llm_notif = new LinearLayoutManager(context);
        _recyclerChart.setLayoutManager(llm_notif);
        _fab = (FloatingActionButton) findViewById(R.id.fabTools);

        _fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mAppAdapter.setShownByDuration(!mAppAdapter.isShownByDuration());
                updateUI(true);
            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mAppAdapter.setPie(!mAppAdapter.isPie());
                updateUI(true);
            }
        });

    }

    public void updateUI(boolean serviceRunning) {
        setVisibility(serviceRunning ? VISIBLE : GONE);
        if(serviceRunning) {
            try {
                refreshAdapter();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //_fab.setImageResource(serviceRunning ? R.mipmap.stop : R.mipmap.record);
    }

    public void refreshAdapter() throws SQLException {
        boolean isShownByDuration = false;
        if(mAppAdapter == null) {
            CloseableIterator<String[]> iterator = getHelper().getEventIteratorTime(Event.APPLICATION);
            AndroidDatabaseResults appResults = (AndroidDatabaseResults) iterator.getRawResults();
            mAppAdapter = new EventAdapter(getContext(), appResults.getRawCursor());
            _recyclerChart.setAdapter(mAppAdapter);
            //mAppAdapter.notifyDataSetChanged();
        }
        else {
            CloseableIterator<String[]> iterator = isShownByDuration ? getHelper().getEventIteratorTime(Event.APPLICATION) : getHelper().getEventIteratorOcc(Event.APPLICATION);
            AndroidDatabaseResults r = (AndroidDatabaseResults) iterator.getRawResults();
            mAppAdapter.swapCursor(r.getRawCursor());
            mAppAdapter.notifyDataSetChanged();
        }

    }

    public sqlHelper getHelper() {
        if(mHelper == null){
            mHelper = OpenHelperManager.getHelper(getContext(), sqlHelper.class);
        }
        return mHelper;
    }
}
