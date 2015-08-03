package com.uandme.flight.activity;

import android.content.Intent;
import android.view.View;
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
        getTopBarRight("Next");
    }

    @Override public View.OnClickListener getRightOnClickListener() {
        return new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(RestrictionMapActivity.this, AircraftPersonnelActivity.class));
            }
        };
    }
}
