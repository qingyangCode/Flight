package com.xiaoqing.flight.util;

import com.xiaoqing.flight.FlightApplication;
import com.xiaoqing.flight.data.dao.AcGrants;
import com.xiaoqing.flight.data.dao.AcGrantsDao;
import com.xiaoqing.flight.data.dao.ActionFeed;
import com.xiaoqing.flight.data.dao.ActionFeedDao;
import com.xiaoqing.flight.data.dao.AddFlightInfo;
import com.xiaoqing.flight.data.dao.AddFlightInfoDao;
import com.xiaoqing.flight.data.dao.AllAcType;
import com.xiaoqing.flight.data.dao.AllAcTypeDao;
import com.xiaoqing.flight.data.dao.AllAircraft;
import com.xiaoqing.flight.data.dao.AllAircraftDao;
import com.xiaoqing.flight.data.dao.AllAirport;
import com.xiaoqing.flight.data.dao.AllAirportDao;
import com.xiaoqing.flight.data.dao.AllSb;
import com.xiaoqing.flight.data.dao.AllSbDao;
import com.xiaoqing.flight.data.dao.DaoSession;
import com.xiaoqing.flight.data.dao.FuleLimit;
import com.xiaoqing.flight.data.dao.FuleLimitDao;
import com.xiaoqing.flight.data.dao.SeatByAcReg;
import com.xiaoqing.flight.data.dao.SeatByAcRegDao;
import com.xiaoqing.flight.data.dao.SystemNotice;
import com.xiaoqing.flight.data.dao.SystemNoticeDao;
import com.xiaoqing.flight.data.dao.SystemVersion;
import com.xiaoqing.flight.data.dao.SystemVersionDao;
import com.xiaoqing.flight.data.dao.User;
import com.xiaoqing.flight.data.dao.UserDao;
import com.xiaoqing.flight.network.synchronous.FeedType;
import de.greenrobot.dao.query.QueryBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by QingYang on 15/8/31.
 */
public class DBManager {

    private DBManager() {}
    private static DBManager sDBManager;
    private static DaoSession daoSession;

    public static DBManager getInstance() {
        if (sDBManager == null)
            sDBManager = new DBManager();
        getSystemVersion();
        return sDBManager;
    }


    private static SystemVersionDao systemVersionDao = null;
    public static void getSystemVersion() {
        if (daoSession == null)
            daoSession = FlightApplication.getDaoSession();
        if (systemVersionDao == null)
            systemVersionDao = daoSession.getSystemVersionDao();

    }

    //获取数据库版本信息数据
    public List<SystemVersion> getQueryBuilderByVersionName(String versionName) {
        return systemVersionDao.queryBuilder().where(
                SystemVersionDao.Properties.VserionName.eq(versionName)).list();
    }

    public List<SystemVersion>  getQueryBuilderByAircraftReg(String versionName, String aircraftReg) {
        return systemVersionDao.queryBuilder()
                .where(SystemVersionDao.Properties.VserionName.eq(versionName),
                        SystemVersionDao.Properties.Resverved.eq(aircraftReg))
                .list();
    }

    public boolean isSystemVersionNotExist(String versionName) {
        List<SystemVersion> queryBuilderByVersionName =
                getQueryBuilderByVersionName(versionName);
        if (queryBuilderByVersionName == null || queryBuilderByVersionName.size() == 0) {
            return true;
        }
        return false;
    }

    //更新数据库版本信息
    public void insertSystemVersion(String versionName, int Version) {
        SystemVersion systemVersion = new SystemVersion();
        systemVersion.setVserionName(versionName);
        systemVersion.setVserion(Version);
        systemVersionDao.insertOrReplace(systemVersion);
    }


    //所有机场名称
    public void insertAllAirPort(ArrayList<AllAirport> iAppObject) {
        if (iAppObject == null) return;
        try {
            List<SystemVersion> airPortVersionList = getQueryBuilderByVersionName(Constants.DB_ALLAIRPORT);
            if (airPortVersionList == null
                    || airPortVersionList.size() == 0
                    || airPortVersionList.get(0).getVserion() != iAppObject.get(0).getSysVersion()) {
                AllAirportDao allAirportDao = daoSession.getAllAirportDao();
                List<AllAirport> list = allAirportDao.queryBuilder().list();
                if (list != null && list.size() > 0) {
                    allAirportDao.deleteInTx(list);
                }
                allAirportDao.insertInTx(iAppObject);
                insertSystemVersion(Constants.DB_ALLAIRPORT, iAppObject.get(0).getSysVersion());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //插入所有用户信息
    public void insertAllUsers(ArrayList<User> allUsers) {
        if (allUsers == null) return;
        try {
            List<SystemVersion> userVersionList = getQueryBuilderByVersionName(Constants.DB_ALLUSER);
            if (userVersionList == null || userVersionList.size() == 0 || userVersionList.get(0).getVserion() != allUsers.get(0).getSysVersion()) {
                UserDao userDao = daoSession.getUserDao();
                List<User> list = userDao.queryBuilder().list();
                if (list != null && list.size() > 0) {
                    userDao.deleteInTx(list);
                }
                userDao.insertInTx(allUsers);
                insertSystemVersion(Constants.DB_ALLUSER, allUsers.get(0).getSysVersion());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //全部机型
    public void insertAllAcType(ArrayList<AllAcType> iAppObject) {
        if (iAppObject == null) return;
        try {
            List<SystemVersion> acTyleVersionList = getQueryBuilderByVersionName(Constants.DB_AllAcType);
            if (acTyleVersionList == null || acTyleVersionList.size() == 0 || acTyleVersionList.get(0).getVserion() == iAppObject.get(0).getSysVersion()) {
                AllAcTypeDao allAcTypeDao = daoSession.getAllAcTypeDao();
                List<AllAcType> list = allAcTypeDao.queryBuilder().list();
                if (list != null && list.size() > 0) {
                    allAcTypeDao.deleteInTx(list);
                }
                allAcTypeDao.insertInTx(iAppObject);
                insertSystemVersion(Constants.DB_AllAcType, iAppObject.get(0).getSysVersion());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //所有机型
    public void insertAllAircrart(ArrayList<AllAircraft> iAppObject) {
        if (iAppObject == null) return;

        List<SystemVersion> queryBuilderByVersionName = getQueryBuilderByVersionName(Constants.DB_ALLAirCart);
        if (queryBuilderByVersionName == null || queryBuilderByVersionName.size() == 0 || queryBuilderByVersionName.get(0).getVserion() != iAppObject.get(0).getSysVersion()) {
            AllAircraftDao allAircraftDao = daoSession.getAllAircraftDao();
            List<AllAircraft> list = allAircraftDao.queryBuilder().list();
            if (list != null && list.size() > 0) {
                allAircraftDao.deleteInTx(list);
            }
            allAircraftDao.insertInTx(iAppObject);
            insertSystemVersion(Constants.DB_ALLAirCart, iAppObject.get(0).getSysVersion());
        }
    }

    //用户授权机型
    public void insertAcGrants(ArrayList<AcGrants> iAppObject) {
        if (iAppObject == null || iAppObject.size() == 0) return;
        List<SystemVersion> queryBuilderByVersionName =
                getQueryBuilderByVersionName(Constants.DB_ACGRANTS);
        if (queryBuilderByVersionName == null || queryBuilderByVersionName.size() == 0
                || queryBuilderByVersionName.get(0).getVserion() != iAppObject.get(0).getSysVersion()) {
            AcGrantsDao acGrantsDao = daoSession.getAcGrantsDao();
            List<AcGrants> list = acGrantsDao.queryBuilder().list();
            if (list != null && list.size() > 0) {
                acGrantsDao.deleteInTx(iAppObject);
            }
            acGrantsDao.insertInTx(iAppObject);
            insertSystemVersion(Constants.DB_ACGRANTS, iAppObject.get(0).getSysVersion());
        }
    }

    //座椅
    public void insertSeatByAcReg(String aircraftReg, ArrayList<SeatByAcReg> iAppObject) {
        if (iAppObject == null || iAppObject.size() == 0) return;
        List<SystemVersion> systemVersions = getQueryBuilderByAircraftReg(Constants.DB_SEATBYREG,
                aircraftReg);
        if (systemVersions == null || systemVersions.size() == 0
                || systemVersions.get(0).getVserion() != iAppObject.get(0).getSysVersion()) {
            SeatByAcRegDao seatByAcRegDao = daoSession.getSeatByAcRegDao();
            List<SeatByAcReg> list = seatByAcRegDao.queryBuilder().where(
                    SeatByAcRegDao.Properties.AcReg.eq(aircraftReg)).list();
            if (list != null && list.size() > 0) {
                seatByAcRegDao.deleteInTx(list);
            }
            seatByAcRegDao.insertInTx(iAppObject);
            insertSystemVersionByAcReg(aircraftReg, iAppObject.get(0).getSysVersion());
        }
    }

    private void insertSystemVersionByAcReg(String aircraftReg, int version) {
        SystemVersion systemVersion = new SystemVersion();
        systemVersion.setResverved(aircraftReg);
        systemVersion.setVserion(version);
        systemVersion.setVserionName(Constants.DB_SEATBYREG);
        systemVersionDao.insert(systemVersion);
    }

    //机型的重心限制
    public void insertFuleLimit(String aircraftType, ArrayList<FuleLimit> iAppObject) {
        List<SystemVersion> queryBuilderByAircraftReg =
                getQueryBuilderByAircraftReg(Constants.DB_FULELIMIT, aircraftType);
        if (queryBuilderByAircraftReg == null || queryBuilderByAircraftReg.size() == 0) {
            FuleLimitDao fuleLimitDao = daoSession.getFuleLimitDao();
            List<FuleLimit> list = fuleLimitDao.queryBuilder()
                    .where(FuleLimitDao.Properties.AcType.eq(aircraftType))
                    .list();
            if (list != null && list.size() > 0) {
                fuleLimitDao.deleteInTx(list);
            }
            fuleLimitDao.insertInTx(iAppObject);
            insertSystemVersion(Constants.DB_FULELIMIT, 0);
        }
    }
    //系统消息
    public void insertSystemNotice(List<SystemNotice> iAppObject) {
        boolean systemVersionNotExist = isSystemVersionNotExist(Constants.DB_SYSTEMNOTICE);
        if (systemVersionNotExist) {
            SystemNoticeDao systemNoticeDao = daoSession.getSystemNoticeDao();
            List<SystemNotice> list = systemNoticeDao.queryBuilder().list();
            if (list != null && list.size() > 0) {
                systemNoticeDao.deleteInTx(list);
            }
            systemNoticeDao.insertInTx(iAppObject);
            insertSystemVersion(Constants.DB_SYSTEMNOTICE, 0);
        }
    }

    //数据同步
    public void insertActionFeed( FeedType feedType, String dataId) {
        ActionFeedDao actionFeedDao = FlightApplication.getDaoSession().getActionFeedDao();
        ActionFeed actionFeed = new ActionFeed();
        actionFeed.setUserCode(UserManager.getInstance().getUser().getUserCode());
        actionFeed.setFeed_type(FeedType.toInt(feedType));
        actionFeed.setFeed_id(dataId);
        actionFeedDao.insert(actionFeed);
    }

    //删除同步数据
    public void deleteActionFeed(ActionFeed actionFeed) {
        final ActionFeedDao actionFeedDao = FlightApplication.getDaoSession().getActionFeedDao();
        final QueryBuilder<ActionFeed> builder = actionFeedDao.queryBuilder();
        builder.where(ActionFeedDao.Properties.Feed_type.eq(actionFeed.getFeed_type()),
                ActionFeedDao.Properties.Feed_id.eq(actionFeed.getFeed_id())).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    //航班信息
    public void insertFlightInfo() {
        AddFlightInfoDao addFlightInfoDao = FlightApplication.getDaoSession().getAddFlightInfoDao();
        List<AddFlightInfo> list = addFlightInfoDao.queryBuilder().where(
                AddFlightInfoDao.Properties.FlightId.eq(UserManager.getInstance().getAddFlightInfo().getFlightId())).list();
        if (list != null && list.size() > 0) {
            addFlightInfoDao.delete(list.get(0));
        }
        addFlightInfoDao.insert(UserManager.getInstance().getAddFlightInfo());
    }

    //删除航班信息
    public void deleteFlightInfo() {
        AddFlightInfoDao addFlightInfoDao = FlightApplication.getDaoSession().getAddFlightInfoDao();
        List<AddFlightInfo> list = addFlightInfoDao.queryBuilder().where(
                AddFlightInfoDao.Properties.FlightId.eq(
                        UserManager.getInstance().getAddFlightInfo().getFlightId())).list();
        if (list != null && list.size() > 0) {
            addFlightInfoDao.delete(list.get(0));
        }
    }

    //差分站
    public void insertAllSb(ArrayList<AllSb> iAppObject) {
        List<SystemVersion> queryBuilderByVersionName =
                getQueryBuilderByVersionName(Constants.DB_ALLSB);
        if (queryBuilderByVersionName == null || queryBuilderByVersionName.size() == 0) {
            AllSbDao allSbDao = daoSession.getAllSbDao();
            List<AllSb> list = allSbDao.queryBuilder().list();
            if (list != null && list.size() > 0) {
                allSbDao.deleteInTx(list);
            }
            allSbDao.insertInTx(iAppObject);
            insertSystemVersion(Constants.DB_ALLSB, 0);
        }
    }

    // 获取机场信息
    public List<AllAirport> getAllAirPort() {
        AllAirportDao allAirportDao = daoSession.getAllAirportDao();
        return allAirportDao.queryBuilder().list();
    }
}
