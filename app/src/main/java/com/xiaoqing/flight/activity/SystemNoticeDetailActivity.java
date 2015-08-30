package com.xiaoqing.flight.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import butterknife.InjectView;
import com.xiaoqing.flight.FlightApplication;
import com.xiaoqing.flight.R;
import com.xiaoqing.flight.data.dao.ReadSystemNotice;
import com.xiaoqing.flight.data.dao.ReadSystemNoticeDao;
import com.xiaoqing.flight.data.dao.SystemNotice;
import com.xiaoqing.flight.data.dao.SystemNoticeDao;
import com.xiaoqing.flight.util.Constants;
import com.xiaoqing.flight.util.ToastUtil;
import com.xiaoqing.flight.util.UserManager;
import java.util.List;

/**
 * Created by QingYang on 15/8/29.
 */
public class SystemNoticeDetailActivity extends BaseActivity{

    @InjectView(R.id.tv_mustRead)
    TextView tv_mustRead;
    @InjectView(R.id.message_container)
    TextView message_container;
    @InjectView(R.id.tv_sender)
    TextView tv_sender;
    @InjectView(R.id.tv_sendTime)
    TextView tv_sendTime;

    private int position;

    private Handler handler = new Handler(){

    };

    @Override public int getContentView() {
        return R.layout.activity_system_notice_detail;
    }

    @Override protected void onloadData() {
        getTopBarTitle("消息详情");

        String notice_id = getIntent().getStringExtra(Constants.PARAM_NOTICEID);
        position = getIntent().getIntExtra(Constants.PARAM_POSITION, 0);

        ReadSystemNoticeDao readSystemNoticeDao =
                FlightApplication.getDaoSession().getReadSystemNoticeDao();
        List<ReadSystemNotice> isReadMessage = readSystemNoticeDao.queryBuilder()
                .where(ReadSystemNoticeDao.Properties.LMsgId.eq(notice_id), ReadSystemNoticeDao.Properties.UserCode.eq(UserManager.getInstance().getUser().getUserCode()))
                .list();
        SystemNoticeDao systemNoticeDao =
                FlightApplication.getDaoSession().getSystemNoticeDao();
        List<SystemNotice> sysnoticeList =
                systemNoticeDao.queryBuilder()
                        .where(SystemNoticeDao.Properties.LMsgId.eq(notice_id)).list();
        if (sysnoticeList != null && sysnoticeList.size() > 0) {
            showDetailMessage(sysnoticeList.get(0));
            if (isReadMessage == null || isReadMessage.size() == 0) {
                ReadSystemNotice readSystemNotice = new ReadSystemNotice();
                readSystemNotice.setIsReaded(true);
                readSystemNotice.setLMsgId(sysnoticeList.get(0).getLMsgId());
                readSystemNotice.setMsustRead(sysnoticeList.get(0).getMsustRead());
                readSystemNotice.setUserCode(UserManager.getInstance().getUser().getUserCode());
                readSystemNoticeDao.insert(readSystemNotice);
            }
        } else {
            ToastUtil.showToast(mContext, R.drawable.toast_warning, getString(R.string.message_read_error));
            handler.postDelayed(new Runnable() {
                @Override public void run() {
                    finish();
                }
            }, 3000);
        }
    }

    private void showDetailMessage(SystemNotice systemNotice) {
        if ("Y".equals(systemNotice.getMsustRead())) {
            tv_mustRead.setVisibility(View.VISIBLE);
        }
        message_container.setText(systemNotice.getStrMessageContent());
        tv_sender.setText(getString(R.string.message_sender, systemNotice.getStrSendUser()));
        tv_sendTime.setText(getString(R.string.message_sender_time, systemNotice.getDtSendDate()));
    }

    @Override public void onLeftClick() {
        //super.onLeftClick();
        backPress();
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            backPress();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void backPress() {
        Intent data = new Intent();
        data.putExtra(Constants.PARAM_POSITION, position);
        setResult(RESULT_OK, data);
        finish();
    }
}
