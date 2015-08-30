package com.xiaoqing.flight.data.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.xiaoqing.flight.data.dao.Passenger;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table PASSENGER.
*/
public class PassengerDao extends AbstractDao<Passenger, Long> {

    public static final String TABLENAME = "PASSENGER";

    /**
     * Properties of entity Passenger.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserName = new Property(1, String.class, "userName", false, "USER_NAME");
        public final static Property UserWeight = new Property(2, Double.class, "userWeight", false, "USER_WEIGHT");
        public final static Property AircraftReg = new Property(3, String.class, "AircraftReg", false, "AIRCRAFT_REG");
        public final static Property IsChecked = new Property(4, Boolean.class, "isChecked", false, "IS_CHECKED");
    };


    public PassengerDao(DaoConfig config) {
        super(config);
    }
    
    public PassengerDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'PASSENGER' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'USER_NAME' TEXT," + // 1: userName
                "'USER_WEIGHT' REAL," + // 2: userWeight
                "'AIRCRAFT_REG' TEXT," + // 3: AircraftReg
                "'IS_CHECKED' INTEGER);"); // 4: isChecked
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'PASSENGER'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Passenger entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String userName = entity.getUserName();
        if (userName != null) {
            stmt.bindString(2, userName);
        }
 
        Double userWeight = entity.getUserWeight();
        if (userWeight != null) {
            stmt.bindDouble(3, userWeight);
        }
 
        String AircraftReg = entity.getAircraftReg();
        if (AircraftReg != null) {
            stmt.bindString(4, AircraftReg);
        }
 
        Boolean isChecked = entity.getIsChecked();
        if (isChecked != null) {
            stmt.bindLong(5, isChecked ? 1l: 0l);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Passenger readEntity(Cursor cursor, int offset) {
        Passenger entity = new Passenger( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // userName
            cursor.isNull(offset + 2) ? null : cursor.getDouble(offset + 2), // userWeight
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // AircraftReg
            cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0 // isChecked
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Passenger entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setUserWeight(cursor.isNull(offset + 2) ? null : cursor.getDouble(offset + 2));
        entity.setAircraftReg(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setIsChecked(cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0);
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Passenger entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Passenger entity) {
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
