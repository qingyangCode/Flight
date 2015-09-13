package com.xiaoqing.flight.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
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
import com.xiaoqing.flight.data.dao.Passenger;
import com.xiaoqing.flight.data.dao.PassengerDao;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
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
    @InjectView(R.id.mListview) ListView mAutoLoadListView;
    @InjectView(R.id.add_person) Button mAddPerson;

    private ListViewAdapter listViewAdapter;
    private String aircraftReg;
    private String aircraftType;

    private List<Passenger> passengerList;
    private boolean isShowCheckBox = false;
    private PassengerDao passengerDao;
    private boolean isLongClick;

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

        Intent data = getIntent();
        if (data != null) {
            aircraftReg = data.getStringExtra(Constants.ACTION_AIRCRAFTREG);
            aircraftType = data.getStringExtra(Constants.ACTION_AIRCRAFTTYPE);
            Bundle extras = data.getExtras();
        }

        //final UserDao userDao = FlightApplication.getDaoSession().getUserDao();
        //final List<User> list = userDao.queryBuilder().list();
        passengerDao = FlightApplication.getDaoSession().getPassengerDao();
        passengerList = passengerDao.queryBuilder()
                .where(PassengerDao.Properties.AircraftReg.eq(aircraftReg))
                .list();

        if (passengerList != null && passengerList.size() > 0) {
            getTopBarRight("编辑");
        }

        listViewAdapter = new ListViewAdapter();
        mAutoLoadListView.setAdapter(listViewAdapter);
    }


    class ListViewAdapter extends BaseAdapter {

        @Override public int getCount() {
            return passengerList.size();
        }

        @Override public Object getItem(int position) {
            return null;
        }

        @Override public long getItemId(int position) {
            return 0;
        }

        @Override public View getView(final int position, View convertView, ViewGroup parent) {
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

            Passenger passenger = passengerList.get(position);
            if (isShowCheckBox) {
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.checkBox.setChecked(passenger.getIsChecked());
            } else {
                holder.checkBox.setVisibility(View.GONE);
            }

            holder.userName.setText(passenger.getUserName());
            final CheckBox checkBox = holder.checkBox;
            holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override public boolean onLongClick(View v) {
                    isLongClick = true;
                    if (!isShowCheckBox) {
                        getTopBarRight("删除");
                        passengerList.get(position).setIsChecked(true);
                    } else {
                        getTopBarRight("编辑");
                    }
                    isShowCheckBox = !isShowCheckBox;
                    notifyDataSetChanged();
                    return false;
                }
            });

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    passengerList.get(position).setIsChecked(checkBox.isChecked());
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
        tv_password.setInputType(EditorInfo.TYPE_CLASS_NUMBER);

        if (!TextUtils.isEmpty(titleText)) title.setText(titleText);
        final TextView tv_userName = (TextView) view.findViewById(R.id.tv_userName);
        if (!TextUtils.isEmpty(hintText)) tv_userName.setHint(hintText);
        if (!TextUtils.isEmpty(hintText2)) tv_password.setHint(hintText2);
        tv_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                alBuilder.dismiss();
                addPassenger(tv_userName, tv_password, flag);
                return false;
            }
        });




        Button done = (Button) view.findViewById(R.id.done);
        Window window = alBuilder.getWindow();
        alBuilder.show();
        alBuilder.setContentView(view);
        done.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                alBuilder.dismiss();
                addPassenger(tv_userName, tv_password, flag);
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

    private void addPassenger(TextView tv_userName, TextView tv_password, int flag) {
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

        if (flag == ACTION_ADDPERSION) {

            try {
                Passenger passenger = new Passenger();
                passenger.setAircraftReg(aircraftReg);
                passenger.setIsChecked(false);
                passenger.setUserName(userName);
                passenger.setUserWeight(Float.parseFloat(password));
                passengerList.add(passenger);
                passengerDao.insert(passenger);
                listViewAdapter.notifyDataSetChanged();
                if (TextUtils.isEmpty(mTopBarRight.getText())) {
                    getTopBarRight("删除");
                    isShowCheckBox = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //confirm(userName, password);
        }
    }







    @Override public View.OnClickListener getRightOnClickListener() {
        return new View.OnClickListener() {
            @Override public void onClick(View v) {
                //showDialog("机长确认", "请输入机长姓名", "机长密码", ACTION_CONFIRM);
                if (isShowCheckBox) {
                    if("删除".equals(mTopBarRight.getText().toString())) {
                        int deleteCount = 0;
                        for (Passenger passenger : passengerList) {
                            if (passenger.getIsChecked()) {
                                deleteCount ++;
                                break;
                            }
                        }
                        if (deleteCount > 0) {
                            new AlertDialog.Builder(mContext).setMessage("确认删除选中的用户？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialog, int which) {
                                    getTopBarRight("编辑");
                                    isShowCheckBox = false;
                                    deletePassenger();
                                }
                            }).setNegativeButton("取消", null).setCancelable(false).show();
                        } else if (!isLongClick) {
                            ToastUtil.showToast(mContext, R.drawable.toast_warning, "请选择要删除的用户");

                    }
                    }

                } else {
                    getTopBarRight("删除");
                    isShowCheckBox = true;
                    listViewAdapter.notifyDataSetChanged();
                }
                isLongClick = false;
            }
        };
    }


    private void deletePassenger() {
        List<Passenger> passengers = new ArrayList<>();
        passengers.addAll(passengerList);
        for(Passenger passenger : passengers) {
            if (passenger.getIsChecked()) {
                passengerDao.delete(passenger);
                passengerList.remove(passenger);
            }
        }
        listViewAdapter.notifyDataSetChanged();
    }

    @Override protected void onPause() {
        super.onPause();
        isShowCheckBox = false;
        listViewAdapter.notifyDataSetChanged();
    }

    @Override public void onLeftClick() {
        //super.onLeftClick();
        backPress();

    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            backPress();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void backPress() {
        Intent intent = new Intent(mContext, EngineRoomActivity.class);
        intent.putExtra(Constants.ACTION_AIRCRAFTREG, aircraftReg);
        intent.putExtra(Constants.ACTION_AIRCRAFTTYPE, aircraftType);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
    }
}
