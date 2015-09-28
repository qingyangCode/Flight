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
import com.xiaoqing.flight.util.LogUtil;
import com.xiaoqing.flight.util.ToastUtil;
import com.xiaoqing.flight.util.UserManager;
import java.util.List;

/**
 * Created by QingYang on 15/9/13.
 */
public class MyReveiver extends BroadcastReceiver{

    private String TAG = MyReveiver.class.getSimpleName();

    @Override public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, intent.getAction(), 1).show();
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        //NetworkInfo activeInfo = manager.getActiveNetworkInfo();
        LogUtil.LOGD(TAG, "mobile:" + mobileInfo.isConnected() + "wifi:" + wifiInfo.isConnected());
        if (mobileInfo != null && mobileInfo.isConnected() || wifiInfo != null && wifiInfo.isConnected()) {
            //checkFlightId();
            ApiServiceManager.getInstance().uploadFlightInfo();
            checkProjectAvailable();
        }
    }

    private void checkProjectAvailable() {
        FlightApplication.getMoccApi().getURLResponse(new ResponseListner<String>() {
            @Override public void onResponse(String response) {
                LogUtil.LOGD(TAG, "checkProjectAvailable ===== " + response);
                if ("Y".equalsIgnoreCase(response)) {
                    UserManager.getInstance().setProjectIsFinish(false);
                } else if ("N".equalsIgnoreCase(response)) {
                    UserManager.getInstance().setProjectIsFinish(true);
                }
            }

            @Override public void onEmptyOrError(String message) {

            }
        });
    }

}
