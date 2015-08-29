package com.xiaoqing.flight.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.xiaoqing.flight.FlightApplication;
import com.xiaoqing.flight.R;
import com.xiaoqing.flight.data.dao.AddFlightInfo;
import com.xiaoqing.flight.data.dao.User;
import com.xiaoqing.flight.data.dao.UserDao;
import com.xiaoqing.flight.entity.AddFlightInfoResponse;
import com.xiaoqing.flight.entity.ValidCaptionResponse;
import com.xiaoqing.flight.network.ResponseListner;
import com.xiaoqing.flight.util.CommonProgressDialog;
import com.xiaoqing.flight.util.Constants;
import com.xiaoqing.flight.util.DateFormatUtil;
import com.xiaoqing.flight.util.ToastUtil;
import com.xiaoqing.flight.util.UserManager;
import com.xiaoqing.flight.util.WindowUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by QingYang on 15/7/29.
 * 飞机上的人员
 */
public class AircraftPersonnelActivity extends BaseActivity {

    private final int ACTION_ADDPERSION = 1;
    private final int ACTION_CONFIRM = 2;
    //@InjectView(R.id.swipe_refresh)
    //SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.mListview) ListView mAutoLoadListView;
    @InjectView(R.id.add_person) Button mAddPerson;

    private ListViewAdapter listViewAdapter;
    private ArrayList<String> userNames;
    private String aircraftReg;
    private String aircraftType;
    private CommonProgressDialog progressDialog;

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
        //getTopBarRight("机长确认");

        Intent data = getIntent();
        if (data != null) {
            aircraftReg = data.getStringExtra(Constants.ACTION_AIRCRAFTREG);
            aircraftType = data.getStringExtra(Constants.ACTION_AIRCRAFTTYPE);
        }

        final UserDao userDao = FlightApplication.getDaoSession().getUserDao();
        final List<User> list = userDao.queryBuilder().list();
        userNames = new ArrayList<>();
        for (User user : list) {
            userNames.add(user.getUserName());
        }
        listViewAdapter = new ListViewAdapter();
        mAutoLoadListView.setAdapter(listViewAdapter);
    }


    class ListViewAdapter extends BaseAdapter {

        @Override public int getCount() {
            return userNames.size();
        }

        @Override public Object getItem(int position) {
            return null;
        }

        @Override public long getItemId(int position) {
            return 0;
        }

        @Override public View getView(int position, View convertView, ViewGroup parent) {
            ViewHoler holder = null;
            if (convertView == null) {
                holder = new ViewHoler();
                convertView = View.inflate(AircraftPersonnelActivity.this, R.layout.item_addperson,null);
                holder.layout = (LinearLayout) convertView.findViewById(R.id.ll);
                holder.userName = (TextView) convertView.findViewById(R.id.tv_userName);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
                convertView.setTag(holder);
            } else {
                holder = (ViewHoler) convertView.getTag();
            }
            holder.userName.setText(userNames.get(position));
            final CheckBox checkBox = holder.checkBox;
            holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override public boolean onLongClick(View v) {
                    checkBox.setChecked(true);
                    notifyDataSetChanged();
                    return false;
                }
            });
            return convertView;
        }
    }

    class ViewHoler {
        LinearLayout layout;
        TextView userName;
        CheckBox checkBox;
    }

    @OnClick(R.id.add_person) public void onAddPersonClick() {
        showDialog("添加机上成员", "请输入姓名", "请输入用户体重", ACTION_ADDPERSION);
    }

    public void showDialog(String titleText, String hintText, final String hintText2, final int flag) {

        final AlertDialog alBuilder = new AlertDialog.Builder(this).create();
        View view = LayoutInflater.from(this).inflate(R.layout.prompt_dialog, null);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        TextView title = (TextView) view.findViewById(R.id.title);
        final TextView tv_password = (TextView) view.findViewById(R.id.tv_password);

        if (!TextUtils.isEmpty(titleText)) title.setText(titleText);
        final TextView tv_userName = (TextView) view.findViewById(R.id.tv_userName);
        if (!TextUtils.isEmpty(hintText)) tv_userName.setHint(hintText);




        Button done = (Button) view.findViewById(R.id.done);
        Window window = alBuilder.getWindow();
        alBuilder.show();
        alBuilder.setContentView(view);
        done.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                String userName = tv_userName.getText().toString().trim();
                String password = tv_password.getText().toString().trim();
                if (TextUtils.isEmpty(userName)) {
                    ToastUtil.showToast(AircraftPersonnelActivity.this, R.drawable.toast_warning,
                            "姓名不能为空！");
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    String text = "";
                    if(flag == ACTION_ADDPERSION) {
                        text = "用户体重不能为空";
                    } else {
                        text = "机长密码不能为空";
                    }
                    ToastUtil.showToast(AircraftPersonnelActivity.this, R.drawable.toast_warning, text);
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
                    confirm(userName, password);
                }
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

    private void confirm(String userName, String password) {
        isCancelAble = false;
        showProgressDialog();
        getMoccApi().validCaption(userName, aircraftType, password, new ResponseListner<ValidCaptionResponse>() {
            @Override public void onResponse(ValidCaptionResponse response) {
                hiddenDialog();
                if (isCancelAble) return;
                if (response != null && response.ResponseObject != null) {
                    if (response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                        addFlightInfo();
                    } else if ("NO".equalsIgnoreCase(response.ResponseObject.ResponseErr)) {
                        ToastUtil.showToast(AircraftPersonnelActivity.this,
                                R.drawable.toast_warning, "当前操作需机长验证，请输入机长信息");
                    } else {
                        ToastUtil.showToast(AircraftPersonnelActivity.this,
                                R.drawable.toast_warning, "未获取到机长信息");
                    }
                }
            }

            @Override public void onEmptyOrError(String message) {
                hiddenDialog();
                if (isCancelAble) return;
                ToastUtil.showToast(AircraftPersonnelActivity.this, R.drawable.toast_warning,
                        getString(R.string.get_data_error));
            }
        });
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
        AddFlightInfo addFlightInfo = UserManager.getInstance().getAddFlightInfo();
        getMoccApi().addFlightInfo(addFlightInfo.getFlightId(), DateFormatUtil.formatZDate(),
                aircraftReg, aircraftType, addFlightInfo.getFlightNo(), addFlightInfo.getDep4Code(),
                addFlightInfo.getDepAirportName(), addFlightInfo.getArr4Code(),
                addFlightInfo.getArrAirportName(), addFlightInfo.getMaxFule(),
                addFlightInfo.getRealFule(), addFlightInfo.getSlieFule(),
                addFlightInfo.getRouteFule(), addFlightInfo.getTofWeight(),
                addFlightInfo.getLandWeight(), addFlightInfo.getNoFuleWeight(),
                addFlightInfo.getAirportLimitWeight(), addFlightInfo.getBalancePic(),
                addFlightInfo.getBalancePicName(),
                UserManager.getInstance().getUser().getUserCode(), DateFormatUtil.formatZDate(),
                new ResponseListner<AddFlightInfoResponse>() {

                    @Override public void onResponse(AddFlightInfoResponse response) {
                        hiddenDialog();
                        if (response != null
                                && response.ResponseObject != null
                                && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                            ToastUtil.showToast(AircraftPersonnelActivity.this,
                                    R.drawable.toast_warning, "航班信息添加成功");
                            UserManager.getInstance().setAddFlightSuccess(true);
                            finish();
                        } else {
                            ToastUtil.showToast(AircraftPersonnelActivity.this,
                                    R.drawable.toast_warning, "服务器繁忙，请稍后再试！");
                        }
                    }

                    @Override public void onEmptyOrError(String message) {
                        hiddenDialog();
                        ToastUtil.showToast(AircraftPersonnelActivity.this,
                                R.drawable.toast_warning, message);
                    }
                });
    }

    @Override public View.OnClickListener getRightOnClickListener() {
        return new View.OnClickListener() {
            @Override public void onClick(View v) {
                showDialog("机长确认", "请输入机长姓名", "机长密码", ACTION_CONFIRM);
            }
        };
    }
}
