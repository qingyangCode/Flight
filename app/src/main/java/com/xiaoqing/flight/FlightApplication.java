package com.xiaoqing.flight;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.xiaoqing.flight.crashlog.CrashHandler;
import com.xiaoqing.flight.data.dao.AddFlightInfo;
import com.xiaoqing.flight.data.dao.DaoMaster;
import com.xiaoqing.flight.data.dao.DaoSession;
import com.xiaoqing.flight.network.MoccApi;
import com.xiaoqing.flight.network.MoccApiImpl;
import com.xiaoqing.flight.util.FileUtil;

/**
 * Created by QingYang on 15/7/19.
 */
public class FlightApplication extends Application {
    private static Context sContext;

    private static AddFlightInfo sAddFlightInfo;

    @Override public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        FileUtil.makeRootDirs();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
        initDatabase();
    }

    public static Context getContext() {
        return sContext;
    }

    public static MoccApi getMoccApi() {
        return new MoccApiImpl();
    }

    private static DaoSession sDaoSession;

    public static AddFlightInfo getAddFlightInfo() {
        if (sAddFlightInfo == null)
            sAddFlightInfo = new AddFlightInfo();
        return sAddFlightInfo;
    }

    public static void createAddFlightInfo() {
        sAddFlightInfo = new AddFlightInfo();
    }


    private void initDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "flight." + BuildConfig.APP_SPORT_TYPE, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        sDaoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoSession() {
        return sDaoSession;
    }
}
