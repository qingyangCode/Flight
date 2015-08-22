package com.uandme.flight.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.uandme.flight.adapter.ListViewAdapter;
import com.uandme.flight.FlightApplication;
import com.uandme.flight.R;
import com.uandme.flight.data.dao.AddFlightInfo;
import com.uandme.flight.data.dao.SeatByAcReg;
import com.uandme.flight.data.dao.User;
import com.uandme.flight.data.dao.UserDao;
import com.uandme.flight.entity.AddFlightInfoResponse;
import com.uandme.flight.entity.GrantsByUserCodeResponse;
import com.uandme.flight.network.ResponseListner;
import com.uandme.flight.util.CommonProgressDialog;
import com.uandme.flight.util.Constants;
import com.uandme.flight.util.DateFormatUtil;
import com.uandme.flight.util.ToastUtil;
import com.uandme.flight.util.UserManager;
import com.uandme.flight.util.WindowUtil;
import com.uandme.flight.widget.AutoLoadListView;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Text;

/**
 * Created by QingYang on 15/7/29.
 * 飞机上的人员
 */
public class AircraftPersonnelActivity extends BaseActivity {

    private final int ACTION_ADDPERSION = 1;
    private final int ACTION_CONFIRM = 2;
    //@InjectView(R.id.swipe_refresh)
    //SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.mListview)
    AutoLoadListView mAutoLoadListView;
    @InjectView(R.id.add_person)
    Button mAddPerson;

    private ListViewAdapter listViewAdapter;
    private ArrayList<String> userNames;
    private String aircraftReg;
    private String aircraftType;


    @Override public int getContentView() {
        return R.layout.activity_aircraftpersonnel;
    }

    @Override protected void onResume() {
        super.onResume();
        if (UserManager.getInstance().isAddFilghtSuccess()) {
            finish();
        }
    }

    @Override protected void onloadData() {
        getTopBarTitle("机上人员姓名");
        getTopBarRight("机长确认");

        Intent data = getIntent();
        if (data != null) {
            aircraftReg = data.getStringExtra(Constants.ACTION_AIRCRAFTREG);
            aircraftType = data.getStringExtra(Constants.ACTION_AIRCRAFTTYPE);
        }

        //// 顶部刷新的样式
        //mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light,
        //        android.R.color.holo_green_light, android.R.color.holo_blue_bright,
        //        android.R.color.holo_orange_light);
        //mSwipeRefreshLayout.setRefreshing(false);
        final UserDao userDao = FlightApplication.getDaoSession().getUserDao();
        final List<User> list = userDao.queryBuilder().list();
        userNames = new ArrayList<>();
        for (User user : list) {
            userNames.add(user.getUserName());
        }
        //mSwipeRefreshLayout.setOnRefreshListener(this);
        listViewAdapter = new ListViewAdapter(AircraftPersonnelActivity.this);
        listViewAdapter.setData(userNames);
        mAutoLoadListView.setAdapter(listViewAdapter);

    }

    @OnClick(R.id.add_person)
    public void onAddPersonClick() {
        showDialog("添加机上成员", "请输入姓名", ACTION_ADDPERSION);
    }

    public void showDialog(String titleText, String hintText, final int flag) {

        final AlertDialog alBuilder = new AlertDialog.Builder(this).create();
        View view = LayoutInflater.from(this).inflate(R.layout.prompt_dialog,
                null);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        TextView title = (TextView) view.findViewById(R.id.title);
        if (!TextUtils.isEmpty(titleText))
            title.setText(titleText);
        final TextView tv_userName = (TextView) view.findViewById(R.id.tv_userName);
        if (!TextUtils.isEmpty(hintText))
            tv_userName.setHint(hintText);
        Button done = (Button) view.findViewById(R.id.done);
        //done.setText(buttonText);
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.iv_checkbox);
        Window window = alBuilder.getWindow();
        alBuilder.show();
        alBuilder.setContentView(view);
        done.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                String userName = tv_userName.getText().toString().trim();
                if (TextUtils.isEmpty(userName)) {
                    ToastUtil.showToast(AircraftPersonnelActivity.this, R.drawable.toast_warning, "姓名不能为空！");
                    return;
                }
                alBuilder.dismiss();
                if (flag == ACTION_ADDPERSION) {
                    userNames.add(userName);
                    listViewAdapter.notifyDataSetChanged();
                    try {
                        UserDao userDao = FlightApplication.getDaoSession().getUserDao();
                        User user = new User();
                        user.setUserName(userName);
                        user.setUserCode(userName);
                        userDao.insert(user);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    confirm(userName);
                }
            }
        });
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int) (WindowUtil.initWindow().getScreenWidth(this) / 1.2f); // 设置宽度
        lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;//显示dialog的时候,就显示软键盘
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;//就是这个属性导致不能获取焦点,默认的是FLAG_NOT_FOCUSABLE,故名思义不能获取输入焦点,
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER);
    }

    /**
     * 验证机长信息
     * @param userName
     */

    private boolean isCancelAble;
    private void confirm(String userName) {
        isCancelAble = false;
        final CommonProgressDialog dialog = new CommonProgressDialog(this);
        dialog.setTip("正在验证..");
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override public void onCancel(DialogInterface dialog) {
                isCancelAble = true;
            }
        });
        dialog.show();
        getMoccApi().getGrantsByUserCode(userName, new ResponseListner<GrantsByUserCodeResponse>() {
            @Override public void onResponse(GrantsByUserCodeResponse response) {
                dialog.dismiss();
                if (isCancelAble)
                    return;
                addFlightInfo();
                //if (response != null && response.ResponseObject != null && response.ResponseObject.ResponseData != null && response.ResponseObject.ResponseData.IAppObject != null) {
                //        if ("Y".equals(response.ResponseObject.ResponseData.IAppObject.get(0).Caption)) {
                //            addFlightInfo();
                //            return;
                //        }
                //}
                //ToastUtil.showToast(AircraftPersonnelActivity.this, R.drawable.toast_warning, "当前操作需机长验证，您输入的不是机长");
            }

            @Override public void onEmptyOrError(String message) {
                dialog.dismiss();
                if (isCancelAble)
                    return;
                if (!TextUtils.isEmpty(message))
                    ToastUtil.showToast(AircraftPersonnelActivity.this, R.drawable.toast_warning, message);
            }
        });
    }

    /**
     * 增加航班信息
     */

    /**
     *
     * @param FlightId
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
     * @param NoFuleWeight//无油重量
     * @param AirportLimitWeight //机坪限重
     * @param BalancePic
     * @param BalancePicName //计算载重图表名
     * @param OpUser
     * @param OpDate
     * @param responseListner
     */
    private void addFlightInfo() {
        AddFlightInfo addFlightInfo = UserManager.getInstance().getAddFlightInfo();
        getMoccApi().addFlightInfo(addFlightInfo.getFlightId(),
                    DateFormatUtil.formatZDate(), aircraftReg, aircraftType,
                    addFlightInfo.getFlightNo(), addFlightInfo.getDep4Code(),
                    addFlightInfo.getDepAirportName(), addFlightInfo.getArr4Code(),
                    addFlightInfo.getArrAirportName(), addFlightInfo.getMaxFule(),
                    addFlightInfo.getRealFule(), addFlightInfo.getSlieFule(),
                    addFlightInfo.getRouteFule(), addFlightInfo.getTofWeight(),
                    addFlightInfo.getLandWeight(),
                    addFlightInfo.getNoFuleWeight(), addFlightInfo.getAirportLimitWeight(),
                    addFlightInfo.getBalancePic(), addFlightInfo.getBalancePicName(),
                UserManager.getInstance().getUser().getUserCode(), DateFormatUtil.formatZDate(),
                new ResponseListner<AddFlightInfoResponse>() {

                    @Override public void onResponse(AddFlightInfoResponse response) {
                        if (response != null && response.ResponseObject != null && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                            UserManager.getInstance().setAddFlightSuccess(true);
                            finish();
                        } else {
                            ToastUtil.showToast(AircraftPersonnelActivity.this, R.drawable.toast_warning, "服务器繁忙，请稍后再试！");
                        }
                    }

                    @Override public void onEmptyOrError(String message) {
                        ToastUtil.showToast(AircraftPersonnelActivity.this, R.drawable.toast_warning, message);
                    }
                });

    }



    @Override public View.OnClickListener getRightOnClickListener() {
        return new View.OnClickListener() {
            @Override public void onClick(View v) {
                showDialog("机长确认", "请输入机长姓名", ACTION_CONFIRM);
            }
        };
    }
}
