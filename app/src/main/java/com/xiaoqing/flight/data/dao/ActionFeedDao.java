package com.xiaoqing.flight.data.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.xiaoqing.flight.data.dao.ActionFeed;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table ACTION_FEED.
*/
public class ActionFeedDao extends AbstractDao<ActionFeed, Long> {

    public static final String TABLENAME = "ACTION_FEED";

    /**
     * Properties of entity ActionFeed.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Feed_type = new Property(1, Integer.class, "feed_type", false, "FEED_TYPE");
        public final static Property Feed_id = new Property(2, String.class, "feed_id", false, "FEED_ID");
        public final static Property UserCode = new Property(3, String.class, "UserCode", false, "USER_CODE");
        public final static Property FlightId = new Property(4, String.class, "FlightId", false, "FLIGHT_ID");
        public final static Property Feed_status = new Property(5, Integer.class, "feed_status", false, "FEED_STATUS");
    };


    public ActionFeedDao(DaoConfig config) {
        super(config);
    }
    
    public ActionFeedDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ACTION_FEED' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'FEED_TYPE' INTEGER," + // 1: feed_type
                "'FEED_ID' TEXT," + // 2: feed_id
                "'USER_CODE' TEXT," + // 3: UserCode
                "'FLIGHT_ID' TEXT," + // 4: FlightId
                "'FEED_STATUS' INTEGER);"); // 5: feed_status
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ACTION_FEED'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ActionFeed entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Integer feed_type = entity.getFeed_type();
        if (feed_type != null) {
            stmt.bindLong(2, feed_type);
        }
 
        String feed_id = entity.getFeed_id();
        if (feed_id != null) {
            stmt.bindString(3, feed_id);
        }
 
        String UserCode = entity.getUserCode();
        if (UserCode != null) {
            stmt.bindString(4, UserCode);
        }
 
        String FlightId = entity.getFlightId();
        if (FlightId != null) {
            stmt.bindString(5, FlightId);
        }
 
        Integer feed_status = entity.getFeed_status();
        if (feed_status != null) {
            stmt.bindLong(6, feed_status);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ActionFeed readEntity(Cursor cursor, int offset) {
        ActionFeed entity = new ActionFeed( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // feed_type
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // feed_id
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // UserCode
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // FlightId
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5) // feed_status
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ActionFeed entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setFeed_type(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setFeed_id(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setUserCode(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setFlightId(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setFeed_status(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(ActionFeed entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(ActionFeed entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
