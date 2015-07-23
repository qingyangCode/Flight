package com.uandme.flight.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.OnClick;
import com.uandme.flight.R;
import com.uandme.flight.adapter.FlightBaseAdapter;
import com.uandme.flight.entity.AllAirCraft;
import com.uandme.flight.network.ResponseListner;
import com.uandme.flight.util.CommonProgressDialog;
import com.uandme.flight.util.ToastUtil;
import com.uandme.flight.util.UserManager;
import java.util.List;

/**
 * Created by QingYang on 15/7/19.
 */
public class MainActivity extends BaseActivity {

    @InjectView(R.id.mListView)
    ListView mListView;
    @InjectView(R.id.tv_top_bar_title)
    TextView mTvTitle;

    private final int SPACETIME = 2000;
    private long mLastBackPressTime;

    @Override public int getContentView() {
        return R.layout.activity_main;
    }

    @Override protected void initView() {
        super.initView();
        mTvTitle.setText("AllAircraft");
    }

    @Override protected void onloadData() {
        final CommonProgressDialog dialog = new CommonProgressDialog(this);
        dialog.setTip("Loading ..");
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        getMoccApi().getAllAircraft(UserManager.getInstance().getUser().getUserCode(),UserManager.getInstance().getUser().getUserCodeCheck(), new ResponseListner<AllAirCraft>() {

            @Override public void onResponse(AllAirCraft response) {
                dialog.dismiss();
                mListView.setAdapter(new PlanAdapterFlight(MainActivity.this,response.ResponseObject.ResponseData.IAppObject, 50, R.layout.item_allaircraft, R.layout.item_failed));
            }

            @Override public void onEmptyOrError(String message) {
                dialog.dismiss();
                ToastUtil.showToast(MainActivity.this, R.drawable.toast_warning,TextUtils.isEmpty(message) ? getString(R.string.get_data_error) : message);
            }
        });
    }

    public class PlanAdapterFlight extends FlightBaseAdapter<AllAirCraft.TIAppObject> {
        public PlanAdapterFlight(Context context, List<AllAirCraft.TIAppObject> iniData,
                int pageSize, int res, int loadingRes) {
            super(context, iniData, pageSize, res, loadingRes);
        }

        @Override public View getView(final int position, View convertView, ViewGroup parent,
               final AllAirCraft.TIAppObject value) {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if(holder == null) {
                holder = new ViewHolder();
                holder.mTvname = (TextView) convertView.findViewById(R.id.tv_text1);
                holder.mTvtext2 = (TextView) convertView.findViewById(R.id.tv_text2);
                holder.mTvtext3 = (TextView) convertView.findViewById(R.id.tv_text3);
                holder.mTvtext4 = (TextView) convertView.findViewById(R.id.tv_text4);
                holder.mTvtext5 = (TextView) convertView.findViewById(R.id.tv_text5);
            }
            convertView.setTag(holder);
            holder.mTvname.setText("BW : " + value.Bw);
            holder.mTvtext2.setText("AcRemark : " + value.AcRemark);
            holder.mTvtext3.setText("AircraftReg : " + value.AircraftReg);
            holder.mTvtext4.setText("AircraftType : "+value.AircraftType);
            holder.mTvtext5.setText("OpDate :" + value.OpDate);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    //ToastUtil.showToast(MainActivity.this,R.drawable.toast_confirm,"position == "+ position);
                    Intent intent = new Intent(MainActivity.this, BasicInfoActivity.class);
                    intent.putExtra("AircraftReg", value.AircraftReg);
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }

    public class ViewHolder {
        TextView mTvname;
        TextView mTvtext2;
        TextView mTvtext3;
        TextView mTvtext4;
        TextView mTvtext5;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - mLastBackPressTime > SPACETIME) {
                mLastBackPressTime = System.currentTimeMillis();
                 Toast.makeText(getBaseContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                finish();
                System.exit(0);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override public boolean isShowTopBarLeft() {
        return false;
    }
}
