package com.xiaoqing.flight.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.xiaoqing.flight.FlightApplication;
import com.xiaoqing.flight.R;
import com.xiaoqing.flight.data.dao.SeatByAcReg;
import com.xiaoqing.flight.data.dao.SeatByAcRegDao;
import com.xiaoqing.flight.data.dao.User;
import com.xiaoqing.flight.data.dao.UserDao;
import com.xiaoqing.flight.entity.EngineRoom;
import com.xiaoqing.flight.entity.GrantsByUserCodeResponse;
import com.xiaoqing.flight.entity.SeatByAcRegResponse;
import com.xiaoqing.flight.network.ResponseListner;
import com.xiaoqing.flight.util.ApiServiceManager;
import com.xiaoqing.flight.util.CommonProgressDialog;
import com.xiaoqing.flight.util.CommonUtils;
import com.xiaoqing.flight.util.Constants;
import com.xiaoqing.flight.util.DateFormatUtil;
import com.xiaoqing.flight.util.LogUtil;
import com.xiaoqing.flight.util.ToastUtil;
import com.xiaoqing.flight.util.UserManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by QingYang on 15/7/23.
 */
public class EngineRoomActivity extends BaseActivity{
    private String TAG = EngineRoom.class.getSimpleName();

    //EditText mSeat;
    //EditText mPassengerName;
    //TextView mArm;
    //EditText mWeight;
    //LinearLayout mLinearLayout;
    View inflate;
    //@InjectView(R.id.mListView)
    //ListView mListView;
    @InjectView(R.id.layout_seat)
    LinearLayout mLinearLayout;
    @InjectView(R.id.tv_seat)
    EditText mSeat;
    @InjectView(R.id.tv_passengerName)
    TextView mPassengerName;
    @InjectView(R.id.tv_Arm)
    TextView mArm;
    @InjectView(R.id.tv_weight)
    EditText mWeight;
    @InjectView(R.id.mLinearLayout)
    LinearLayout mSeatLinearLayout;



    private String aircraftReg;
    private String aircraftType;
    private ArrayList<SeatByAcReg> seatList;
    private float lastPosition = 0;
    private View view = null;
    private ImageView upView = null;
    private ImageView downView = null;
    private ImageView centerView = null;
    private boolean isUpSelected;
    private boolean isDownSelected;
    View layout_top;
    View layout_center;
    View layout_bottom;
    TextView topTitle;
    TextView centerTitle;
    TextView bottomTitle;
    private List<SeatByAcReg> showList;

    @Override protected void onResume() {
        super.onResume();
        if (UserManager.getInstance().isAddFilghtSuccess())
            finish();
    }

    @Override public int getContentView() {
        return R.layout.activity_engineroom;
    }

    @Override protected void onloadData() {
        Intent data = getIntent();
        seatList = new ArrayList<>();
        if (data != null) {
            aircraftReg = data.getStringExtra(Constants.ACTION_AIRCRAFTREG);
            aircraftType = data.getStringExtra(Constants.ACTION_AIRCRAFTTYPE);
        }



        mTopBarTitle.setText("机舱信息");
        mTopBarRight.setText("下一步");

        //inflate = View.inflate(this, R.layout.header_engineroom, null);
        //mLinearLayout = (LinearLayout) inflate.findViewById(R.id.layout_seat);
        //mSeat = (EditText) inflate.findViewById(R.id.tv_seat);
        //mPassengerName = (EditText) inflate.findViewById(R.id.tv_passengerName);
        //mArm = (TextView) inflate.findViewById(R.id.tv_Arm);
        //mWeight = (EditText) inflate.findViewById(R.id.tv_weight);

        mSeat.setEnabled(false);
        mSeat.setText("座位");
        mPassengerName.setEnabled(false);
        mPassengerName.setBackgroundResource(0);
        mPassengerName.setGravity(Gravity.CENTER);
        mPassengerName.setText("姓名");
        mArm.setEnabled(false);
        mArm.setText("力臂 (in.)");
        mWeight.setEnabled(false);
        mWeight.setBackgroundResource(0);
        mWeight.setText("重量 (lb.)");
        //mListView.addHeaderView(inflate);
        getSeatByDB();
        if (seatList == null || seatList.size() == 0) {
            if (CommonUtils.isNetworkConnected(this))
                getSeatByNet();
        }
    }

    @OnClick(R.id.add_person)
    public void onAddPersonClick() {
        startActivity(new Intent(this, AircraftPersonnelActivity.class));
    }

    private void addSeatView() {
        for(SeatByAcReg seatByAcReg : seatList) {
            if (seatByAcReg.getXPos() != 0) {
                isUpSelected = true;
                if (((seatByAcReg.getXPos() != lastPosition + 0.01) && (seatByAcReg.getXPos() != lastPosition + 0.02) && (seatByAcReg.getXPos() != lastPosition + 0.03)
                        && (seatByAcReg.getXPos() != lastPosition - 0.01) && (seatByAcReg.getXPos() != lastPosition - 0.02) && (seatByAcReg.getXPos() != lastPosition - 0.03) && lastPosition != seatByAcReg.getXPos())  || lastPosition == 0 ) {
                    view = View.inflate(this, R.layout.item_seat, null);
                    upView = (ImageView) view.findViewById(R.id.iv_up);
                    downView = (ImageView) view.findViewById(R.id.iv_down);
                    centerView = (ImageView) view.findViewById(R.id.iv_center);
                    layout_top = view.findViewById(R.id.layout_top);
                    layout_center = view.findViewById(R.id.layout_center);
                    layout_bottom = view.findViewById(R.id.layout_bottom);
                    topTitle = (TextView) view.findViewById(R.id.tv_topid);
                    centerTitle = (TextView) view.findViewById(R.id.tv_centerid);
                    bottomTitle = (TextView) view.findViewById(R.id.tv_bottomid);
                    mLinearLayout.addView(view);
                }
                if (view == null || upView == null || downView == null)
                    continue;
                    if ("LEFT".equals(seatByAcReg.getDirection())) {
                        if (seatByAcReg.getYPos() < 0.35) {
                            topTitle.setText(seatByAcReg.getSeatCode());
                            upView.setVisibility(View.VISIBLE);
                            upView.setBackgroundResource(R.drawable.seat_left_selector);
                        } else if (seatByAcReg.getYPos() >= 0.35 && seatByAcReg.getYPos() < 0.5) {
                            centerTitle.setText(seatByAcReg.getSeatCode());
                            layout_top.setVisibility(View.GONE);
                            layout_bottom.setVisibility(View.GONE);
                            centerView.setVisibility(View.VISIBLE);
                            layout_center.setVisibility(View.VISIBLE);
                            centerView.setBackgroundResource(R.drawable.seat_left_selector);
                        } else {
                            bottomTitle.setText(seatByAcReg.getSeatCode());
                            downView.setVisibility(View.VISIBLE);
                            downView.setBackgroundResource(R.drawable.seat_left_selector);
                        }
                    } else if ("UP".equals(seatByAcReg.getDirection())) {
                        if (seatByAcReg.getYPos() < 0.35) {
                            topTitle.setText(seatByAcReg.getSeatCode());
                            upView.setVisibility(View.VISIBLE);
                            upView.setBackgroundResource(R.drawable.seat_up_selector);
                        } else if (seatByAcReg.getYPos() >= 0.35 && seatByAcReg.getYPos() < 0.5) {
                            centerTitle.setText(seatByAcReg.getSeatCode());
                            layout_top.setVisibility(View.GONE);
                            layout_bottom.setVisibility(View.GONE);
                            layout_center.setVisibility(View.VISIBLE);
                            centerView.setBackgroundResource(R.drawable.seat_up_selector);
                        } else {
                            bottomTitle.setText(seatByAcReg.getSeatCode());
                            downView.setVisibility(View.VISIBLE);
                            downView.setBackgroundResource(R.drawable.seat_up_selector);
                        }
                    } else if ("DOWN".equals(seatByAcReg.getDirection())) {
                        if (seatByAcReg.getYPos() < 0.35) {
                            topTitle.setText(seatByAcReg.getSeatCode());
                            upView.setVisibility(View.VISIBLE);
                            upView.setBackgroundResource(R.drawable.seat_down_selector);
                        } else if (seatByAcReg.getYPos() >= 0.35 && seatByAcReg.getYPos() < 0.5) {
                            centerTitle.setText(seatByAcReg.getSeatCode());
                            layout_top.setVisibility(View.GONE);
                            layout_bottom.setVisibility(View.GONE);
                            layout_center.setVisibility(View.VISIBLE);
                            centerView.setBackgroundResource(R.drawable.seat_down_selector);
                        } else {
                            bottomTitle.setText(seatByAcReg.getSeatCode());
                            downView.setVisibility(View.VISIBLE);
                            downView.setBackgroundResource(R.drawable.seat_down_selector);
                        }
                    } else if ("RIGHT".equals(seatByAcReg.getDirection())) {
                        if (seatByAcReg.getYPos() < 0.35) {
                            topTitle.setText(seatByAcReg.getSeatCode());
                            upView.setVisibility(View.VISIBLE);
                            upView.setBackgroundResource(R.drawable.seat_right_selector);
                        } else if (seatByAcReg.getYPos() >= 0.35 && seatByAcReg.getYPos() < 0.5) {
                            centerTitle.setText(seatByAcReg.getSeatCode());
                            layout_top.setVisibility(View.GONE);
                            layout_bottom.setVisibility(View.GONE);
                            layout_center.setVisibility(View.VISIBLE);
                            centerView.setBackgroundResource(R.drawable.seat_right_selector);
                        } else {
                            bottomTitle.setText(seatByAcReg.getSeatCode());
                            downView.setVisibility(View.VISIBLE);
                            downView.setBackgroundResource(R.drawable.seat_right_selector);
                        }
                    }
                lastPosition = seatByAcReg.getXPos();
                upView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        if (isUpSelected) {
                            upView.setSelected(true);
                        } else {
                            upView.setSelected(false);
                        }
                        isUpSelected = !isUpSelected;
                    }
                });
                downView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        if (isDownSelected) {
                            downView.setSelected(true);
                        } else {
                            downView.setSelected(false);
                        }
                        isDownSelected = !isDownSelected;
                    }
                });

            }
        }

    }

    private void getSeatByDB() {
        SeatByAcRegDao seatByAcRegDao = FlightApplication.getDaoSession().getSeatByAcRegDao();
        List<SeatByAcReg> lists = seatByAcRegDao.queryBuilder()
                .where(SeatByAcRegDao.Properties.AcReg.eq(aircraftReg))
                .list();
        seatList.addAll(lists);

        Collections.sort(seatList, new Comparator<SeatByAcReg>() {

            @Override public int compare(SeatByAcReg lhs, SeatByAcReg rhs) {
                return lhs.getXPos() > rhs.getXPos() ? 1 : -1;
            }
        });

        addSeatView();

         showList = new ArrayList<>();
        //Collections.copy(showList, lists);

        Collections.sort(lists, new Comparator<SeatByAcReg>() {
            @Override public int compare(SeatByAcReg lhs, SeatByAcReg rhs) {
                return lhs.getSeatId() > rhs.getSeatId() ? 1 : -1;
            }
        });

        showList.addAll(lists);

        for ( int index = 0; index < showList.size() ; index ++) {
            final SeatByAcReg seatByAcReg = showList.get(index);
            LogUtil.LOGD(TAG, "排序后结果： " + seatByAcReg.getXPos() + "   :  " + seatByAcReg.getYPos() +  " ==  " + seatByAcReg.toString());
            final int position = index;
            if (seatByAcReg.getXPos() != 0) {
                View convertView = View.inflate(EngineRoomActivity.this, R.layout.item_seatlayout, null);
                TextView tv_seat = (TextView) convertView.findViewById(R.id.tv_seat);
                AutoCompleteTextView et_passenger = (AutoCompleteTextView) convertView.findViewById(R.id.tv_passengerName);
                final UserDao userDao = FlightApplication.getDaoSession().getUserDao();
                final List<User> list = userDao.queryBuilder().list();
                String [] names = new String[list.size()];
                for(int i = 0; i < list.size(); i++) {
                    names[i] = list.get(i).getUserName();
                }
                et_passenger.setAdapter(new ArrayAdapter<String>(EngineRoomActivity.this, R.layout.list_item, names));
                TextView tv_arm = (TextView) convertView.findViewById(R.id.tv_Arm);
                final TextView et_weight = (EditText) convertView.findViewById(R.id.tv_weight);


                tv_seat.setText(seatByAcReg.getSeatCode());
                if ("C".equalsIgnoreCase(seatByAcReg.getSeatType())) {
                    et_passenger.setEnabled(false);
                    et_passenger.setText("货物");
                    et_passenger.setBackgroundResource(0);
                } else {
                    et_passenger.setText("");
                    et_passenger.setBackgroundResource(R.drawable.engineeroom_name_bg);
                }
                tv_arm.setText(seatByAcReg.getAcTypeLb()+"");
                et_weight.setText("0");
                if (180 > seatByAcReg.getAcTypeSeatLimit()) {
                    et_weight.setBackgroundColor(getResources().getColor(R.color.red));
                } else {
                    et_weight.setBackgroundResource(R.drawable.engineeroom_name_bg);
                }

                LogUtil.LOGD(TAG, "position ===== " + position);
                et_passenger.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override public void afterTextChanged(Editable s) {
                        if (!TextUtils.isEmpty(s.toString()))
                            showList.get(position).setUserName(s.toString());
                    }
                });
                et_weight.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override public void afterTextChanged(Editable s) {
                        if (!TextUtils.isEmpty(s.toString())) {
                            float weight = 0;
                            try {
                                weight = Float.parseFloat(s.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (weight > seatByAcReg.getAcTypeSeatLimit()) {
                                et_weight.setBackgroundColor(getResources().getColor(R.color.red));
                            } else {
                                et_weight.setBackgroundResource(R.drawable.engineeroom_name_bg);
                            }
                            showList.get(position).setSeatWeight(weight);
                        }
                    }
                });
                mSeatLinearLayout.addView(convertView);
            }
            showList.get(position).setSeatWeight(180f);

        }
    }

    private CommonProgressDialog dialog;
    private void getSeatByNet() {
        if (seatList == null) {
            dialog = new CommonProgressDialog(this);
            dialog.setTip("加载中 ..");
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        ApiServiceManager.getInstance().getSeatInfo(aircraftReg, new ResponseListner<SeatByAcRegResponse>() {
            @Override public void onResponse(SeatByAcRegResponse response) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                getSeatByDB();
            }

            @Override public void onEmptyOrError(String message) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    ToastUtil.showToast(EngineRoomActivity.this, R.drawable.toast_warning, message);
                }
            }
        });
    }


    class ViewHolder {
        TextView tv_seat;
        AutoCompleteTextView et_passenger;
        TextView tv_arm;
        EditText et_weight;
    }

    private void handleWeight(final ViewHolder holder, final SeatByAcReg value) {
        holder.et_weight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    holder.et_weight.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override public void afterTextChanged(Editable s) {
                            if (!TextUtils.isEmpty(s)) {
                                float weight = Float.parseFloat(s.toString());
                                if (weight > value.getAcTypeSeatLimit()) {
                                    holder.et_weight.setBackgroundColor(getResources().getColor(R.color.red));
                                } else {
                                    holder.et_weight.setBackgroundResource(R.drawable.engineeroom_name_bg);
                                }
                            }
                        }
                    });
                }
            }
        });

    }

    @Override public View.OnClickListener getRightOnClickListener() {
        return new View.OnClickListener() {

            @Override public void onClick(View v) {

                for(SeatByAcReg seatByAcReg : seatList) {
                    if (TextUtils.isEmpty(seatByAcReg.getUserName()) && ("机长".equals(seatByAcReg.getSeatCode()) || "副驾驶".equals(seatByAcReg.getSeatCode()))) {
                        ToastUtil.showToast(EngineRoomActivity.this, R.drawable.toast_warning, "座位 " + seatByAcReg.getSeatCode() + "不能为空 ！");
                        return;
                    } else if (seatByAcReg.getXPos() != 0) {
                        if (seatByAcReg.getAcTypeSeatLimit() - seatByAcReg.getAcRegCargWeight()  - seatByAcReg.getSeatWeight() < 0) {
                            ToastUtil.showToast(EngineRoomActivity.this, R.drawable.toast_warning, "第" + seatByAcReg.getSeatCode() + "座椅超过最后所允许的重量！");
                            return;
                        }
                    }
                }

                /**
                 * 请求增加机上人员信息
                 * @param AircraftReg //机号
                 * @param SeatId //座椅编号
                 * @param FlightId  //航班编号
                 * @param SeatCode  //座椅代码
                 * @param SeatType //座椅类型 S座椅 C货物
                 * @param AcTypeSeatLimit  //机型座椅限重
                 * @param AcTypeLj  //机型座椅力臂
                 * @param AcRegCagrWeight  //飞机额外物品重量
                 * @param AcRegCagLj 座椅力臂
                 * @param SeatLastLimit //机型限重减去飞机额外物品后的最大重量限制
                 * @param PassagerName //乘客名称
                 * @param RealWeight //乘客/货 实际重量
                 * @param OpUser
                 * @param OpDate
                 * @param responseListner
                 */

                final ArrayList<Integer> failSeatInfo  = new ArrayList<Integer>();
                for (final SeatByAcReg seatByAcReg : showList) {
                    if (seatByAcReg.getXPos() == 0) {
                        continue;
                    }
                    if (UserManager.getInstance().getUser() == null)
                        return;
                    getMoccApi().addFlightCd(aircraftReg, seatByAcReg.getSeatId() + "",
                            UserManager.getInstance().getAddFlightInfo().getFlightId(),
                            seatByAcReg.getSeatCode(), seatByAcReg.getSeatType(),
                            seatByAcReg.getAcTypeSeatLimit() + "", seatByAcReg.getAcTypeLb()+"",
                            seatByAcReg.getAcRegCargWeight()+"", seatByAcReg.getAcTypeLb()+"",
                            (seatByAcReg.getAcTypeSeatLimit() - seatByAcReg.getSeatWeight()) + "",
                            seatByAcReg.getUserName(), seatByAcReg.getSeatWeight()+"",
                            UserManager.getInstance().getUser().getUserCode(),
                            DateFormatUtil.formatZDate(),
                            new ResponseListner<GrantsByUserCodeResponse>() {
                                @Override
                                public void onResponse(GrantsByUserCodeResponse response) {
                                    if (response != null
                                            && response.ResponseObject != null
                                            && response.ResponseObject.ResponseCode
                                            == Constants.RESULT_OK) {

                                    } else {
                                        //上传飞机成员失败
                                        failSeatInfo.add(seatByAcReg.getSeatId());
                                    }
                                }

                                @Override public void onEmptyOrError(String message) {
                                    LogUtil.LOGD(TAG, "上传飞机成员错误： " + message);
                                    failSeatInfo.add(seatByAcReg.getSeatId());}
                            });
                }

                Intent intent =
                            new Intent(EngineRoomActivity.this, RestrictionMapActivity.class);
                intent.putExtra(Constants.ACTION_AIRCRAFTTYPE, aircraftType);
                intent.putExtra(Constants.ACTION_AIRCRAFTREG, aircraftReg);
                //intent.putExtra(Constants.ACTION_SEATLIST, seatList);
                intent.putExtra(Constants.ACTION_FAILSEATINFOLIST, failSeatInfo);
                startActivity(intent);
            }
        };
    }
}
