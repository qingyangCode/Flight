package com.xiaoqing.flight.util;

import com.xiaoqing.flight.FlightApplication;
import com.xiaoqing.flight.data.dao.AddFlightInfo;
import com.xiaoqing.flight.data.dao.User;
import com.xiaoqing.flight.data.dao.UserDao;
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
                } else {
                    mLoginUserInfo = new User();
                }

        }
        return mLoginUserInfo;
    }


    public void onLogin() {
        ApiServiceManager.getInstance().getAllUser(null);
        ApiServiceManager.getInstance().getAllAcType(null);
        ApiServiceManager.getInstance().getAllSb(null);
        ApiServiceManager.getInstance().getAllAirport(null);
        ApiServiceManager.getInstance().getAcGrants(null);
        ApiServiceManager.getInstance().getAllAircraft();
        ApiServiceManager.getInstance().getSystemMessage(
                DateFormatUtil.getTimes(DateFormatUtil.TIME_TWODAYS), null);
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
