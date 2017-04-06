package com.strime.applogger.cards;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.strime.applogger.MainActivity;
import com.strime.applogger.R;
import com.strime.applogger.interfaces.ManagerListener;

/**
 * Created by gsa13442 on 06/04/2017.
 */

public class RecordCard extends RelativeLayout {

    private static final int LOW = 0;
    private static final int MEDIUM = 1;
    private static final int PRO = 2;
    private static final int ULTRA = 3;


    private TextView _title;
    private TextView _sliderExplainations;

    private SeekBar _lvlRecordSlider;
    private FloatingActionButton _fab;

    private int confValue;
    private ManagerListener managerListener;

    public RecordCard(Context context) {
        super(context);
        loadAttributes(context);
    }

    public RecordCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadAttributes(context);
    }

    public RecordCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        loadAttributes(context);
    }

    private void loadAttributes(Context context) {
        View.inflate(context, R.layout.card_conf_record, this);
        _title = (TextView) findViewById(R.id.tv_record_tittle);
        _sliderExplainations = (TextView) findViewById(R.id.lvl_slider_explanation);
        _lvlRecordSlider = (SeekBar) findViewById(R.id.seekBarLvl);
        _fab = (FloatingActionButton) findViewById(R.id.flaottingButton);
        _fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                managerListener.startService();
            }
        });


        _lvlRecordSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateExplanation(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        updateExplanation(0);


    }
    private void updateExplanation(int index) {
        confValue = index;
        String txt="";
        switch (index) {
            case LOW:
                txt = "Will only record app navigation.";
                break;
            case MEDIUM:
                txt = "Allowed to record notification(s).";
                break;
            case PRO:
                txt = "Notification(s) + event(s).";
                break;
            case ULTRA:
                txt = "Notification(s) + event(s) + GPS position each 10 minutes.";
                break;
        }
        _sliderExplainations.setText(txt);
    }


    public int getConfValue() {
        return confValue;
    }

    public void updateUI(boolean serviceRunning) {
        setVisibility(serviceRunning ? GONE : VISIBLE);
    }

    public void setManagerListener(ManagerListener managerListener) {
        this.managerListener = managerListener;
    }
}
