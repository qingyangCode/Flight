package com.uandme.flight.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import butterknife.InjectView;
import com.uandme.flight.FlightApplication;
import com.uandme.flight.R;
import com.uandme.flight.data.dao.AllAircraft;
import com.uandme.flight.data.dao.AllSb;
import com.uandme.flight.data.dao.AllSbDao;
import com.uandme.flight.entity.AllSbResponse;
import com.uandme.flight.entity.SeatByAcRegResponse;
import com.uandme.flight.network.ResponseListner;
import com.uandme.flight.util.ApiServiceManager;
import com.uandme.flight.util.ToastUtil;
import java.util.ArrayList;
import java.util.List;

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

    private String[] titles;
    private ArrayAdapter<String> adapter;
    private String aircraftReg;
    private String aircraftType;
    private Double lj;
    private int bw;
    private List<AllAircraft> list;

    @Override public int getContentView() {
        return R.layout.activity_basicinfo;
    }

    @Override protected void onloadData() {
        Intent data = getIntent();
        if (data != null) {
            aircraftReg = data.getStringExtra("AircraftReg");
            aircraftType = data.getStringExtra("AircraftType");
            lj = data.getDoubleExtra("Lj", 0);
            bw = data.getIntExtra("Bw", 0);
            mWeight.setEnabled(false);
            mWeight.setText(bw + "");
            mFocus.setEnabled(false);
            mFocus.setText((int) (lj / bw) + "");
        }

        mTopBarTitle.setText("飞机基本信息");
        mTopBarLeft.setImageResource(R.drawable.common_topnav_back);
        mTopBarRight.setText("下一步");
        titles = new String[]{};
        getAllSbFromDB();
        if (titles.length == 0) {
            ApiServiceManager.getInstance().getAllSb(new ResponseListner<AllSbResponse>() {
                @Override public void onResponse(AllSbResponse response) {
                    getAllSbFromDB();
                }

                @Override public void onEmptyOrError(String message) {
                    ToastUtil.showToast(BasicInfoActivity.this, R.drawable.toast_warning, message);
                }
            });
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, titles);
        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(adapter);
        //添加事件Spinner事件监听
        mSpinner.setOnItemSelectedListener(new SpinnerSelectedListener());
        //设置默认值
        mSpinner.setVisibility(View.VISIBLE);
        //mPlayNO.setText(aircraftReg);
        ApiServiceManager.getInstance().getSeatInfo(aircraftReg ,null);
    }

    private void getAllSbFromDB() {
        AllSbDao allSbDao = FlightApplication.getDaoSession().getAllSbDao();
        List<AllSb> list =
                allSbDao.queryBuilder().where(AllSbDao.Properties.AcType.eq(aircraftType)).list();
        if (list != null && list.size() > 0) {
            titles = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                titles[i] = list.get(i).getSbName();
            }
        }
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

                if(TextUtils.isEmpty(mPlayNO.getText().toString().trim())) {
                    ToastUtil.showToast(BasicInfoActivity.this, R.drawable.toast_warning, "航班号不能为空");
                    return;
                } else if (TextUtils.isEmpty(mOrigination.getText().toString().trim())) {
                    ToastUtil.showToast(BasicInfoActivity.this, R.drawable.toast_warning, "起飞机场不能为空");
                    return;
                }else if(TextUtils.isEmpty(mDestination.getText().toString().trim())) {
                    ToastUtil.showToast(BasicInfoActivity.this, R.drawable.toast_warning, "目的机场不能为空");
                    return;
                }


                Intent intent = new Intent(BasicInfoActivity.this, EngineRoomActivity.class);
                //intent.putExtra("Lj", lj);
                //intent.putExtra("OpDate", opDate);
                //intent.putExtra("SysVersion", sysVersion);
                intent.putExtra("AircraftReg", aircraftReg);
                intent.putExtra("AircraftType", aircraftType);
                //intent.putExtra("Bw", bw);
                startActivity(intent);
            }
        };
    }

}
