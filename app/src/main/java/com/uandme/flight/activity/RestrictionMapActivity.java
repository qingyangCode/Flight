package com.uandme.flight.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.InjectView;
import com.uandme.flight.FlightApplication;
import com.uandme.flight.R;
import com.uandme.flight.data.dao.AcWeightLimit;
import com.uandme.flight.data.dao.AllAcType;
import com.uandme.flight.data.dao.AllAcTypeDao;
import com.uandme.flight.entity.AcWeightLimitByAcTypeResponse;
import com.uandme.flight.entity.FuleLimitByAcType;
import com.uandme.flight.entity.LineData;
import com.uandme.flight.network.ResponseListner;
import com.uandme.flight.util.Constants;
import com.uandme.flight.util.LogUtil;
import com.uandme.flight.util.ToastUtil;
import com.uandme.flight.widget.MyLineDegreeView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.w3c.dom.Text;

/**
 * Created by QingYang on 15/7/24.
 * 限制图
 */
public class RestrictionMapActivity extends BaseActivity{
    private String TAG = RestrictionMapActivity.class.getSimpleName();

    @InjectView(R.id.tv_maxoil)
    TextView mMaxOil;
    @InjectView(R.id.tv_realityoil)
    EditText mRealityouil;
    @InjectView(R.id.tv_slideoil)
    EditText mSlideOil;
    @InjectView(R.id.tv_flyoil)
    EditText mFlyOil;
    @InjectView(R.id.tv_beforeweight)
    EditText mBeforeWeight;
    @InjectView(R.id.tv_down)
    EditText mDownWight;
    @InjectView(R.id.tv_mac)
    TextView mMac;
    @InjectView(R.id.line_charview)
    MyLineDegreeView mLineCharView;
    @InjectView(R.id.tv_reality_focus)
    TextView mRealityFocus;
    @InjectView(R.id.tv_slide_focus)
    TextView mSlideFocus;
    @InjectView(R.id.tv_fly_focus)
    TextView mFlyFocus;
    @InjectView(R.id.tv_be_fore_focus)
    TextView mBroreFocus;
    @InjectView(R.id.tv_down_focus)
    TextView mDownFocus;
    private ArrayList<Float> lineX;
    private ArrayList<Float> lineY;

    @Override public int getContentView() {
        return R.layout.activity_restrictionmap;
    }

    @Override protected void onloadData() {
        mTopBarTitle.setText("限制图");
        getTopBarRight("下一步");
        lineX = new ArrayList<>();
        lineY = new ArrayList<>();
        String aircraftType = getIntent().getStringExtra("AircraftType");

        float weightCg = getWeightCg(mRealityouil);
        mRealityouil.setText(weightCg + "");
        float weightCg1 = getWeightCg(mSlideOil);
        mSlideFocus.setText(weightCg1 + "");
        float weightCg2 = getWeightCg(mFlyOil);
        mFlyOil.setText(weightCg2 + "");
        float weightCg3 = getWeightCg(mBeforeWeight);
        mBeforeWeight.setText(weightCg3 + "");
        float weightCg4 = getWeightCg(mDownWight);
        mDownWight.setText(weightCg4+"");

        showLineCharView();

        AllAcTypeDao allAcTypeDao = FlightApplication.getDaoSession().getAllAcTypeDao();
        List<AllAcType> allAcTypeList = allAcTypeDao.queryBuilder()
                .where(AllAcTypeDao.Properties.AircraftType.eq(aircraftType))
                .list();
        if (allAcTypeList != null && allAcTypeList.size() > 0) {
            AllAcType allAcType = allAcTypeList.get(0);
            //mMaxOil.setText(allAcTypeList.get(0));
       /* 获取机型重心限制信息表
                * @param AircraftType 请求的机型
                * @param PortLimit 机坪限重
                * @param TofWeightLimit 起飞重量
                * @param LandWeightLimit 落地重量
                * @param Mzfw 最大无油重量
                * @param OpDate 操作日期
                * @param SysVersion 版本信息
            */
        //获取重心限制信息
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //    String time = sdf.format(new Date());
            getMoccApi().getAcWeightLimitByAcType(allAcType.getAircraftType(), String.valueOf(allAcType.getPortLimit()),
                mBeforeWeight.getText().toString(), mDownWight.getText().toString(), String.valueOf(allAcType.getMzfw()),
                    "2015-08-09T16:00:31", String.valueOf(allAcType.getSysVersion()), new ResponseListner<AcWeightLimitByAcTypeResponse>() {
                    @Override public void onResponse(AcWeightLimitByAcTypeResponse response) {
                        if (response != null && response.ResponseObject != null && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                            showCharView(response.ResponseObject.ResponseData.IAppObject);
                        } else {
                            ToastUtil.showToast(RestrictionMapActivity.this, R.drawable.toast_warning, getString(R.string.get_data_error));
                        }
                    }

                    @Override public void onEmptyOrError(String message) {
                        ToastUtil.showToast(RestrictionMapActivity.this, R.drawable.toast_warning, getString(R.string.get_data_error));
                    }
                });
        }
    }

    private void showCharView(ArrayList<AcWeightLimit> acWeightLimits) {
        LineData lineData = new LineData();
        ArrayList<Float> lineX = new ArrayList<Float>();
        ArrayList<Float> lineY = new ArrayList<Float>();
        for (int i = 0 ; i < acWeightLimits.size(); i++ ) {
            String weight = acWeightLimits.get(i).getWeight();
            float wei = 0;
            if (!TextUtils.isEmpty(weight))
                wei = Float.parseFloat(weight);
            lineX.add(wei);
            String Cg1 = acWeightLimits.get(i).getWeightCg1();
            float weiCg1 = 0;
            if (!TextUtils.isEmpty(Cg1))
                weiCg1 = Float.parseFloat(Cg1);
            lineY.add(weiCg1);
        }

        for (int y = acWeightLimits.size() - 1; y > 0; y--) {
            String weight = acWeightLimits.get(y).getWeight();
            float wei = 0;
            if (!TextUtils.isEmpty(weight))
                wei =Float.parseFloat(weight);
            lineX.add(wei);
            String weightCg2 = acWeightLimits.get(y).getWeightCg2();
            float weiCg2 = 0;
            if (!TextUtils.isEmpty(weightCg2)) {
                lineY.add(weiCg2);
            }
        }
        lineData.setDatasX(lineX);
        lineData.setDatasY(lineY);
        mLineCharView.addData(lineData);

    }

    private void showLineCharView() {
        LineData lineData = new LineData();
        lineData.setDatasX(lineX);
        lineData.setDatasY(lineY);
        mLineCharView.setData(lineData);

    }

    private float getWeightCg(TextView tv) {
        String weight = tv.getText().toString().trim();
        float result = 0;
        float weighFloat = 0;
        if (!TextUtils.isEmpty(weight)) {
            weighFloat = Float.parseFloat(weight);
            float lj = weighFloat / 100;
            double weightCg = lj * 347.51 / weighFloat;
            result =  (float)weightCg;
        }
        lineX.add(weighFloat);
        lineY.add(result);
        return result;
    }


    @Override public View.OnClickListener getRightOnClickListener() {
        return new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(RestrictionMapActivity.this, AircraftPersonnelActivity.class));
            }
        };
    }
}
