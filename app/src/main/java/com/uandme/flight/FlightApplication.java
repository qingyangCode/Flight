package com.uandme.flight;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import com.uandme.flight.network.MoccApi;
import com.uandme.flight.network.MoccApiImpl;

/**
 * Created by QingYang on 15/7/19.
 */
public class FlightApplication extends Application {
    private static Context mContext;

    @Override public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }

    public static MoccApi getMoccApi() {
        return new MoccApiImpl();
    }



}
