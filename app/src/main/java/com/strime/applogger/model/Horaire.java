package com.strime.applogger.model;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by gsa13442 on 05/04/2017.
 */

public class Horaire {
    public int hour;
    public int minute;
    public long timestamp;

    public String[] infos;
    public Event event;

    public Horaire(long t, Event event) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(t);
        this.timestamp=t;
        this.hour=cal.get(Calendar.HOUR_OF_DAY);
        this.minute=cal.get(Calendar.MINUTE);
        this.event = event;
        this.infos = this.event.getAppName().split(Event.SEPARATOR_NOTIF);
    }
}
