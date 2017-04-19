package com.strime.applogger.model;

import java.util.Calendar;

/**
 * Created by gsa13442 on 05/04/2017.
 */

public class Horaire {
    public int hour;
    public int minute;
    public long timestamp;

    public String notifName;
    public String infos;
    public Notif notif;

    public Horaire(long t, Notif notif) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(t);
        this.timestamp=t;
        this.hour=cal.get(Calendar.HOUR_OF_DAY);
        this.minute=cal.get(Calendar.MINUTE);
        this.notif = notif;
        this.notifName = notif.getNotifName();
        this.infos = this.notif.getNotifInfos();
    }
}
