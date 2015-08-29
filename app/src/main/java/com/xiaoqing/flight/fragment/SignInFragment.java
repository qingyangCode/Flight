package com.xiaoqing.flight.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.xiaoqing.flight.R;
import com.xiaoqing.flight.activity.MainActivity;
import com.xiaoqing.flight.data.dao.User;
import com.xiaoqing.flight.entity.LoginUserInfo;
import com.xiaoqing.flight.network.ResponseListner;
import com.xiaoqing.flight.util.ApiServiceManager;
import com.xiaoqing.flight.util.CommonProgressDialog;
import com.xiaoqing.flight.util.DateFormatUtil;
import com.xiaoqing.flight.util.PreferenceUtils;
import com.xiaoqing.flight.util.ToastUtil;
import com.xiaoqing.flight.util.UserManager;

/**
 * Created by QingYang on 15/7/19.
 */
public class SignInFragment extends BaseFragment {

    @InjectView(R.id.btn_signIn) Button mBtnSignIn;
    @InjectView(R.id.et_username) EditText mEtUserName;
    @InjectView(R.id.et_pwssword) EditText mEtPassword;
    @InjectView(R.id.iv_checkbox) CheckBox mCheckbox;
    @InjectView(R.id.layout_top) RelativeLayout mLayoutTop;

    @Override public View getContentView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragment_signin, null);
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //if (PreferenceUtils.getInstance().getRememberPassword()) {
        mEtUserName.setText(PreferenceUtils.getInstance().getUserName());
            //mEtPassword.setText(PreferenceUtils.getInstance().getPassword());
        //    mCheckbox.setChecked(true);
        //} else {
        //    mCheckbox.setChecked(false);
        //}
        mEtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                onSignInClick();
                return false;
            }
        });
    }

    @OnClick(R.id.btn_signIn) public void onSignInClick() {
        final String userName = mEtUserName.getText().toString().trim();
        final String password = mEtPassword.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            ToastUtil.showToast(getActivity(), R.drawable.toast_warning,
                    "用户名不能为空");
            return;
        } else if (TextUtils.isEmpty(password)) {
            ToastUtil.showToast(getActivity(), R.drawable.toast_warning,
                    "密码不能为空");
            return;
        }
        PreferenceUtils.getInstance().saveUserName(userName);
        mLayoutTop.setVisibility(View.VISIBLE);
        final CommonProgressDialog progressDialog = new CommonProgressDialog(getActivity());
        progressDialog.setTip("正在登录 ..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
        getMoccApi().doLogin(userName, password, new ResponseListner<LoginUserInfo>() {
            @Override public void onResponse(LoginUserInfo userInfo) {
                progressDialog.dismiss();
                mLayoutTop.setVisibility(View.GONE);
                //UserManager.getInstance().getSystemMessage();
                if(userInfo != null) {
                    User user = new User();
                    user.setUserName(userName);
                    user.setCheckCode(userInfo.getUserCodeCheck());
                    user.setUserCode(userInfo.getUserCode());
                    user.setUserPassWord(password);
                    UserManager.getInstance().setUserInfo(user);
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    ApiServiceManager.getInstance().getSystemMessage(DateFormatUtil.getTimes(DateFormatUtil.TIME_TWODAYS), null);
                    getActivity().finish();
                }
                //PreferenceUtils.getInstance().saveRememberPassword(mCheckbox.isChecked());
                //PreferenceUtils.getInstance().saveUserName(userName);
                //PreferenceUtils.getInstance().savePassword(password);
            }

            @Override public void onEmptyOrError(String message) {
                progressDialog.dismiss();
                mLayoutTop.setVisibility(View.GONE);
                ToastUtil.showToast(getActivity(), R.drawable.toast_warning, TextUtils.isEmpty(message)? getString(R.string.get_data_error) : message);
            }
        });
    }

    @OnClick(R.id.layout_remember_password) public void onRememberPasswordClick() {
        if (mCheckbox.isChecked()) {
            mCheckbox.setChecked(false);
        } else {
            mCheckbox.setChecked(true);
        }

    }
}
