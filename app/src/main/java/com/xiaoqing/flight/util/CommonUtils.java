package com.xiaoqing.flight.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.xiaoqing.flight.entity.BaseResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

public class CommonUtils {
	
	public static String xml2JSON(String xml) {
		try {
			JSONObject obj = XML.toJSONObject(xml);
			return obj.toString();
		} catch (JSONException e) {
			System.err.println("net" + e.getLocalizedMessage());
			return "";
		}
	}


	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	public void commonIsNotNull(BaseResponse response) {
		if (response != null) {

		}
	}

}
