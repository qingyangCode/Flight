package com.xiaoqing.flight.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.InjectView;
import com.xiaoqing.flight.FlightApplication;
import com.xiaoqing.flight.R;
import com.xiaoqing.flight.data.dao.AcGrants;
import com.xiaoqing.flight.data.dao.AcGrantsDao;
import com.xiaoqing.flight.data.dao.AcWeightLimit;
import com.xiaoqing.flight.data.dao.AddFlightInfo;
import com.xiaoqing.flight.data.dao.AllAcSb;
import com.xiaoqing.flight.data.dao.AllAcSbDao;
import com.xiaoqing.flight.data.dao.AllAcType;
import com.xiaoqing.flight.data.dao.AllAcTypeDao;
import com.xiaoqing.flight.data.dao.AllAircraft;
import com.xiaoqing.flight.data.dao.AllAircraftDao;
import com.xiaoqing.flight.data.dao.FuleLimit;
import com.xiaoqing.flight.data.dao.FuleLimitDao;
import com.xiaoqing.flight.data.dao.SeatByAcReg;
import com.xiaoqing.flight.entity.AddFlightInfoResponse;
import com.xiaoqing.flight.entity.FuleLimitResponse;
import com.xiaoqing.flight.entity.ValidCaptionResponse;
import com.xiaoqing.flight.network.ResponseListner;
import com.xiaoqing.flight.network.synchronous.FeedType;
import com.xiaoqing.flight.util.ApiServiceManager;
import com.xiaoqing.flight.util.CommonProgressDialog;
import com.xiaoqing.flight.util.Constants;
import com.xiaoqing.flight.util.DBManager;
import com.xiaoqing.flight.util.DateFormatUtil;
import com.xiaoqing.flight.util.FormatUtil;
import com.xiaoqing.flight.util.LogUtil;
import com.xiaoqing.flight.util.MACUtil;
import com.xiaoqing.flight.util.ToastUtil;
import com.xiaoqing.flight.util.UserManager;
import com.xiaoqing.flight.util.WindowUtil;
import com.xiaoqing.flight.widget.MyView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by QingYang on 15/7/24.
 * 限制图
 */
public class RestrictionMapActivity extends BaseActivity{
    private String TAG = RestrictionMapActivity.class.getSimpleName();

    private final int ACTION_ADDFILGHT = 1;

    @InjectView(R.id.tv_maxoil)
    TextView mMaxOil;
    @InjectView(R.id.tv_realityoil)
    EditText mRealityouil;
    @InjectView(R.id.tv_slideoil)
    EditText mSlideOil;
    @InjectView(R.id.tv_flyoil)
    EditText mFlyOil;
    @InjectView(R.id.tv_beforeweight)
    TextView mBeforeWeight;
    @InjectView(R.id.tv_down)
    TextView mDownWight;
    @InjectView(R.id.tv_mac)
    TextView mMac;
    @InjectView(R.id.line_charview)
    MyView  mLineCharView;
    @InjectView(R.id.tv_flightCg)
    TextView mFlightCg;
    //@InjectView(R.id.tv_reality_focus)
    //TextView mRealityFocus;
    //@InjectView(R.id.tv_slide_focus)
    //TextView mSlideFocus;
    //@InjectView(R.id.tv_fly_focus)
    //TextView mFlyFocus;
    //@InjectView(R.id.tv_be_fore_focus)
    //TextView mBroreFocus;
    //@InjectView(R.id.tv_down_focus)
    //TextView mDownFocus;
    private ArrayList<Float> lineX;
    private ArrayList<Float> lineY;

    private ArrayList<SeatByAcReg> seatList;
    private String aircraftType;
    private float  airLj = 0;
    List<AllAcType> allAcTypeList;
    private float allWeight;
    private String aircraftReg;
    private Context mContext;
    private CommonProgressDialog progressDialog;

    private Handler handler = new Handler() {
        @Override public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ACTION_ADDFILGHT://添加航班信息
                    addFlightInfo();
                    break;
            }
        }
    };


    @Override public int getContentView() {
        return R.layout.activity_restrictionmap;
    }

    @Override public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mContext = RestrictionMapActivity.this;
    }

    @Override protected void onResume() {
        super.onResume();
        if (UserManager.getInstance().isAddFilghtSuccess()) {
            hiddenRightBar();
        }
    }

    @Override protected void onloadData() {
        mTopBarTitle.setText("限制图");
        getTopBarRight("机长确认");
        lineX = new ArrayList<>();
        lineY = new ArrayList<>();
        aircraftType = getIntent().getStringExtra(Constants.ACTION_AIRCRAFTTYPE);
        aircraftReg = getIntent().getStringExtra(Constants.ACTION_AIRCRAFTREG);
        seatList = (ArrayList<SeatByAcReg>) getIntent().getSerializableExtra(Constants.ACTION_SEATLIST);
        //飞机基本信息
        AllAircraftDao allAircraftDao = FlightApplication.getDaoSession().getAllAircraftDao();
        List<AllAircraft> allAircraftList = allAircraftDao.queryBuilder()
                .where(AllAircraftDao.Properties.AircraftReg.eq(aircraftReg))
                .list();

        if (allAircraftList != null && allAircraftList.size() > 0)
        airLj = allAircraftList.get(0).getLj();

        updateWeightInfos();


        //float weightCg = getWeightCg(mRealityouil);
        //mRealityouil.setText(weightCg + "");
        //float weightCg1 = getWeightCg(mSlideOil);
        //mSlideFocus.setText(weightCg1 + "");
        //float weightCg2 = getWeightCg(mFlyOil);
        //mFlyOil.setText(weightCg2 + "");
        //float weightCg3 = getWeightCg(mBeforeWeight);
        //mBeforeWeight.setText(weightCg3 + "");
        //float weightCg4 = getWeightCg(mDownWight);
        //mDownWight.setText(weightCg4+"");

        //机型信息 最大起飞重量 最大无油重量 最大机坪重量
        AllAcTypeDao allAcTypeDao = FlightApplication.getDaoSession().getAllAcTypeDao();
        allAcTypeList = allAcTypeDao.queryBuilder()
                .where(AllAcTypeDao.Properties.AircraftType.eq(aircraftType))
                .list();
        if (allAcTypeList != null && allAcTypeList.size() > 0) {
            AllAcType allAcType = allAcTypeList.get(0);
            mMaxOil.setText(allAcType.getMaxFule() + "");

            getFuleLimitFromDB();


            //getMoccApi().getAcWeightLimitByAcType(allAcType.getAircraftType(), String.valueOf(allAcType.getPortLimit()),
            //    mBeforeWeight.getText().toString(), mDownWight.getText().toString(), String.valueOf(allAcType.getMzfw()),
            //        DateFormatUtil.formatTDate(), String.valueOf(allAcType.getSysVersion()), new ResponseListner<AcWeightLimitResponse>() {
            //        @Override public void onResponse(AcWeightLimitResponse response) {
            //            if (response != null && response.ResponseObject != null && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
            //                showCharView(response.ResponseObject.ResponseData.IAppObject);
            //            } else {
            //                ToastUtil.showToast(RestrictionMapActivity.this, R.drawable.toast_warning, getString(R.string.get_data_error));
            //            }
            //        }
            //
            //        @Override public void onEmptyOrError(String message) {
            //            ToastUtil.showToast(RestrictionMapActivity.this, R.drawable.toast_warning, getString(R.string.get_data_error));
            //        }
            //    });
        }

        mMaxOil.addTextChangedListener(mTextWatcher);
        mRealityouil.addTextChangedListener(mTextWatcher);
        mSlideOil.addTextChangedListener(mTextWatcher);
        mFlyOil.addTextChangedListener(mTextWatcher);



    }

    private List<FuleLimit> getFuleLimitFromDB() {
        FuleLimitDao fuleLimitDao = FlightApplication.getDaoSession().getFuleLimitDao();
        List<FuleLimit> list = fuleLimitDao.queryBuilder()
                .where(FuleLimitDao.Properties.AcType.eq(aircraftType))
                .list();

        if (list != null && list.size() > 0) {
            //showCharView(list);
        }

        return list;
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override public void afterTextChanged(Editable s) {
            updateWeightInfos();
        }
    };

    //飞机重量
    private void updateWeightInfos() {
        AllAcTypeDao allAcTypeDao = FlightApplication.getDaoSession().getAllAcTypeDao();
        List<AllAcType> allAcTypeList = allAcTypeDao.queryBuilder()
                .where(AllAircraftDao.Properties.AircraftType.eq(aircraftType))
                .list();
        if (allAcTypeList != null && allAcTypeList.size() > 0) {
            AllAcType allAcType = allAcTypeList.get(0);
            float gooodsWeights = 0;
            float totalPassengerLj = 0;
            if (seatList != null && seatList.size() > 0)
                for(SeatByAcReg seatByAcReg : seatList) {
                    //已有的货物重量 ＋ 座椅上人 或 物品重量
                    gooodsWeights += seatByAcReg.getAcRegCargWeight() + seatByAcReg.getSeatWeight();
                    if ("C".equalsIgnoreCase(seatByAcReg.getSeatType())) {//货物 ＋ 额外物品重量
                        totalPassengerLj = totalPassengerLj + (seatByAcReg.getSeatWeight() + seatByAcReg.getAcRegCargWeight()) * seatByAcReg.getAcTypeLb();
                    } else {
                        totalPassengerLj += seatByAcReg.getAcTypeLb() * seatByAcReg.getSeatWeight();
                    }
                }


            String realOil = mRealityouil.getText().toString().trim();
            String slideOil = mSlideOil.getText().toString().trim();
            String flyOil = mFlyOil.getText().toString().trim();
            float realOilFloat = 0;
            float slideOilFloat = 0;
            float flyOilFloat = 0;
            float beforeWeightFloat = 0;
            float downWeightFloat = 0;
            String noFuleWeight = UserManager.getInstance().getAddFlightInfo().getNoFuleWeight();
            float noFuleweight = 0;
            try {
                if (!TextUtils.isEmpty(realOil))
                    realOilFloat = Float.parseFloat(realOil);
                if (!TextUtils.isEmpty(slideOil))
                    slideOilFloat = Float.parseFloat(slideOil);
                if (!TextUtils.isEmpty(flyOil))
                    flyOilFloat = Float.parseFloat(flyOil);
                if (!TextUtils.isEmpty(noFuleWeight))
                    noFuleweight = Float.parseFloat(noFuleWeight);
            } catch (Exception e) {
                e.printStackTrace();
            }

            float upWeight =
                    noFuleweight + realOilFloat + gooodsWeights - slideOilFloat;//TODO 加设备重量 ＋ 额外重量  // 基本空重 ＋ 设备
            LogUtil.LOGD(TAG, "noFuleweight : " + noFuleweight +" realOilFloat : " + realOilFloat + " gooodsWeights : " + gooodsWeights + " slideOilFloat : " + slideOilFloat);
            mBeforeWeight.setText(FormatUtil.formatTo2Decimal(upWeight));

            float downWeight =
                    noFuleweight + realOilFloat + gooodsWeights
                            - slideOilFloat  - flyOilFloat;

            mDownWight.setText(FormatUtil.formatTo2Decimal(downWeight));
            allWeight = noFuleweight + realOilFloat + gooodsWeights;

            //起飞重心： 力矩／重量 （基本空重力矩 ＋ 乘客 + 货物 ＋ 额外重量 ＋ 设备 ＋ 起飞油量）/（实际油量 － 滑行油量 + 基本空中 ＋ 设备重 ＋ 乘客重 ＋ 货物重）// 油量力矩

            //所有设备的lj
            float totalSbLj = 0;
            List<Integer> sbList = UserManager.getInstance().getAddFlightInfo().getSbList();
            if (sbList != null && sbList.size() > 0) {
                for (Integer sbInfo : sbList) {
                    AllAcSbDao allAcSbDao = FlightApplication.getDaoSession().getAllAcSbDao();
                    List<AllAcSb> list = allAcSbDao.queryBuilder()
                            .where(AllAcSbDao.Properties.SbId.eq(sbInfo),
                                    AllAcSbDao.Properties.AcReg.eq(aircraftReg))
                            .list();
                    if (list != null && list.size() > 0) {
                        totalSbLj += list.get(0).getSbWeight() * list.get(0).getSbLb();
                    }
                }
            }

            //起飞油量力矩
            float beforeLj = 0;
            float beforeFlightOil = realOilFloat - slideOilFloat;
            FuleLimitDao fuleLimitDao = FlightApplication.getDaoSession().getFuleLimitDao();
            List<FuleLimit> fuleLimits = fuleLimitDao.queryBuilder().list();
            int temp = 0;

            for (int i = 0; i < fuleLimits.size(); i++) {// 二分法计算输入油量最近的取值
                if (beforeFlightOil == fuleLimits.get(i).getFuleWeight()) {
                    beforeLj = fuleLimits.get(i).getFuleLj();
                    break;
                }
            }

            if (beforeLj == 0) {
                int left = 0;
                int right = fuleLimits.size() - 1;
                while (left < right) {
                    int middle = (left + right) / 2;
                    if (beforeFlightOil < fuleLimits.get(middle).getFuleWeight()) {
                        right = middle - 1;
                    } else {
                        left = middle + 1;
                    }
                    temp = middle;
                }
                if (beforeFlightOil < fuleLimits.get(temp).getFuleWeight()) {
                    beforeLj = fuleLimits.get(temp).getFuleLj()
                            - (fuleLimits.get(temp).getFuleWeight() - fuleLimits.get(temp - 1)
                            .getFuleWeight()) * (fuleLimits.get(temp).getFuleLj() - beforeFlightOil)
                            / (fuleLimits.get(temp).getFuleWeight() - fuleLimits.get(temp - 1)
                            .getFuleWeight());
                }
            }

           LogUtil.LOGD(TAG, "before fly oil == " + beforeLj);


            float flightCg = 0;
            try {
                flightCg = (airLj + totalPassengerLj + totalSbLj + beforeLj) / ( - slideOilFloat + allWeight);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mFlightCg.setText(FormatUtil.formatTo2Decimal(flightCg));

            getFuleLimit();


        }
    }

    private int reqeustCount = 0;

    private void getFuleLimit() {
        float oilLj = 0;
        FuleLimitDao fuleLimitDao = FlightApplication.getDaoSession().getFuleLimitDao();
        List<FuleLimit> fullLimitList = fuleLimitDao.queryBuilder()
                .where(FuleLimitDao.Properties.AcType.eq(aircraftType))
                .list();
        String realOilInput = mRealityouil.getText().toString().trim();
        float realOil = 0;
        try{
            if (!TextUtils.isEmpty(realOilInput))
                realOil = Float.parseFloat(realOilInput);
        }catch (Exception e){

        }
        if (fullLimitList != null && fullLimitList.size() > 0) {
            for (FuleLimit fuleLimit : fullLimitList) {

                if (Math.abs(fuleLimit.getFuleWeight() - realOil) < 80) {
                    oilLj = fuleLimit.getFuleLj();
                }
            }
        }

        double Cg = (airLj + airLj) / allWeight;
        float weightCg = (float)Cg;
        float mac = 0;
        if (MACUtil.TYPE_CE560.equals(aircraftType)) {
            mac = MACUtil.get560Mac(weightCg);
        } else if (MACUtil.TYPE_CE680.equals(aircraftType)) {
            mac = MACUtil.get680Mac(weightCg);
        } else if (MACUtil.TYPE_CE750.equals(aircraftType)) {
            mac = MACUtil.get750Mac(weightCg);
        } else if (MACUtil.TYPE_G450.equals(aircraftType)) {
            mac = MACUtil.getG450Mac(weightCg);
        }

        if (mac != 0)
            mMac.setText(FormatUtil.formatTo2Decimal(mac));
        else
            mMac.setText(" -- ");


        if ((fullLimitList == null || fullLimitList.size() == 0) && reqeustCount < 1) {
            reqeustCount ++;
            if (allAcTypeList != null && allAcTypeList.size() > 0) {
                AllAcType allAcType = allAcTypeList.get(0);
                ApiServiceManager.getInstance().getFuleLimitByAcType(allAcType.getAircraftType(),
                        allAcType.getPortLimit()+"", mBeforeWeight.getText().toString(),
                        mDownWight.getText().toString(), allAcType.getMzfw()+"", DateFormatUtil.formatTDate(),
                        UserManager.getInstance().getUser().getSysVersion()+"", new ResponseListner<FuleLimitResponse>() {

                            @Override public void onResponse(FuleLimitResponse response) {
                                if (response != null && response.ResponseObject != null && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                                    getFuleLimit();
                                }
                            }

                            @Override public void onEmptyOrError(String message) {
                                if (!TextUtils.isEmpty(message))
                                    ToastUtil.showToast(RestrictionMapActivity.this, R.drawable.toast_warning, message);
                            }
                        });

            }
        }
    }

    private void showCharView(ArrayList<AcWeightLimit> acWeightLimits) {
        String [] xData = new String[acWeightLimits.size()];
        String [] yData = new String[acWeightLimits.size()];
        String [] datas = new String[acWeightLimits.size() * 2];
        for (int i = 0 ; i < acWeightLimits.size(); i++ ) {
            String weight = acWeightLimits.get(i).getWeight();
            //float wei = 0;
            //if (!TextUtils.isEmpty(weight))
            //    wei = Float.parseFloat(weight);
            xData[i] = weight;
            String Cg1 = acWeightLimits.get(i).getWeightCg1();
            //float weiCg1 = 0;
            //if (!TextUtils.isEmpty(Cg1))
            //    weiCg1 = Float.parseFloat(Cg1);
            yData[i] = (380 + (i * 5))+"";
            datas[i] = Cg1;
        }

        for (int y = acWeightLimits.size() - 1; y > 0; y--) {
            //String weight = acWeightLimits.get(y).getWeight();
            //float wei = 0;
            //if (!TextUtils.isEmpty(weight))
            //    wei =Float.parseFloat(weight);
            //lineX.add(wei);
            String weightCg2 = acWeightLimits.get(y).getWeightCg2();
            datas[y * 2] = weightCg2;
            //float weiCg2 = 0;
            //if (!TextUtils.isEmpty(weightCg2)) {
            //    lineY.add(weiCg2);
            //}
        }
        //mLineCharView.addData(lineData);
        mLineCharView.SetInfo(xData, yData, datas, "限制图");

    }



    private float getWeightCg(TextView tv) {
        String weight = tv.getText().toString().trim();
        float result = 0;
        float weighFloat = 0;
        if (!TextUtils.isEmpty(weight)) {
            weighFloat = Float.parseFloat(weight);
            float lj = weighFloat / 100;
            double weightCg = lj * 347.51 / weighFloat;
            result =  (float)weightCg;
        }
        lineX.add(weighFloat);
        lineY.add(result);
        return result;
    }


    @Override public View.OnClickListener getRightOnClickListener() {
        return new View.OnClickListener() {
            @Override public void onClick(View v) {

                String maxOil = mMaxOil.getText().toString().trim();
                String realityOil = mRealityouil.getText().toString().trim();
                String slideOil = mSlideOil.getText().toString().trim();
                String flyOil = mFlyOil.getText().toString().trim();
                String beforeWeight = mBeforeWeight.getText().toString().trim();
                String downWeight = mDownWight.getText().toString().trim();
                String macValue = mMac.getText().toString().trim();

                if (TextUtils.isEmpty(maxOil)) {
                    ToastUtil.showToast(mContext, R.drawable.toast_warning, "最大燃油量不能为空");
                    return;
                } else if (TextUtils.isEmpty(realityOil)) {
                    ToastUtil.showToast(mContext, R.drawable.toast_warning, "实际加油量不能为空");
                    return;
                } else if (TextUtils.isEmpty(slideOil)) {
                    ToastUtil.showToast(mContext, R.drawable.toast_warning, "滑行油量不能为空");
                    return;
                } else if (TextUtils.isEmpty(flyOil)) {
                    ToastUtil.showToast(mContext, R.drawable.toast_warning, "航路油量不能为空");
                    return;
                }

                AddFlightInfo addFlightInfo = UserManager.getInstance().getAddFlightInfo();
                addFlightInfo.setMaxFule(maxOil);
                addFlightInfo.setRealFule(realityOil);
                addFlightInfo.setSlieFule(slideOil);
                addFlightInfo.setRouteFule(flyOil);
                addFlightInfo.setTofWeight(beforeWeight);
                addFlightInfo.setLandWeight(downWeight);
                addFlightInfo.setAirportLimitWeight(allAcTypeList.get(0).getPortLimit() + "");
                addFlightInfo.setBalancePic(addFlightInfo.getFlightNo());
                addFlightInfo.setBalancePicName(addFlightInfo.getFlightNo());

                //Intent intent =
                //        new Intent(RestrictionMapActivity.this, AircraftPersonnelActivity.class);
                //intent.putExtra(Constants.ACTION_AIRCRAFTTYPE, aircraftType);
                //intent.putExtra(Constants.ACTION_AIRCRAFTREG, aircraftreg);
                //startActivity(intent);
                showDialog("机长确认", "请输入机长姓名", "机长密码");


            }
        };
    }

    public void showDialog(String titleText, String hintText, final String hintText2) {

        final AlertDialog alBuilder = new AlertDialog.Builder(this).create();
        View view = LayoutInflater.from(this).inflate(R.layout.prompt_dialog, null);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        TextView title = (TextView) view.findViewById(R.id.title);
        final EditText tv_password = (EditText) view.findViewById(R.id.tv_password);
        tv_password.setTransformationMethod(PasswordTransformationMethod.getInstance());


        if (!TextUtils.isEmpty(titleText)) title.setText(titleText);
        final TextView tv_userName = (TextView) view.findViewById(R.id.tv_userName);
        if (!TextUtils.isEmpty(hintText)) tv_userName.setHint(hintText);
        if (!TextUtils.isEmpty(hintText2)) tv_password.setHint(hintText2);
        tv_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                alBuilder.dismiss();
                confirm(tv_userName.getText().toString().trim(), tv_password.getText().toString().trim());
                return false;
            }
        });

        Button done = (Button) view.findViewById(R.id.done);
        Window window = alBuilder.getWindow();
        alBuilder.show();
        alBuilder.setContentView(view);
        done.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                confirm(tv_userName.getText().toString().trim(), tv_password.getText().toString().trim());
                alBuilder.dismiss();
            }
        });
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int) (WindowUtil.initWindow().getScreenWidth(this) / 1.2f); // 设置宽度
        lp.softInputMode =
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;//显示dialog的时候,就显示软键盘
        lp.flags =
                WindowManager.LayoutParams.FLAG_DIM_BEHIND;//就是这个属性导致不能获取焦点,默认的是FLAG_NOT_FOCUSABLE,故名思义不能获取输入焦点,
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER);
    }

    /**
     * 验证机长信息
     *
     * @param userName
     */

    private boolean isCancelAble;
    //机长验证弹出框
    private void confirm(final String userName, final String password) {
        isCancelAble = false;
        showProgressDialog();
        getMoccApi().validCaption(userName, aircraftType, password, new ResponseListner<ValidCaptionResponse>() {
            @Override public void onResponse(ValidCaptionResponse response) {
                hiddenDialog();
                if (isCancelAble) return;
                if (response != null && response.ResponseObject != null) {
                    if (response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                        //addFlightInfo();
                        verifiFlightInfo();
                    } else if ("NO".equalsIgnoreCase(response.ResponseObject.ResponseErr)) {
                        ToastUtil.showToast(mContext,
                                R.drawable.toast_warning, "当前操作需机长验证，请输入机长信息");
                    } else {
                        //ToastUtil.showToast(mContext,
                        //        R.drawable.toast_warning, "未获取到机长信息");
                        boolean b = vidifyCaptainByDB(userName, password);
                        if (b) {
                            //addFlightInfo();
                            verifiFlightInfo();
                        } else {
                            ToastUtil.showToast(mContext,
                                R.drawable.toast_warning, "机长信息不正确，请重新输入");
                        }
                    }
                }
            }

            @Override public void onEmptyOrError(String message) {
                hiddenDialog();
                if (isCancelAble) return;
                if(vidifyCaptainByDB(userName, password)) {
                    //addFlightInfo();
                    verifiFlightInfo();
                } else {
                    ToastUtil.showToast(mContext,
                            R.drawable.toast_warning, "机长信息不正确，请重新输入");
                }
                //ToastUtil.showToast(mContext, R.drawable.toast_warning,
                //        getString(R.string.get_data_error));
            }
        });
    }

    //机长本地数据库验证
    private boolean vidifyCaptainByDB (String userName, String password) {
        AcGrantsDao acGrantsDao = FlightApplication.getDaoSession().getAcGrantsDao();
        List<AcGrants> list = acGrantsDao.queryBuilder()
                .where(AcGrantsDao.Properties.UserCode.eq(userName),
                        AcGrantsDao.Properties.AcReg.eq(aircraftReg))
                .list();
        if (list != null && list.size() > 0) {
            if ("Y".equalsIgnoreCase(list.get(0).getIsCaption())) {
                return true;
            }
        }
        return false;
    }

    //验证飞机信息
    private void verifiFlightInfo() {
        String realityOil = mRealityouil.getText().toString().trim();
        String slideOil = mSlideOil.getText().toString().trim();
        String flyOil = mFlyOil.getText().toString().trim();
        String beforeWeight = mBeforeWeight.getText().toString().trim();
        String downWeight = mDownWight.getText().toString().trim();
        String macValue = mMac.getText().toString().trim();

        //所有座椅重量
        float totalSeatWeight = 0;
        for (SeatByAcReg seatByAcReg : seatList) {
            if (seatByAcReg.getXPos() != 0) {
                if (!TextUtils.isEmpty(seatByAcReg.getUserName()) || ("C".equalsIgnoreCase(seatByAcReg.getSeatType()) && seatByAcReg.getSeatWeight() != 0)) {
                    totalSeatWeight = totalSeatWeight + seatByAcReg.getSeatWeight() + seatByAcReg.getAcRegCargWeight();
                }
            }
        }

        AddFlightInfo addFlightInfo = UserManager.getInstance().getAddFlightInfo();
        String noFuleWeight = addFlightInfo.getNoFuleWeight();
        String weightCg = addFlightInfo.getWeightCg();
        float nofuWeightFloat = 0;//实际最大无油重量
        float realOilFloat = 0;
        float slideOilFloat = 0;
        float flyOilFloat = 0;
        float beforeWeightFloat = 0;
        float downWeightFloat = 0;
        try {
            if (!TextUtils.isEmpty(noFuleWeight))
                nofuWeightFloat = Float.parseFloat(noFuleWeight);
            if (!TextUtils.isEmpty(realityOil))
                realOilFloat = Float.parseFloat(realityOil);
            if (!TextUtils.isEmpty(slideOil))
                slideOilFloat = Float.parseFloat(slideOil);
            if (!TextUtils.isEmpty(flyOil))
                flyOilFloat = Float.parseFloat(flyOil);
            if (!TextUtils.isEmpty(beforeWeight))
                beforeWeightFloat = Float.parseFloat(beforeWeight);
            if (!TextUtils.isEmpty(downWeight) && !"--".equals(downWeight))
                downWeightFloat = Float.parseFloat(downWeight);
        } catch (Exception e) {
            e.printStackTrace();
        }

        AllAcTypeDao allAcTypeDao = FlightApplication.getDaoSession().getAllAcTypeDao();
        List<AllAcType> list = allAcTypeDao.queryBuilder()
                .where(AllAcTypeDao.Properties.AircraftType.eq(aircraftType))
                .list();

        if (list != null && list.size() > 0) {
            AllAcType allAcType = list.get(0);
            float qifeiweight = nofuWeightFloat + realOilFloat + totalSeatWeight - slideOilFloat;
            float zhouluweight = qifeiweight - flyOilFloat;
            //最大起飞重量
            if (allAcType.getMzfw() -  (nofuWeightFloat + totalSeatWeight) < 0) {
                ToastUtil.showToast(mContext, R.drawable.toast_warning, "无燃油重量超过了最大无燃油重量限制, 超重 : " + Math.abs(
                        (nofuWeightFloat + totalSeatWeight) - allAcType.getMzfw()) +" lb");
                return;
            } else if (allAcType.getPortLimit() - (nofuWeightFloat + realOilFloat + totalSeatWeight) < 0) {
                ToastUtil.showToast(mContext, R.drawable.toast_warning, "滑行重量超过了机坪重量限制, 超重 : " + Math.abs((nofuWeightFloat + realOilFloat + totalSeatWeight) - allAcType.getPortLimit())+"lb");
                return;
            } else if(allAcType.getTofWeightLimit() < qifeiweight) {
                ToastUtil.showToast(mContext, R.drawable.toast_warning, "起飞重量超过了最大起飞重量限制, 超重 : "
                        + Math.abs(qifeiweight - allAcType.getTofWeightLimit()) + " lb");
                return;
            } else if (allAcType.getLandWeightLimit() - zhouluweight < 0) {
                ToastUtil.showToast(mContext, R.drawable.toast_warning, "着陆重量超过了着陆重量限制, 超重 : "
                        + Math.abs(zhouluweight- allAcType.getLandWeightLimit()) + " lb");
                return;
            } else {
               //TODO 重心前限和后限

            }
        }
        checkFlightId();
    }

    /**
     * 增加航班信息
     */

    /**
     * @param FlightDate //航班日期
     * @param AircraftReg ////飞机号
     * @param AircraftType //机型
     * @param FlightNo //航班号
     * @param Dep4Code //出发机场四字代码
     * @param DepAirportName //出发机场名
     * @param Arr4Code //到达机场四字代码
     * @param ArrAirportName //到达机场名
     * @param MaxFule //机型最大燃油
     * @param RealFule //实际加油
     * @param SlieFule //滑行油量
     * @param RouteFule //航段耗油
     * @param TofWeight //起飞重量
     * @param LandWeight //落地重量
     * @param AirportLimitWeight //机坪限重
     * @param BalancePicName //计算载重图表名
     */
    private void addFlightInfo() {
        showProgressDialog();
        //TODO 查询  飞行ID
        ApiServiceManager.getInstance().addFlightInfo(UserManager.getInstance().getAddFlightInfo(), new ResponseListner<AddFlightInfoResponse>() {
                @Override public void onResponse(AddFlightInfoResponse response) {
                hiddenDialog();
                if (response != null
                        && response.ResponseObject != null
                        && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                    //ToastUtil.showToast(mContext,
                    //        R.drawable.toast_warning, "航班信息添加成功");
                    Toast.makeText(mContext, "航班信息添加成功", Toast.LENGTH_LONG).show();
                    UserManager.getInstance().setAddFlightSuccess(true);
                    mTopBarRight.setVisibility(View.GONE);
                    //上传机上成员信息
                    ApiServiceManager.getInstance().uploadAirPersonInfo();
                } else {
                    addFlightInfoToDB();
                    ToastUtil.showToast(mContext,
                            R.drawable.toast_warning, !TextUtils.isEmpty(response.ResponseObject.ResponseErr) ? response.ResponseObject.ResponseErr : "服务器繁忙，请稍后再试" );
                }
            }

            @Override public void onEmptyOrError(String message) {
                hiddenDialog();
                addFlightInfoToDB();
                //ToastUtil.showToast(mContext,
                //        R.drawable.toast_warning, message);

            }
        });
    }

    //校验航班主键ID是否存在
    private void checkFlightId() {
        String flightId = UserManager.getInstance().getAddFlightInfo().getFlightId();
        int flightID = 0;
        try {
            flightID = Integer.parseInt(flightId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (flightID == 0) {
            ApiServiceManager.getInstance().getFilghtId(new ResponseListner<String>() {
                @Override public void onResponse(String response) {
                    if (!TextUtils.isEmpty(response)) {
                        try {
                            Integer.parseInt(response);
                            UserManager.getInstance().getAddFlightInfo().setFlightId(response);
                            handler.sendEmptyMessage(ACTION_ADDFILGHT);
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtil.showToast(mContext, R.drawable.toast_warning, response);
                        }
                    } else {
                        hiddenDialog();
                        addFlightInfoToDB();
                    }

                }

                @Override public void onEmptyOrError(String message) {
                    hiddenDialog();
                    addFlightInfoToDB();
                }
            });
        } else {
            addFlightInfo();
        }
    }

    private void addFlightInfoToDB() {
        DBManager.getInstance().insertActionFeed(FeedType.ADD_PLAYINFO,
                UserManager.getInstance().getAddFlightInfo().getFlightId());
        DBManager.getInstance().insertFlightInfo();
        UserManager.getInstance().setAddFlightSuccess(true);
        mTopBarRight.setVisibility(View.GONE);
        ToastUtil.showToast(mContext, R.drawable.toast_confirm, "当前网络不稳定，数据已存储，在网络正常时自动同步数据");
    }
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new CommonProgressDialog(this);
            progressDialog.setTip("正在验证..");
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override public void onCancel(DialogInterface dialog) {
                isCancelAble = true;
            }
        });
        progressDialog.show();
    }

    private void hiddenDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override protected void onStop() {
        super.onStop();
        reqeustCount = 0;
    }
}
