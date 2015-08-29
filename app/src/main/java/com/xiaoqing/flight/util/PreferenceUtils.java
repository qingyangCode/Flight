package com.xiaoqing.flight.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.xiaoqing.flight.FlightApplication;


public class PreferenceUtils {

    private final String TAG = PreferenceUtils.class.getSimpleName();

    private SharedPreferences mSp;
    private static PreferenceUtils mPreferenceUtils;
    private SharedPreferences.Editor mEditor;
    private Context mContext;

    public static  PreferenceUtils getInstance() {
        if (mPreferenceUtils == null) {
            mPreferenceUtils = new PreferenceUtils();
        }
        return mPreferenceUtils;
    }

    PreferenceUtils() {
        mContext = FlightApplication.getContext();
        mSp = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEditor = mSp.edit();
    }


    public void saveUser(long userid) {
        mEditor.putString("pref_userid",String.valueOf(userid)).commit();
    }

    public String getUser() {
        return mSp.getString("pref_userid","");
    }

    public void saveRememberPassword(boolean checked) {
        mEditor.putBoolean("pref_remember_password",checked).commit();
    }

    public boolean getRememberPassword() {
        return mSp.getBoolean("pref_remember_password", false);
    }

    public void saveUserName(String username) {
        mEditor.putString("pref_username", username).commit();
    }

    public void savePassword(String password) {
        mEditor.putString("pref_password", password).commit();
    }

    public String getUserName() {
        return mSp.getString("pref_username",null);
    }

    public String getPassword() {
        return mSp.getString("pref_password", null);
    }
}
