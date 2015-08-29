package com.xiaoqing.flight.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.xiaoqing.flight.R;
import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;


public class ToastUtil {

    public static void showToast(Context context, int stringResId) {
        showToast(context, Integer.MIN_VALUE, context.getString(stringResId));
    }
    public static void showToast(Context context, int iv_toast_header, int tv_toast_con) {
        showToast(context,iv_toast_header,context.getString(tv_toast_con));
    }
    public static void showToast(Context context, int iv_toast_header, String tv_toast_con) {
        Crouton.cancelAllCroutons();
        final View view = View.inflate(context, R.layout.toast_common_layout, null);
        LinearLayout ll_toast_container = (LinearLayout) view.findViewById(R.id.ll_toast_container);
        ImageView iv_toast_indicator = (ImageView) ll_toast_container.findViewById(R.id.iv_toast_indicator);
        TextView tv_toast_content = (TextView) ll_toast_container.findViewById(R.id.tv_toast_content);
        if (iv_toast_header > 0) iv_toast_indicator.setBackgroundResource(iv_toast_header);
        tv_toast_content.setText(tv_toast_con);

        if(context instanceof Activity){
            final Crouton crouton = Crouton.make((Activity) context, ll_toast_container, null).setConfiguration(new Configuration.Builder().setInAnimation(R.anim.abc_slide_in_top).setOutAnimation(R.anim.abc_slide_out_top).setDuration(3000).build());
            ll_toast_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    crouton.hide();
                }
            });
            crouton.show();
        }
    }
}
