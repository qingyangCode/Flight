package com.uandme.flight.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.InjectView;
import com.uandme.flight.FlightApplication;
import com.uandme.flight.R;
import com.uandme.flight.data.dao.SeatByAcReg;
import com.uandme.flight.data.dao.SeatByAcRegDao;
import com.uandme.flight.data.dao.User;
import com.uandme.flight.data.dao.UserDao;
import com.uandme.flight.entity.EngineRoom;
import com.uandme.flight.entity.SeatByAcRegResponse;
import com.uandme.flight.network.ResponseListner;
import com.uandme.flight.util.ApiServiceManager;
import com.uandme.flight.util.CommonProgressDialog;
import com.uandme.flight.util.CommonUtils;
import com.uandme.flight.util.LogUtil;
import com.uandme.flight.util.ToastUtil;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Text;

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
    private List<SeatByAcReg> seatList;



    @Override public int getContentView() {
        return R.layout.activity_engineroom;
    }

    @Override protected void onloadData() {
        Intent data = getIntent();
        seatList = new ArrayList<>();
        if (data != null) {
            aircraftReg = data.getStringExtra("AircraftReg");
            aircraftType = data.getStringExtra("AircraftType");
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
        } else {
            addSeatView();
        }
    }


    private float lastPosition = 0;
    private View view = null;
    private ImageView upView = null;
    private ImageView downView = null;
    private boolean isUpSelected;
    private boolean isDownSelected;


    private void addSeatView() {
        for(SeatByAcReg seatByAcReg : seatList) {
            if (seatByAcReg.getXPos() != 0) {
                isUpSelected = true;
                if ((lastPosition + 0.03 < seatByAcReg.getXPos() && lastPosition - 0.03 > seatByAcReg.getXPos()) || lastPosition == 0 || lastPosition != seatByAcReg.getXPos()) {
                    view = View.inflate(this, R.layout.item_seat, null);
                    upView = (ImageView) view.findViewById(R.id.iv_up);
                    downView = (ImageView) view.findViewById(R.id.iv_down);
                    mLinearLayout.addView(view);
                }
                if (view == null || upView == null || downView == null)
                    continue;
                    if ("LEFT".equals(seatByAcReg.getDirection())) {
                        if (seatByAcReg.getYPos() < 0.45) {
                            upView.setVisibility(View.VISIBLE);
                            upView.setBackgroundResource(R.drawable.seat_left_selector);
                        } else {
                            downView.setVisibility(View.VISIBLE);
                            downView.setBackgroundResource(R.drawable.seat_left_selector);
                        }
                    } else if ("UP".equals(seatByAcReg.getDirection())) {
                        if (seatByAcReg.getYPos() < 0.45) {
                            upView.setVisibility(View.VISIBLE);
                            upView.setBackgroundResource(R.drawable.seat_up_selector);
                        } else {
                            downView.setVisibility(View.VISIBLE);
                            downView.setBackgroundResource(R.drawable.seat_up_selector);
                        }
                    } else if ("DOWN".equals(seatByAcReg.getDirection())) {
                        if (seatByAcReg.getYPos() < 0.45) {
                            upView.setVisibility(View.VISIBLE);
                            upView.setBackgroundResource(R.drawable.seat_down_selector);
                        } else {
                            downView.setVisibility(View.VISIBLE);
                            downView.setBackgroundResource(R.drawable.seat_down_selector);
                        }
                    } else if ("RIGHT".equals(seatByAcReg.getDirection())) {
                        if (seatByAcReg.getYPos() < 0.45) {
                            upView.setVisibility(View.VISIBLE);
                            upView.setBackgroundResource(R.drawable.seat_right_selector);
                        } else {
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

    private ArrayList<SeatByAcReg> seatInfos;
    private ArrayList<Float> weightList;
    private void getSeatByDB() {
        seatInfos = new ArrayList<>();
        weightList = new ArrayList<>();
        SeatByAcRegDao seatByAcRegDao = FlightApplication.getDaoSession().getSeatByAcRegDao();
        seatList = seatByAcRegDao.queryBuilder()
                .where(SeatByAcRegDao.Properties.AcReg.eq(aircraftReg))
                .list();

        Collections.sort(seatList, new Comparator<SeatByAcReg>() {

            @Override public int compare(SeatByAcReg lhs, SeatByAcReg rhs) {
                return lhs.getXPos() > rhs.getXPos() ? 1 : -1;
            }
        });

        for ( int index = 0; index < seatList.size() ; index ++) {
            final SeatByAcReg seatByAcReg = seatList.get(index);
            LogUtil.LOGD(TAG, "排序后结果： " + seatByAcReg.getXPos() + "   :  " + seatByAcReg.getYPos() +  " ==  " + seatByAcReg.toString());

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
                et_weight.setText("180");
                if (180 > seatByAcReg.getAcTypeSeatLimit()) {
                    et_weight.setBackgroundColor(getResources().getColor(R.color.red));
                } else {
                    et_weight.setBackgroundResource(R.drawable.engineeroom_name_bg);
                }
                final int position = index;
                LogUtil.LOGD(TAG, "position ===== " + position);
                tv_seat.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override public void afterTextChanged(Editable s) {
                        if (!TextUtils.isEmpty(s.toString()))
                            seatInfos.get(position).setUserName(s.toString());
                            //seatByAcReg.setUserName(s.toString());
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
                            //seatByAcReg.setAcRegCargWeight(weight);
                            //try {
                            weightList.set(position, weight);
                                //seatInfos.get(position).setAcRegCargWeight(weight);
                            //} catch (Exception e) {
                            //    e.printStackTrace();
                            //}
                        }
                    }
                });
                mSeatLinearLayout.addView(convertView);
                seatInfos.add(seatByAcReg);
            }
            if (seatByAcReg.getXPos() != 0) {
                weightList.add(180f);
            } else {
                weightList.add(0f);
            }

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

    //private void switchButtonBackground(Button button) {
    //    if(button.isSelected()) {
    //        button.setSelected(false);
    //    } else {
    //        button.setSelected(true);
    //    }
    //}



    //private View.OnTouchListener onNameTouchListener = new View.OnTouchListener() {
    //    @Override public boolean onTouch(View v, MotionEvent event) {
    //        if(event.getAction() == MotionEvent.ACTION_UP){
    //            index1 = (Integer) v.getTag();
    //        }
    //        return false;
    //    }
    //};
    //
    //private View.OnTouchListener onWeightTouchListener = new View.OnTouchListener() {
    //    @Override public boolean onTouch(View v, MotionEvent event) {
    //        if(event.getAction() == MotionEvent.ACTION_UP){
    //            index2 = (Integer) v.getTag();
    //        }
    //        return false;
    //    }
    //};





    //private Integer index1 = -1;
    //private Integer index2 = -1;



    //public class EngineAdapter extends BaseAdapter{
    //
    //    private List<Map<String , String>> nameLists;
    //    private List<Map<String , String>> weightLists;
    //    public EngineAdapter() {
    //        nameLists = new ArrayList<Map<String, String>>();
    //        weightLists = new ArrayList<Map<String, String>>();
    //        for(int i = 0; i < seatList.size(); i++) {
    //            nameLists.add(new HashMap<String, String>());
    //            weightLists.add(new HashMap<String, String>());
    //        }
    //    }
    //
    //    @Override public int getCount() {
    //        return seatList.size();
    //    }
    //
    //    @Override public View getView(final int position, View convertView, ViewGroup parent) {
    //        final ViewHolder holder;
    //        if (convertView == null) {
    //            holder = new ViewHolder();
    //            convertView = View.inflate(EngineRoomActivity.this, R.layout.item_seatlayout, null);
    //            holder.tv_seat = (TextView) convertView.findViewById(R.id.tv_seat);
    //            holder.et_passenger = (AutoCompleteTextView) convertView.findViewById(R.id.tv_passengerName);
    //            holder.et_passenger.setTag(position);
    //            holder.et_passenger.setOnTouchListener(onNameTouchListener);
    //            final UserDao userDao = FlightApplication.getDaoSession().getUserDao();
    //            final List<User> list = userDao.queryBuilder().list();
    //            String [] names = new String[list.size()];
    //            for(int i = 0; i < list.size(); i++) {
    //                names[i] = list.get(i).getUserName();
    //            }
    //            holder.et_passenger.setAdapter(new ArrayAdapter<String>(EngineRoomActivity.this, R.layout.list_item, names));
    //            holder.tv_arm = (TextView) convertView.findViewById(R.id.tv_Arm);
    //            holder.et_weight = (EditText) convertView.findViewById(R.id.tv_weight);
    //            holder.et_weight.setTag(position);
    //            holder.et_weight.setOnTouchListener(onWeightTouchListener);
    //            convertView.setTag(holder);
    //        } else {
    //            holder = (ViewHolder) convertView.getTag();
    //            holder.et_passenger.setTag(position);
    //            holder.et_weight.setTag(position);
    //        }
    //        final SeatByAcReg seatInfos = seatList.get(position);
    //
    //        holder.tv_seat.setEnabled(false);
    //        holder.tv_seat.setText(seatInfos.getSeatCode());
    //        TextWatcher nameWatcher = new TextWatcher() {
    //            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    //
    //            }
    //
    //            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
    //
    //            }
    //
    //            @Override public void afterTextChanged(Editable s) {
    //                //seatInfos.userName = s.toString().trim();
    //                if (!TextUtils.isEmpty(s)) {
    //                    int position = (Integer) holder.et_passenger.getTag();
    //                    // 当EditText数据发生改变的时候存到data变量中
    //                    nameLists.get(position).put("userNames", s.toString());
    //                }
    //            }
    //        };
    //        TextWatcher weightWatcher = new TextWatcher() {
    //            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    //
    //            }
    //
    //            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
    //
    //            }
    //
    //            @Override public void afterTextChanged(Editable s) {
    //                //seatInfos.userName = s.toString().trim();
    //                if (!TextUtils.isEmpty(s)) {
    //                    int tag = (int) holder.et_weight.getTag();
    //                    weightLists.get(tag).put("userWeight", s.toString());
    //                    LogUtil.LOGD(TAG, "========userWeight=======" + s.toString());
    //                }
    //            }
    //        };
    //        holder.et_passenger.addTextChangedListener(nameWatcher);
    //        holder.et_weight.addTextChangedListener(weightWatcher);
    //
    //        if (nameLists != null && nameLists.size() > position) {
    //            String userName = nameLists.get(position).get("userNames");
    //            if(!TextUtils.isEmpty(userName)) {
    //                holder.et_passenger.setText(userName);
    //            } else {
    //                holder.et_passenger.setText("");
    //            }
    //
    //
    //
    //        }
    //        if (weightLists != null && weightLists.size() > 0) {
    //            String weight = weightLists.get(position).get("userWeight");
    //            if (!TextUtils.isEmpty(weight)) {
    //                holder.et_weight.setText(weight);
    //            } else {
    //                holder.et_weight.setText("180");
    //            }
    //        }
    //
    //        holder.tv_arm.setText(seatInfos.getAcTypeLb() + "");
    //
    //        //holder.et_passenger.clearFocus();
    //        //if (index1 != -1 && index1 == position) {
    //        //    holder.et_passenger.requestFocus();
    //        //}
    //        //
    //        //if(index2 != -1 && index2 == position) {
    //        //    holder.et_weight.requestFocus();
    //        //}
    //
    //        handleWeight(holder, seatInfos);
    //
    //
    //
    //        return convertView;
    //    }
    //
    //    @Override public Object getItem(int position) {
    //        return null;
    //    }
    //
    //    @Override public long getItemId(int position) {
    //        return 0;
    //    }
    //}

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
                Intent intent =
                            new Intent(EngineRoomActivity.this, RestrictionMapActivity.class);
                if(!TextUtils.isEmpty(aircraftType)) {
                    intent.putExtra("AircraftType", aircraftType);
                }
                intent.putExtra("weightList", weightList);
                startActivity(intent);
            }
        };
    }
}
