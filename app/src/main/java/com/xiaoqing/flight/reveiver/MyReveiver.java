package com.xiaoqing.flight.reveiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;
import com.xiaoqing.flight.FlightApplication;
import com.xiaoqing.flight.data.dao.ActionFeed;
import com.xiaoqing.flight.data.dao.ActionFeedDao;
import com.xiaoqing.flight.data.dao.AddFlightInfo;
import com.xiaoqing.flight.data.dao.AddFlightInfoDao;
import com.xiaoqing.flight.entity.AddFlightInfoResponse;
import com.xiaoqing.flight.network.ResponseListner;
import com.xiaoqing.flight.network.synchronous.FeedType;
import com.xiaoqing.flight.util.ApiServiceManager;
import com.xiaoqing.flight.util.Constants;
import com.xiaoqing.flight.util.DBManager;
import com.xiaoqing.flight.util.ToastUtil;
import com.xiaoqing.flight.util.UserManager;
import java.util.List;

/**
 * Created by QingYang on 15/9/13.
 */
public class MyReveiver extends BroadcastReceiver{

    private final int ACTION_ADDFILGHT = 1;

    private String mFlightId = "";
    private Context mContext;

    private Handler handler = new Handler() {
        @Override public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ACTION_ADDFILGHT:
                    ApiServiceManager.getInstance().feedFlightInfo(mFlightId);
                    break;
            }
        }
    };

    @Override public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, intent.getAction(), 1).show();
        mContext = context;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        //NetworkInfo activeInfo = manager.getActiveNetworkInfo();
        //Toast.makeText(context, "mobile:"
        //        + mobileInfo.isConnected()
        //        + "\n"
        //        + "wifi:"
        //        + wifiInfo.isConnected()
        //        , Toast.LENGTH_LONG).show();
        //Toast.makeText(context, "active: " + activeInfo.getTypeName(), Toast.LENGTH_LONG).show();
        if (mobileInfo != null && mobileInfo.isConnected() || wifiInfo != null && wifiInfo.isConnected()) {
            checkFlightId();
            checkProjectAvailable();
        }
    }

    private void checkProjectAvailable() {
        FlightApplication.getMoccApi().getURLResponse(new ResponseListner<String>() {
            @Override public void onResponse(String response) {
                if ("Y".equalsIgnoreCase(response)) {
                    UserManager.getInstance().setProjectIsFinish(false);
                } else if ("N".equalsIgnoreCase(response)) {
                    UserManager.getInstance().setProjectIsFinish(true);
                }
                //Toast.makeText(mContext, "urlResponse = " + response, Toast.LENGTH_LONG).show();
            }

            @Override public void onEmptyOrError(String message) {

            }
        });
    }

    private void checkFlightId() {
        String flightId = UserManager.getInstance().getAddFlightInfo().getFlightId();
        int flightID = 0;
        try {
            if (!TextUtils.isEmpty(flightId))
                flightID = Integer.parseInt(flightId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (flightID == 0) {
            ApiServiceManager.getInstance().getFilghtId(new ResponseListner<String>() {
                @Override public void onResponse(String response) {
                    if (!TextUtils.isEmpty(response)) {
                        mFlightId = response;
                        //UserManager.getInstance().getAddFlightInfo().setFlightId(response);
                        handler.sendEmptyMessage(ACTION_ADDFILGHT);
                    }

                }

                @Override public void onEmptyOrError(String message) {
                }
            });
        } else {
            ApiServiceManager.getInstance().feedFlightInfo(mFlightId);
        }
    }



}
