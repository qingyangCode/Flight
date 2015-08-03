package com.uandme.flight.activity;

import com.uandme.flight.R;

/**
 * Created by QingYang on 15/7/24.
 */
public class RestrictionMapActivity extends BaseActivity{

    @Override public int getContentView() {
        return R.layout.activity_restrictionmap;
    }

    @Override protected void onloadData() {
        mTopBarTitle.setText("限制图");
    }


}
