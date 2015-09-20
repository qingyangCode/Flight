package com.xiaoqing.flight.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.InjectView;
import com.xiaoqing.flight.FlightApplication;
import com.xiaoqing.flight.R;
import com.xiaoqing.flight.data.dao.AddFlightInfo;
import com.xiaoqing.flight.data.dao.AllAcSb;
import com.xiaoqing.flight.data.dao.AllAcSbDao;
import com.xiaoqing.flight.data.dao.AllAirport;
import com.xiaoqing.flight.data.dao.AllAirportDao;
import com.xiaoqing.flight.data.dao.AllSb;
import com.xiaoqing.flight.data.dao.AllSbDao;
import com.xiaoqing.flight.entity.AllSbResponse;
import com.xiaoqing.flight.network.ResponseListner;
import com.xiaoqing.flight.util.ApiServiceManager;
import com.xiaoqing.flight.util.Constants;
import com.xiaoqing.flight.util.DBManager;
import com.xiaoqing.flight.util.DateFormatUtil;
import com.xiaoqing.flight.util.FormatUtil;
import com.xiaoqing.flight.util.ToastUtil;
import com.xiaoqing.flight.util.UserManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by QingYang on 15/7/23.
 */
public class BasicInfoActivity extends BaseActivity {

    //@InjectView(R.id.spinner) Spinner mSpinner;
    @InjectView(R.id.tv_weight) EditText mWeight;
    @InjectView(R.id.tv_focus) EditText mFocus;
    @InjectView(R.id.tv_playNO) EditText mPlayNO;
    @InjectView(R.id.tv_originating) AutoCompleteTextView mOrigination;
    @InjectView(R.id.tv_destination) AutoCompleteTextView mDestination;
    @InjectView(R.id.layout_chafenzhan) LinearLayout mPointLayout;

    private ArrayAdapter<String> adapter;
    private String aircraftReg;
    private String aircraftType;
    private float lj;
    private float bw;
    private List<AllAcSb> allSbList;

    private int AIR_DEP = 1;//起飞机场
    private int AIR_ARR = 2;//到达机场

    @Override protected void onResume() {
        super.onResume();
        if (UserManager.getInstance().isAddFilghtSuccess())
            finish();
    }

    @Override public int getContentView() {
        return R.layout.activity_basicinfo;
    }

    @Override protected void onloadData() {
        mTopBarTitle.setText("飞机基本信息");
        mTopBarRight.setText("下一步");

        Intent data = getIntent();
        if (data != null) {
            aircraftReg = data.getStringExtra("AircraftReg");
            aircraftType = data.getStringExtra("AircraftType");
            lj = data.getFloatExtra("Lj", 0);
            bw = data.getFloatExtra("Bw", 0);
            mWeight.setEnabled(false);
            mWeight.setText(FormatUtil.formatTo2Decimal(bw));
            mFocus.setEnabled(false);
            mFocus.setText(FormatUtil.formatTo2Decimal(lj / bw));
        }

        List<AllAcSb> allAcSbFromDB = getAllSbFromDB();
        if (allAcSbFromDB == null || allAcSbFromDB.size() == 0) {
            ApiServiceManager.getInstance().getAllSb(new ResponseListner<AllSbResponse>() {
                @Override public void onResponse(AllSbResponse response) {
                    getAllSbFromDB();
                }

                @Override public void onEmptyOrError(String message) {
                    ToastUtil.showToast(BasicInfoActivity.this, R.drawable.toast_warning, message);
                }
            });
        }

        //机场代码
        List<AllAirport> allAirportList = DBManager.getInstance().getAllAirPort();
        String[] airName = null;
        if (allAirportList != null) {
            airName = new String[allAirportList.size() * 2];
            for(int i = 0; i <  allAirportList.size(); i++) {
                airName[i] = allAirportList.get(i).getStrAirportName();
            }

            for (int j = 0; j < allAirportList.size(); j++) {
                airName[j + allAirportList.size()] = allAirportList.get(j).getStr4code();
            }
        }
        ArrayAdapter<String> stringArrayAdapter =
                new ArrayAdapter<String>(mContext, R.layout.list_item, airName);
        mOrigination.setAdapter(stringArrayAdapter);
        mDestination.setAdapter(stringArrayAdapter);
        //ApiServiceManager.getInstance().getSeatInfo(aircraftReg, null);
        ApiServiceManager.getInstance().getFilghtId(new ResponseListner<String>() {
            @Override public void onResponse(String response) {
                UserManager.getInstance().getAddFlightInfo().setFlightId(response);
            }

            @Override public void onEmptyOrError(String message) {
                UserManager.getInstance().getAddFlightInfo().setFlightId(DateFormatUtil.formatTDate());

            }
        });
    }

    @Override protected void initEvents() {
        super.initEvents();
        mOrigination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override public void afterTextChanged(Editable s) {
                String airName = s.toString();
                if (checkChinese(airName)) {
                    getAir4Code(airName, AIR_DEP);
                    UserManager.getInstance().getAddFlightInfo().setDepAirportName(airName);
                } else {
                    getAirName(airName, AIR_DEP);
                    UserManager.getInstance().getAddFlightInfo().setDep4Code(airName);
                }
            }
        });
        mDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override public void afterTextChanged(Editable s) {
                String airName = s.toString();
                if (checkChinese(airName)) {
                    getAir4Code(airName, AIR_ARR);
                    UserManager.getInstance().getAddFlightInfo().setArrAirportName(airName);
                } else {
                    getAirName(airName, AIR_ARR);
                    UserManager.getInstance().getAddFlightInfo().setArr4Code(airName);
                }
            }
        });
    }

    //获取飞机四字代码 1 起飞机场  2 降落机场
    private void getAirName(String airName, int type) {
        if (!TextUtils.isEmpty(airName)) {
            AllAirportDao allAirportDao =
                    FlightApplication.getDaoSession().getAllAirportDao();
            List<AllAirport> list = allAirportDao.queryBuilder()
                    .where(AllAirportDao.Properties.Str4code.eq(airName))
                    .list();
            if (list != null && list.size() != 0) {
                String strAirportName = list.get(0).getStrAirportName();
                if (type == AIR_DEP) {
                    mOrigination.setText(strAirportName);
                    UserManager.getInstance().getAddFlightInfo().setDepAirportName(strAirportName);
                } else {
                    mDestination.setText(strAirportName);
                    UserManager.getInstance().getAddFlightInfo().setArrAirportName(strAirportName);
                }
            }
        }
    }

    private void getAir4Code(String airName, int type) {
        if (!TextUtils.isEmpty(airName)) {
            AllAirportDao allAirportDao =
                    FlightApplication.getDaoSession().getAllAirportDao();
            List<AllAirport> list = allAirportDao.queryBuilder()
                    .where(AllAirportDao.Properties.StrAirportName.eq(airName))
                    .list();
            if (list != null && list.size() != 0) {
                if (type == AIR_DEP) {
                    UserManager.getInstance().getAddFlightInfo().setDep4Code(list.get(0)
                            .getStr4code());
                } else {
                    UserManager.getInstance().getAddFlightInfo().setArr4Code(list.get(0)
                            .getStr4code());
                }
            }
        }
    }


    public static boolean checkChinese(String sequence) {
        final String format = "[\\u4E00-\\u9FA5\\uF900-\\uFA2D]";
        boolean result = false;
        Pattern pattern = Pattern.compile(format);
        Matcher matcher = pattern.matcher(sequence);
        result = matcher.find();
        return result;
    }


    /**
     * 差分站设备
     */
    private List<AllAcSb> getAllSbFromDB() {


        AllAcSbDao allAcSbDao = FlightApplication.getDaoSession().getAllAcSbDao();
        allSbList =
                allAcSbDao.queryBuilder().where(AllAcSbDao.Properties.AcReg.eq(aircraftReg)).list();
        final HashMap<Integer, Float> weightList = new HashMap<>();
        mPointLayout.removeAllViews();
        for (final AllAcSb allAcSb : allSbList) {
            View view = View.inflate(BasicInfoActivity.this, R.layout.layout_chafenzhan, null);
            TextView machineTitle = (TextView) view.findViewById(R.id.tv_firstName_title);
            CheckBox checkbox = (CheckBox) view.findViewById(R.id.checkbox);
            AllSbDao allSbDao = FlightApplication.getDaoSession().getAllSbDao();
            List<AllSb> list = allSbDao.queryBuilder()
                    .where(AllSbDao.Properties.AcType.eq(aircraftType),
                            AllSbDao.Properties.SbId.eq(allAcSb.getSbId()))
                    .list();
            if (list != null && list.size() != 0)
                machineTitle.setText(list.get(0).getSbName());

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Float aFloat = weightList.get(allAcSb.getSbId());
                    if (isChecked) {
                        if (aFloat == null) {
                            weightList.put(allAcSb.getSbId(), allAcSb.getSbWeight());
                        }
                        ArrayList<Integer> sbList = new ArrayList<>();
                        sbList.add(allAcSb.getSbId());
                        UserManager.getInstance().getAddFlightInfo().setSbList(sbList);
                    } else {
                        if (aFloat != null) {
                            weightList.remove(allAcSb.getSbId());
                        }
                        List<Integer> sbList =
                                UserManager.getInstance().getAddFlightInfo().getSbList();
                        if (sbList != null && sbList.size() > 0 && sbList.contains(allAcSb.getSbId())) {
                            sbList.remove(allAcSb.getSbId());
                        }
                    }
                    float weightCount = 0;
                    float allSbLj = 0;
                    for (Integer sbID : weightList.keySet()) {
                        weightCount += weightList.get(sbID);
                        if (sbID == allAcSb.getSbId()) {
                            allSbLj += allAcSb.getSbLb() * weightList.get(sbID);
                        }
                    } mWeight.setText((bw + weightCount) + "");

                    mFocus.setText(FormatUtil.formatTo2Decimal((allSbLj + lj) / (bw + weightCount)));
                }
            });
            mPointLayout.addView(view);
        }

        return allSbList;

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
                } else if (playNO.equals("CFI0")) {
                    ToastUtil.showToast(BasicInfoActivity.this, R.drawable.toast_warning, "请补全航班号");
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
                //addFlightInfo.setDep4Code(from);
                //addFlightInfo.setDepAirportName(from);
                //addFlightInfo.setArr4Code(toAirport);
                addFlightInfo.setArrAirportName(toAirport);
                addFlightInfo.setNoFuleWeight(mWeight.getText().toString().trim());
                addFlightInfo.setWeightCg(mFocus.getText().toString().trim());

                UserManager.getInstance().clearUserNameMaps();

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
