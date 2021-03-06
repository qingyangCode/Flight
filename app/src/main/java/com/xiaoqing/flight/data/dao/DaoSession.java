package com.xiaoqing.flight.data.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.xiaoqing.flight.data.dao.User;
import com.xiaoqing.flight.data.dao.SystemVersion;
import com.xiaoqing.flight.data.dao.AllAcType;
import com.xiaoqing.flight.data.dao.AcWeightLimit;
import com.xiaoqing.flight.data.dao.FuleLimit;
import com.xiaoqing.flight.data.dao.AllAircraft;
import com.xiaoqing.flight.data.dao.AllSb;
import com.xiaoqing.flight.data.dao.SeatByAcReg;
import com.xiaoqing.flight.data.dao.AddFlightInfo;
import com.xiaoqing.flight.data.dao.SystemNotice;
import com.xiaoqing.flight.data.dao.ReadSystemNotice;
import com.xiaoqing.flight.data.dao.Passenger;
import com.xiaoqing.flight.data.dao.AcGrants;
import com.xiaoqing.flight.data.dao.ActionFeed;
import com.xiaoqing.flight.data.dao.AllAirport;
import com.xiaoqing.flight.data.dao.UploadAirPerson;
import com.xiaoqing.flight.data.dao.AllAcSb;

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
import com.xiaoqing.flight.data.dao.UploadAirPersonDao;
import com.xiaoqing.flight.data.dao.AllAcSbDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig userDaoConfig;
    private final DaoConfig systemVersionDaoConfig;
    private final DaoConfig allAcTypeDaoConfig;
    private final DaoConfig acWeightLimitDaoConfig;
    private final DaoConfig fuleLimitDaoConfig;
    private final DaoConfig allAircraftDaoConfig;
    private final DaoConfig allSbDaoConfig;
    private final DaoConfig seatByAcRegDaoConfig;
    private final DaoConfig addFlightInfoDaoConfig;
    private final DaoConfig systemNoticeDaoConfig;
    private final DaoConfig readSystemNoticeDaoConfig;
    private final DaoConfig passengerDaoConfig;
    private final DaoConfig acGrantsDaoConfig;
    private final DaoConfig actionFeedDaoConfig;
    private final DaoConfig allAirportDaoConfig;
    private final DaoConfig uploadAirPersonDaoConfig;
    private final DaoConfig allAcSbDaoConfig;

    private final UserDao userDao;
    private final SystemVersionDao systemVersionDao;
    private final AllAcTypeDao allAcTypeDao;
    private final AcWeightLimitDao acWeightLimitDao;
    private final FuleLimitDao fuleLimitDao;
    private final AllAircraftDao allAircraftDao;
    private final AllSbDao allSbDao;
    private final SeatByAcRegDao seatByAcRegDao;
    private final AddFlightInfoDao addFlightInfoDao;
    private final SystemNoticeDao systemNoticeDao;
    private final ReadSystemNoticeDao readSystemNoticeDao;
    private final PassengerDao passengerDao;
    private final AcGrantsDao acGrantsDao;
    private final ActionFeedDao actionFeedDao;
    private final AllAirportDao allAirportDao;
    private final UploadAirPersonDao uploadAirPersonDao;
    private final AllAcSbDao allAcSbDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        userDaoConfig.initIdentityScope(type);

        systemVersionDaoConfig = daoConfigMap.get(SystemVersionDao.class).clone();
        systemVersionDaoConfig.initIdentityScope(type);

        allAcTypeDaoConfig = daoConfigMap.get(AllAcTypeDao.class).clone();
        allAcTypeDaoConfig.initIdentityScope(type);

        acWeightLimitDaoConfig = daoConfigMap.get(AcWeightLimitDao.class).clone();
        acWeightLimitDaoConfig.initIdentityScope(type);

        fuleLimitDaoConfig = daoConfigMap.get(FuleLimitDao.class).clone();
        fuleLimitDaoConfig.initIdentityScope(type);

        allAircraftDaoConfig = daoConfigMap.get(AllAircraftDao.class).clone();
        allAircraftDaoConfig.initIdentityScope(type);

        allSbDaoConfig = daoConfigMap.get(AllSbDao.class).clone();
        allSbDaoConfig.initIdentityScope(type);

        seatByAcRegDaoConfig = daoConfigMap.get(SeatByAcRegDao.class).clone();
        seatByAcRegDaoConfig.initIdentityScope(type);

        addFlightInfoDaoConfig = daoConfigMap.get(AddFlightInfoDao.class).clone();
        addFlightInfoDaoConfig.initIdentityScope(type);

        systemNoticeDaoConfig = daoConfigMap.get(SystemNoticeDao.class).clone();
        systemNoticeDaoConfig.initIdentityScope(type);

        readSystemNoticeDaoConfig = daoConfigMap.get(ReadSystemNoticeDao.class).clone();
        readSystemNoticeDaoConfig.initIdentityScope(type);

        passengerDaoConfig = daoConfigMap.get(PassengerDao.class).clone();
        passengerDaoConfig.initIdentityScope(type);

        acGrantsDaoConfig = daoConfigMap.get(AcGrantsDao.class).clone();
        acGrantsDaoConfig.initIdentityScope(type);

        actionFeedDaoConfig = daoConfigMap.get(ActionFeedDao.class).clone();
        actionFeedDaoConfig.initIdentityScope(type);

        allAirportDaoConfig = daoConfigMap.get(AllAirportDao.class).clone();
        allAirportDaoConfig.initIdentityScope(type);

        uploadAirPersonDaoConfig = daoConfigMap.get(UploadAirPersonDao.class).clone();
        uploadAirPersonDaoConfig.initIdentityScope(type);

        allAcSbDaoConfig = daoConfigMap.get(AllAcSbDao.class).clone();
        allAcSbDaoConfig.initIdentityScope(type);

        userDao = new UserDao(userDaoConfig, this);
        systemVersionDao = new SystemVersionDao(systemVersionDaoConfig, this);
        allAcTypeDao = new AllAcTypeDao(allAcTypeDaoConfig, this);
        acWeightLimitDao = new AcWeightLimitDao(acWeightLimitDaoConfig, this);
        fuleLimitDao = new FuleLimitDao(fuleLimitDaoConfig, this);
        allAircraftDao = new AllAircraftDao(allAircraftDaoConfig, this);
        allSbDao = new AllSbDao(allSbDaoConfig, this);
        seatByAcRegDao = new SeatByAcRegDao(seatByAcRegDaoConfig, this);
        addFlightInfoDao = new AddFlightInfoDao(addFlightInfoDaoConfig, this);
        systemNoticeDao = new SystemNoticeDao(systemNoticeDaoConfig, this);
        readSystemNoticeDao = new ReadSystemNoticeDao(readSystemNoticeDaoConfig, this);
        passengerDao = new PassengerDao(passengerDaoConfig, this);
        acGrantsDao = new AcGrantsDao(acGrantsDaoConfig, this);
        actionFeedDao = new ActionFeedDao(actionFeedDaoConfig, this);
        allAirportDao = new AllAirportDao(allAirportDaoConfig, this);
        uploadAirPersonDao = new UploadAirPersonDao(uploadAirPersonDaoConfig, this);
        allAcSbDao = new AllAcSbDao(allAcSbDaoConfig, this);

        registerDao(User.class, userDao);
        registerDao(SystemVersion.class, systemVersionDao);
        registerDao(AllAcType.class, allAcTypeDao);
        registerDao(AcWeightLimit.class, acWeightLimitDao);
        registerDao(FuleLimit.class, fuleLimitDao);
        registerDao(AllAircraft.class, allAircraftDao);
        registerDao(AllSb.class, allSbDao);
        registerDao(SeatByAcReg.class, seatByAcRegDao);
        registerDao(AddFlightInfo.class, addFlightInfoDao);
        registerDao(SystemNotice.class, systemNoticeDao);
        registerDao(ReadSystemNotice.class, readSystemNoticeDao);
        registerDao(Passenger.class, passengerDao);
        registerDao(AcGrants.class, acGrantsDao);
        registerDao(ActionFeed.class, actionFeedDao);
        registerDao(AllAirport.class, allAirportDao);
        registerDao(UploadAirPerson.class, uploadAirPersonDao);
        registerDao(AllAcSb.class, allAcSbDao);
    }
    
    public void clear() {
        userDaoConfig.getIdentityScope().clear();
        systemVersionDaoConfig.getIdentityScope().clear();
        allAcTypeDaoConfig.getIdentityScope().clear();
        acWeightLimitDaoConfig.getIdentityScope().clear();
        fuleLimitDaoConfig.getIdentityScope().clear();
        allAircraftDaoConfig.getIdentityScope().clear();
        allSbDaoConfig.getIdentityScope().clear();
        seatByAcRegDaoConfig.getIdentityScope().clear();
        addFlightInfoDaoConfig.getIdentityScope().clear();
        systemNoticeDaoConfig.getIdentityScope().clear();
        readSystemNoticeDaoConfig.getIdentityScope().clear();
        passengerDaoConfig.getIdentityScope().clear();
        acGrantsDaoConfig.getIdentityScope().clear();
        actionFeedDaoConfig.getIdentityScope().clear();
        allAirportDaoConfig.getIdentityScope().clear();
        uploadAirPersonDaoConfig.getIdentityScope().clear();
        allAcSbDaoConfig.getIdentityScope().clear();
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public SystemVersionDao getSystemVersionDao() {
        return systemVersionDao;
    }

    public AllAcTypeDao getAllAcTypeDao() {
        return allAcTypeDao;
    }

    public AcWeightLimitDao getAcWeightLimitDao() {
        return acWeightLimitDao;
    }

    public FuleLimitDao getFuleLimitDao() {
        return fuleLimitDao;
    }

    public AllAircraftDao getAllAircraftDao() {
        return allAircraftDao;
    }

    public AllSbDao getAllSbDao() {
        return allSbDao;
    }

    public SeatByAcRegDao getSeatByAcRegDao() {
        return seatByAcRegDao;
    }

    public AddFlightInfoDao getAddFlightInfoDao() {
        return addFlightInfoDao;
    }

    public SystemNoticeDao getSystemNoticeDao() {
        return systemNoticeDao;
    }

    public ReadSystemNoticeDao getReadSystemNoticeDao() {
        return readSystemNoticeDao;
    }

    public PassengerDao getPassengerDao() {
        return passengerDao;
    }

    public AcGrantsDao getAcGrantsDao() {
        return acGrantsDao;
    }

    public ActionFeedDao getActionFeedDao() {
        return actionFeedDao;
    }

    public AllAirportDao getAllAirportDao() {
        return allAirportDao;
    }

    public UploadAirPersonDao getUploadAirPersonDao() {
        return uploadAirPersonDao;
    }

    public AllAcSbDao getAllAcSbDao() {
        return allAcSbDao;
    }

}
