package com.uandme.flight.util;

import com.uandme.flight.FlightApplication;
import com.uandme.flight.data.dao.SystemVersion;
import com.uandme.flight.data.dao.SystemVersionDao;
import com.uandme.flight.data.dao.User;
import com.uandme.flight.data.dao.UserDao;
import com.uandme.flight.entity.AllUsers;
import com.uandme.flight.entity.LoginUserInfo;
import com.uandme.flight.network.ResponseListner;
import java.util.ArrayList;
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
        return sUserManager;
    }

    public void setUserInfo(User lUser) {
        this.mLoginUserInfo = lUser;
    }

    public User getUser() {
        if (mLoginUserInfo == null)
            mLoginUserInfo = new User();
        return mLoginUserInfo;
    }


    public void onLogin() {
        FlightApplication.getMoccApi().getAllUser(UserManager.getInstance().getUser().getUserName(),
                new ResponseListner<AllUsers> () {

                    @Override public void onResponse(AllUsers response) {
                        if (response != null && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                            UserDao userDao = FlightApplication.getDaoSession().getUserDao();
                            SystemVersionDao systemVersionDao =
                                    FlightApplication.getDaoSession().getSystemVersionDao();
                            final List<SystemVersion> list = systemVersionDao.queryBuilder().where(SystemVersionDao.Properties.VserionName.eq(Constants.DB_ALLUSER)).list();
                            if (list != null && list.size() > 0) {
                                if(list.get(0).getVserion() != Integer.parseInt(response.ResponseObject.ResponseData.IAppObject.get(0).SysVersion)) {
                                    insertUsers(response, userDao, systemVersionDao);
                                }
                            } else {
                                insertUsers(response, userDao, systemVersionDao);
                            }
                        }
                    }

                    private void insertUsers(AllUsers response, UserDao userDao, SystemVersionDao systemVersionDao) {
                        List<User> users = new ArrayList<User>(){};
                        for(AllUsers.TIAppObject allUser : response.ResponseObject.ResponseData.IAppObject) {
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

                        SystemVersion systemVersion = new SystemVersion();
                        systemVersion.setVserionName(Constants.DB_ALLUSER);
                        systemVersion.setVserion(users.get(0).getSysVersion());
                        systemVersionDao.insert(systemVersion);
                        LogUtil.LOGD(TAG, "插入数据 reslut ============  ");
                        userDao.insertOrReplaceInTx(users);
                    }

                    @Override public void onEmptyOrError(String message) {
                        LogUtil.LOGD(TAG, "get AllUser error " + message);
                    }
                });
    }

}
