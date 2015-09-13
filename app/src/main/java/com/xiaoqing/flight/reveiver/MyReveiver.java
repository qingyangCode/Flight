package com.xiaoqing.flight.reveiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.xiaoqing.flight.util.ToastUtil;
import com.xiaoqing.flight.util.UserManager;
import java.util.List;

/**
 * Created by QingYang on 15/9/13.
 */
public class MyReveiver extends BroadcastReceiver{

    @Override public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, intent.getAction(), 1).show();
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
            feedFlightInfo();
        }
    }


    //同步无网络下航班信息
    private void feedFlightInfo() {
        ActionFeedDao actionFeedDao = FlightApplication.getDaoSession().getActionFeedDao();
        List<ActionFeed> list = actionFeedDao.queryBuilder()
                .where(ActionFeedDao.Properties.UserCode.eq(
                        UserManager.getInstance().getUser().getUserCode()),
                        ActionFeedDao.Properties.Feed_type.eq(
                                FeedType.toInt(FeedType.ADD_PLAYINFO)))
                .list();
        if (list != null && list.size() > 0) {
            ActionFeed actionFeed = list.get(0);
            AddFlightInfoDao addFlightInfoDao = FlightApplication.getDaoSession().getAddFlightInfoDao();
            List<AddFlightInfo> flightInfoList = addFlightInfoDao.queryBuilder()
                    .where(AddFlightInfoDao.Properties.FlightId.eq(actionFeed.getFeed_id()))
                    .list();
            if (flightInfoList != null && flightInfoList.size() > 0) {
                ApiServiceManager.getInstance().addFlightInfo(flightInfoList.get(0), new ResponseListner<AddFlightInfoResponse>() {
                    @Override public void onResponse(AddFlightInfoResponse response) {
                        if (response != null && response.ResponseObject != null && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                            ApiServiceManager.getInstance().uploadAirPersonInfo();
                        }
                    }

                    @Override public void onEmptyOrError(String message) {

                    }
                });
            }
        }
    }
}
