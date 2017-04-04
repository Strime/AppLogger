package com.balram.applocker;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.balram.applocker.service.BackgroundService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Context.NOTIFICATION_SERVICE;
import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private Context appContext;

    @Before
    public void before() throws Exception {
        appContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void test_insert_notif_and_get_them() {
        Intent intent = new Intent(appContext, BackgroundService.class);
        int confValue = 1;
        intent.putExtra(BackgroundService.conf, confValue);
        intent.putExtra(BackgroundService.action, BackgroundService.ACTIONS.STARTRECORDING);
        appContext.startService(intent);


        Notification n  = new Notification.Builder(appContext)
                .setContentTitle("New mail from " + "test@gmail.com")
                .setContentText("Subject")
                .setSmallIcon(R.drawable.icon)
                .setAutoCancel(false).build();

        NotificationManager notificationManager = (NotificationManager) appContext.getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);

    }


}
