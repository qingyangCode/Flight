package com.uandme.flight.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import butterknife.ButterKnife;
import com.uandme.flight.FlightApplication;
import com.uandme.flight.network.MoccApi;

/**
 * Created by QingYang on 15/7/23.
 */
public abstract class BaseActivity extends Activity{


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

    };

    public abstract int getContentView();

    public MoccApi getMoccApi() {
        return FlightApplication.getMoccApi();
    }
}
