package com.xiaoqing.flight.data.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.xiaoqing.flight.data.dao.AcGrants;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table AcGrants.
*/
public class AcGrantsDao extends AbstractDao<AcGrants, Long> {

    public static final String TABLENAME = "AcGrants";

    /**
     * Properties of entity AcGrants.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserCode = new Property(1, String.class, "UserCode", false, "USER_CODE");
        public final static Property AcReg = new Property(2, String.class, "AcReg", false, "AC_REG");
        public final static Property AcType = new Property(3, String.class, "AcType", false, "AC_TYPE");
        public final static Property AcRegBw = new Property(4, Float.class, "AcRegBw", false, "AC_REG_BW");
        public final static Property AcLj = new Property(5, Float.class, "AcLj", false, "AC_LJ");
        public final static Property IsCaption = new Property(6, String.class, "IsCaption", false, "IS_CAPTION");
        public final static Property SysVersion = new Property(7, int.class, "SysVersion", false, "SYS_VERSION");
    };


    public AcGrantsDao(DaoConfig config) {
        super(config);
    }
    
    public AcGrantsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'AcGrants' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'USER_CODE' TEXT," + // 1: UserCode
                "'AC_REG' TEXT," + // 2: AcReg
                "'AC_TYPE' TEXT," + // 3: AcType
                "'AC_REG_BW' REAL," + // 4: AcRegBw
                "'AC_LJ' REAL," + // 5: AcLj
                "'IS_CAPTION' TEXT," + // 6: IsCaption
                "'SYS_VERSION' INTEGER NOT NULL );"); // 7: SysVersion
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'AcGrants'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, AcGrants entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String UserCode = entity.getUserCode();
        if (UserCode != null) {
            stmt.bindString(2, UserCode);
        }
 
        String AcReg = entity.getAcReg();
        if (AcReg != null) {
            stmt.bindString(3, AcReg);
        }
 
        String AcType = entity.getAcType();
        if (AcType != null) {
            stmt.bindString(4, AcType);
        }
 
        Float AcRegBw = entity.getAcRegBw();
        if (AcRegBw != null) {
            stmt.bindDouble(5, AcRegBw);
        }
 
        Float AcLj = entity.getAcLj();
        if (AcLj != null) {
            stmt.bindDouble(6, AcLj);
        }
 
        String IsCaption = entity.getIsCaption();
        if (IsCaption != null) {
            stmt.bindString(7, IsCaption);
        }
        stmt.bindLong(8, entity.getSysVersion());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public AcGrants readEntity(Cursor cursor, int offset) {
        AcGrants entity = new AcGrants( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // UserCode
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // AcReg
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // AcType
            cursor.isNull(offset + 4) ? null : cursor.getFloat(offset + 4), // AcRegBw
            cursor.isNull(offset + 5) ? null : cursor.getFloat(offset + 5), // AcLj
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // IsCaption
            cursor.getInt(offset + 7) // SysVersion
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, AcGrants entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserCode(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setAcReg(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setAcType(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setAcRegBw(cursor.isNull(offset + 4) ? null : cursor.getFloat(offset + 4));
        entity.setAcLj(cursor.isNull(offset + 5) ? null : cursor.getFloat(offset + 5));
        entity.setIsCaption(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setSysVersion(cursor.getInt(offset + 7));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(AcGrants entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(AcGrants entity) {
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