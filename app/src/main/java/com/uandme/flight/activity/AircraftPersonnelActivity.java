package com.uandme.flight.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Pair;
import butterknife.InjectView;
import com.uandme.autoloadlistview.LoadingFooter;
import com.uandme.autoloadlistview.zlistview.ListViewAdapter;
import com.uandme.flight.R;
import com.uandme.flight.widget.AutoLoadListView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by QingYang on 15/7/29.
 * 飞机上的人员
 */
public class AircraftPersonnelActivity extends BaseActivity
        implements SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.mListview)
    AutoLoadListView mAutoLoadListView;

    private List<Pair<String, Boolean>> userNames;

    @Override public int getContentView() {
        return R.layout.activity_aircraftpersonnel;
    }

    @Override protected void onloadData() {
        getTopBarTitle("机上人员姓名");
        //// 顶部刷新的样式
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_green_light, android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light);
        mSwipeRefreshLayout.setRefreshing(false);
        userNames = new ArrayList<Pair<String, Boolean>>();
        userNames.add(new Pair<String, Boolean>("边振霄", false));
        userNames.add(new Pair<String, Boolean>("刘忠龙", true));
        userNames.add(new Pair<String, Boolean>("王立群", false));
        userNames.add(new Pair<String, Boolean>("王恺斌", true));
        userNames.add(new Pair<String, Boolean>("王东旭", true));

        mSwipeRefreshLayout.setOnRefreshListener(this);
        ListViewAdapter listViewAdapter = new ListViewAdapter(AircraftPersonnelActivity.this);
        listViewAdapter.setData(userNames);
        mAutoLoadListView.setAdapter(listViewAdapter);
    }

    @Override public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        mAutoLoadListView.smoothScrollToPosition(0);
        mAutoLoadListView.setState(LoadingFooter.State.Idle);
    }
}
