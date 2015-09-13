package com.xiaoqing.flight.data.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.xiaoqing.flight.data.dao.AcWeightLimit;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table AC_WEIGHT_LIMIT.
*/
public class AcWeightLimitDao extends AbstractDao<AcWeightLimit, Long> {

    public static final String TABLENAME = "AC_WEIGHT_LIMIT";

    /**
     * Properties of entity AcWeightLimit.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property AcType = new Property(1, String.class, "AcType", false, "AC_TYPE");
        public final static Property Weight = new Property(2, String.class, "Weight", false, "WEIGHT");
        public final static Property WeightCg1 = new Property(3, String.class, "WeightCg1", false, "WEIGHT_CG1");
        public final static Property WeightCg2 = new Property(4, String.class, "WeightCg2", false, "WEIGHT_CG2");
        public final static Property OpUser = new Property(5, String.class, "OpUser", false, "OP_USER");
        public final static Property SysVersion = new Property(6, int.class, "SysVersion", false, "SYS_VERSION");
        public final static Property OpDate = new Property(7, String.class, "OpDate", false, "OP_DATE");
    };


    public AcWeightLimitDao(DaoConfig config) {
        super(config);
    }
    
    public AcWeightLimitDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'AC_WEIGHT_LIMIT' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'AC_TYPE' TEXT," + // 1: AcType
                "'WEIGHT' TEXT," + // 2: Weight
                "'WEIGHT_CG1' TEXT," + // 3: WeightCg1
                "'WEIGHT_CG2' TEXT," + // 4: WeightCg2
                "'OP_USER' TEXT," + // 5: OpUser
                "'SYS_VERSION' INTEGER NOT NULL ," + // 6: SysVersion
                "'OP_DATE' TEXT);"); // 7: OpDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'AC_WEIGHT_LIMIT'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, AcWeightLimit entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String AcType = entity.getAcType();
        if (AcType != null) {
            stmt.bindString(2, AcType);
        }
 
        String Weight = entity.getWeight();
        if (Weight != null) {
            stmt.bindString(3, Weight);
        }
 
        String WeightCg1 = entity.getWeightCg1();
        if (WeightCg1 != null) {
            stmt.bindString(4, WeightCg1);
        }
 
        String WeightCg2 = entity.getWeightCg2();
        if (WeightCg2 != null) {
            stmt.bindString(5, WeightCg2);
        }
 
        String OpUser = entity.getOpUser();
        if (OpUser != null) {
            stmt.bindString(6, OpUser);
        }
        stmt.bindLong(7, entity.getSysVersion());
 
        String OpDate = entity.getOpDate();
        if (OpDate != null) {
            stmt.bindString(8, OpDate);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public AcWeightLimit readEntity(Cursor cursor, int offset) {
        AcWeightLimit entity = new AcWeightLimit( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // AcType
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // Weight
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // WeightCg1
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // WeightCg2
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // OpUser
            cursor.getInt(offset + 6), // SysVersion
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7) // OpDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, AcWeightLimit entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setAcType(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setWeight(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setWeightCg1(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setWeightCg2(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setOpUser(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setSysVersion(cursor.getInt(offset + 6));
        entity.setOpDate(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(AcWeightLimit entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(AcWeightLimit entity) {
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
