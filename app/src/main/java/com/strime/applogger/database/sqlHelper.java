package com.strime.applogger.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.strime.applogger.R;
import com.strime.applogger.model.Event;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by GSA13442 on 28/02/2017.
 */

public class sqlHelper extends OrmLiteSqliteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "AppLogger.db";
    private static final String TAG = "sqlHelper";

    private Dao<Event, Integer> eventDao;

    public sqlHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Event.class);
        } catch (SQLException e) {
            Log.d(TAG, "Unable to create database",e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Event.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Log.d(TAG, "Unable to drop database",e);
        }
    }


    public Dao<Event, Integer> getEventDao() throws SQLException {
        if(eventDao == null) {
            eventDao = getDao(Event.class);
        }
        return eventDao;
    }


    public CloseableIterator<String[]> getEventIteratorOcc(int id_type_event) throws SQLException {
        return getEventDao().queryRaw("SELECT COUNT(inserted_time) AS nbOcc, event_name, _id FROM app_event where type_event = " + id_type_event +
                " GROUP BY event_name ORDER BY nbOcc desc").closeableIterator();
    }

    public CloseableIterator<String[]> getEventIteratorTime(int id_type_event) throws SQLException {
        return getEventDao().queryRaw("SELECT sum(inserted_time) AS totalInsert, sum(end_time) AS totalEnd, event_name, _id" +
                " FROM app_event where type_event = " + id_type_event +
                " GROUP BY event_name").closeableIterator();
    }

    public CloseableIterator<Event> getNotifIterator() throws SQLException {
        return getEventDao().queryBuilder().where().eq("type_event",Event.ACTION_NOTIFICATION).iterator();
    }
    public Event getLastAction(int type_action) throws SQLException {
        final Dao<Event, Integer> eventDao = getEventDao();
        return eventDao.queryBuilder().orderBy("_id", false).where().eq("type_event", type_action).queryForFirst();
    }

    public void update(Event e) throws SQLException {
        getEventDao().update(e);
    }
}