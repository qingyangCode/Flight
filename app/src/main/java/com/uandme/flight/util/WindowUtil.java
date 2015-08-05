package com.uandme.flight.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

/**
 * 屏幕密度，尺寸的单类
 * 
 * @author liuwenji
 * 
 */
public class WindowUtil {

	static WindowUtil windowUtil;

	public static WindowUtil initWindow() {
		if (windowUtil == null) {
			windowUtil = new WindowUtil();
		}
		return windowUtil;
	}

	// 获取当前屏幕属性
	public DisplayMetrics getDisplayMetrics(Context context) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		Display display = ((Activity) context).getWindowManager()
				.getDefaultDisplay();
		display.getMetrics(displayMetrics);
		return displayMetrics;
	}

	// 得到当前屏幕密度比例
	public float getDensityScale(Context context) {
		return getDisplayMetrics(context).scaledDensity;
	}

	// 得到屏幕密度
	public int getDensity(Context context) {
		return getDisplayMetrics(context).densityDpi;
	}

	// 得到比例（得到当前的密度是其它分辨率的倍数，，baseDensity为不同分辨率下的密度，比如:当前密度是800X480的几倍，以便于兼容不同屏幕）
	public float getScale(Context context, int baseDensity) {// 120dpi 160dpi
																// 240dpi
		return getDensity(context) / baseDensity;
	}

	// 得到屏幕的宽
	public int getScreenWidth(Context context) {
		return getDisplayMetrics(context).widthPixels;
	}

	// 得到屏幕的高
	public int getScreenHeight(Context context) {
		return getDisplayMetrics(context).heightPixels;
	}

	// 宽度比例
	public float getWidthScale(Context context) {
		return ((float)getDisplayMetrics(context).widthPixels) / 480.0f;
	}

	// dip to px(宽度比例计算)
	public float dip2px_width(Context context, float dpValue) {
		return dpValue * getWidthScale(context);
	}

	// px to dip(宽度比例计算)
	public float px2dip_width(Context context, float pxValue) {
		return pxValue / getWidthScale(context);
	}

	// 高度比例
	public float getHeightScale(Context context) {
		return getDisplayMetrics(context).heightPixels / 800;
	}

	// dip to px(高度比例计算)
	public float dip2px_height(Context context, float dpValue) {
		return dpValue * getHeightScale(context);
	}

	// px to dip(高度比例计算)
	public float px2dip_height(Context context, float pxValue) {
		return pxValue / getHeightScale(context);
	}

	public static float getScale(Context context) {

		Point point = getDisplaySize(context);

		if (isTablet(context)) {
			return point.x / 600f;
		} else {
			return screenWidth(context) / 2f;
		}

	}

	public static Point getDisplaySize(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		Point point = new Point();
		display.getSize(point);
		return point;
	}

	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK)
				>= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	public static float screenWidth(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		return dm.widthPixels / dm.xdpi;
	}

	public int dp2px(float value, Context context){
		final DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, dm);
	}

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
