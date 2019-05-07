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
import com.xiaoqing.flight.data.dao.AcGrantsDao;
import com.xiaoqing.flight.data.dao.ActionFeed;
import com.xiaoqing.flight.data.dao.ActionFeedDao;
import com.xiaoqing.flight.data.dao.AddFlightInfo;
import com.xiaoqing.flight.data.dao.AddFlightInfoDao;
import com.xiaoqing.flight.data.dao.AllAircraft;
import com.xiaoqing.flight.data.dao.AllAircraftDao;
import com.xiaoqing.flight.data.dao.DaoSession;
import com.xiaoqing.flight.entity.AcGrantsResponse;
import com.xiaoqing.flight.entity.AllAirCraftResponse;
import com.xiaoqing.flight.network.ResponseListner;
import com.xiaoqing.flight.network.synchronous.FeedType;
import com.xiaoqing.flight.util.ApiServiceManager;
import com.xiaoqing.flight.util.CommonProgressDialog;
import com.xiaoqing.flight.util.CommonUtils;
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
    private int NETWORKCONNECTCOUNT;

    @Override protected void onStart() {
        super.onStart();
        ApiServiceManager.getInstance().checkUpdate(MainActivity.this);
        mSystemNoticeReceiver = new SystemNoticeReceiver();
        registerReceiver(mSystemNoticeReceiver, new IntentFilter(Constants.BROADCAST_SYSTEMNOTICE));
    }

    @Override protected void onResume() {
        super.onResume();

        UserManager.getInstance().setAddFlightSuccess(false);
//        getMoccApi().getURLResponse(new ResponseListner<String>() {
//            @Override public void onResponse(String response) {
//                if ("Y".equalsIgnoreCase(response)) {
//                    UserManager.getInstance().setProjectIsFinish(false);
//                } else if ("N".equalsIgnoreCase(response)) {
//                    UserManager.getInstance().setProjectIsFinish(true);
//                }
//                //Toast.makeText(mContext, "urlResponse = " + response, Toast.LENGTH_LONG).show();
//            }
//
//            @Override public void onEmptyOrError(String message) {
//
//            }
//        });
    }

    @Override public int getContentView() {
        return R.layout.activity_main;
    }

    @Override protected void initView() {
        super.initView();
        mTvTitle.setText("授权机型列表");
    }

    @Override protected void onloadData() {
        AcGrantLists = new ArrayList<>();
        final CommonProgressDialog dialog = new CommonProgressDialog(this);
        dialog.setTip("加载中 ..");
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        planAdapterFlight = new PlanAdapterFlight();
        mListView.setAdapter(planAdapterFlight);

        List<AcGrants> acGrantsFormDB = getAcGrantsFormDB();
        if (acGrantsFormDB == null || acGrantsFormDB.size() == 0) {
            NETWORKCONNECTCOUNT ++;
            ApiServiceManager.getInstance().getAcGrants(new ResponseListner<AcGrantsResponse>() {
                @Override public void onResponse(AcGrantsResponse response) {
                    dialog.dismiss();
                    if (response != null
                            && response.ResponseObject != null
                            && response.ResponseObject.ResponseData != null
                            && response.ResponseObject.ResponseData.IAppObject != null) {
                    getAcGrantsFormDB();
                    } else {
                        layout_empty.setVisibility(View.VISIBLE);
                    }
                }

                @Override public void onEmptyOrError(String message) {
                    dialog.dismiss();
                    ToastUtil.showToast(MainActivity.this, R.drawable.toast_warning,
                            TextUtils.isEmpty(message) ? getString(R.string.get_data_error) : message);
                }
            });
        } else {
            dialog.dismiss();
        }
    }

    private List<AcGrants> getAcGrantsFormDB() {
        FlightApplication.getDaoSession().clear();
        AcGrantsDao acGrantsDao = FlightApplication.getDaoSession().getAcGrantsDao();

        List<AcGrants> acGrantses = acGrantsDao.queryBuilder()
                .where(AcGrantsDao.Properties.UserCode.eq(
                        UserManager.getInstance().getUser().getUserCode()))
                .list();

        if (acGrantses != null && acGrantses.size() > 0) {
            AcGrantLists.clear();
            AcGrantLists.addAll(acGrantses);
            planAdapterFlight.notifyDataSetChanged();
        }
        return acGrantses;
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

                    //后台修改的数据，由于数据库有缓存，在点击之后数据没有变，所以在这重新查询一遍数据库
                    AcGrantsDao acGrantsDao = FlightApplication.getDaoSession().getAcGrantsDao();
                    List<AcGrants> acGrantses = acGrantsDao.queryBuilder()
                            .where(AcGrantsDao.Properties.AcReg.eq(
                                    value.getAcReg()))
                            .list();

                    String acReg = value.getAcReg();
                    Float acLj = value.getAcLj();
                    String acType = value.getAcType();
                    Float acRegBw = value.getAcRegBw();
                    if (acGrantses != null && acGrantses.get(0) != null) {
                        AcGrants grants = acGrantses.get(0);
                        acReg = grants.getAcReg();
                        acLj = grants.getAcLj();
                        acType = grants.getAcType();
                        acRegBw = grants.getAcRegBw();
                    }

                    Intent intent = new Intent(MainActivity.this, BasicInfoActivity.class);
                    intent.putExtra("Lj", acLj);
                    intent.putExtra("AircraftReg", acReg);
                    intent.putExtra("AircraftType", acType);
                    intent.putExtra("Bw", acRegBw);
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

    /**
     * 提示用户查看新系统消息
     */
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
