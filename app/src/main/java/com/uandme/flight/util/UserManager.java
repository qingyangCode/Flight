package com.uandme.flight.util;

import android.text.TextUtils;
import com.uandme.flight.FlightApplication;
import com.uandme.flight.R;
import com.uandme.flight.data.dao.AddFlightInfo;
import com.uandme.flight.data.dao.AllAcType;
import com.uandme.flight.data.dao.AllAcTypeDao;
import com.uandme.flight.data.dao.AllAircraft;
import com.uandme.flight.data.dao.AllAircraftDao;
import com.uandme.flight.data.dao.AllSbDao;
import com.uandme.flight.data.dao.SystemVersion;
import com.uandme.flight.data.dao.SystemVersionDao;
import com.uandme.flight.data.dao.User;
import com.uandme.flight.data.dao.UserDao;
import com.uandme.flight.entity.AcWeightLimitByAcTypeResponse;
import com.uandme.flight.entity.AllAcTypeResponse;
import com.uandme.flight.entity.AllSbResponse;
import com.uandme.flight.entity.AllUsers;
import com.uandme.flight.network.ResponseListner;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by QingYang on 15/7/22.
 */
public class UserManager {
    private String TAG = UserManager.class.getSimpleName();

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
                            user.setCheckCode(allUser.UserCode);
                            user.setActiveStart(allUser.ActiveStart);
                            user.setDepCode(allUser.DepCode);
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
        } else {
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

}
