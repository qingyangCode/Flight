package com.xiaoqing.flight.activity;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.InjectView;
import com.xiaoqing.flight.FlightApplication;
import com.xiaoqing.flight.R;
import com.xiaoqing.flight.data.dao.AcWeightLimit;
import com.xiaoqing.flight.data.dao.AcWeightLimitDao;
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
import com.xiaoqing.flight.util.CommonProgressDialog;
import com.xiaoqing.flight.util.Constants;
import com.xiaoqing.flight.util.DateFormatUtil;
import com.xiaoqing.flight.util.FormatUtil;
import com.xiaoqing.flight.util.LogUtil;
import com.xiaoqing.flight.util.MACUtil;
import com.xiaoqing.flight.util.ToastUtil;
import com.xiaoqing.flight.util.UserManager;
import com.xiaoqing.flight.widget.MyView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by QingYang on 15/7/24.
 * 限制图
 */
public class RestrictionMapActivity extends BaseActivity{
    private String TAG = RestrictionMapActivity.class.getSimpleName();



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
    private float flightCg = 0;//起飞重心
    private float mac = 0;//起飞MAC
    private String captainName = "";
    private float upWeight = 0;



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
            //hiddenRightBar();
            finish();
        }
    }

    @Override protected void onloadData() {
        mTopBarTitle.setText("油量信息");
        getTopBarRight("下一步");
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


        //机型信息 最大起飞重量 最大无油重量 最大机坪重量
        AllAcTypeDao allAcTypeDao = FlightApplication.getDaoSession().getAllAcTypeDao();
        allAcTypeList = allAcTypeDao.queryBuilder()
                .where(AllAcTypeDao.Properties.AircraftType.eq(aircraftType))
                .list();
        if (allAcTypeList != null && allAcTypeList.size() > 0) {
            AllAcType allAcType = allAcTypeList.get(0);
            mMaxOil.setText(allAcType.getMaxFule() + "");
            mSlideOil.setText(allAcType.getSlideFule() + "");
            getFuleLimitFromDB();
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
            float passengerWeight = 0;
            float articleWeight = 0;
            if (seatList != null && seatList.size() > 0)
                for(SeatByAcReg seatByAcReg : seatList) {
                    //已有的货物重量 ＋ 座椅上人 或 物品重量
                    gooodsWeights += seatByAcReg.getAcRegCargWeight() + seatByAcReg.getSeatWeight();
                    if ("C".equalsIgnoreCase(seatByAcReg.getSeatType())) {//货物 ＋ 额外物品重量
                        articleWeight = seatByAcReg.getSeatWeight() + seatByAcReg.getAcRegCargWeight();
                        totalPassengerLj = totalPassengerLj + (seatByAcReg.getSeatWeight() + seatByAcReg.getAcRegCargWeight()) * seatByAcReg.getAcTypeLb();
                    } else {
                        totalPassengerLj += seatByAcReg.getAcTypeLb() * seatByAcReg.getSeatWeight();
                        passengerWeight = seatByAcReg.getSeatWeight();
                    }
                }

            UserManager.getInstance().getAddFlightInfo().setPassengerWeight(FormatUtil.formatTo2Decimal(passengerWeight));
            UserManager.getInstance().getAddFlightInfo().setArticleWeight(
                    FormatUtil.formatTo2Decimal(articleWeight));


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

            upWeight =
                    noFuleweight + realOilFloat + gooodsWeights - slideOilFloat;//TODO 加设备重量 ＋ 额外重量  // 基本空重 ＋ 设备
            LogUtil.LOGD(TAG, "noFuleweight : "
                    + noFuleweight
                    + " realOilFloat : "
                    + realOilFloat
                    + " gooodsWeights : "
                    + gooodsWeights
                    + " slideOilFloat : "
                    + slideOilFloat);
            mBeforeWeight.setText(FormatUtil.formatTo2Decimal(upWeight));

            float downWeight =
                    noFuleweight + realOilFloat + gooodsWeights
                            - slideOilFloat  - flyOilFloat;

            mDownWight.setText(FormatUtil.formatTo2Decimal(downWeight));
            allWeight = noFuleweight + realOilFloat + gooodsWeights;

            UserManager.getInstance().getAddFlightInfo().setBeforeFlyFule(FormatUtil.formatTo2Decimal(realOilFloat - slideOilFloat));

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
            //起飞油量力矩
            beforeLj = getOilWeightLj(beforeFlightOil);

            float landOilLj = getOilWeightLj(realOilFloat - slideOilFloat - flyOilFloat);

            try {
                flightCg = (airLj + totalPassengerLj + totalSbLj + beforeLj) / ( - slideOilFloat + allWeight);
            } catch (Exception e) {
                e.printStackTrace();
            }
            float landCg = 0;
            try {
                landCg = (airLj + totalPassengerLj + totalSbLj + landOilLj) / (allWeight - slideOilFloat - flyOilFloat);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //起飞重心前后限
            Pair<Float, Float> beforeFuleLimit = getFuleLimit(-slideOilFloat + allWeight);
            if (beforeFuleLimit != null) {
                UserManager.getInstance().getAddFlightInfo().setBeforeWCgmin(FormatUtil.formatTo2Decimal(beforeFuleLimit.first));
                UserManager.getInstance().getAddFlightInfo().setBeforeWCgmax(FormatUtil.formatTo2Decimal(beforeFuleLimit.second));
            }

            //着陆重心前后限
            Pair<Float, Float> landFuleLimit = getFuleLimit(allWeight - slideOilFloat - flyOilFloat);
            if (landFuleLimit != null) {
                UserManager.getInstance().getAddFlightInfo().setLandWCgmin(FormatUtil.formatTo2Decimal(landFuleLimit.first));
                UserManager.getInstance().getAddFlightInfo().setLandWCgmax(FormatUtil.formatTo2Decimal(landFuleLimit.second));
            }

            //着陆重心
            UserManager.getInstance().getAddFlightInfo().setLandWeightCg(FormatUtil.formatTo2Decimal(landCg));

            mFlightCg.setText(FormatUtil.formatTo2Decimal(flightCg));
            getMAC(flightCg);


        }
    }

    //油量力矩
    private float getOilWeightLj(float OilWeight) {
        float oilLj = 0;
        FuleLimitDao fuleLimitDao = FlightApplication.getDaoSession().getFuleLimitDao();
        List<FuleLimit> fuleLimits = fuleLimitDao.queryBuilder().where(FuleLimitDao.Properties.AcType.eq(aircraftType)).list();
        int temp = 0;

        for (int i = 0; i < fuleLimits.size(); i++) {// 二分法计算输入油量最近的取值
            if (OilWeight == fuleLimits.get(i).getFuleWeight()) {
                oilLj = fuleLimits.get(i).getFuleLj();
                break;
            }
        }

        if (oilLj == 0) {
            int left = 0;
            int right = fuleLimits.size() - 1;
            while (left < right) {
                int middle = (left + right) / 2;
                if (OilWeight < fuleLimits.get(middle).getFuleWeight()) {
                    right = middle - 1;
                } else {
                    left = middle + 1;
                }
                temp = middle;
            }
            if (OilWeight < fuleLimits.get(temp+1).getFuleWeight()) {
                oilLj = fuleLimits.get(temp + 1).getFuleLj()
                        - (fuleLimits.get(temp + 1).getFuleLj() - fuleLimits.get(temp).getFuleLj()) * (fuleLimits.get(temp + 1).getFuleWeight() - OilWeight)
                        / (fuleLimits.get(temp + 1).getFuleWeight() - fuleLimits.get(temp)
                        .getFuleWeight());
            }
        }

        LogUtil.LOGD(TAG, "before fly oil Lj == " + oilLj);
        return oilLj;
    }

    private int reqeustCount = 0;

    private void getMAC(final float flightCg) {
        if (MACUtil.TYPE_CE560.equals(aircraftType)) {
            mac = MACUtil.get560Mac(flightCg);
        } else if (MACUtil.TYPE_CE680.equals(aircraftType)) {
            mac = MACUtil.get680Mac(flightCg);
        } else if (MACUtil.TYPE_CE750.equals(aircraftType)) {
            mac = MACUtil.get750Mac(flightCg);
        } else if (MACUtil.TYPE_G450.equals(aircraftType)) {
            mac = MACUtil.getG450Mac(flightCg);
        }

        if (mac != 0)
            mMac.setText(FormatUtil.formatTo2Decimal(mac));
        else
            mMac.setText(" -- ");
    }

    //重心前后限
    private Pair<Float, Float> getFuleLimit(final float weight) {

        ArrayList<AcWeightLimit> acWeightLimits = new ArrayList<>();

        AcWeightLimitDao acWeightLimitDao =
                FlightApplication.getDaoSession().getAcWeightLimitDao();
        List<AcWeightLimit> list = acWeightLimitDao.queryBuilder()
                .where(AcWeightLimitDao.Properties.AcType.eq(aircraftType))
                .list();

        acWeightLimits.addAll(list);

        Collections.sort(acWeightLimits, new Comparator<AcWeightLimit>() {
            @Override public int compare(AcWeightLimit lhs, AcWeightLimit rhs) {
                return lhs.getWeight() > rhs.getWeight() ? 1 : -1;
            }
        });
        if (acWeightLimits != null && acWeightLimits.size() != 0) {
            for (AcWeightLimit acWeightLimit : acWeightLimits) {
                if (weight == acWeightLimit.getWeight()) {
                    return new Pair<Float, Float>(acWeightLimit.getWeightCg1(), acWeightLimit.getWeightCg2());
                }
            }

            int left = 0;
            int right = acWeightLimits.size() -1;
            int temp = 0;

            while (left < right) {
                int middle = (left + right) / 2;
                if (weight < acWeightLimits.get(temp).getWeight()) {
                    right = middle - 1;
                } else {
                    left = middle + 1;
                }
                temp = middle;
            }

            float weightLimitMin = 0;
            float weightLimitMax = 0;

            if (weight < acWeightLimits.get(temp+1).getWeight()) {
                AcWeightLimit acWLimitMax = acWeightLimits.get(temp + 1);
                AcWeightLimit acWLimitMin = acWeightLimits.get(temp);

                weightLimitMin = acWLimitMax.getWeightCg1() - (acWLimitMax.getWeight() - weight) * (acWLimitMax.getWeightCg1() - acWLimitMin.getWeightCg1()) / (acWLimitMax.getWeight() - acWLimitMin.getWeight());

                weightLimitMax = acWLimitMax.getWeightCg2() - (acWLimitMax.getWeight() - weight) * (acWLimitMax.getWeightCg2() - acWLimitMin.getWeightCg2()) / (acWLimitMax.getWeight() - acWLimitMin.getWeight());

                LogUtil.LOGD(TAG, "weightLimitMin == " + weightLimitMin+ "  weightLimitMax == " + weightLimitMax);
                return new Pair<Float, Float>(weightLimitMin, weightLimitMax);

                //oilLj = fuleLimits.get(temp + 1).getFuleLj()
                //        - (fuleLimits.get(temp + 1).getFuleLj() - fuleLimits.get(temp).getFuleLj()) * (fuleLimits.get(temp + 1).getFuleWeight() - OilWeight)
                //        / (fuleLimits.get(temp + 1).getFuleWeight() - fuleLimits.get(temp)
                //        .getFuleWeight());
            }


        }

        //FuleLimitDao fuleLimitDao = FlightApplication.getDaoSession().getFuleLimitDao();
        //List<FuleLimit> fullLimitList = fuleLimitDao.queryBuilder()
        //        .where(FuleLimitDao.Properties.AcType.eq(aircraftType))
        //        .list();
        //
        //if (fullLimitList != null && fullLimitList.size() != 0) {
        //
        //    for (FuleLimit fuleLimit : fullLimitList) {
        //        if (weight == fuleLimit.getFuleWeight()) {
        //            return new Pair<Float, Float>(fuleLimit.get);
        //        }
        //    }
        //}
        //
        //if ((fullLimitList == null || fullLimitList.size() == 0) && reqeustCount < 1) {
        //    reqeustCount ++;
        //    if (allAcTypeList != null && allAcTypeList.size() > 0) {
        //        AllAcType allAcType = allAcTypeList.get(0);
        //        ApiServiceManager.getInstance().getFuleLimitByAcType(allAcType.getAircraftType(),
        //                allAcType.getPortLimit()+"", mBeforeWeight.getText().toString(),
        //                mDownWight.getText().toString(), allAcType.getMzfw()+"", DateFormatUtil.formatTDate(),
        //                UserManager.getInstance().getUser().getSysVersion()+"", new ResponseListner<FuleLimitResponse>() {
        //
        //                    @Override public void onResponse(FuleLimitResponse response) {
        //                        if (response != null && response.ResponseObject != null && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
        //                            getMAC(weight);
        //                        }
        //                    }
        //
        //                    @Override public void onEmptyOrError(String message) {
        //                        if (!TextUtils.isEmpty(message))
        //                            ToastUtil.showToast(RestrictionMapActivity.this,
        //                                    R.drawable.toast_warning, message);
        //                    }
        //                });
        //
        //    }
        //}
        return null;
    }

    private void showCharView(ArrayList<AcWeightLimit> acWeightLimits) {
        String [] xData = new String[acWeightLimits.size()];
        String [] yData = new String[acWeightLimits.size()];
        String [] datas = new String[acWeightLimits.size() * 2];
        for (int i = 0 ; i < acWeightLimits.size(); i++ ) {
            //String weight = acWeightLimits.get(i).getWeight();
            //float wei = 0;
            //if (!TextUtils.isEmpty(weight))
            //    wei = Float.parseFloat(weight);
            //xData[i] = weight;
            //String Cg1 = acWeightLimits.get(i).getWeightCg1();
            //float weiCg1 = 0;
            //if (!TextUtils.isEmpty(Cg1))
            //    weiCg1 = Float.parseFloat(Cg1);
            yData[i] = (380 + (i * 5))+"";
            //datas[i] = Cg1;
        }

        for (int y = acWeightLimits.size() - 1; y > 0; y--) {
            //String weight = acWeightLimits.get(y).getWeight();
            //float wei = 0;
            //if (!TextUtils.isEmpty(weight))
            //    wei =Float.parseFloat(weight);
            //lineX.add(wei);
            //String weightCg2 = acWeightLimits.get(y).getWeightCg2();
            //datas[y * 2] = weightCg2;
            //float weiCg2 = 0;
            //if (!TextUtils.isEmpty(weightCg2)) {
            //    lineY.add(weiCg2);
            //}
        }
        //mLineCharView.addData(lineData);
        mLineCharView.SetInfo(xData, yData, datas, "限制图");

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
                addFlightInfo.setTofWeight(beforeWeight);//起飞重量
                addFlightInfo.setLandWeight(downWeight);//着陆重量
                addFlightInfo.setAirportLimitWeight(allAcTypeList.get(0).getPortLimit() + "");
                addFlightInfo.setBalancePic(addFlightInfo.getFlightNo());
                addFlightInfo.setBalancePicName(addFlightInfo.getFlightNo());
                addFlightInfo.setTkoZx(String.valueOf(flightCg));
                addFlightInfo.setTkoMac(String.valueOf(mac));
                addFlightInfo.setOpDate(DateFormatUtil.formatZDate());
                addFlightInfo.setAircraftReg(aircraftReg);


                boolean verify = verifiFlightInfo();
                if (verify) {
                    Intent intent = new Intent(RestrictionMapActivity.this, ManifestActivity.class);
                    intent.putExtra(Constants.ACTION_AIRCRAFTTYPE, aircraftType);
                    intent.putExtra(Constants.ACTION_AIRCRAFTREG, aircraftReg);
                    startActivity(intent);
                }
                    //showDialog("机长确认", "请输入机长姓名", "机长密码");


            }
        };
    }





    //验证飞机信息
    private boolean verifiFlightInfo() {
        String realityOil = mRealityouil.getText().toString().trim();
        String slideOil = mSlideOil.getText().toString().trim();
        String flyOil = mFlyOil.getText().toString().trim();
        String beforeWeight = mBeforeWeight.getText().toString().trim();
        String downWeight = mDownWight.getText().toString().trim();
        String macValue = mMac.getText().toString().trim();

        //所有座椅重量
        float totalSeatWeight = 0;
        for (SeatByAcReg seatByAcReg : seatList) {
            if (seatByAcReg.getXPos() != 0 || "C".equalsIgnoreCase(seatByAcReg.getSeatType())) {
                //if (!TextUtils.isEmpty(seatByAcReg.getUserName()) ) {
                totalSeatWeight = totalSeatWeight + seatByAcReg.getSeatWeight() + seatByAcReg.getAcRegCargWeight();
                //}
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
            float zhouluweight = upWeight - flyOilFloat;
            //最大起飞重量
            if (allAcType.getMzfw() -  (nofuWeightFloat + totalSeatWeight) < 0) {
                ToastUtil.showToast(mContext, R.drawable.toast_warning, "无燃油重量超过了最大无燃油重量限制, 超重 : " + Math.abs(
                        (nofuWeightFloat + totalSeatWeight) - allAcType.getMzfw()) +" lb");
                return false;
            } else if (allAcType.getPortLimit() - (nofuWeightFloat + realOilFloat + totalSeatWeight) < 0) {
                ToastUtil.showToast(mContext, R.drawable.toast_warning, "滑行重量超过了机坪重量限制, 超重 : " + Math.abs((nofuWeightFloat + realOilFloat + totalSeatWeight) - allAcType.getPortLimit())+"lb");
                return false;
            } else if(allAcType.getTofWeightLimit() < upWeight) {
                ToastUtil.showToast(mContext, R.drawable.toast_warning, "起飞重量超过了最大起飞重量限制, 超重 : "
                        + Math.abs(upWeight - allAcType.getTofWeightLimit()) + " lb");
                return false;
            } else if (allAcType.getLandWeightLimit() - zhouluweight < 0) {
                ToastUtil.showToast(mContext, R.drawable.toast_warning, "着陆重量超过了着陆重量限制, 超重 : "
                        + Math.abs(zhouluweight- allAcType.getLandWeightLimit()) + " lb");
                return false;
            } else {
               //TODO 重心前限和后限

            }
        }

        return true;

    }


    @Override protected void onStop() {
        super.onStop();
        reqeustCount = 0;
    }
}
