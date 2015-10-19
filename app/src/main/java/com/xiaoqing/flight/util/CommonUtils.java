package com.xiaoqing.flight.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import com.xiaoqing.flight.FlightApplication;
import com.xiaoqing.flight.data.dao.FuleLimit;
import com.xiaoqing.flight.data.dao.FuleLimitDao;
import com.xiaoqing.flight.entity.BaseResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

public class CommonUtils {

	private String TAG = CommonUtils.class.getSimpleName();
	
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


	public static String getVersionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return "1";
	}

	public static int getVersionCode(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static void startWebView(Context context, String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	//油量力矩
	public static float getOilWeightLj(float OilWeight , String aircraftType) {
		ArrayList<FuleLimit> fuleLimits = new ArrayList<>();
		float oilLj = 0;
		FuleLimitDao fuleLimitDao = FlightApplication.getDaoSession().getFuleLimitDao();
		List<FuleLimit> list = fuleLimitDao.queryBuilder().where(FuleLimitDao.Properties.AcType.eq(aircraftType)).list();
		fuleLimits.addAll(list);

		Collections.sort(fuleLimits, new Comparator<FuleLimit>() {
			@Override public int compare(FuleLimit lhs, FuleLimit rhs) {
				return lhs.getFuleWeight() > rhs.getFuleWeight() ? 1 : -1;
			}
		});

		int temp = 0;
		//遍历数据，如数据库中存在当前重量力矩，则直接返回力矩
		for (int i = 0; i < fuleLimits.size(); i++) {
			if (OilWeight == fuleLimits.get(i).getFuleWeight()) {
				oilLj = fuleLimits.get(i).getFuleLj();
				break;
			}
		}
		// 二分法计算输入油量最近的取值
		if (oilLj == 0) {
			int left = 0;
			int right = fuleLimits.size() - 1;
			while (left < right) {
				int middle = (left + right) / 2;
				if(temp == middle) break;
				if (OilWeight < fuleLimits.get(middle).getFuleWeight()) {
					right = middle;
				} else {
					left = middle;
				}
				temp = middle;
			}

			int index = 0;
			if (OilWeight < fuleLimits.get(temp).getFuleWeight()) {
				index = temp;
			} else {
				index = temp + 1;
			}

			if (index == 0) {
				oilLj = fuleLimits.get(index).getFuleLj() * OilWeight / fuleLimits.get(index).getFuleWeight() ;
			} else {
				//算法得到力矩
				try {
					oilLj = fuleLimits.get(index).getFuleLj()
							- (fuleLimits.get(index).getFuleLj() - fuleLimits.get(index - 1).getFuleLj())
							* (fuleLimits.get(index).getFuleWeight() - OilWeight) / (fuleLimits.get(
							index).getFuleWeight() - fuleLimits.get(index - 1).getFuleWeight());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
		//LogUtil.LOGD(TAG, "before fly oil Lj == " + oilLj);
		return oilLj;
	}
}
