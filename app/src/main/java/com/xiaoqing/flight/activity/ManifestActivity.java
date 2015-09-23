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
import com.xiaoqing.flight.data.dao.AddFlightInfo;
import com.xiaoqing.flight.data.dao.UploadAirPerson;
import com.xiaoqing.flight.data.dao.UploadAirPersonDao;
import com.xiaoqing.flight.entity.AddFlightInfoResponse;
import com.xiaoqing.flight.entity.ValidCaptionResponse;
import com.xiaoqing.flight.network.ResponseListner;
import com.xiaoqing.flight.network.synchronous.FeedType;
import com.xiaoqing.flight.util.ApiServiceManager;
import com.xiaoqing.flight.util.CommonProgressDialog;
import com.xiaoqing.flight.util.Constants;
import com.xiaoqing.flight.util.DBManager;
import com.xiaoqing.flight.util.FormatUtil;
import com.xiaoqing.flight.util.ToastUtil;
import com.xiaoqing.flight.util.UserManager;
import com.xiaoqing.flight.util.WindowUtil;
import java.util.List;
import org.w3c.dom.Text;

/**
 * Created by QingYang on 15/9/20.
 */
public class ManifestActivity extends BaseActivity {

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
    private String aircraftReg;
    private String aircraftType;

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
        return R.layout.activity_mainfest;
    }


    @Override protected void onloadData() {
        getTopBarTitle("电子舱单");
        getTopBarRight("机长确认");

        aircraftType = getIntent().getStringExtra(Constants.ACTION_AIRCRAFTTYPE);
        aircraftReg = getIntent().getStringExtra(Constants.ACTION_AIRCRAFTREG);

        AddFlightInfo addFlightInfo = UserManager.getInstance().getAddFlightInfo();
        if (addFlightInfo != null) {
            if (!TextUtils.isEmpty(addFlightInfo.getOpDate()))
                tv_date.setText(addFlightInfo.getOpDate().substring(0, addFlightInfo.getOpDate().indexOf(".")));
            tv_acreg.setText(addFlightInfo.getAircraftReg());
            tv_flightNo.setText(addFlightInfo.getFlightNo());
            tv_flyAirport.setText(addFlightInfo.getDepAirportName());
            tv_arrAirport.setText(addFlightInfo.getArrAirportName());

            tv_useWeight.setText(addFlightInfo.getNoFuleWeight());
            try {
                tv_useWeightCg.setText(FormatUtil.formatTo2Decimal(Float.parseFloat(addFlightInfo.getWeightCg())));
            } catch (Exception e) {
                e.printStackTrace();
            }


            tv_realOil.setText(addFlightInfo.getRealFule());
            tv_bfOil.setText(addFlightInfo.getBeforeFlyFule());
            tv_bfWeight.setText(addFlightInfo.getTofWeight());
            try {
                tv_bfWeightCg.setText(FormatUtil.formatTo2Decimal(Float.parseFloat(addFlightInfo.getTkoZx())));
                tv_mac.setText(FormatUtil.formatTo2Decimal(Float.parseFloat(addFlightInfo.getTkoMac())));
            } catch (Exception e) {
                e.printStackTrace();
            }
            tv_bfw_min.setText(addFlightInfo.getBeforeWCgmin());
            tv_bfw_max.setText(addFlightInfo.getBeforeWCgmax());

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

               if ("S".equals(person.getSeatType())) {
                   mLayoutPassenter.addView(view);
                   passengerWeight += person.getRealWeight();
               } else if ("C".equalsIgnoreCase(person.getSeatType())) {
                   mLayoutGoods.addView(view);
                   goodsWeight += person.getRealWeight();

               }
           }
        }

        tv_passengerWeight.setText(FormatUtil.formatTo2Decimal(passengerWeight));
        tv_goodsweight.setText(FormatUtil.formatTo2Decimal(goodsWeight));
        UserManager.getInstance().getAddFlightInfo().setPassengerWeight(
                FormatUtil.formatTo2Decimal(passengerWeight));
        UserManager.getInstance().getAddFlightInfo().setArticleWeight(FormatUtil.formatTo2Decimal(goodsWeight));
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
        UserManager.getInstance().getAddFlightInfo().setCaption(userName);
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
                        ToastUtil.showToast(mContext, R.drawable.toast_warning, "当前操作需机长验证，请输入机长信息");
                    } else {
                        //ToastUtil.showToast(mContext,
                        //        R.drawable.toast_warning, "未获取到机长信息");
                        boolean b = vidifyCaptainByDB(userName, password);
                        if (b) {
                            //addFlightInfo();
                            //verifiFlightInfo();
                            checkFlightId();
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
                    //verifiFlightInfo();
                    checkFlightId();
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
                    tv_captain.setText(UserManager.getInstance().getAddFlightInfo().getCaption());
                    //上传机上成员信息
                    ApiServiceManager.getInstance().uploadAirPersonInfo();
                    //DBManager.getInstance().deleteFlightInfo(UserManager.getInstance().getAddFlightInfo().getFlightId());
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
        tv_captain.setText(UserManager.getInstance().getAddFlightInfo().getCaption());
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
