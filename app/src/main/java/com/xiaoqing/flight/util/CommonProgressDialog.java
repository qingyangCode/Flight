package com.xiaoqing.flight.util;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.xiaoqing.flight.R;



public class CommonProgressDialog extends Dialog {


    private TextView mTvTip;

    public CommonProgressDialog(Context context) {
        super(context, R.style.theme_common_progress_dialog);
        View view = LayoutInflater.from(context).inflate(R.layout.common_progress_dialog, null);
        mTvTip = (TextView) view.findViewById(R.id.tv_tip);
        setContentView(view);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    public void setTip(String tip) {
        mTvTip.setText(tip);
    }

    public TextView getTvTip() {
        return mTvTip;
    }

    public void setTip(int resId) {
        mTvTip.setText(resId);
    }

}
