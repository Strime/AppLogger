package com.strime.applogger.service;

import android.app.Notification;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
import java.util.HashMap;

/**
 * Created by gsa13442 on 04/04/2017.
 */

public class OMGService extends NotificationListenerService {
    private static final String TAG = "OMGService";
    private HashMap<Integer,Event> notifs = new HashMap<>();
    private sqlHelper mHelper;
    private PackageManager pm;

    @Override
    public void onCreate() {
        super.onCreate();
        OpenHelperManager.setHelper(new sqlHelper(OMGService.this));
        pm = getPackageManager();
        findCurrentNotifications();
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
            mHelper = OpenHelperManager.getHelper(OMGService.this, sqlHelper.class);
        }
        return mHelper;
    }

    private void insertNotification(Integer snbId, String text) {
        try {
            final Dao<Event, Integer> eventDao = getHelper().getEventDao();
            notifs.put(snbId, new Event(text, Event.ACTION_NOTIFICATION));
            eventDao.create(notifs.get(notifs.size()-1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteNotification(Integer snbId) {
        try {
            final Dao<Event, Integer> eventDao = getHelper().getEventDao();
            Event notif = notifs.remove(snbId);
            notif.setEndTime(System.currentTimeMillis());
            eventDao.update(notif);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        //logNLS("have " + mCurrentNotificationsCounts + " active notifications");
        //mPostedNotification = sbn;
        Bundle extras = sbn.getNotification().extras;
        logNLS(String.format("onNotificationPosted : insert... %d (%s)",sbn.getId(),extras.getString(Notification.EXTRA_TITLE)));
        insertNotification(sbn.getId(), getAppNameOfPackage(sbn.getPackageName())
                + Event.SEPARATOR_NOTIF
                + extras.getString(Notification.EXTRA_TITLE)
                + Event.SEPARATOR_NOTIF
                + extras.getString(Notification.EXTRA_TEXT));
    }


    private String getAppNameOfPackage(String processName) {
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(processName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        return (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

        Bundle extras = sbn.getNotification().extras;
        logNLS(String.format("onNotificationRemoved : removed... %d (%s)",sbn.getId(),extras.getString(Notification.EXTRA_TITLE)));

        deleteNotification(sbn.getId());
    }

    private void findCurrentNotifications() {
        StatusBarNotification[] activesNos = getActiveNotifications();
        if(activesNos !=null && activesNos.length > 0) {
            for (StatusBarNotification activeNos : activesNos) {
                Bundle extras = activeNos.getNotification().extras;
                logNLS(String.format("findCurrentNotifications : insert... %d (%s)", activeNos.getId(), extras.getString(Notification.EXTRA_TITLE)));
                insertNotification(activeNos.getId(), extras.getString(Notification.EXTRA_TITLE) + " / " + extras.getString(Notification.EXTRA_TEXT));
            }
        }
    }

    private static void logNLS(Object object) {
        Log.i(TAG, ""+object);
    }

}
