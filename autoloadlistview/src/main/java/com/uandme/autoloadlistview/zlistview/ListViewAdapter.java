/*
 * Copyright (c) 2014, 青岛司通科技有限公司 All rights reserved.
 * File Name：OtherAdapter.java
 * Version：V1.0
 * Author：zhaokaiqiang
 * Date：2015-1-4
 */

package com.uandme.autoloadlistview.zlistview;

import android.app.Activity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.uandme.autoloadlistview.R;
import com.uandme.autoloadlistview.zlistview.adapter.BaseSwipeAdapter;
import com.uandme.autoloadlistview.zlistview.enums.DragEdge;
import com.uandme.autoloadlistview.zlistview.enums.ShowMode;
import com.uandme.autoloadlistview.zlistview.listener.SimpleSwipeListener;
import java.util.List;

public class ListViewAdapter extends BaseSwipeAdapter {

	protected static final String TAG = "ListViewAdapter";


	private Activity context;
	private List<Pair<String, Boolean>> userInfos;

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
		return context.getLayoutInflater().inflate(R.layout.item_listview,
				parent, false);
	}

	public void setData(List<Pair<String,Boolean>> pairs) {
		this.userInfos = pairs;
	}

	public List<Pair<String, Boolean>> getData() {
		return userInfos;
	}

	@Override
	public void fillValues(final int position, View convertView) {
		final ZSwipeItem swipeItem = (ZSwipeItem) convertView
				.findViewById(R.id.swipe_item);
		LinearLayout ll = (LinearLayout) convertView.findViewById(R.id.ll);

		TextView tv_userName = (TextView) convertView.findViewById(R.id.tv_userName);
		CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.cb_checkbox);
		tv_userName.setText(userInfos.get(position).first);
		if(userInfos.get(position).second) {
			checkBox.setChecked(true);
		} else {
			checkBox.setChecked(false);
		}


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
					userInfos.remove(position);
					notifyDataSetChanged();
				}
				swipeItem.close();
			}
		});
	}
}
