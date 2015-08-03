package com.uandme.flight.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.uandme.flight.R;

/**
 * Created by QingYang on 15/7/23.
 */
public class BasicInfoActivity extends BaseActivity {

    @InjectView(R.id.spinner) Spinner mSpinner;
    @InjectView(R.id.tv_weight) EditText mWeight;
    @InjectView(R.id.tv_focus) EditText mFocus;
    @InjectView(R.id.tv_playNO) EditText mPlayNO;
    @InjectView(R.id.tv_originating) EditText mOrigination;
    @InjectView(R.id.tv_destination) EditText mDestination;

    private String[] titles = new String[] { "不含差分站", "含差分站" };
    private ArrayAdapter<String> adapter;
    private String aircraftReg;
    private String lj;
    private String opDate;
    private String sysVersion;
    private String bw;

    @Override public int getContentView() {
        return R.layout.activity_basicinfo;
    }

    @Override protected void onloadData() {
        Intent data = getIntent();
        if (data != null) {
            aircraftReg = data.getStringExtra("AircraftReg");
            lj = data.getStringExtra("Lj");
            opDate = data.getStringExtra("OpDate");
            sysVersion = data.getStringExtra("SysVersion");
            bw = data.getStringExtra("Bw");
            mWeight.setEnabled(false);
            mWeight.setText(bw);
            mFocus.setEnabled(false);
            mFocus.setText((int)(Double.parseDouble(lj) / Double.parseDouble(bw)) + "");
        }

        mTopBarTitle.setText("飞机基本信息");
        mTopBarLeft.setImageResource(R.drawable.common_topnav_back);
        mTopBarRight.setText("Next");

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, titles);
        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(adapter);
        //添加事件Spinner事件监听
        mSpinner.setOnItemSelectedListener(new SpinnerSelectedListener());
        //设置默认值
        mSpinner.setVisibility(View.VISIBLE);
        //mPlayNO.setText(aircraftReg);
    }

    //使用数组形式操作
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            //view.setText("你的血型是："+m[arg2]);
        }

        public void onNothingSelected(AdapterView<?> arg0) {

        }
    }

    @Override public View.OnClickListener getRightOnClickListener() {
        return new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(BasicInfoActivity.this, EngineRoomActivity.class);
                intent.putExtra("Lj", lj);
                intent.putExtra("OpDate", opDate);
                intent.putExtra("SysVersion", sysVersion);
                intent.putExtra("AircraftReg", aircraftReg);
                intent.putExtra("Bw", bw);
                startActivity(intent);
            }
        };
    }
}
