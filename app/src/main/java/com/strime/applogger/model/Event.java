package com.strime.applogger.model;

import java.io.Serializable;
import java.util.ArrayList;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by GSA13442 on 28/02/2017.
 */

@DatabaseTable(tableName="app_event")
public class Event implements Serializable {
    public static final int APPLICATION = 0;
    public static final int ACTION_SHUTDOWN = 1;
    public static final int ACTION_PHONE = 2;

    public static final int NO_ENDING_TIME = -1;

    @DatabaseField(generatedId = true, columnName = "_id")
    private int eventId;

    @DatabaseField(columnName = "event_name")
    private String appName;

    @DatabaseField(columnName = "type_event")
    private int typeEvent;

    @DatabaseField(columnName = "inserted_time")
    private long insertedTime;

    @DatabaseField(columnName = "end_time")
    private long endTime;


    public Event() {
        super();
    }

    public Event(String appName){
        this(appName, APPLICATION);
    }
    public Event(String appName, int type){
        super();

        this.insertedTime =  System.currentTimeMillis();
        this.endTime = NO_ENDING_TIME;
        this.endTime = System.currentTimeMillis();

        this.appName=appName;
        this.typeEvent = type;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
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

    public int getTypeEvent() {
        return typeEvent;
    }

    public void setTypeEvent(int typeEvent) {
        this.typeEvent = typeEvent;
    }
}
