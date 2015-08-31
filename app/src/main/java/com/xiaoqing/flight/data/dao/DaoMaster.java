package com.xiaoqing.flight.data.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.identityscope.IdentityScopeType;

import com.xiaoqing.flight.data.dao.UserDao;
import com.xiaoqing.flight.data.dao.SystemVersionDao;
import com.xiaoqing.flight.data.dao.AllAcTypeDao;
import com.xiaoqing.flight.data.dao.AcWeightLimitDao;
import com.xiaoqing.flight.data.dao.FuleLimitDao;
import com.xiaoqing.flight.data.dao.AllAircraftDao;
import com.xiaoqing.flight.data.dao.AllSbDao;
import com.xiaoqing.flight.data.dao.SeatByAcRegDao;
import com.xiaoqing.flight.data.dao.AddFlightInfoDao;
import com.xiaoqing.flight.data.dao.SystemNoticeDao;
import com.xiaoqing.flight.data.dao.ReadSystemNoticeDao;
import com.xiaoqing.flight.data.dao.PassengerDao;
import com.xiaoqing.flight.data.dao.AcGrantsDao;
import com.xiaoqing.flight.data.dao.ActionFeedDao;
import com.xiaoqing.flight.data.dao.AllAirportDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * Master of DAO (schema version 1): knows all DAOs.
*/
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 1;

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
        UserDao.createTable(db, ifNotExists);
        SystemVersionDao.createTable(db, ifNotExists);
        AllAcTypeDao.createTable(db, ifNotExists);
        AcWeightLimitDao.createTable(db, ifNotExists);
        FuleLimitDao.createTable(db, ifNotExists);
        AllAircraftDao.createTable(db, ifNotExists);
        AllSbDao.createTable(db, ifNotExists);
        SeatByAcRegDao.createTable(db, ifNotExists);
        AddFlightInfoDao.createTable(db, ifNotExists);
        SystemNoticeDao.createTable(db, ifNotExists);
        ReadSystemNoticeDao.createTable(db, ifNotExists);
        PassengerDao.createTable(db, ifNotExists);
        AcGrantsDao.createTable(db, ifNotExists);
        ActionFeedDao.createTable(db, ifNotExists);
        AllAirportDao.createTable(db, ifNotExists);
    }
    
    /** Drops underlying database table using DAOs. */
    public static void dropAllTables(SQLiteDatabase db, boolean ifExists) {
        UserDao.dropTable(db, ifExists);
        SystemVersionDao.dropTable(db, ifExists);
        AllAcTypeDao.dropTable(db, ifExists);
        AcWeightLimitDao.dropTable(db, ifExists);
        FuleLimitDao.dropTable(db, ifExists);
        AllAircraftDao.dropTable(db, ifExists);
        AllSbDao.dropTable(db, ifExists);
        SeatByAcRegDao.dropTable(db, ifExists);
        AddFlightInfoDao.dropTable(db, ifExists);
        SystemNoticeDao.dropTable(db, ifExists);
        ReadSystemNoticeDao.dropTable(db, ifExists);
        PassengerDao.dropTable(db, ifExists);
        AcGrantsDao.dropTable(db, ifExists);
        ActionFeedDao.dropTable(db, ifExists);
        AllAirportDao.dropTable(db, ifExists);
    }
    
    public static abstract class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }
    
    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db, true);
            onCreate(db);
        }
    }

    public DaoMaster(SQLiteDatabase db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(UserDao.class);
        registerDaoClass(SystemVersionDao.class);
        registerDaoClass(AllAcTypeDao.class);
        registerDaoClass(AcWeightLimitDao.class);
        registerDaoClass(FuleLimitDao.class);
        registerDaoClass(AllAircraftDao.class);
        registerDaoClass(AllSbDao.class);
        registerDaoClass(SeatByAcRegDao.class);
        registerDaoClass(AddFlightInfoDao.class);
        registerDaoClass(SystemNoticeDao.class);
        registerDaoClass(ReadSystemNoticeDao.class);
        registerDaoClass(PassengerDao.class);
        registerDaoClass(AcGrantsDao.class);
        registerDaoClass(ActionFeedDao.class);
        registerDaoClass(AllAirportDao.class);
    }
    
    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }
    
    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }
    
}
