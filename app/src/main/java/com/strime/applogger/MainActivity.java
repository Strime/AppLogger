package com.strime.applogger;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.balram.locker.utils.Locker;
import com.balram.locker.view.AppLocker;
import com.balram.locker.view.LockActivity;
import com.strime.applogger.adapter.EventAdapter;
import com.strime.applogger.adapter.EventNotifAdapter;
import com.strime.applogger.database.sqlHelper;
import com.strime.applogger.interfaces.ToolChangedListener;
import com.strime.applogger.model.Event;
import com.strime.applogger.service.ListenningService;
import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.CloseableIterator;

import java.sql.SQLException;

import static com.github.florent37.expectanim.core.Expectations.width;


public class MainActivity extends LockActivity implements ToolChangedListener {

    /**
     * CONF VALUES
     */
    private static final int LOW = 0;
    private static final int MEDIUM = 1;
    private static final int PRO = 2;
    private static final int ULTRA = 3;
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";

    //private FloatingActionButton fab;
    private EventAdapter mAppAdapter;
    private EventNotifAdapter mNotifAdapter;

    //RECYCLERS
    private RecyclerView appRecycler;
    private RecyclerView notifRecycler;

    //NESTED
    private NestedScrollView nestedRecord;
    private NestedScrollView nestedResult;

    //SQL
    private sqlHelper mHelper;

    private BroadcastReceiver receiver;

    private CardView cardViewRecord;
    private SeekBar lvlRecordSlider;
    private TextView tvExplanation;
    private FloatingActionButton fab;
    //private View llBottomSheet;

    private int confValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
        //showPhoneStatePermission();

        /*
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
        */
        fab = (FloatingActionButton) findViewById(R.id.flaottingButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListeningView();
                updateUI();
            }
        });

        OpenHelperManager.setHelper(new sqlHelper(MainActivity.this));


        nestedRecord = (NestedScrollView) findViewById(R.id.nestedRecord);
        nestedResult = (NestedScrollView) findViewById(R.id.nestedResult);
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


    private boolean showNotificationPermission() {
        if (!isNotificationListenerEnabled()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs to checks notifications");
            builder.setMessage("Please grant access.");
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                }
            });
            builder.show();
            return false;
        }
        return true;
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
        if(showPhoneStatePermission() && showNotificationPermission()) {
            Intent intent = new Intent(getApplication(), ListenningService.class);
            intent.putExtra(ListenningService.conf, confValue);
            intent.putExtra(ListenningService.action, ListenningService.ACTIONS.STARTRECORDING);
            startService(intent);


            try {
                CloseableIterator<String[]> iterator = getHelper().getEventIteratorTime(Event.APPLICATION);
                AndroidDatabaseResults appResults = (AndroidDatabaseResults) iterator.getRawResults();
                mAppAdapter = new EventAdapter(MainActivity.this, appResults.getRawCursor(), this);


                CloseableIterator<Event> notifIte = getHelper().getNotifIterator();
                AndroidDatabaseResults notifResults = (AndroidDatabaseResults) notifIte.getRawResults();
                mNotifAdapter = new EventNotifAdapter(MainActivity.this, notifResults.getRawCursor(), this);

                if(appResults.getRawCursor().getCount() > 0) {
                    appRecycler.setAdapter(mAppAdapter);
                    notifRecycler.setAdapter(mNotifAdapter);
                    cardViewRecord.setVisibility(View.GONE);
                    appRecycler.setVisibility(View.VISIBLE);
                    notifRecycler.setVisibility(View.VISIBLE);
                }
                /*if(notifResults.getRawCursor().getCount() > 0) {
                    notifRecycler.setAdapter(mNotifAdapter);
                    cardViewRecord.setVisibility(View.GONE);
                    notifRecycler.setVisibility(View.VISIBLE);
                }*/
            } catch (SQLException e) {
                e.printStackTrace();
            }

            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    mAppAdapter.notifyDataSetChanged();
                }
            };
            registerReceiver(receiver, new IntentFilter(ListenningService.APPFIND));
        }
    }

    public sqlHelper getHelper() {
        if(mHelper == null){
            mHelper = OpenHelperManager.getHelper(MainActivity.this, sqlHelper.class);
        }
        return mHelper;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isMyServiceRunning(ListenningService.class)) {
            showListeningView();
        }

        if(receiver!=null){
            registerReceiver(receiver, new IntentFilter(
                    ListenningService.APPFIND));
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
        if(isMyServiceRunning(ListenningService.class)) {
            //fab.setImageResource(R.mipmap.stop);
            nestedRecord.setVisibility(View.GONE);
            nestedResult.setVisibility(View.VISIBLE);

        } else {
            //fab.setImageResource(R.mipmap.record);
            nestedRecord.setVisibility(View.VISIBLE);
            nestedResult.setVisibility(View.GONE);
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
        Notification n  = new Notification.Builder(this)
                .setContentTitle("New mail from " + "test@gmail.com")
                .setContentText("Subject")
                .setSmallIcon(R.drawable.icon)
                .setAutoCancel(false).build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);

        /*try {
            //TERNAIRE EN FONCTION DU BOOLEAN
            CloseableIterator<String[]> iterator = isShownByDuration ? getHelper().getEventIteratorTime(Event.APPLICATION) : getHelper().getEventIteratorOcc(Event.APPLICATION);
            AndroidDatabaseResults r = (AndroidDatabaseResults) iterator.getRawResults();
            mAppAdapter.swapCursor(r.getRawCursor());
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void animateClock(final EventNotifAdapter.EventViewHolder evh, final Integer h, final Integer m) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                evh.getClockImageView().animateToTime(h, m);
                evh.getTvTime().setText(String.format("%d:%d",h, m));
            }
        });
    }


    private boolean isNotificationListenerEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }



}
