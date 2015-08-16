/*
 * Copyright (c) 2014, 青岛司通科技有限公司 All rights reserved.
 * File Name：OtherAdapter.java
 * Version：V1.0
 * Author：zhaokaiqiang
 * Date：2015-1-4
 */

package com.uandme.flight.adapter;

import android.app.Activity;
import android.database.Cursor;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.uandme.autoloadlistview.R;
import com.uandme.autoloadlistview.zlistview.ZSwipeItem;
import com.uandme.autoloadlistview.zlistview.adapter.BaseSwipeAdapter;
import com.uandme.autoloadlistview.zlistview.enums.DragEdge;
import com.uandme.autoloadlistview.zlistview.enums.ShowMode;
import com.uandme.autoloadlistview.zlistview.listener.SimpleSwipeListener;
import com.uandme.flight.FlightApplication;
import com.uandme.flight.data.dao.User;
import com.uandme.flight.data.dao.UserDao;
import com.uandme.flight.util.LogUtil;
import java.util.List;

public class ListViewAdapter extends BaseSwipeAdapter {

	protected static final String TAG = "ListViewAdapter";


	private Activity context;
	private List<String> userInfos;
	private OnBottonClick mOnBottonClick;

	public ListViewAdapter(Activity context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return userInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getSwipeLayoutResourceId(int position) {
		return R.id.swipe_item;
	}

	@Override
	public View generateView(int position, ViewGroup parent) {
		View view = context.getLayoutInflater().inflate(R.layout.item_listview,
				parent, false);
		return view;
	}

	@Override protected View bottomAddView(ViewGroup parent) {
		return context.getLayoutInflater().inflate(R.layout.item_bottom_add, parent, false);
	}

	public void setData(List<String> pairs) {
		this.userInfos = pairs;
	}

	public List<String> getData() {
		return userInfos;
	}

	@Override
	public void fillValues(final int position, View convertView) {
		final ZSwipeItem swipeItem = (ZSwipeItem) convertView
				.findViewById(R.id.swipe_item);
		LinearLayout ll = (LinearLayout) convertView.findViewById(R.id.ll);

		TextView tv_userName = (TextView) convertView.findViewById(R.id.tv_userName);
		tv_userName.setText(userInfos.get(position));


		swipeItem.setShowMode(ShowMode.PullOut);
		swipeItem.setDragEdge(DragEdge.Right);

		swipeItem.addSwipeListener(new SimpleSwipeListener() {
			@Override
			public void onOpen(ZSwipeItem layout) {
				Log.d(TAG, "打开:" + position);
			}

			@Override
			public void onClose(ZSwipeItem layout) {
				Log.d(TAG, "关闭:" + position);
			}

			@Override
			public void onStartOpen(ZSwipeItem layout) {
				Log.d(TAG, "准备打开:" + position);
			}

			@Override
			public void onStartClose(ZSwipeItem layout) {
				Log.d(TAG, "准备关闭:" + position);
			}

			@Override
			public void onHandRelease(ZSwipeItem layout, float xvel, float yvel) {
				Log.d(TAG, "手势释放");
			}

			@Override
			public void onUpdate(ZSwipeItem layout, int leftOffset,
			                     int topOffset) {
				Log.d(TAG, "位置更新");
			}
		});

		ll.setOnClickListener(new OnClickListener() {

			@Override public void onClick(View v) {
				if(userInfos.size() > position) {
					UserDao userDao = FlightApplication.getDaoSession().getUserDao();
					Cursor cursor = userDao.getDatabase()
						.rawQuery("delete from "
								+ userDao.TABLENAME
								+ " where USER_NAME = '" + userInfos.get(position) +"'", null);
					if (cursor != null) {
						cursor.moveToFirst();
						LogUtil.LOGD(TAG, "delete User column count === " + cursor.getColumnCount());
					}
					userInfos.remove(position);
					notifyDataSetChanged();
				}
				swipeItem.close();
			}
		});
	}

	public interface OnBottonClick {
		void onClick();
	}

	public void setOnBottomClick(OnBottonClick onBottonClick) {
		this.mOnBottonClick = onBottonClick;
	}
}
