package com.xiaoqing.flight.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.OnClick;
import com.xiaoqing.flight.FlightApplication;
import com.xiaoqing.flight.R;
import com.xiaoqing.flight.adapter.FlightBaseAdapter;
import com.xiaoqing.flight.data.dao.AcGrants;
import com.xiaoqing.flight.data.dao.ActionFeed;
import com.xiaoqing.flight.data.dao.ActionFeedDao;
import com.xiaoqing.flight.data.dao.AddFlightInfo;
import com.xiaoqing.flight.data.dao.AddFlightInfoDao;
import com.xiaoqing.flight.data.dao.AllAircraft;
import com.xiaoqing.flight.data.dao.AllAircraftDao;
import com.xiaoqing.flight.entity.AcGrantsResponse;
import com.xiaoqing.flight.entity.AllAirCraftResponse;
import com.xiaoqing.flight.network.ResponseListner;
import com.xiaoqing.flight.network.synchronous.FeedType;
import com.xiaoqing.flight.util.ApiServiceManager;
import com.xiaoqing.flight.util.CommonProgressDialog;
import com.xiaoqing.flight.util.Constants;
import com.xiaoqing.flight.util.ToastUtil;
import com.xiaoqing.flight.util.UserManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by QingYang on 15/7/19.
 */
public class MainActivity extends BaseActivity {

    @InjectView(R.id.mListView)
    ListView mListView;
    @InjectView(R.id.tv_top_bar_title)
    TextView mTvTitle;
    @InjectView(R.id.message_redpoint)
    ImageView mRedpoint;
    @InjectView(R.id.layout_message)
    View layout_message;
    @InjectView(R.id.layout_empty)
    View layout_empty;

    private final int SPACETIME = 2000;
    private long mLastBackPressTime;
    SystemNoticeReceiver mSystemNoticeReceiver;
    private ArrayList<AcGrants> AcGrantLists;
    private PlanAdapterFlight planAdapterFlight;

    @Override protected void onStart() {
        super.onStart();
        mSystemNoticeReceiver = new SystemNoticeReceiver();
        registerReceiver(mSystemNoticeReceiver, new IntentFilter(Constants.BROADCAST_SYSTEMNOTICE));
    }

    @Override protected void onResume() {
        super.onResume();
        UserManager.getInstance().setAddFlightSuccess(false);
        //同步无网络下飞机信息
        ActionFeedDao actionFeedDao = FlightApplication.getDaoSession().getActionFeedDao();
        List<ActionFeed> list = actionFeedDao.queryBuilder()
                .where(ActionFeedDao.Properties.UserCode.eq(
                        UserManager.getInstance().getUser().getUserCode()), ActionFeedDao.Properties.Feed_type.eq(
                        FeedType.toInt(FeedType.ADD_PLAYINFO)))
                .list();
        if (list != null && list.size() > 0) {
            ActionFeed actionFeed = list.get(0);
            AddFlightInfoDao addFlightInfoDao = FlightApplication.getDaoSession().getAddFlightInfoDao();
            List<AddFlightInfo> flightInfoList = addFlightInfoDao.queryBuilder()
                    .where(AddFlightInfoDao.Properties.FlightId.eq(actionFeed.getFeed_id()))
                    .list();
            if (flightInfoList != null && flightInfoList.size() > 0) {
                ApiServiceManager.getInstance().addFlightInfo(flightInfoList.get(0),null);
            }
        }
    }

    @Override public int getContentView() {
        return R.layout.activity_main;
    }

    @Override protected void initView() {
        super.initView();
        mTvTitle.setText("全部飞机");
    }

    @Override protected void onloadData() {
        final CommonProgressDialog dialog = new CommonProgressDialog(this);
        dialog.setTip("加载中 ..");
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        planAdapterFlight = new PlanAdapterFlight();
        mListView.setAdapter(planAdapterFlight);

        getMoccApi().getAcGrants(new ResponseListner<AcGrantsResponse>() {
            @Override public void onResponse(AcGrantsResponse response) {
                dialog.dismiss();
                if (response != null && response.ResponseObject != null && response.ResponseObject.ResponseData != null && response.ResponseObject.ResponseData.IAppObject != null) {
                    AcGrantLists =
                            response.ResponseObject.ResponseData.IAppObject;
                    UserManager.getInstance().insertAcGrants(AcGrantLists);
                    planAdapterFlight.notifyDataSetChanged();
                } else {
                    layout_empty.setVisibility(View.VISIBLE);
                }
            }

            @Override public void onEmptyOrError(String message) {
                dialog.dismiss();
                ToastUtil.showToast(MainActivity.this, R.drawable.toast_warning,TextUtils.isEmpty(message) ? getString(R.string.get_data_error) : message);
            }
        });


        getMoccApi().getAllAircraft(UserManager.getInstance().getUser().getUserCode(), new ResponseListner<AllAirCraftResponse>() {

            @Override public void onResponse(AllAirCraftResponse response) {
                //dialog.dismiss();
                UserManager.getInstance().onLogin();
                if (response != null && response.ResponseObject != null) {
                    if (response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                        UserManager.getInstance().insertAllAircrartDB(
                                response.ResponseObject.ResponseData.IAppObject);
                        //mListView.setAdapter(new PlanAdapterFlight(MainActivity.this,response.ResponseObject.ResponseData.IAppObject, 50, R.layout.item_allaircraft, R.layout.item_failed));
                    } else {
                        //ToastUtil.showToast(MainActivity.this, R.drawable.toast_warning, response.ResponseObject.ResponseErr);
                    }
                } else {
                    //ToastUtil.showToast(MainActivity.this, R.drawable.toast_warning, getString(R.string.get_data_error));
                }
            }

            @Override public void onEmptyOrError(String message) {
                //dialog.dismiss();
                //ToastUtil.showToast(MainActivity.this, R.drawable.toast_warning,TextUtils.isEmpty(message) ? getString(R.string.get_data_error) : message);
            }
        });
    }


    @OnClick(R.id.layout_message)
    public void onNoticeClick() {
        startActivity(new Intent(MainActivity.this, SystemNoticeActivity.class));
    }

    public class PlanAdapterFlight extends BaseAdapter {

        @Override public int getCount() {
            return AcGrantLists == null ? 0 : AcGrantLists.size();
        }

        @Override public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.item_allaircraft, null);
                holder.number = (TextView) convertView.findViewById(R.id.tv_number);
                holder.type = (TextView) convertView.findViewById(R.id.tv_type);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final AcGrants value = AcGrantLists.get(position);

            holder.number.setText(value.getAcReg());
            holder.type.setText(value.getAcType());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, BasicInfoActivity.class);
                    intent.putExtra("Lj", value.getAcLj());
                    intent.putExtra("AircraftReg", value.getAcReg());
                    intent.putExtra("AircraftType", value.getAcType());
                    intent.putExtra("Bw", value.getAcRegBw());
                    startActivity(intent);
                }
            });
            return convertView;
        }

        @Override public Object getItem(int position) {
            return null;
        }

        @Override public long getItemId(int position) {
            return 0;
        }


    }

    public class ViewHolder {
        TextView number;
        TextView type;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - mLastBackPressTime > SPACETIME) {
                mLastBackPressTime = System.currentTimeMillis();
                 Toast.makeText(getBaseContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                finish();
                //System.exit(0);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override public boolean isShowTopBarLeft() {
        return false;
    }




    class SystemNoticeReceiver extends BroadcastReceiver {

        @Override public void onReceive(Context context, Intent intent) {
            boolean hasNewNotice = intent.getBooleanExtra(Constants.PARAM_HASNEWNOTICE, false);
            if (hasNewNotice) {
                mRedpoint.setVisibility(View.VISIBLE);
            } else {
                mRedpoint.setVisibility(View.GONE);
            }
        }
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (mSystemNoticeReceiver != null)
            unregisterReceiver(mSystemNoticeReceiver);
    }
}
