package com.uandme.flight.data.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.uandme.flight.data.dao.User;
import com.uandme.flight.data.dao.SystemVersion;

import com.uandme.flight.data.dao.UserDao;
import com.uandme.flight.data.dao.SystemVersionDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig userDaoConfig;
    private final DaoConfig systemVersionDaoConfig;

    private final UserDao userDao;
    private final SystemVersionDao systemVersionDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        userDaoConfig.initIdentityScope(type);

        systemVersionDaoConfig = daoConfigMap.get(SystemVersionDao.class).clone();
        systemVersionDaoConfig.initIdentityScope(type);

        userDao = new UserDao(userDaoConfig, this);
        systemVersionDao = new SystemVersionDao(systemVersionDaoConfig, this);

        registerDao(User.class, userDao);
        registerDao(SystemVersion.class, systemVersionDao);
    }
    
    public void clear() {
        userDaoConfig.getIdentityScope().clear();
        systemVersionDaoConfig.getIdentityScope().clear();
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public SystemVersionDao getSystemVersionDao() {
        return systemVersionDao;
    }

}
