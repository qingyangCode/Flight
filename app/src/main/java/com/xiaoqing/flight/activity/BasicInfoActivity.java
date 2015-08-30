package com.xiaoqing.flight.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.InjectView;
import com.xiaoqing.flight.FlightApplication;
import com.xiaoqing.flight.R;
import com.xiaoqing.flight.data.dao.AddFlightInfo;
import com.xiaoqing.flight.data.dao.AllAircraft;
import com.xiaoqing.flight.data.dao.AllSb;
import com.xiaoqing.flight.data.dao.AllSbDao;
import com.xiaoqing.flight.entity.AllSbResponse;
import com.xiaoqing.flight.network.ResponseListner;
import com.xiaoqing.flight.util.ApiServiceManager;
import com.xiaoqing.flight.util.Constants;
import com.xiaoqing.flight.util.FormatUtil;
import com.xiaoqing.flight.util.ToastUtil;
import com.xiaoqing.flight.util.UserManager;
import java.util.HashMap;
import java.util.List;

/**
 * Created by QingYang on 15/7/23.
 */
public class BasicInfoActivity extends BaseActivity {

    //@InjectView(R.id.spinner) Spinner mSpinner;
    @InjectView(R.id.tv_weight) EditText mWeight;
    @InjectView(R.id.tv_focus) EditText mFocus;
    @InjectView(R.id.tv_playNO) EditText mPlayNO;
    @InjectView(R.id.tv_originating) EditText mOrigination;
    @InjectView(R.id.tv_destination) EditText mDestination;
    @InjectView(R.id.layout_chafenzhan) LinearLayout mPointLayout;

    private String[] titles;
    private ArrayAdapter<String> adapter;
    private String aircraftReg;
    private String aircraftType;
    private double lj;
    private double bw;
    private List<AllSb> allSbList;

    @Override protected void onResume() {
        super.onResume();
        if (UserManager.getInstance().isAddFilghtSuccess())
            finish();
    }

    @Override public int getContentView() {
        return R.layout.activity_basicinfo;
    }

    @Override protected void onloadData() {
        Intent data = getIntent();
        if (data != null) {
            aircraftReg = data.getStringExtra("AircraftReg");
            aircraftType = data.getStringExtra("AircraftType");
            lj = data.getDoubleExtra("Lj", 0);
            bw = data.getDoubleExtra("Bw", 0);
            mWeight.setEnabled(false);
            mWeight.setText(FormatUtil.formatTo2Decimal(bw));
            mFocus.setEnabled(false);
            mFocus.setText(FormatUtil.formatTo2Decimal(lj / bw));
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
        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, titles);
        ////设置下拉列表的风格
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //
        //mSpinner.setAdapter(adapter);
        ////添加事件Spinner事件监听
        //mSpinner.setOnItemSelectedListener(new SpinnerSelectedListener());
        ////设置默认值
        //mSpinner.setVisibility(View.VISIBLE);
        //mPlayNO.setText(aircraftReg);
        ApiServiceManager.getInstance().getSeatInfo(aircraftReg ,null);
        ApiServiceManager.getInstance().getFilghtId(new ResponseListner<String>() {
            @Override public void onResponse(String response) {
                UserManager.getInstance().getAddFlightInfo().setFlightId(response);
            }

            @Override public void onEmptyOrError(String message) {

            }
        });
    }

    private void getAllSbFromDB() {
        AllSbDao allSbDao = FlightApplication.getDaoSession().getAllSbDao();
        allSbList =
                allSbDao.queryBuilder().where(AllSbDao.Properties.AcType.eq(aircraftType)).list();
        if (allSbList != null && allSbList.size() > 0) {
            titles = new String[allSbList.size()];
            for (int i = 0; i < allSbList.size(); i++) {
                titles[i] = allSbList.get(i).getSbName();
            }
        }
        final HashMap<Integer, Double> weightList = new HashMap<>();
        mPointLayout.removeAllViews();
        for (final AllSb allSb : allSbList) {
            View view = View.inflate(BasicInfoActivity.this, R.layout.layout_chafenzhan, null);
            TextView machineTitle = (TextView) view.findViewById(R.id.tv_firstName_title);
            CheckBox checkbox = (CheckBox) view.findViewById(R.id.checkbox);
            machineTitle.setText(allSb.getSbName());

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Double aDouble = weightList.get(allSb.getSbId());
                    if (isChecked) {
                        if (aDouble == null) {
                            weightList.put(allSb.getSbId(), allSb.getSbWeight());
                        }
                    } else {
                        if (aDouble != null) {
                            weightList.remove(allSb.getSbId());
                        }
                    }
                    double weightCount = 0;
                    for (Integer sbID : weightList.keySet()) {
                        weightCount += weightList.get(sbID);
                    }
                    mWeight.setText((bw + weightCount)+"");
                    mFocus.setText(FormatUtil.formatTo2Decimal(lj / (bw + weightCount)));

                }
            });
            mPointLayout.addView(view);
        }

    }



    @Override public View.OnClickListener getRightOnClickListener() {
        return new View.OnClickListener() {
            @Override public void onClick(View v) {
                String playNO = mPlayNO.getText().toString().trim();
                String from = mOrigination.getText().toString().trim();
                String toAirport = mDestination.getText().toString().trim();
                if(TextUtils.isEmpty(playNO)) {
                    ToastUtil.showToast(BasicInfoActivity.this, R.drawable.toast_warning, "航班号不能为空");
                    return;
                } else if (TextUtils.isEmpty(from)) {
                    ToastUtil.showToast(BasicInfoActivity.this, R.drawable.toast_warning, "起飞机场不能为空");
                    return;
                }else if(TextUtils.isEmpty(toAirport)) {
                    ToastUtil.showToast(BasicInfoActivity.this, R.drawable.toast_warning, "目的机场不能为空");
                    return;
                }

                AddFlightInfo addFlightInfo = UserManager.getInstance().getAddFlightInfo();
                addFlightInfo.setAircraftReg(aircraftReg);
                addFlightInfo.setAircraftType(aircraftType);
                addFlightInfo.setFlightNo(playNO);
                addFlightInfo.setDep4Code(from);
                addFlightInfo.setDepAirportName(from);
                addFlightInfo.setArr4Code(toAirport);
                addFlightInfo.setArrAirportName(toAirport);
                addFlightInfo.setNoFuleWeight(mWeight.getText().toString().trim());

                Intent intent = new Intent(BasicInfoActivity.this, EngineRoomActivity.class);
                //intent.putExtra("Lj", lj);
                //intent.putExtra("OpDate", opDate);
                //intent.putExtra("SysVersion", sysVersion);
                intent.putExtra(Constants.ACTION_AIRCRAFTREG, aircraftReg);
                intent.putExtra(Constants.ACTION_AIRCRAFTTYPE, aircraftType);
                //intent.putExtra("Bw", bw);
                startActivity(intent);
            }
        };
    }

}
