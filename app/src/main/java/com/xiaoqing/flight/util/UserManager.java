package com.xiaoqing.flight.util;

import com.xiaoqing.flight.FlightApplication;
import com.xiaoqing.flight.R;
import com.xiaoqing.flight.data.dao.AddFlightInfo;
import com.xiaoqing.flight.data.dao.AllAcType;
import com.xiaoqing.flight.data.dao.AllAcTypeDao;
import com.xiaoqing.flight.data.dao.AllAircraft;
import com.xiaoqing.flight.data.dao.AllAircraftDao;
import com.xiaoqing.flight.data.dao.AllSbDao;
import com.xiaoqing.flight.data.dao.SystemVersion;
import com.xiaoqing.flight.data.dao.SystemVersionDao;
import com.xiaoqing.flight.data.dao.User;
import com.xiaoqing.flight.data.dao.UserDao;
import com.xiaoqing.flight.entity.AllAcTypeResponse;
import com.xiaoqing.flight.entity.AllUsers;
import com.xiaoqing.flight.network.ResponseListner;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by QingYang on 15/7/22.
 */
public class UserManager {
    private String TAG = UserManager.class.getSimpleName();
    private boolean isAddFilghtSuccess = false;
    private User mLoginUserInfo;

    private UserManager() {
    }

    private static UserManager sUserManager;

    public static UserManager getInstance() {
        if (sUserManager == null) sUserManager = new UserManager();
        getSystemVersion();
        return sUserManager;
    }

    public void setUserInfo(User lUser) {
        this.mLoginUserInfo = lUser;
    }

    public User getUser() {
        if (mLoginUserInfo == null) {
                UserDao userDao = FlightApplication.getDaoSession().getUserDao();
                List<User> list = userDao.queryBuilder()
                        .where(UserDao.Properties.UserCode.eq(
                                PreferenceUtils.getInstance().getUserName()))
                        .list();
                if(list != null && list.size() > 0) {
                    User user = list.get(0);
                    this.mLoginUserInfo = user;
                }

        }
        return mLoginUserInfo;
    }

    private static SystemVersionDao systemVersionDao = null;
    public static void getSystemVersion() {
        if (systemVersionDao == null)
            systemVersionDao = FlightApplication.getDaoSession().getSystemVersionDao();

    }

    /**
     *
     * 获取数据库版本信息数据
     * @param versionName
     * @return
     */
    private List getQueryBuilderByVersionName(String versionName) {
        return systemVersionDao.queryBuilder().where(SystemVersionDao.Properties.VserionName.eq(versionName)).list();
    }

    /**
     * 更新数据库版本信息
     * @param versionName
     * @param Version
     */
    public void insertSystemVersion(String versionName, int Version) {
        SystemVersion systemVersion = new SystemVersion();
        systemVersion.setVserionName(versionName);
        systemVersion.setVserion(Version);
        systemVersionDao.insertOrReplace(systemVersion);
    }


    public void onLogin() {
        FlightApplication.getMoccApi().getAllUser(UserManager.getInstance().getUser().getUserName(),
                new ResponseListner<AllUsers>() {

                    @Override public void onResponse(AllUsers response) {
                        if (response != null
                                && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                            List<SystemVersion> list =
                                    getQueryBuilderByVersionName(Constants.DB_ALLUSER);
                            if (list != null && list.size() > 0) {
                                if (list.get(0).getVserion() != Integer.parseInt(
                                        response.ResponseObject.ResponseData.IAppObject.get(
                                                0).SysVersion)) {
                                    insertUsers(response);
                                }
                            } else {
                                insertUsers(response);
                            }
                        }
                    }

                    private void insertUsers(AllUsers response) {
                        UserDao userDao = FlightApplication.getDaoSession().getUserDao();
                        List<User> users = new ArrayList<User>() {
                        };
                        for (AllUsers.TIAppObject allUser : response.ResponseObject.ResponseData.IAppObject) {
                            User user = new User();
                            user.setUserName(allUser.UserName);
                            user.setUserPassWord(allUser.UserPassWord);
                            user.setUserCode(allUser.UserCode);
                            user.setActiveStart(allUser.ActiveStart);
                            user.setDepCode(allUser.DepCode);
                            user.setCheckCode(allUser.CodeCheck);
                            user.setGrant_S_M(allUser.Grant_S_M);
                            users.add(user);
                            //long insert = userDao.insert(user);
                        }

                        insertSystemVersion(Constants.DB_ALLUSER, users.get(0).getSysVersion());
                        LogUtil.LOGD(TAG, "插入数据 reslut ============  ");
                        userDao.insertOrReplaceInTx(users);
                    }

                    @Override public void onEmptyOrError(String message) {
                        LogUtil.LOGD(TAG, "get AllUser error " + message);
                    }
                });

        FlightApplication.getMoccApi().getAllAcType(new ResponseListner<AllAcTypeResponse>() {
            @Override public void onResponse(AllAcTypeResponse response) {
                if (response != null
                        && response.ResponseObject != null
                        && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {

                    SystemVersionDao systemVersionDao =
                            FlightApplication.getDaoSession().getSystemVersionDao();

                    List<SystemVersion> list = getQueryBuilderByVersionName(Constants.DB_AllAcType);
                    if (list != null && list.size() > 0) {
                        if (list.get(0).getVserion()
                                != response.ResponseObject.ResponseData.IAppObject.get(0)
                                .getSysVersion()) {
                            insertOrReplaceAllAcType(response);
                        }
                    } else {
                        insertOrReplaceAllAcType(response);
                    }
                }
            }

            private void insertOrReplaceAllAcType(AllAcTypeResponse response) {
                AllAcTypeDao allAcTypeDao = FlightApplication.getDaoSession().getAllAcTypeDao();
                insertSystemVersion(Constants.DB_AllAcType,
                        response.ResponseObject.ResponseData.IAppObject.get(0).getSysVersion());
                allAcTypeDao.insertOrReplaceInTx(response.ResponseObject.ResponseData.IAppObject);
            }

            @Override public void onEmptyOrError(String message) {
                LogUtil.LOGD(TAG, "response Message ==== " + message);
            }
        });

        ApiServiceManager.getInstance().getAllSb(null);
    }

    /**
     * 所有飞机信息，存入数据库
     * @param allAircarts
     */
    public void insertAllAircrartDB(ArrayList<AllAircraft> allAircarts) {
        List<SystemVersion> list = getQueryBuilderByVersionName(Constants.DB_ALLAirCart);
        if (list != null && list.size() > 0) {
            if (allAircarts.get(0).getSysVersion() != list.get(0).getVserion()) {
                insertAllAircart(allAircarts);
            }
        } else if (allAircarts != null ) {
            insertAllAircart(allAircarts);
        }
    }

    private void insertAllAircart(ArrayList<AllAircraft> allAircarts) {
        AllAircraftDao allAircraftDao = FlightApplication.getDaoSession().getAllAircraftDao();
        insertSystemVersion(Constants.DB_ALLAirCart, allAircarts.get(0).getSysVersion());
        allAircraftDao.insertOrReplaceInTx(allAircarts);
    }

    private AddFlightInfo mAddFilghtInfo;
    public AddFlightInfo getAddFlightInfo() {
        if (mAddFilghtInfo == null) {
            mAddFilghtInfo = new AddFlightInfo();
        }
        return mAddFilghtInfo;
    }


    public void setAddFlightSuccess(boolean isAddFilghtSuccess) {
        this.isAddFilghtSuccess = isAddFilghtSuccess;
    }

    public boolean isAddFilghtSuccess() {
        return isAddFilghtSuccess;
    }

}
