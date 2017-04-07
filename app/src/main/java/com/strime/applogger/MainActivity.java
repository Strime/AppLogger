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
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.strime.applogger.service.ListenningService;


public class MainActivity extends LockActivity implements ManagerListener {

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

    //SQL
    private BroadcastReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));

        /*llBottomSheet = findViewById(R.id.bottom_sheet);// init the bottom sheet behavior
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

        OpenHelperManager.setHelper(new sqlHelper(MainActivity.this));

        /**
         * CARDS
         */
        appCard = (AppCard) findViewById(R.id.event_app_card);
        notifCard = (NotifCard) findViewById(R.id.notif_card);

        appCard.setUnderCard(notifCard);

        scrollView = (NestedScrollView) findViewById(R.id.scrollView);
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                final float percent = (scrollY * 1f) / (v.getMaxScrollAmount() - 300);
                Log.d("TAG SCROLL","p:"+percent);
                appCard.animMove(percent);
            }
        });
        recordCard = (RecordCard) findViewById(R.id.record_card);
        recordCard.setManagerListener(this);

        /**
         * NOTIF RECYCLER
         */
        /*notifLayout = (RelativeLayout) findViewById(R.id.event_notif_recycler);

        cardViewRecord = (CardView) findViewById(R.id.layoutRecord);*/


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

}
