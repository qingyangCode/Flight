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
import com.xiaoqing.flight.util.CommonUtils;
import com.xiaoqing.flight.util.Constants;
import com.xiaoqing.flight.util.DateFormatUtil;
import com.xiaoqing.flight.util.FormatUtil;
import com.xiaoqing.flight.util.LogUtil;
import com.xiaoqing.flight.util.MACUtil;
import com.xiaoqing.flight.util.ToastUtil;
import com.xiaoqing.flight.util.UserManager;
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
    @InjectView(R.id.tv_flightCg)
    TextView mFlightCg;

    private ArrayList<SeatByAcReg> seatList;
    private String aircraftType;
    private float  airLj = 0;
    List<AllAcType> allAcTypeList;
    private float allWeight;
    private String aircraftReg;
    private Context mContext;
    private CommonProgressDialog progressDialog;
    private float flightCg = 0;//起飞重心
    private float landCg = 0;//着陆中心
    private float useWeightCg = 0;//使用空重重心
    private float mac = 0;//起飞MAC
    private float upWeight = 0;
    //着陆重心前后限
    private Pair<Float, Float> landFuleLimit;
    //起飞中心前后限
    private Pair<Float, Float> beforeFuleLimit;
    private AllAcType mAllAcType;


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
            finish();
        }
    }

    @Override protected void onloadData() {
        mTopBarTitle.setText("油量信息");
        getTopBarRight("下一步");
        aircraftType = getIntent().getStringExtra(Constants.ACTION_AIRCRAFTTYPE);
        aircraftReg = getIntent().getStringExtra(Constants.ACTION_AIRCRAFTREG);
        seatList = (ArrayList<SeatByAcReg>) getIntent().getSerializableExtra(Constants.ACTION_SEATLIST);
        //飞机基本信息
        AllAircraftDao allAircraftDao = FlightApplication.getDaoSession().getAllAircraftDao();
        List<AllAircraft> allAircraftList = allAircraftDao.queryBuilder()
                .where(AllAircraftDao.Properties.AircraftReg.eq(aircraftReg))
                .list();
        if (allAircraftList != null && allAircraftList.size() > 0) {
            airLj = allAircraftList.get(0).getLj();

        }

        updateWeightInfos();

        //机型信息 最大起飞重量 最大无油重量 最大机坪重量
        AllAcTypeDao allAcTypeDao = FlightApplication.getDaoSession().getAllAcTypeDao();
        allAcTypeList = allAcTypeDao.queryBuilder()
                .where(AllAcTypeDao.Properties.AircraftType.eq(aircraftType))
                .list();
        if (allAcTypeList != null && allAcTypeList.size() > 0) {
            mAllAcType = allAcTypeList.get(0);
            mMaxOil.setText(mAllAcType.getMaxFule() + "");
            mSlideOil.setText(mAllAcType.getSlideFule() + "");
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
            float gooodsWeights = 0;
            float totalPassengerLj = 0;
            float passengerWeight = 0;
            float articleWeight = 0;
            float useWeight = 0;
            float useWeightLj = 0;
            if (seatList != null && seatList.size() > 0)
                for(SeatByAcReg seatByAcReg : seatList) {
                    //已有的货物重量 ＋ 座椅上人 或 物品重量
                    gooodsWeights += seatByAcReg.getAcRegCargWeight() + seatByAcReg.getSeatWeight();
                    if ("C".equalsIgnoreCase(seatByAcReg.getSeatType())) {//货物 ＋ 额外物品重量
                        articleWeight = seatByAcReg.getSeatWeight() + seatByAcReg.getAcRegCargWeight();
                        totalPassengerLj = totalPassengerLj + (seatByAcReg.getSeatWeight() + seatByAcReg.getAcRegCargWeight()) * seatByAcReg.getAcTypeLb();
                        useWeight += seatByAcReg.getAcRegCargWeight();
                        useWeightLj += seatByAcReg.getAcTypeLb() * seatByAcReg.getAcRegCargWeight();
                    } else {
                        totalPassengerLj += seatByAcReg.getAcTypeLb() * seatByAcReg.getSeatWeight();
                        passengerWeight = seatByAcReg.getSeatWeight();
                    }
                }

            FlightApplication.getAddFlightInfo().setPassengerWeight(FormatUtil.formatTo2Decimal(passengerWeight));
            FlightApplication.getAddFlightInfo().setArticleWeight(
                    FormatUtil.formatTo2Decimal(articleWeight));


            String realOil = mRealityouil.getText().toString().trim();
            String slideOil = mSlideOil.getText().toString().trim();
            String flyOil = mFlyOil.getText().toString().trim();
            float realOilFloat = 0;
            float slideOilFloat = 0;
            float flyOilFloat = 0;
            String basicWt = FlightApplication.getAddFlightInfo().getBasicWeight();
            float basicWeight = 0;
            try {
                if (!TextUtils.isEmpty(realOil))
                    realOilFloat = Float.parseFloat(realOil);
                if (!TextUtils.isEmpty(slideOil))
                    slideOilFloat = Float.parseFloat(slideOil);
                if (!TextUtils.isEmpty(flyOil))
                    flyOilFloat = Float.parseFloat(flyOil);
                if (!TextUtils.isEmpty(basicWt))
                    basicWeight = Float.parseFloat(basicWt);
            } catch (Exception e) {
                e.printStackTrace();
            }

            upWeight =
                    basicWeight + realOilFloat + gooodsWeights - slideOilFloat;//TODO 加设备重量 ＋ 额外重量  // 基本空重 ＋ 设备
            LogUtil.LOGD(TAG, "basicWeight : "
                    + basicWeight
                    + " realOilFloat : "
                    + realOilFloat
                    + " gooodsWeights : "
                    + gooodsWeights
                    + " slideOilFloat : "
                    + slideOilFloat);
            mBeforeWeight.setText(FormatUtil.formatTo2Decimal(upWeight));

            float downWeight =
                    basicWeight + realOilFloat + gooodsWeights
                            - slideOilFloat  - flyOilFloat;

            mDownWight.setText(FormatUtil.formatTo2Decimal(downWeight));
            allWeight = basicWeight + realOilFloat + gooodsWeights;

            FlightApplication.getAddFlightInfo().setBeforeFlyFule(FormatUtil.formatTo2Decimal(realOilFloat - slideOilFloat));

            //起飞重心： 力矩／重量 （基本空重力矩 ＋ 乘客 + 货物 ＋ 额外重量 ＋ 设备 ＋ 起飞油量）/（实际油量 － 滑行油量 + 基本空中 ＋ 设备重 ＋ 乘客重 ＋ 货物重）// 油量力矩

            //所有设备的lj
            float totalSbLj = 0;
            List<Integer> sbList = FlightApplication.getAddFlightInfo().getSbList();
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

            //起飞重心前后限
            float beforeWeight = 0;
            //优先使用数据结构中的LimitType 字段，理论上不用机型去匹配
            if ((mAllAcType != null && "Y".equalsIgnoreCase(mAllAcType.getLimitType())) || "G450".equalsIgnoreCase(aircraftType)) {
                beforeWeight = allWeight - realOilFloat;
            } else {
                beforeWeight = allWeight - slideOilFloat;
            }

            float landWeight = 0;
            if ((mAllAcType != null && "Y".equalsIgnoreCase(mAllAcType.getLimitType())) || "G450".equalsIgnoreCase(aircraftType)) {
                landWeight = allWeight - realOilFloat;
            } else {
                landWeight = allWeight - slideOilFloat - flyOilFloat;
            }


            float slideWightLj = 0;
            try {
                slideWightLj = CommonUtils.getOilWeightLj(realOilFloat, aircraftType);
            } catch (Exception e) {
                e.printStackTrace();
            }

            float slideWeightCg = 0;
            try {
                slideWeightCg = (airLj + totalPassengerLj + totalSbLj + slideWightLj) / allWeight;
            } catch (Exception e) {
                e.printStackTrace();
            }

            FlightApplication.getAddFlightInfo().setSlideWeight(allWeight);
            FlightApplication.getAddFlightInfo().setSlideWeightCg(slideWeightCg);




            //起飞油量力矩
            float beforeLj = 0;
            //起飞油量
            float beforeFlightOil = 0;
            if ((mAllAcType != null && "N".equalsIgnoreCase(mAllAcType.getLimitType())) || !"G450".equalsIgnoreCase(aircraftType)) {
                beforeFlightOil = realOilFloat - slideOilFloat;
            }
            //起飞油量力矩
            if ((mAllAcType != null && "N".equalsIgnoreCase(mAllAcType.getLimitType())) || !"G450".equalsIgnoreCase(aircraftType)) {
                beforeLj = CommonUtils.getOilWeightLj(beforeFlightOil, aircraftType);
            }

            //着陆油量力矩
            float landOilLj = 0;
            if ((mAllAcType != null && "N".equalsIgnoreCase(mAllAcType.getLimitType())) || !"G450".equalsIgnoreCase(aircraftType)) {
                landOilLj = CommonUtils.getOilWeightLj(realOilFloat - slideOilFloat - flyOilFloat, aircraftType);
            }

            try {
                flightCg = (airLj + totalPassengerLj + totalSbLj + beforeLj) / (beforeWeight);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                landCg = (airLj + totalPassengerLj + totalSbLj + landOilLj) / (landWeight);
            } catch (Exception e) {
                e.printStackTrace();
            }
            float allsbLj = 0;
            try {
                allsbLj = FlightApplication.getAddFlightInfo().getAllSbLj();
            } catch (Exception e) {
                e.printStackTrace();
            }
            useWeightCg = (airLj + useWeightLj + allsbLj) / ( basicWeight + useWeight);

            FlightApplication.getAddFlightInfo().setNoFuleWeight(allWeight - realOilFloat);
            FlightApplication.getAddFlightInfo().setNoFuleLj(airLj + totalPassengerLj + totalSbLj);
            FlightApplication.getAddFlightInfo().setUseWeight(basicWeight + useWeight);
            FlightApplication.getAddFlightInfo().setUseWeightCg(useWeightCg);

            beforeFuleLimit = getFuleLimit(beforeWeight);
            if (beforeFuleLimit != null) {
                FlightApplication.getAddFlightInfo().setBeforeWCgmin(FormatUtil.formatTo2Decimal(beforeFuleLimit.first));
                FlightApplication.getAddFlightInfo().setBeforeWCgmax(FormatUtil.formatTo2Decimal(beforeFuleLimit.second));
            }
            //着陆重心前后限
            landFuleLimit = getFuleLimit(landWeight);
            if (landFuleLimit != null) {
                FlightApplication.getAddFlightInfo().setLandWCgmin(FormatUtil.formatTo2Decimal(landFuleLimit.first));
                FlightApplication.getAddFlightInfo().setLandWCgmax(FormatUtil.formatTo2Decimal(landFuleLimit.second));
            }
            //着陆重心
            FlightApplication.getAddFlightInfo().setLandWeightCg(FormatUtil.formatTo2Decimal(landCg));
            mFlightCg.setText(FormatUtil.formatTo2Decimal(flightCg));
            getMAC(flightCg);
        }
    }




    private void getMAC(final float flightCg) {
        if (MACUtil.TYPE_CE560.equals(aircraftType)) {
            mac = MACUtil.get560Mac(flightCg);
        } else if (MACUtil.TYPE_CE680.equals(aircraftType)) {
            mac = MACUtil.get680Mac(flightCg);
        } else if (MACUtil.TYPE_CE750.equals(aircraftType)) {
            mac = MACUtil.get750Mac(flightCg);
        } else if (MACUtil.TYPE_G450.equals(aircraftType)) {
            mac = MACUtil.getG450Mac(flightCg);
        } else if (MACUtil.TYPE_CE560_PLUS.equals(aircraftType)) {
            mac = MACUtil.get560Mac(flightCg);

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
                if (temp == middle) break;
                if (weight < acWeightLimits.get(middle).getWeight()) {
                    right = middle;
                } else {
                    left = middle;
                }
                temp = middle;
            }

            float weightLimitMin = 0;
            float weightLimitMax = 0;


            int index = 0;
            if (weight < acWeightLimits.get(temp).getWeight()) {
                index = temp;
            } else {
                index = temp + 1;
            }

            try {
                AcWeightLimit acWLimitMax = acWeightLimits.get(index);
                AcWeightLimit acWLimitMin = acWeightLimits.get(index - 1);

                weightLimitMin = acWLimitMax.getWeightCg1()
                        - (acWLimitMax.getWeight() - weight) * (acWLimitMax.getWeightCg1() - acWLimitMin.getWeightCg1()) / (acWLimitMax.getWeight()
                        - acWLimitMin.getWeight());

                weightLimitMax = acWLimitMax.getWeightCg2()
                        - (acWLimitMax.getWeight() - weight) * (acWLimitMax.getWeightCg2() - acWLimitMin.getWeightCg2()) / (acWLimitMax.getWeight()
                        - acWLimitMin.getWeight());

                LogUtil.LOGD(TAG, "weightLimitMin == " + weightLimitMin + "  weightLimitMax == " + weightLimitMax);
                return new Pair<Float, Float>(weightLimitMin, weightLimitMax);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
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

                AddFlightInfo addFlightInfo = FlightApplication.getAddFlightInfo();
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

        AddFlightInfo addFlightInfo = FlightApplication.getAddFlightInfo();
        String basicWeight = addFlightInfo.getBasicWeight();
        float basicWeightFloat = 0;//基本空重 : 飞机空重 ＋ 设备重
        float realOilFloat = 0;
        float flyOilFloat = 0;
        try {
            if (!TextUtils.isEmpty(basicWeight))
                basicWeightFloat = Float.parseFloat(basicWeight);
            if (!TextUtils.isEmpty(realityOil))
                realOilFloat = Float.parseFloat(realityOil);
            if (!TextUtils.isEmpty(flyOil))
                flyOilFloat = Float.parseFloat(flyOil);
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
            if (allAcType.getMzfw() -  (basicWeightFloat + totalSeatWeight) < 0) {
                ToastUtil.showToast(mContext, R.drawable.toast_warning, "无燃油重量超过了最大无燃油重量限制, 超重 : " + Math.abs(
                        (basicWeightFloat + totalSeatWeight) - allAcType.getMzfw()) +" lb");
                return false;
            } else if (allAcType.getPortLimit() - (basicWeightFloat + realOilFloat + totalSeatWeight) < 0) {
                ToastUtil.showToast(mContext, R.drawable.toast_warning, "滑行重量超过了机坪重量限制, 超重 : " + Math.abs((
                        basicWeightFloat
                                + realOilFloat + totalSeatWeight) - allAcType.getPortLimit())+"lb");
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
               // 重心前限和后限
                if (beforeFuleLimit != null && "N".equalsIgnoreCase(allAcType.getLimitType())) {
                    if (flightCg < beforeFuleLimit.first) {
                        ToastUtil.showToast(mContext, R.drawable.toast_warning, "起飞重心小于起飞前限限制 : " + FormatUtil.formatTo2Decimal(
                                Math.abs(beforeFuleLimit.first - flightCg)));
                        return false;
                    } else if (flightCg > beforeFuleLimit.second ) {
                        ToastUtil.showToast(mContext, R.drawable.toast_warning, "起飞重心大于起飞后限限制 : " + FormatUtil.formatTo2Decimal(
                                Math.abs(beforeFuleLimit.second - flightCg)));
                        return false;
                    }
                }

                if (landFuleLimit != null && "N".equalsIgnoreCase(allAcType.getLimitType())) {
                    if (landCg < landFuleLimit.first) {
                        ToastUtil.showToast(mContext, R.drawable.toast_warning, "着陆重心小于着陆前限限制 : " + FormatUtil.formatTo2Decimal(
                                Math.abs(landFuleLimit.first - landCg)));
                        return false;
                    } else if (landCg > landFuleLimit.second) {
                        ToastUtil.showToast(mContext, R.drawable.toast_warning, "着陆重心大于着陆后限限制 : " + FormatUtil.formatTo2Decimal(
                                Math.abs(landFuleLimit.second - landCg)));
                        return false;
                    }
                }
            }
        }

        return true;

    }


    @Override protected void onStop() {
        super.onStop();
    }
}
