package com.xiaoqing.flight.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import com.xiaoqing.flight.data.dao.AcWeightLimitDao;
import com.xiaoqing.flight.data.dao.AddFlightInfo;
import com.xiaoqing.flight.data.dao.AllAcType;
import com.xiaoqing.flight.data.dao.AllAcTypeDao;
import com.xiaoqing.flight.data.dao.UploadAirPerson;
import com.xiaoqing.flight.data.dao.UploadAirPersonDao;
import com.xiaoqing.flight.data.dao.User;
import com.xiaoqing.flight.data.dao.UserDao;
import com.xiaoqing.flight.entity.AddFlightInfoResponse;
import com.xiaoqing.flight.entity.LineCharData;
import com.xiaoqing.flight.entity.ValidCaptionResponse;
import com.xiaoqing.flight.network.ResponseListner;
import com.xiaoqing.flight.network.synchronous.FeedType;
import com.xiaoqing.flight.util.ApiServiceManager;
import com.xiaoqing.flight.util.CommonProgressDialog;
import com.xiaoqing.flight.util.CommonUtils;
import com.xiaoqing.flight.util.Constants;
import com.xiaoqing.flight.util.DBManager;
import com.xiaoqing.flight.util.FormatUtil;
import com.xiaoqing.flight.util.LogUtil;
import com.xiaoqing.flight.util.ToastUtil;
import com.xiaoqing.flight.util.UserManager;
import com.xiaoqing.flight.util.WindowUtil;
import com.xiaoqing.flight.widget.GravityView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by QingYang on 15/9/20.
 */
public class ManifestActivity extends BaseActivity implements GravityView.GetGravity {

    private final int ACTION_ADDFILGHT = 1;

    @InjectView(R.id.tv_date)
    TextView tv_date;
    @InjectView(R.id.tv_captain)
    TextView tv_captain;
    @InjectView(R.id.tv_acreg)
    TextView tv_acreg;
    @InjectView(R.id.tv_flightNo)
    TextView tv_flightNo;
    @InjectView(R.id.tv_flyAirport)
    TextView tv_flyAirport;
    @InjectView(R.id.tv_arrAirport)
    TextView tv_arrAirport;
    @InjectView(R.id.tv_useWeight)
    TextView tv_useWeight;
    @InjectView(R.id.tv_useWeightCg)
    TextView tv_useWeightCg;
    @InjectView(R.id.tv_passengerWeight)
    TextView tv_passengerWeight;
    @InjectView(R.id.tv_goodsweight)
    TextView tv_goodsweight;
    @InjectView(R.id.tv_realOil)
    TextView tv_realOil;
    @InjectView(R.id.tv_bfOil)
    TextView tv_bfOil;
    @InjectView(R.id.tv_bfWeight)
    TextView tv_bfWeight;
    @InjectView(R.id.tv_bfWeightCg)
    TextView tv_bfWeightCg;
    @InjectView(R.id.tv_mac)
    TextView tv_mac;
    @InjectView(R.id.tv_bfw_min)
    TextView tv_bfw_min;
    @InjectView(R.id.tv_bfw_max)
    TextView tv_bfw_max;
    @InjectView(R.id.tv_arrWeight)
    TextView tv_arrWeight;
    @InjectView(R.id.tv_arrWeightCg)
    TextView tv_arrWeightCg;
    @InjectView(R.id.tv_arr_min)
    TextView tv_arr_min;
    @InjectView(R.id.tv_arr_max)
    TextView tv_arr_max;

    @InjectView(R.id.layout_passenger)
    LinearLayout mLayoutPassenter;
    @InjectView(R.id.layout_goods)
    LinearLayout mLayoutGoods;

    @InjectView(R.id.layout_limitview)
    View layout_limitview;
    @InjectView(R.id.layout_g450)
    View layout_g450;
    @InjectView(R.id.layout_beforefly)
    View beforeFly;

    @InjectView(R.id.tv_bfWeight1)
    TextView tv_bfWeight1;
    @InjectView(R.id.tv_landWeight1)
    TextView tv_landWeight1;
    @InjectView(R.id.tv_nofuleWeight)
    TextView tv_nofuleWeight;
    @InjectView(R.id.tv_nofuleWeightCg)
    TextView tv_nofuleWeightCg;
    @InjectView(R.id.tv_nofuleWeightCgtmin)
    TextView tv_nofuleWeightCgtmin;
    @InjectView(R.id.tv_nofuleWeightCgmax)
    TextView tv_nofuleWeightCgmax;

    @InjectView(R.id.layout_person_title)
    View layout_person_title;
    @InjectView(R.id.layout_goods_title)
    View layout_goods_title;

    @InjectView(R.id.tv_operator)
    TextView mOperator;

    @InjectView(R.id.gravity_view)
    GravityView gravityView;
    private String aircraftReg;
    private String aircraftType;

    private CommonProgressDialog progressDialog;

    private LineCharData mLineCharData;

    private AddFlightInfo addFlightInfo;


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
        return R.layout.activity_mainfest;
    }


    @Override protected void onloadData() {
        getTopBarTitle("电子舱单");
        getTopBarRight("机长确认");
        gravityView.setGravityListener(this);
        mLineCharData = new LineCharData();

        aircraftType = getIntent().getStringExtra(Constants.ACTION_AIRCRAFTTYPE);
        aircraftReg = getIntent().getStringExtra(Constants.ACTION_AIRCRAFTREG);

        addFlightInfo = FlightApplication.getAddFlightInfo();
        showCharView();

        if ("G450".equalsIgnoreCase(aircraftType)) {
            layout_limitview.setVisibility(View.GONE);
            layout_g450.setVisibility(View.VISIBLE);
            beforeFly.setVisibility(View.GONE);
        }

        if (addFlightInfo != null) {
            if (!TextUtils.isEmpty(addFlightInfo.getOpDate())) {
                String substring = addFlightInfo.getOpDate()
                        .substring(0, addFlightInfo.getOpDate().indexOf("."));
                if (!TextUtils.isEmpty(substring))
                    tv_date.setText(substring.replace("T","\n"));
            }

            mOperator.setText("操作人："+addFlightInfo.getOpUserName());

            tv_acreg.setText(addFlightInfo.getAircraftReg());
            tv_flightNo.setText(addFlightInfo.getFlightNo());
            tv_flyAirport.setText(addFlightInfo.getDepAirportName());
            tv_arrAirport.setText(addFlightInfo.getArrAirportName());

            tv_useWeight.setText(FormatUtil.formatTo2Decimal(addFlightInfo.getUseWeight()));
            tv_useWeightCg.setText(FormatUtil.formatTo2Decimal(addFlightInfo.getUseWeightCg()));



            tv_realOil.setText(addFlightInfo.getRealFule());
            tv_bfOil.setText(addFlightInfo.getBeforeFlyFule());
            tv_bfWeight.setText(addFlightInfo.getTofWeight());

            tv_bfWeight1.setText(addFlightInfo.getTofWeight());
            tv_landWeight1.setText(addFlightInfo.getLandWeight());
            tv_nofuleWeight.setText(FormatUtil.formatTo2Decimal(addFlightInfo.getNoFuleWeight()));

            try {
                tv_bfWeightCg.setText(FormatUtil.formatTo2Decimal(Float.parseFloat(addFlightInfo.getTkoZx())));
                tv_mac.setText(FormatUtil.formatTo2Decimal(Float.parseFloat(addFlightInfo.getTkoMac())));
                tv_nofuleWeightCg.setText(FormatUtil.formatTo2Decimal(Float.parseFloat(addFlightInfo.getTkoZx())));
            } catch (Exception e) {
                e.printStackTrace();
            }
            tv_bfw_min.setText(addFlightInfo.getBeforeWCgmin());
            tv_bfw_max.setText(addFlightInfo.getBeforeWCgmax());

            tv_nofuleWeightCgtmin.setText(addFlightInfo.getBeforeWCgmin());
            tv_nofuleWeightCgmax.setText(addFlightInfo.getBeforeWCgmax());

            tv_arrWeight.setText(addFlightInfo.getLandWeight());

            tv_arrWeightCg.setText(addFlightInfo.getLandWeightCg());

            tv_arr_min.setText(addFlightInfo.getLandWCgmin());
            tv_arr_max.setText(addFlightInfo.getLandWCgmax());

        }


        if (TextUtils.isEmpty(addFlightInfo.getFlightId())) {
            return;
        }

        UploadAirPersonDao uploadAirPersonDao =
                FlightApplication.getDaoSession().getUploadAirPersonDao();
        List<UploadAirPerson> list = uploadAirPersonDao.queryBuilder()
                .where(UploadAirPersonDao.Properties.FlightId.eq(addFlightInfo.getFlightId()))
                .list();
        if (mLayoutPassenter != null)
            mLayoutPassenter.removeAllViews();
        if (mLayoutGoods != null) mLayoutGoods.removeAllViews();
        float passengerWeight = 0;
        float goodsWeight = 0;
        //float exitgoodsWeight = 0;
        if (list != null && list.size() != 0) {
           for (UploadAirPerson person : list) {
               View view = View.inflate(mContext, R.layout.layout_mainfest_item, null);
               TextView tv_seat = (TextView) view.findViewById(R.id.tv_seat);
               TextView tv_userName = (TextView) view.findViewById(R.id.tv_userName);
               TextView tv_weight = (TextView) view.findViewById(R.id.tv_weight);
               TextView tv_lj = (TextView) view.findViewById(R.id.tv_lj);
               tv_seat.setText(person.getSeatCode());
               if (person.getRealWeight() != 0)
                    tv_userName.setText(person.getPassagerName());
               tv_weight.setText(FormatUtil.formatTo2Decimal(person.getRealWeight()));
               tv_lj.setText(FormatUtil.formatTo2Decimal(person.getAcRegCagLj()));

               if (person.getRealWeight() != 0) {
                   if ("S".equals(person.getSeatType())) {
                       mLayoutPassenter.addView(view);
                       passengerWeight += person.getRealWeight();
                   } else if ("C".equalsIgnoreCase(person.getSeatType())) {
                       mLayoutGoods.addView(view);
                       goodsWeight += person.getRealWeight();
                       //exitgoodsWeight += person.getAcRegCargWeight();
                   }
               }
           }
        } else {
            layout_person_title.setVisibility(View.GONE);
            layout_goods_title.setVisibility(View.GONE);
        }

        tv_passengerWeight.setText(FormatUtil.formatTo2Decimal(passengerWeight));
        tv_goodsweight.setText(FormatUtil.formatTo2Decimal(goodsWeight));
        FlightApplication.getAddFlightInfo().setPassengerWeight(
                FormatUtil.formatTo2Decimal(passengerWeight));
        FlightApplication.getAddFlightInfo().setArticleWeight(
                FormatUtil.formatTo2Decimal(goodsWeight));

    }

    private void showCharView() {
        AcWeightLimitDao acWeightLimitDao = FlightApplication.getDaoSession().getAcWeightLimitDao();
        List<AcWeightLimit> list = acWeightLimitDao.queryBuilder()
                .where(AcWeightLimitDao.Properties.AcType.eq(aircraftType))
                .list();
        ArrayList<LineCharData.WeightLimitData> weightLimitDatas = new ArrayList<>();
        //重心前后限数据
        if (list != null && list.size() != 0) {
            for (AcWeightLimit acWeightLimit : list) {
                LineCharData.WeightLimitData weightLimitData = new LineCharData.WeightLimitData();
                weightLimitData.setWeight(acWeightLimit.getWeight());
                weightLimitData.setWeightCg1(acWeightLimit.getWeightCg1());
                weightLimitData.setWeightCg2(acWeightLimit.getWeightCg2());
                weightLimitDatas.add(weightLimitData);
            }
            Collections.sort(weightLimitDatas, new Comparator<LineCharData.WeightLimitData>() {
                @Override public int compare(LineCharData.WeightLimitData lhs,
                        LineCharData.WeightLimitData rhs) {
                    return lhs.getWeight() > rhs.getWeight() ? 1 : -1;
                }
            });
            mLineCharData.setWeightLimitDatas(weightLimitDatas);
        }

        //重心数据
        if (addFlightInfo != null) {
            ArrayList<LineCharData.WeightData> weightDatas = new ArrayList<>();
            //无燃油重心
            LineCharData.WeightData weightData = new LineCharData.WeightData();
            weightData.setWeight(addFlightInfo.getNoFuleWeight());
            float nofuleWeightCg = 0;
            try {
                nofuleWeightCg = addFlightInfo.getNoFuleLj() / addFlightInfo.getNoFuleWeight();
            } catch (Exception e) {
                e.printStackTrace();
            }
            weightData.setWeightCg(nofuleWeightCg);
            weightDatas.add(weightData);

            //滑行重心
            weightData = new LineCharData.WeightData();
            weightData.setWeight(addFlightInfo.getSlideWeight());
            weightData.setWeightCg(addFlightInfo.getSlideWeightCg());
            weightDatas.add(weightData);

            // 起飞重心
            weightData = new LineCharData.WeightData();
            float beforeWeight = 0;
            float beforeWeightCg = 0;
            try {
                beforeWeight =  Float.parseFloat(addFlightInfo.getTofWeight());
                beforeWeightCg = Float.parseFloat(addFlightInfo.getTkoZx());
            } catch (Exception e) {
                e.printStackTrace();
            }
            weightData.setWeight(beforeWeight);
            weightData.setWeightCg(beforeWeightCg);
            weightDatas.add(weightData);

            //着陆重心
            weightData = new LineCharData.WeightData();
            float landWeight = 0;
            float landWeightCg = 0;
            try {
                landWeight = Float.parseFloat(addFlightInfo.getLandWeight());
                landWeightCg = Float.parseFloat(addFlightInfo.getLandWeightCg());
            } catch (Exception e) {
                e.printStackTrace();
            }

            weightData.setWeight(landWeight);
            weightData.setWeightCg(landWeightCg);
            weightDatas.add(weightData);

            //
            AllAcTypeDao allAcTypeDao = FlightApplication.getDaoSession().getAllAcTypeDao();
            List<AllAcType> acTypeList = allAcTypeDao.queryBuilder()
                    .where(AllAcTypeDao.Properties.AircraftType.eq(aircraftType))
                    .list();

            if (acTypeList != null && acTypeList.size() != 0) {
                mLineCharData.setMaxFlyweight(acTypeList.get(0).getTofWeightLimit());
                mLineCharData.setMaxLandWeight(acTypeList.get(0).getLandWeightLimit());
                mLineCharData.setMaxNofuleWeight(acTypeList.get(0).getMzfw());
            }

            mLineCharData.setWeightDatas(weightDatas);
            mLineCharData.setWeightLimitDatas(weightLimitDatas);
        }

        gravityView.postDelayed(new Runnable() {
            @Override public void run() {
                gravityView.setLcd(mLineCharData);
            }
        }, 100);
    }

    //根据重量获取重心
    @Override
    public float getWeightCg (float weight) {
        float weightCg = 0;
        float oilWeight = 0;
        if (addFlightInfo != null) {
            oilWeight = weight - addFlightInfo.getNoFuleWeight();
            if (oilWeight < 0) oilWeight = 0;
            float oilWeightLj = CommonUtils.getOilWeightLj(oilWeight, aircraftType);
            weightCg = (addFlightInfo.getNoFuleLj() + oilWeightLj)/weight;
        }
        LogUtil.LOGD("weightCg", "weightCg ======= " + weightCg);
        return weightCg;
    }



    @Override public View.OnClickListener getRightOnClickListener() {
        return new View.OnClickListener() {
            @Override public void onClick(View v) {
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
                String name = tv_userName.getText().toString().trim();
                String pasWord = tv_password.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(ManifestActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(pasWord)) {
                    Toast.makeText(ManifestActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                confirm(name, pasWord);
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
    private void confirm( final String userName, final String password) {
        FlightApplication.getAddFlightInfo().setCaption(userName);
        isCancelAble = false;
        showProgressDialog();
        getMoccApi().validCaption(userName, aircraftType, password, new ResponseListner<ValidCaptionResponse>() {
            @Override public void onResponse(ValidCaptionResponse response) {
                hiddenDialog();
                if (isCancelAble) return;
                if (response != null && response.ResponseObject != null) {
                    if (response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                        //addFlightInfo();
                        checkFlightId();
                    } else if ("NO".equalsIgnoreCase(response.ResponseObject.ResponseErr)) {
                        Toast.makeText(ManifestActivity.this, "该用户未授权当前机型，请确认后重新输入", Toast.LENGTH_SHORT).show();
                    } else {
                        //ToastUtil.showToast(mContext,
                        //        R.drawable.toast_warning, "未获取到机长信息");
                        boolean b = vidifyCaptainByDB(userName, password);
                        if (b) {
                            //addFlightInfo();
                            //verifiFlightInfo();
                            checkFlightId();
                        }
                    }
                }
            }

            @Override public void onEmptyOrError(String message) {
                hiddenDialog();
                if (isCancelAble) return;
                if(vidifyCaptainByDB(userName, password)) {
                    //addFlightInfo();
                    //verifiFlightInfo();
                    checkFlightId();
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
                UserDao userDao = FlightApplication.getDaoSession().getUserDao();
                List<User> list1 = userDao.queryBuilder()
                        .where(UserDao.Properties.UserCode.eq(userName),
                                UserDao.Properties.UserPassWord.eq(password))
                        .list();
                if (list1 != null && list1.size() > 0) {
                    return true;
                } else {
                    Toast.makeText(ManifestActivity.this, "用户名或密码不正确", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ManifestActivity.this, "该用户未授权当前机型，请确认后重新输入", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(ManifestActivity.this, "该用户未授权当前机型，请确认后重新输入", Toast.LENGTH_SHORT).show();
        }
        return false;
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
        ApiServiceManager.getInstance().addFlightInfo(FlightApplication.getAddFlightInfo(), new ResponseListner<AddFlightInfoResponse>() {
            @Override public void onResponse(AddFlightInfoResponse response) {
                hiddenDialog();
                if (response != null
                        && response.ResponseObject != null
                        && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                    Toast.makeText(mContext, "航班信息添加成功", Toast.LENGTH_LONG).show();
                    UserManager.getInstance().setAddFlightSuccess(true);
                    mTopBarRight.setVisibility(View.GONE);

                    showCaptionName();

                    //上传机上成员信息
                    ApiServiceManager.getInstance().uploadAirPersonInfo(FlightApplication.getAddFlightInfo().getFlightId());
                } else {
                    addFlightInfoToDB();
                    ToastUtil.showToast(mContext,
                            R.drawable.toast_warning, !TextUtils.isEmpty(response.ResponseObject.ResponseErr) ? response.ResponseObject.ResponseErr : "服务器繁忙，请稍后再试" );
                }
            }



            @Override public void onEmptyOrError(String message) {
                hiddenDialog();
                addFlightInfoToDB();

            }
        });
    }

    private void showCaptionName() {
        UserDao userDao = FlightApplication.getDaoSession().getUserDao();
        List<User> list = userDao.queryBuilder()
                .where(UserDao.Properties.UserCode.eq(
                        FlightApplication.getAddFlightInfo().getCaption()))
                .list();
        if (list != null && list.size() != 0) {
            tv_captain.setText(list.get(0).getUserName());
        } else {
            tv_captain.setText(FlightApplication.getAddFlightInfo().getCaption());
        }
    }

    //校验航班主键ID是否存在
    private void checkFlightId() {
        String flightId = FlightApplication.getAddFlightInfo().getFlightId();
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
                            FlightApplication.getAddFlightInfo().setFlightId(response);
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
                FlightApplication.getAddFlightInfo().getFlightId());
        DBManager.getInstance().insertFlightInfo();
        UserManager.getInstance().setAddFlightSuccess(true);
        mTopBarRight.setVisibility(View.GONE);
        tv_captain.setText(FlightApplication.getAddFlightInfo().getCaption());
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
}
