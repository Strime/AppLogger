package com.balram.applocker;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.balram.applocker.adapter.EventAdapter;
import com.balram.applocker.adapter.EventNotifAdapter;
import com.balram.applocker.database.SqlHelper;
import com.balram.applocker.interfaces.ToolChangedListener;
import com.balram.applocker.model.Event;
import com.balram.applocker.service.BackgroundService;
import com.balram.locker.utils.Locker;
import com.balram.locker.view.AppLocker;
import com.balram.locker.view.LockActivity;
import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.CloseableIterator;

import java.sql.SQLException;


public class MainActivity extends LockActivity implements ToolChangedListener {

    /**
     * CONF VALUES
     */
    private static final int LOW = 0;
    private static final int MEDIUM = 1;
    private static final int PRO = 2;
    private static final int ULTRA = 3;

    private FloatingActionButton fab;
    private EventAdapter mAppAdapter;
    private EventNotifAdapter mNotifAdapter;

    //RECYCLERS
    private RecyclerView appRecycler;
    private RecyclerView notifRecycler;

    //SQL
    private SqlHelper mHelper;

    private BroadcastReceiver receiver;

    private CardView cardViewRecord;
    private SeekBar lvlRecordSlider;
    private TextView tvExplanation;
    private BottomSheetBehavior bottomSheetBehavior;
    private View llBottomSheet;


    private int confValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //showPhoneStatePermission();

        fab = (FloatingActionButton) findViewById(R.id.flaottingButton);
        llBottomSheet = findViewById(R.id.bottom_sheet);// init the bottom sheet behavior
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        llBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // this part hides the button immediately and waits bottom sheet
                // to collapse to show
                if (BottomSheetBehavior.STATE_EXPANDED == newState) {
                    fab.animate().scaleX(0).scaleY(0).setDuration(300).start();
                } else if (BottomSheetBehavior.STATE_DRAGGING == newState) {
                    fab.animate().scaleX(0).scaleY(0).setDuration(300).start();
                } else if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                    fab.animate().scaleX(1).scaleY(1).setDuration(300).start();
                }
            }
            @Override
            public void onSlide(@NonNull final View bottomSheet, float slideOffset) {}
        });
        

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListeningView();
                updateUI();
            }
        });

        OpenHelperManager.setHelper(new SqlHelper(MainActivity.this));
        /**
         * APP RECYCLER
         */
        appRecycler = (RecyclerView) findViewById(R.id.event_app_recycler);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        appRecycler.setLayoutManager(llm);

        /**
         * NOTIF RECYCLER
         */
        notifRecycler = (RecyclerView) findViewById(R.id.event_notif_recycler);
        LinearLayoutManager llm_notif = new LinearLayoutManager(this);
        notifRecycler.setLayoutManager(llm_notif);


        cardViewRecord = (CardView) findViewById(R.id.layoutRecord);
        tvExplanation = (TextView) findViewById(R.id.lvl_slider_explanation);
        updateExplanation(0);

        lvlRecordSlider = (SeekBar) findViewById(R.id.seekBarLvl);
        lvlRecordSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

        updateUI();
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
        tvExplanation.setText(txt);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean showPhoneStatePermission() {
        if (!checkPackageUsagePermission()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs to others apps usage access");
            builder.setMessage("Please grant access.");
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                }
            });
            builder.show();
            return false;
        }
        return true;
    }

    private boolean checkPackageUsagePermission() {
        AppOpsManager appOps = (AppOpsManager) getApplicationContext()
                .getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getApplicationContext().getPackageName());

        if (mode == AppOpsManager.MODE_DEFAULT) {
            return (getApplicationContext().checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        }

        return (mode == AppOpsManager.MODE_ALLOWED);
    }

    private void showListeningView() {
        if(showPhoneStatePermission()) {
            Intent intent = new Intent(getApplication(), BackgroundService.class);
            intent.putExtra(BackgroundService.conf, confValue);
            intent.putExtra(BackgroundService.action, BackgroundService.ACTIONS.STARTRECORDING);
            startService(intent);

            try {
                CloseableIterator<String[]> iterator = getHelper().getEventIteratorTime(Event.APPLICATION);
                AndroidDatabaseResults r = (AndroidDatabaseResults) iterator.getRawResults();
                mAppAdapter = new EventAdapter(MainActivity.this, r.getRawCursor(), this);


                CloseableIterator<String[]> notifIte = getHelper().getEventIteratorTime(Event.APPLICATION);
                AndroidDatabaseResults notifResults = (AndroidDatabaseResults) notifIte.getRawResults();
                mNotifAdapter = new EventNotifAdapter(MainActivity.this, notifResults.getRawCursor(), this);

                if(r.getRawCursor().getCount() > 0) {
                    appRecycler.setAdapter(mAppAdapter);
                    //TODO : CHECK IF THERE IS DATA OR NOT
                    cardViewRecord.setVisibility(View.GONE);
                    appRecycler.setVisibility(View.VISIBLE);
                    notifRecycler.setVisibility(View.VISIBLE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    mAppAdapter.notifyDataSetChanged();
                }
            };
            registerReceiver(receiver, new IntentFilter(BackgroundService.APPFIND));
        }
    }

    public SqlHelper getHelper() {
        if(mHelper == null){
            mHelper = OpenHelperManager.getHelper(MainActivity.this, SqlHelper.class);
        }
        return mHelper;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(receiver!=null){
            registerReceiver(receiver, new IntentFilter(
                    BackgroundService.APPFIND));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mHelper!=null) {
            OpenHelperManager.releaseHelper();
            mHelper = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Locker.DISABLE_PASSLOCK:
                break;
            case Locker.ENABLE_PASSLOCK:
            case Locker.CHANGE_PASSWORD:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, getString(R.string.setup_passcode),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
        updateUI();
    }

    private void updateUI() {
        if (!AppLocker.getInstance().getAppLock().isPasscodeSet()) {
            int type = Locker.ENABLE_PASSLOCK;
            Intent intent = new Intent(this, LockActivity.class);
            intent.putExtra(Locker.TYPE, type);
            startActivityForResult(intent, type);
        }
        if(isMyServiceRunning(BackgroundService.class)) {
            fab.setImageResource(R.mipmap.stop);
        } else {
            fab.setImageResource(R.mipmap.record);
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onToolChanged(boolean isShownByDuration) {
        try {
            //TERNAIRE EN FONCTION DU BOOLEAN
            CloseableIterator<String[]> iterator = isShownByDuration ? getHelper().getEventIteratorTime(Event.APPLICATION) : getHelper().getEventIteratorOcc(Event.APPLICATION);
            AndroidDatabaseResults r = (AndroidDatabaseResults) iterator.getRawResults();
            mAppAdapter.swapCursor(r.getRawCursor());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
