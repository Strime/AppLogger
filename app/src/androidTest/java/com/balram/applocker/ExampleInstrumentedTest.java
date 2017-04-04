package com.balram.applocker;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.balram.applocker.service.ListenningService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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
        Intent intent = new Intent(appContext, ListenningService.class);
        int confValue = 1;
        intent.putExtra(ListenningService.conf, confValue);
        intent.putExtra(ListenningService.action, ListenningService.ACTIONS.STARTRECORDING);
        appContext.startService(intent);




    }


}
