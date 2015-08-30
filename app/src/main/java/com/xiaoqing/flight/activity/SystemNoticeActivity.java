package com.xiaoqing.flight.activity;

import android.content.Intent;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.InjectView;
import com.xiaoqing.flight.FlightApplication;
import com.xiaoqing.flight.R;
import com.xiaoqing.flight.data.dao.DaoSession;
import com.xiaoqing.flight.data.dao.ReadSystemNotice;
import com.xiaoqing.flight.data.dao.ReadSystemNoticeDao;
import com.xiaoqing.flight.data.dao.SystemNotice;
import com.xiaoqing.flight.data.dao.SystemNoticeDao;
import com.xiaoqing.flight.entity.MessageResponse;
import com.xiaoqing.flight.network.ResponseListner;
import com.xiaoqing.flight.util.ApiServiceManager;
import com.xiaoqing.flight.util.CommonUtils;
import com.xiaoqing.flight.util.Constants;
import com.xiaoqing.flight.util.DateFormatUtil;
import com.xiaoqing.flight.util.ToastUtil;
import com.xiaoqing.flight.util.UserManager;
import java.util.List;

/**
 * Created by QingYang on 15/8/29.
 */
public class SystemNoticeActivity extends  BaseActivity{

    @InjectView(R.id.mListView)
    ListView mListView;
    @InjectView(R.id.layout_empty)
    View layout_empty;

    private SysNoticeAdapter sysNoticeAdapter;
    private List<SystemNotice> noticeList;
    private List<ReadSystemNotice> readNoticeLists;


    @Override public int getContentView() {
        return R.layout.activity_systemnotice;
    }

    @Override protected void onloadData() {
        getTopBarTitle("系统消息");

        getNoticeStatusFormDB();
        sysNoticeAdapter = new SysNoticeAdapter();
        mListView.setAdapter(sysNoticeAdapter);
        List<SystemNotice> systemNoticeFromDB = getSystemNoticeFromDB();
        if (systemNoticeFromDB != null && systemNoticeFromDB.size() > 0) {
            noticeList = systemNoticeFromDB;
            sysNoticeAdapter.notifyDataSetChanged();
        } else {
            if (!CommonUtils.isNetworkConnected(SystemNoticeActivity.this)) {
                ToastUtil.showToast(SystemNoticeActivity.this, R.drawable.toast_warning,
                        getString(R.string.common_net_error));
                layout_empty.setVisibility(View.VISIBLE);
                return;
            }
            ApiServiceManager.getInstance().getSystemMessage(
                    DateFormatUtil.getTimes(DateFormatUtil.TIME_TWODAYS),
                    new ResponseListner<MessageResponse>() {
                        @Override public void onResponse(MessageResponse response) {
                            if (response != null
                                    && response.ResponseObject != null
                                    && response.ResponseObject.ResponseCode
                                    == Constants.RESULT_OK && response.ResponseObject.ResponseData.IAppObject != null) {
                                noticeList = response.ResponseObject.ResponseData.IAppObject;
                                sysNoticeAdapter.notifyDataSetChanged();
                            } else {
                                layout_empty.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override public void onEmptyOrError(String message) {
                            ToastUtil.showToast(SystemNoticeActivity.this, R.drawable.toast_warning,
                                    message);
                            layout_empty.setVisibility(View.VISIBLE);
                        }
                    });
        }


    }

    private List<SystemNotice> getSystemNoticeFromDB() {
        SystemNoticeDao systemNoticeDao = FlightApplication.getDaoSession().getSystemNoticeDao();
        return systemNoticeDao.queryBuilder().list();
    }

    private void getNoticeStatusFormDB() {
        ReadSystemNoticeDao readSystemNoticeDao =
                FlightApplication.getDaoSession().getReadSystemNoticeDao();
        readNoticeLists = readSystemNoticeDao.queryBuilder().where(ReadSystemNoticeDao.Properties.UserCode.eq(
                UserManager.getInstance().getUser().getUserCode())).list();
    }

    class SysNoticeAdapter extends BaseAdapter {

        @Override public int getCount() {
            return noticeList == null ? 0 : noticeList.size();
        }

        @Override public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(SystemNoticeActivity.this, R.layout.item_systemnotice, null);
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.iv_redpoint = (ImageView) convertView.findViewById(R.id.iv_redpoint);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final SystemNotice systemNotice = noticeList.get(position);

            holder.tv_name.setText(systemNotice.getStrSendUser());
            holder.tv_content.setText(systemNotice.getStrMessageContent());
            holder.tv_time.setText(systemNotice.getDtSendDate());
            holder.iv_icon.setImageResource(R.drawable.systemnotice);

            int recordCount = 0;
            if (readNoticeLists != null)
            for (ReadSystemNotice readSystemNotice : readNoticeLists) {
                if (readSystemNotice.getLMsgId().equals(systemNotice.getLMsgId())) {
                    //readSystemNotice.setIsReaded(true);
                    recordCount ++;
                }
            }

            if (recordCount > 0) {
                holder.iv_redpoint.setVisibility(View.GONE);
            } else {
                holder.iv_redpoint.setVisibility(View.VISIBLE);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Intent intent = new Intent(SystemNoticeActivity.this, SystemNoticeDetailActivity.class);
                    intent.putExtra(Constants.PARAM_NOTICEID, systemNotice.getLMsgId());
                    intent.putExtra(Constants.PARAM_POSITION, position);
                    startActivityForResult(intent, Constants.RESLUT_READNOTICE);
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

    class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_content;
        TextView tv_time;
        ImageView iv_redpoint;
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RESLUT_READNOTICE && resultCode == RESULT_OK) {
            getNoticeStatusFormDB();
            sysNoticeAdapter.notifyDataSetChanged();
        }
    }

    @Override public void onLeftClick() {
        backPress();
    }

    private void backPress() {
        if (readNoticeLists.size() == noticeList.size()) {
            sendBroadcast(new Intent(Constants.BROADCAST_SYSTEMNOTICE).putExtra(Constants.PARAM_NOTICEID, false));
        }
        finish();
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            backPress();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
