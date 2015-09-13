package com.xiaoqing.flight.data.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.xiaoqing.flight.data.dao.AllAcType;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table ALL_AC_TYPE.
*/
public class AllAcTypeDao extends AbstractDao<AllAcType, Long> {

    public static final String TABLENAME = "ALL_AC_TYPE";

    /**
     * Properties of entity AllAcType.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property AircraftType = new Property(1, String.class, "AircraftType", false, "AIRCRAFT_TYPE");
        public final static Property PortLimit = new Property(2, float.class, "PortLimit", false, "PORT_LIMIT");
        public final static Property LimitType = new Property(3, String.class, "LimitType", false, "LIMIT_TYPE");
        public final static Property AircraftTypeChName = new Property(4, String.class, "AircraftTypeChName", false, "AIRCRAFT_TYPE_CH_NAME");
        public final static Property TofWeightLimit = new Property(5, float.class, "TofWeightLimit", false, "TOF_WEIGHT_LIMIT");
        public final static Property LandWeightLimit = new Property(6, float.class, "LandWeightLimit", false, "LAND_WEIGHT_LIMIT");
        public final static Property Mzfw = new Property(7, float.class, "Mzfw", false, "MZFW");
        public final static Property Mac = new Property(8, String.class, "Mac", false, "MAC");
        public final static Property Mac2 = new Property(9, String.class, "Mac2", false, "MAC2");
        public final static Property OpDate = new Property(10, String.class, "OpDate", false, "OP_DATE");
        public final static Property SysVersion = new Property(11, int.class, "SysVersion", false, "SYS_VERSION");
        public final static Property UserCode = new Property(12, String.class, "UserCode", false, "USER_CODE");
        public final static Property MacFlg = new Property(13, String.class, "MacFlg", false, "MAC_FLG");
        public final static Property MaxFule = new Property(14, Float.class, "MaxFule", false, "MAX_FULE");
    };


    public AllAcTypeDao(DaoConfig config) {
        super(config);
    }
    
    public AllAcTypeDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ALL_AC_TYPE' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'AIRCRAFT_TYPE' TEXT NOT NULL ," + // 1: AircraftType
                "'PORT_LIMIT' REAL NOT NULL ," + // 2: PortLimit
                "'LIMIT_TYPE' TEXT," + // 3: LimitType
                "'AIRCRAFT_TYPE_CH_NAME' TEXT," + // 4: AircraftTypeChName
                "'TOF_WEIGHT_LIMIT' REAL NOT NULL ," + // 5: TofWeightLimit
                "'LAND_WEIGHT_LIMIT' REAL NOT NULL ," + // 6: LandWeightLimit
                "'MZFW' REAL NOT NULL ," + // 7: Mzfw
                "'MAC' TEXT," + // 8: Mac
                "'MAC2' TEXT," + // 9: Mac2
                "'OP_DATE' TEXT," + // 10: OpDate
                "'SYS_VERSION' INTEGER NOT NULL ," + // 11: SysVersion
                "'USER_CODE' TEXT," + // 12: UserCode
                "'MAC_FLG' TEXT," + // 13: MacFlg
                "'MAX_FULE' REAL);"); // 14: MaxFule
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ALL_AC_TYPE'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, AllAcType entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getAircraftType());
        stmt.bindDouble(3, entity.getPortLimit());
 
        String LimitType = entity.getLimitType();
        if (LimitType != null) {
            stmt.bindString(4, LimitType);
        }
 
        String AircraftTypeChName = entity.getAircraftTypeChName();
        if (AircraftTypeChName != null) {
            stmt.bindString(5, AircraftTypeChName);
        }
        stmt.bindDouble(6, entity.getTofWeightLimit());
        stmt.bindDouble(7, entity.getLandWeightLimit());
        stmt.bindDouble(8, entity.getMzfw());
 
        String Mac = entity.getMac();
        if (Mac != null) {
            stmt.bindString(9, Mac);
        }
 
        String Mac2 = entity.getMac2();
        if (Mac2 != null) {
            stmt.bindString(10, Mac2);
        }
 
        String OpDate = entity.getOpDate();
        if (OpDate != null) {
            stmt.bindString(11, OpDate);
        }
        stmt.bindLong(12, entity.getSysVersion());
 
        String UserCode = entity.getUserCode();
        if (UserCode != null) {
            stmt.bindString(13, UserCode);
        }
 
        String MacFlg = entity.getMacFlg();
        if (MacFlg != null) {
            stmt.bindString(14, MacFlg);
        }
 
        Float MaxFule = entity.getMaxFule();
        if (MaxFule != null) {
            stmt.bindDouble(15, MaxFule);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public AllAcType readEntity(Cursor cursor, int offset) {
        AllAcType entity = new AllAcType( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // AircraftType
            cursor.getFloat(offset + 2), // PortLimit
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // LimitType
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // AircraftTypeChName
            cursor.getFloat(offset + 5), // TofWeightLimit
            cursor.getFloat(offset + 6), // LandWeightLimit
            cursor.getFloat(offset + 7), // Mzfw
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // Mac
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // Mac2
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // OpDate
            cursor.getInt(offset + 11), // SysVersion
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // UserCode
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // MacFlg
            cursor.isNull(offset + 14) ? null : cursor.getFloat(offset + 14) // MaxFule
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, AllAcType entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setAircraftType(cursor.getString(offset + 1));
        entity.setPortLimit(cursor.getFloat(offset + 2));
        entity.setLimitType(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setAircraftTypeChName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setTofWeightLimit(cursor.getFloat(offset + 5));
        entity.setLandWeightLimit(cursor.getFloat(offset + 6));
        entity.setMzfw(cursor.getFloat(offset + 7));
        entity.setMac(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setMac2(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setOpDate(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setSysVersion(cursor.getInt(offset + 11));
        entity.setUserCode(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setMacFlg(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setMaxFule(cursor.isNull(offset + 14) ? null : cursor.getFloat(offset + 14));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(AllAcType entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(AllAcType entity) {
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
