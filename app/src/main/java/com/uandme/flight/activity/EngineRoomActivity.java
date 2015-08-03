package com.uandme.flight.activity;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.uandme.flight.R;
import com.uandme.flight.adapter.FlightBaseAdapter;
import com.uandme.flight.entity.EngineRoom;
import com.uandme.flight.entity.SeatByAcReg;
import com.uandme.flight.network.ResponseListner;
import com.uandme.flight.util.CommonProgressDialog;
import com.uandme.flight.util.Constants;
import com.uandme.flight.util.ToastUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by QingYang on 15/7/23.
 */
public class EngineRoomActivity extends BaseActivity implements View.OnClickListener{
    private String TAG = EngineRoom.class.getSimpleName();
    //@InjectView(R.id.btn1)
    Button mBtn1;
    //@InjectView(R.id.btn2)
    Button mBtn2;
    //@InjectView(R.id.btn3)
    Button mBtn3;
    //@InjectView(R.id.btn5)
    Button mBtn5;
    //@InjectView(R.id.btn6)
    Button mBtn6;
    //@InjectView(R.id.btn7)
    Button mBtn7;
    //@InjectView(R.id.btn8)
    Button mBtn8;
    //@InjectView(R.id.btn9)
    Button mBtn9;
    //@InjectView(R.id.btn10)
    Button mBtn10;
    //@InjectView(R.id.btn11)
    Button mBtn11;
    //@InjectView(R.id.tv_seat)
    EditText mSeat;
    //@InjectView(R.id.tv_passengerName)
    EditText mPassengerName;
    //@InjectView(R.id.tv_Arm)
    TextView mArm;
    //@InjectView(R.id.tv_weight)
    EditText mWeight;

    //@InjectView(R.id.mListView)
    ListView mListView;

    private View inflate;


    private String aircraftReg;
    private String lj;
    private String opDate;
    private String sysVersion;
    private String bw;

    @Override public int getContentView() {
        return R.layout.activity_engineroom;
    }

    @Override protected void onloadData() {
        Intent data = getIntent();
        if (data != null) {
            aircraftReg = data.getStringExtra("AircraftReg");
            lj = data.getStringExtra("Lj");
            opDate = data.getStringExtra("OpDate");
            sysVersion = data.getStringExtra("SysVersion");
            bw = data.getStringExtra("Bw");
        }

        final CommonProgressDialog dialog = new CommonProgressDialog(this);
        dialog.setTip("Loading ..");
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        mTopBarTitle.setText("机舱信息");
        mTopBarRight.setText("Next");

        mListView = (ListView) findViewById(R.id.mListView);
        inflate = View.inflate(this, R.layout.header_engineroom, null);
        mBtn1 = (Button) inflate.findViewById(R.id.btn1);
        mBtn2 = (Button) inflate.findViewById(R.id.btn2);
        mBtn3 = (Button) inflate.findViewById(R.id.btn3);
        mBtn5 = (Button) inflate.findViewById(R.id.btn5);
        mBtn6 = (Button) inflate.findViewById(R.id.btn6);
        mBtn7 = (Button) inflate.findViewById(R.id.btn7);
        mBtn8 = (Button) inflate.findViewById(R.id.btn8);
        mBtn9 = (Button) inflate.findViewById(R.id.btn9);
        mBtn10 = (Button) inflate.findViewById(R.id.btn10);
        mBtn11 = (Button) inflate.findViewById(R.id.btn11);
        mSeat = (EditText) inflate.findViewById(R.id.tv_seat);
        mPassengerName = (EditText) inflate.findViewById(R.id.tv_passengerName);
        mArm = (TextView) inflate.findViewById(R.id.tv_Arm);
        mWeight = (EditText) inflate.findViewById(R.id.tv_weight);
        mBtn1.setOnClickListener(this);
        mBtn2.setOnClickListener(this);
        mBtn3.setOnClickListener(this);
        mBtn5.setOnClickListener(this);
        mBtn6.setOnClickListener(this);
        mBtn7.setOnClickListener(this);
        mBtn8.setOnClickListener(this);
        mBtn9.setOnClickListener(this);
        mBtn10.setOnClickListener(this);
        mBtn11.setOnClickListener(this);


        mSeat.setEnabled(false);
        mSeat.setText("座位");
        mPassengerName.setEnabled(false);
        mPassengerName.setBackgroundResource(0);
        mPassengerName.setGravity(Gravity.CENTER);
        mPassengerName.setText("乘客姓名");
        mArm.setEnabled(false);
        mArm.setText("力矩 (in.)");
        mWeight.setEnabled(false);
        mWeight.setBackgroundResource(0);
        mWeight.setText("重量 (lb.)");
        //ButterKnife.inject(inflate);
        mListView.addHeaderView(inflate);



        getMoccApi().getSeatByAcReg(aircraftReg, bw, lj, opDate, sysVersion, new ResponseListner<SeatByAcReg>(){

            @Override public void onResponse(SeatByAcReg response) {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
                if (response != null && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                    mListView.setAdapter(new EngineAdapter(EngineRoomActivity.this, response.ResponseObject.ResponseData.IAppObject, 50, R.layout.item_seatlayout, R.layout.item_failed));
                }else {
                    mListView.setAdapter(new EngineAdapter(EngineRoomActivity.this, new ArrayList<SeatByAcReg.SeatInfos>(), 50, R.layout.item_seatlayout, R.layout.item_failed));
                }
            }

            @Override public void onEmptyOrError(String message) {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
                ToastUtil.showToast(EngineRoomActivity.this, R.drawable.toast_warning,!TextUtils.isEmpty(message) ? message : getString(R.string.get_data_error));
            }
        });


        List<EngineRoom> engineRooms = new ArrayList<EngineRoom>();

        for (int i = 1; i < 11; i++) {
            if(i == 4)
                continue;
            EngineRoom room = new EngineRoom();
            room.seat = "seat " + i;
            room.passengerName = "name "+ i;
            room.arm = String.valueOf((new Random()).nextInt(400));
            if(i < 3)
                room.weight = 200 + "";
            else if(i < 6)
                room.weight = 170 + "";
            else
                room.weight = 0 +"";

            engineRooms.add(room);
        }

        //mListView.setAdapter(new EngineAdapter(this, engineRooms, 50, R.layout.item_seatlayout, R.layout.item_failed));

    }

    //@OnClick({R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btn10, R.id.btn11})
    public void onViewClick(View v) {

    }

    private void switchButtonBackground(Button button) {
        if(button.isSelected()) {
            button.setSelected(false);
        } else {
            button.setSelected(true);
        }
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.btn1:
                switchButtonBackground(mBtn1);
                break;
            case  R.id.btn2:
                switchButtonBackground(mBtn2);
                break;
            case  R.id.btn3:
                switchButtonBackground(mBtn3);
                break;
            case  R.id.btn5:
                switchButtonBackground(mBtn5);
                break;
            case  R.id.btn6:
                switchButtonBackground(mBtn6);
                break;
            case  R.id.btn7:
                switchButtonBackground(mBtn7);
                break;
            case  R.id.btn8:
                switchButtonBackground(mBtn8);
                break;
            case  R.id.btn9:
                switchButtonBackground(mBtn9);
                break;
            case  R.id.btn10:
                switchButtonBackground(mBtn10);
                break;
            case  R.id.btn11:
                switchButtonBackground(mBtn11);
                break;
        }
    }

    public class EngineAdapter extends FlightBaseAdapter<SeatByAcReg.SeatInfos>{

        public EngineAdapter(Context context, List<SeatByAcReg.SeatInfos> iniData, int pageSize, int res,
                int loadingRes) {
            super(context, iniData, pageSize, res, loadingRes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent, final SeatByAcReg.SeatInfos value) {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if(holder == null) {
                holder = new ViewHolder();
                holder.tv_seat = (TextView) convertView.findViewById(R.id.tv_seat);
                holder.et_passenger = (EditText) convertView.findViewById(R.id.tv_passengerName);
                holder.tv_arm = (TextView) convertView.findViewById(R.id.tv_Arm);
                holder.et_weight = (EditText) convertView.findViewById(R.id.tv_weight);
            }
            convertView.setTag(holder);
            holder.tv_seat.setEnabled(false);
            holder.tv_seat.setText(value.SeatCode);
            holder.et_passenger.setText("");
            holder.tv_arm.setText(value.AcTypeLb + "");
            holder.et_weight.setText("180");
            handleWeight(holder, value);

            return convertView;
        }

    }

    class ViewHolder {
        TextView tv_seat;
        EditText et_passenger;
        TextView tv_arm;
        EditText et_weight;
    }

    private void handleWeight(final ViewHolder holder, final SeatByAcReg.SeatInfos value) {
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
                                if (weight > value.AcTypeSeatLimit) {
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
                startActivity(new Intent(EngineRoomActivity.this, RestrictionMapActivity.class));
            }
        };
    }
}
