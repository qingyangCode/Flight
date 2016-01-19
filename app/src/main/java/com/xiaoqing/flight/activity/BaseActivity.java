package com.xiaoqing.flight.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.xiaoqing.flight.FlightApplication;
import com.xiaoqing.flight.R;
import com.xiaoqing.flight.network.MoccApi;
import com.xiaoqing.flight.network.ResponseListner;
import com.xiaoqing.flight.util.ApiServiceManager;
import com.xiaoqing.flight.util.UserManager;

/**
 * Created by QingYang on 15/7/23.
 */
public abstract class BaseActivity extends Activity{

    protected Context mContext;

    @InjectView(R.id.iv_top_bar_left)
    ImageView mTopBarLeft;
    @InjectView(R.id.tv_top_bar_title)
    TextView mTopBarTitle;
    @InjectView(R.id.tv_top_bar_right)
    TextView mTopBarRight;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(getContentView());
        ButterKnife.inject(this);
        initView();
        onloadData();
        initEvents();
    }

    @Override protected void onResume() {
        super.onResume();
        if (UserManager.getInstance().getProjectIsFinish() && !(mContext instanceof MainActivity)) {
            finish();
            Toast.makeText(mContext, "您的项目未被授权使用，请联系作者授权后使用！！", Toast.LENGTH_LONG).show();
        }

        //checkFlightId();
        if (this instanceof RestrictionMapActivity || this instanceof ManifestActivity) {
            //not upload
        } else {
            ApiServiceManager.getInstance().uploadFlightInfo();
        }

    }

    protected void initEvents() {

    };

    protected abstract void onloadData();

    protected void initView() {
        if(isShowTopBarLeft()) {
            mTopBarLeft.setImageResource(R.drawable.common_topnav_back);
        }
        if(mTopBarRight != null)
            mTopBarRight.setOnClickListener(getRightOnClickListener());
    };

    public abstract int getContentView();

    public MoccApi getMoccApi() {
        return FlightApplication.getMoccApi();
    }

    @OnClick(R.id.iv_top_bar_left)
    public void onLeftClick() {
        if(isShowTopBarLeft())
            finish();
    }

    public void getTopBarTitle(String title) {
        mTopBarTitle.setText(title);
    }


    public void getTopBarRight(String right) {
        mTopBarRight.setText(right);
    }

    public void hiddenRightBar() {
        mTopBarRight.setVisibility(View.GONE);
    }

    public View.OnClickListener getRightOnClickListener(){
        return null;
    }

    public boolean isShowTopBarLeft() {
        return true;
    }


}
