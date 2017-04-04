package com.strime.applogger.service;

import android.app.Notification;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.strime.applogger.database.sqlHelper;
import com.strime.applogger.model.Event;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gsa13442 on 04/04/2017.
 */

public class NotificationService extends NotificationListenerService {
    private static final String TAG = "TEST";
    private static final int EVENT_UPDATE_CURRENT_NOS = 0;
    public static final String ACTION_NLS_CONTROL = "com.seven.notificationlistenerdemo.NLSCONTROL";
    public static List<StatusBarNotification[]> mCurrentNotifications = new ArrayList<StatusBarNotification[]>();
    public static int mCurrentNotificationsCounts = 0;
    public static StatusBarNotification mPostedNotification;
    public static StatusBarNotification mRemovedNotification;

    private sqlHelper mHelper;


    @Override
    public void onCreate() {
        super.onCreate();
        OpenHelperManager.setHelper(new sqlHelper(NotificationService.this));

        logNLS("onCreate...");
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_NLS_CONTROL);
        updateCurrentNotifications();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // a.equals("b");
        logNLS("onBind...");
        return super.onBind(intent);
    }



    private sqlHelper getHelper() {
        if(mHelper == null){
            mHelper = OpenHelperManager.getHelper(NotificationService.this, sqlHelper.class);
        }
        return mHelper;
    }



    private void insertNotification(String text) {
        try {
            final Dao<Event, Integer> eventDao = getHelper().getEventDao();
            eventDao.create(new Event(text, Event.ACTION_NOTIFICATION));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        updateCurrentNotifications();
        logNLS("onNotificationPosted...");
        logNLS("have " + mCurrentNotificationsCounts + " active notifications");
        mPostedNotification = sbn;
        Bundle extras = sbn.getNotification().extras;
        insertNotification(extras.getString(Notification.EXTRA_TITLE) + " / "+extras.getString(Notification.EXTRA_TEXT));
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        updateCurrentNotifications();
        logNLS("removed...");
        logNLS("have " + mCurrentNotificationsCounts + " active notifications");
        mRemovedNotification = sbn;
    }

    private void updateCurrentNotifications() {
        try {
            StatusBarNotification[] activeNos = getActiveNotifications();
            if (mCurrentNotifications.size() == 0) {
                mCurrentNotifications.add(null);
            }
            mCurrentNotifications.set(0, activeNos);
            mCurrentNotificationsCounts = activeNos.length;
        } catch (Exception e) {
            logNLS("Should not be hee!!");
            e.printStackTrace();
        }
    }

    public static StatusBarNotification[] getCurrentNotifications() {
        if (mCurrentNotifications.size() == 0) {
            logNLS("mCurrentNotifications size is ZERO!!");
            return null;
        }
        return mCurrentNotifications.get(0);
    }

    private static void logNLS(Object object) {
        Log.i(TAG, ""+object);
    }

}
