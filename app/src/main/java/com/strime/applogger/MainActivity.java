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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balram.locker.utils.Locker;
import com.balram.locker.view.AppLocker;
import com.balram.locker.view.LockActivity;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.strime.applogger.cards.AppCard;
import com.strime.applogger.cards.NotifCard;
import com.strime.applogger.cards.RecordCard;
import com.strime.applogger.database.sqlHelper;
import com.strime.applogger.interfaces.ManagerListener;
import com.strime.applogger.myviews.MyButton;
import com.strime.applogger.service.ListenningService;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;
import com.yarolegovich.slidingrootnav.callback.DragStateListener;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends LockActivity implements ManagerListener, DragStateListener {

    /**
     * CONF VALUES
     */
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";

    //private FloatingActionButton fab;
    /*private AppEventAdapter mAppAdapter;
    private NotifEventAdapter mNotifAdapter;*/

    //RECYCLERS
    private AppCard appCard;
    private RecordCard recordCard;
    private NotifCard notifCard;

    private NestedScrollView scrollView;

    //MENUS
    private RelativeLayout relativChangePwd;
    private RelativeLayout relativHistory;
    private RelativeLayout relativOut;
    private TextView _duration;
    private MyButton _pause;
    private MyButton _stop;

    //SQL
    private BroadcastReceiver receiver;
    private Timer _timerUpdateTvDuration = new Timer();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuLayout(R.layout.menu_layout)
                .withDragDistance(140) //Horizontal translation of a view. Default == 180dp
                .withRootViewScale(0.7f) //Content view's scale will be interpolated between 1f and 0.7f. Default == 0.65f;
                .withRootViewElevation(10) //Content view's elevation will be interpolated between 0 and 10dp. Default == 8.
                .withRootViewYTranslation(4) //Content view's translationY will be interpolated between 0 and 4. Default == 0
                .addDragStateListener(this)
                .inject();

        relativChangePwd = (RelativeLayout) findViewById(R.id.relativ_change_pwd);
        relativChangePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int type = Locker.CHANGE_PASSWORD;
                Intent intent = new Intent(MainActivity.this, LockActivity.class);
                intent.putExtra(Locker.TYPE, type);
                startActivityForResult(intent, type);
            }
        });

        relativHistory = (RelativeLayout) findViewById(R.id.relativ_history);
        relativHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","show history now");
            }
        });

        relativOut = (RelativeLayout) findViewById(R.id.relative_out);
        relativOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        _duration = (TextView) findViewById(R.id.tvDuration);

        _stop = (MyButton) findViewById(R.id.btnStop);
        _stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        _stop = (MyButton) findViewById(R.id.btnStop);
        _stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //my
            }
        });

        OpenHelperManager.setHelper(new sqlHelper(MainActivity.this));

        /**
         * CARDS
         */
        appCard = (AppCard) findViewById(R.id.event_app_card);
        notifCard = (NotifCard) findViewById(R.id.notif_card);

        appCard.setUnderCard(notifCard);

        scrollView = (NestedScrollView) findViewById(R.id.scrollView);
        /*scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                final float percent = (scrollY * 1f) / (v.getMaxScrollAmount() - 300);
                Log.d("TAG SCROLL","p:"+percent);
                appCard.animMove(percent);
            }
        });*/

        recordCard = (RecordCard) findViewById(R.id.record_card);
        recordCard.setManagerListener(this);

        updateUI();
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
            intent.putExtra(ListenningService.conf, recordCard.getConfValue());
            intent.putExtra(ListenningService.action, ListenningService.ACTIONS.STARTRECORDING);
            startService(intent);

            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_tutorial),Context.MODE_PRIVATE);
            sharedPref.edit().putLong("last_started_time", System.currentTimeMillis()).commit();

           /* if(appResults.getRawCursor().getCount() > 0) {
                appCard.setAdapter(mAppAdapter);
                //notifLayout.setAdapter(mNotifAdapter);
                //notifLayout.setVisibility(View.VISIBLE);
            }*/
           /* if(notifResults.getRawCursor().getCount() > 0) {
                notifRecycler.setAdapter(mNotifAdapter);
                cardViewRecord.setVisibility(View.GONE);
                notifRecycler.setVisibility(View.VISIBLE);
            }*/

            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //mAppAdapter.notifyDataSetChanged();
                }
            };
            registerReceiver(receiver, new IntentFilter(ListenningService.APPFIND));
        }
    }

   /* public sqlHelper getHelper() {
        if(mHelper == null){
            mHelper = OpenHelperManager.getHelper(MainActivity.this, sqlHelper.class);
        }
        return mHelper;
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        if(isMyServiceRunning(ListenningService.class)) {
            showListeningView();
        }

        if(receiver!=null){
            registerReceiver(receiver, new IntentFilter(ListenningService.APPFIND));
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Locker.DISABLE_PASSLOCK:
                break;
            case Locker.ENABLE_PASSLOCK:
            case Locker.CHANGE_PASSWORD:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, getString(R.string.change_passcode),
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
        boolean service_running = isMyServiceRunning(ListenningService.class);

        appCard.updateUI(service_running);
        notifCard.updateUI(service_running);
        recordCard.updateUI(service_running);
        scrollView.setVisibility(service_running ? View.VISIBLE : View.GONE);
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

    public void onToolChanged(boolean isShownByDuration) {
        Notification n  = new Notification.Builder(this)
                .setContentTitle("New mail from " + "test@gmail.com")
                .setContentText("Subject")
                .setSmallIcon(R.drawable.icon)
                .setAutoCancel(false).build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);
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

    @Override
    public void startService() {
        showListeningView();
        updateUI();
    }

    @Override
    public void onDragStart() {

    }

    @Override
    public void onDragEnd(boolean isMenuOpened) {
        if(!isMyServiceRunning(ListenningService.class))
            return;
        if(isMenuOpened) {
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_tutorial),Context.MODE_PRIVATE);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(sharedPref.getLong("last_started_time",0));

            String h = cal.get(Calendar.HOUR_OF_DAY) < 10 ? "0"+cal.get(Calendar.HOUR_OF_DAY) : ""+cal.get(Calendar.HOUR_OF_DAY);
            String m = cal.get(Calendar.MINUTE) < 10 ? "0"+cal.get(Calendar.MINUTE) : ""+cal.get(Calendar.MINUTE);
            String s = cal.get(Calendar.SECOND) < 10 ? "0"+cal.get(Calendar.SECOND) : ""+cal.get(Calendar.SECOND);

            _duration.setText(String.format("Started at  %s:%s:%s",h,m,s));

            /*_timerUpdateTvDuration.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }
            }, 0, 1000);*/
        } else {
            _duration.setText("");
        }

    }
}
