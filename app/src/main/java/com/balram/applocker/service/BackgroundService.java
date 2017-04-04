package com.balram.applocker.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.balram.applocker.MainActivity;
import com.balram.applocker.R;
import com.balram.applocker.database.SqlHelper;
import com.balram.applocker.model.Event;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by GSA13442 on 27/02/2017.
 */
public class BackgroundService extends NotificationListenerService {
    private static final String TAG = "SERVICE";
    private static final String IGNORE_TASK = "IGNORE_TASK";

    private Handler handler = new Handler();
    private SqlHelper mHelper;
    private static final String NAVIGATE_PACKAGE = "com.google.android.googlequicksearchbox";
    private BroadcastReceiver receiver;
    private boolean shouldCheckApp;
    private PackageManager pm;
    private String launcherPackage;
    private int confValue;


    public static enum ACTIONS {
        STARTRECORDING;
    }

    public static final String conf = "CONFIGURATION";
    public static final String action = "ACTION";
    public static final String appname = "NAME";
    public static final String APPFIND = "com.sancassg.appfind";


    public BackgroundService() {  }

    @Override
    public void onCreate() {
        pm = getPackageManager();
        Toast.makeText(getBaseContext(), "Service is created", Toast.LENGTH_SHORT).show();
        OpenHelperManager.setHelper(new SqlHelper(BackgroundService.this));
        shouldCheckApp = true;

        /**
         * GET LAUNCHER PACKAGE STRING NAME
         */
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        launcherPackage = resolveInfo.activityInfo.packageName;


        confValue = intent.getIntExtra(conf, 0);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "ACTION INTENT "+intent.getAction());

                if(intent.getAction().equals(Intent.ACTION_CAMERA_BUTTON)) {
                    Log.d(TAG,"ACTION_CAMERA_BUTTON");
                }

                if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                    String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER).toString();
                    Log.d(TAG,"ACTION_NEW_OUTGOING_CALL "+number);


                    try {
                        insertEvent(Event.ACTION_PHONE, number);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    shouldCheckApp = false;

                    try {
                        insertEvent(Event.ACTION_SHUTDOWN);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                if(intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                    shouldCheckApp = true;

                    try {
                        Event e = getHelper().getLastAction(Event.ACTION_SHUTDOWN);
                        e.setEndTime(System.currentTimeMillis());
                        getHelper().update(e);

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    handler.postDelayed(checkRunningApp, 1000);
                }
                if(intent.getAction().equals(Intent.ACTION_CALL)) {

                }
            }
        };

        IntentFilter filter = new IntentFilter();

        /**
         * CONF LVL 0
         */
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        /**
         * CONF LVL 1
         */
        filter.addAction(Intent.ACTION_DIAL);
        filter.addAction(Intent.ACTION_CAMERA_BUTTON);
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);

        registerReceiver(receiver, filter);

        super.onCreate();
    }
    private void insertEvent(int type_action) throws Exception {

        switch (type_action) {
            case Event.ACTION_SHUTDOWN:
                insertEvent(type_action, "Screen off");
                break;
            default:
                throw new Exception("NOT YET HANDLE :" +type_action);
        }

    }

    private void insertEvent(int type_action, String text) {
        try {
            final Dao<Event, Integer> eventDao = getHelper().getEventDao();
            eventDao.create(new Event(text, type_action));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d("onNotificationPosted", "id = " + sbn.getId() + "Package Name" + sbn.getPackageName() +
                "Post time = " + sbn.getPostTime() + "Tag = " + sbn.getTag());

        try {
            insertEvent(Event.ACTION_NOTIFICATION, sbn.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private SqlHelper getHelper() {
        if(mHelper == null){
            mHelper = OpenHelperManager.getHelper(BackgroundService.this, SqlHelper.class);
        }
        return mHelper;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getBaseContext(), "Service is destroyed", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch ((ACTIONS) intent.getSerializableExtra(BackgroundService.action)) {
            case STARTRECORDING:
                Intent notificationIntent = new Intent(this, MainActivity.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                        notificationIntent, 0);

                Notification notification = new NotificationCompat.Builder(this)
                        .setContentTitle("Truiton Music Player")
                        .setTicker("Truiton Music Player")
                        .setContentIntent(pendingIntent)
                        .setContentText("Test")
                        .setOngoing(true).build();
                startForeground(101,notification);

                handler.postDelayed(checkRunningApp, 1000);
                break;
        }
        return START_NOT_STICKY;
    }

    private final Runnable checkRunningApp = new Runnable(){

        @Override
        public void run(){

            String currentApp = getTopTask();
            if(!currentApp.equals(IGNORE_TASK)) {
                manageTopTask(currentApp);

                Intent intent = new Intent(BackgroundService.APPFIND);
                intent.putExtra(BackgroundService.appname, currentApp);
                sendBroadcast(intent);
            }

            if (shouldCheckApp) {
                handler.postDelayed(checkRunningApp, 1000);
            }


        };
    };

    private String getTopTask() {
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    String packageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                    return getAppNameOfPackage(packageName);
                }
            }
        } else {
            ActivityManager am = (ActivityManager) getApplication().getSystemService(ACTIVITY_SERVICE);

            // get the info from the currently running task
            List<ActivityManager.RunningAppProcessInfo> taskInfo = am.getRunningAppProcesses();


            //si on ne navigue pas dans les menus
            if (!taskInfo.get(0).processName.equals(NAVIGATE_PACKAGE)) {
                return getAppNameOfPackage(taskInfo.get(0).processName);
            }
        }
        return IGNORE_TASK;
    }

    private String getAppNameOfPackage(String processName) {
        ApplicationInfo ai;
        if(shouldIgnoreThisPackage(processName)) {
            return IGNORE_TASK;
        }
        try {
            ai = pm.getApplicationInfo(processName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        return (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
    }

    private boolean shouldIgnoreThisPackage(String processName) {
        if(processName.contains(launcherPackage)
                || processName.contains(getApplicationContext().getPackageName())
                || processName.contains(getString(R.string.android_systemui))
                || !shouldCheckApp) {
            return true;
        }
        return false;
    }

    private void manageTopTask(String applicationName) {
        try {
            final Dao<Event, Integer> eventDao = getHelper().getEventDao();
            List<Event> events = eventDao.queryForAll();
            if (events.size() > 0) {
                Event lastEvent = events.get(events.size() - 1);
                if (!lastEvent.getAppName().equals(applicationName)) {
                    eventDao.create(new Event(applicationName));
                    Log.d(TAG, applicationName + " inserted !");
                } else {
                    lastEvent.setEndTime(System.currentTimeMillis());
                    eventDao.update(lastEvent);
                    Log.d(TAG, applicationName + " updated !");
                }
            } else {
                eventDao.create(new Event(applicationName));
                Log.d(TAG, applicationName + " inserted !");
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
