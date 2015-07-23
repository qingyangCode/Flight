package com.uandme.flight.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.uandme.flight.FlightApplication;
import com.uandme.flight.R;
import com.uandme.flight.network.MoccApi;

/**
 * Created by QingYang on 15/7/23.
 */
public abstract class BaseActivity extends Activity{

    @InjectView(R.id.iv_top_bar_left)
    ImageView mTopBarLeft;
    @InjectView(R.id.tv_top_bar_title)
    TextView mTopBarTitle;
    @InjectView(R.id.tv_top_bar_right)
    TextView mTopBarRight;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        ButterKnife.inject(this);
        initView();
        onloadData();
        initEvents();
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
        finish();
    }

    public void getTopBarTitle(String title) {
        mTopBarTitle.setText(title);
    }


    public void getTopBarRight(String right) {
        mTopBarRight.setText(right);
    }

    public View.OnClickListener getRightOnClickListener(){
        return null;
    }

    public boolean isShowTopBarLeft() {
        return true;
    }

}
