package com.uandme.flight.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.InjectView;
import com.uandme.flight.FlightApplication;
import com.uandme.flight.R;
import com.uandme.flight.adapter.FlightBaseAdapter;
import com.uandme.flight.data.dao.AllAircraft;
import com.uandme.flight.data.dao.AllAircraftDao;
import com.uandme.flight.entity.AllAirCraftResponse;
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
        mTvTitle.setText("全部飞机");
    }

    @Override protected void onloadData() {
        final CommonProgressDialog dialog = new CommonProgressDialog(this);
        dialog.setTip("加载中 ..");
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        getMoccApi().getAllAircraft(UserManager.getInstance().getUser().getUserCode(), new ResponseListner<AllAirCraftResponse>() {

            @Override public void onResponse(AllAirCraftResponse response) {
                dialog.dismiss();
                UserManager.getInstance().onLogin();
                if (response != null && response.ResponseObject != null && response.ResponseObject.ResponseData!= null) {
                    UserManager.getInstance().insertAllAircrartDB(
                            response.ResponseObject.ResponseData.IAppObject);
                    mListView.setAdapter(new PlanAdapterFlight(MainActivity.this,response.ResponseObject.ResponseData.IAppObject, 50, R.layout.item_allaircraft, R.layout.item_failed));
                } else {
                    ToastUtil.showToast(MainActivity.this, R.drawable.toast_warning, getString(R.string.get_data_error));
                }
            }

            @Override public void onEmptyOrError(String message) {
                dialog.dismiss();
                ToastUtil.showToast(MainActivity.this, R.drawable.toast_warning,TextUtils.isEmpty(message) ? getString(R.string.get_data_error) : message);
            }
        });
    }

    public class PlanAdapterFlight extends FlightBaseAdapter<AllAircraft> {
        public PlanAdapterFlight(Context context, List<AllAircraft> iniData,
                int pageSize, int res, int loadingRes) {
            super(context, iniData, pageSize, res, loadingRes);
        }

        @Override public View getView(final int position, View convertView, ViewGroup parent,
               final AllAircraft value) {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if(holder == null) {
                holder = new ViewHolder();
                holder.number = (TextView) convertView.findViewById(R.id.tv_number);
                holder.type = (TextView) convertView.findViewById(R.id.tv_type);
            }
            convertView.setTag(holder);
            holder.number.setText(value.getAircraftReg());
            holder.type.setText(value.getAircraftType());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, BasicInfoActivity.class);
                    intent.putExtra("Lj", value.getLj());
                    intent.putExtra("AircraftReg", value.getAircraftReg());
                    intent.putExtra("AircraftType", value.getAircraftType());
                    intent.putExtra("Bw", value.getBw());
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }

    public class ViewHolder {
        TextView number;
        TextView type;
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
                //System.exit(0);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override public boolean isShowTopBarLeft() {
        return false;
    }
}
