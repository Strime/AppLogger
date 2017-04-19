package com.strime.applogger.stub;

import com.strime.applogger.model.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by gsa13442 on 04/04/2017.
 */

public class StubSql {


    public static ArrayList<Event> getStubNotifEvent() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,17);
        cal.set(Calendar.MINUTE,30);

        /*ArrayList<Event> res = new ArrayList<>();

        Event event_1 = new Event(String.format("Dealabs%sNotification Title%sDescription more details",Event.SEPARATOR_NOTIF,Event.SEPARATOR_NOTIF), Event.ACTION_NOTIFICATION);
        event_1.setInsertedTime(cal.getTimeInMillis());
        cal.set(Calendar.HOUR_OF_DAY,19);
        event_1.setEndTime(cal.getTimeInMillis());
        res.add(event_1);

        Event event_2 = new Event(String.format("YouTube%sNotification Title%sDescription more details",Event.SEPARATOR_NOTIF,Event.SEPARATOR_NOTIF), Event.ACTION_NOTIFICATION);
        cal.set(Calendar.HOUR_OF_DAY,17);
        cal.set(Calendar.MINUTE,30);
        event_2.setInsertedTime(cal.getTimeInMillis());
        cal.set(Calendar.MINUTE,50);
        event_2.setEndTime(cal.getTimeInMillis());
        res.add(event_2);

        Event event_3 = new Event(String.format("SMS%sNotification Title%sDescription more details",Event.SEPARATOR_NOTIF,Event.SEPARATOR_NOTIF), Event.ACTION_NOTIFICATION);
        cal.set(Calendar.HOUR_OF_DAY,16);
        cal.set(Calendar.MINUTE,30);
        event_3.setInsertedTime(cal.getTimeInMillis());
        cal.set(Calendar.HOUR_OF_DAY,19);
        cal.set(Calendar.MINUTE,50);
        event_3.setEndTime(cal.getTimeInMillis());
        res.add(event_3);

        Event event_4 = new Event(String.format("INBOX%sNotification Title%sDescription more details",Event.SEPARATOR_NOTIF,Event.SEPARATOR_NOTIF), Event.ACTION_NOTIFICATION);
        cal.set(Calendar.HOUR_OF_DAY,16);
        cal.set(Calendar.MINUTE,04);
        event_4.setInsertedTime(cal.getTimeInMillis());
        cal.set(Calendar.HOUR_OF_DAY,19);
        cal.set(Calendar.MINUTE,07);
        event_4.setEndTime(cal.getTimeInMillis());
        res.add(event_4);*/

        return null;
    }
}
