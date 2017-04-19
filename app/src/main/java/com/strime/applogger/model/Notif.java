package com.strime.applogger.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by GSA13442 on 28/02/2017.
 */

@DatabaseTable(tableName="app_notif")
public class Notif implements Serializable {
    public static final int NO_ENDING_TIME = -1;
    public static final String SEPARATOR_NOTIF = "&&";

    public static final String MISSING_INFOS = "MISSING INFOS";

    @DatabaseField(generatedId = true, columnName = "_id")
    private int eventId;

    @DatabaseField(columnName = "notif_name")
    private String notifName;

    @DatabaseField(columnName = "notif_infos")
    private String notifInfos;

    @DatabaseField(columnName = "notif_more_infos")
    private String notifMoreInfos;

    @DatabaseField(columnName = "inserted_time")
    private long insertedTime;

    @DatabaseField(columnName = "end_time")
    private long endTime;


    public Notif() {
        super();
    }

    public Notif(String notifName, String notifInfos, String notifMoreInfos){
        super();

        this.insertedTime =  System.currentTimeMillis();
        this.endTime = NO_ENDING_TIME;
        this.notifName = notifName;
        this.notifInfos = notifInfos;
        this.notifMoreInfos = notifMoreInfos;
    }

    public String getNotifName() {
        return notifName;
    }

    public void setNotifName(String notifName) {
        this.notifName = notifName;
    }

    public long getInsertedTime() {
        return insertedTime;
    }

    public void setInsertedTime(long insertedTime) {
        this.insertedTime = insertedTime;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getNotifInfos() {
        return (notifInfos == null ? MISSING_INFOS : notifInfos)+" / "+(notifMoreInfos == null ? MISSING_INFOS : notifMoreInfos);
    }

    public ArrayList<Horaire> getsHoraires() {
        ArrayList<Horaire> horaires = new ArrayList<>();
        horaires.add(new Horaire(getInsertedTime(),this));
        horaires.add(new Horaire(getEndTime(),this));
        return horaires;
    }
}
