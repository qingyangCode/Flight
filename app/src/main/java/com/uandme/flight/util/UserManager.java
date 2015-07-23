package com.uandme.flight.util;

import com.uandme.flight.entity.LoginUserInfo;

/**
 * Created by QingYang on 15/7/22.
 */
public class UserManager {

    private LoginUserInfo mLoginUserInfo;

    private UserManager() {
    }

    private static UserManager sUserManager;

    public static UserManager getInstance() {
        if (sUserManager == null) sUserManager = new UserManager();
        return sUserManager;
    }

    public void setUserInfo(LoginUserInfo lUser) {
        this.mLoginUserInfo = lUser;
    }

    public LoginUserInfo getUser() {
        return mLoginUserInfo;
    }
}
