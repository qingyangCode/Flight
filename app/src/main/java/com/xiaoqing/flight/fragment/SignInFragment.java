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
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.OnClick;
import com.xiaoqing.flight.FlightApplication;
import com.xiaoqing.flight.R;
import com.xiaoqing.flight.activity.MainActivity;
import com.xiaoqing.flight.activity.ResetPasswordActivity;
import com.xiaoqing.flight.data.dao.User;
import com.xiaoqing.flight.data.dao.UserDao;
import com.xiaoqing.flight.entity.LoginUserInfoResponse;
import com.xiaoqing.flight.network.ResponseListner;
import com.xiaoqing.flight.util.ApiServiceManager;
import com.xiaoqing.flight.util.CommonProgressDialog;
import com.xiaoqing.flight.util.CommonUtils;
import com.xiaoqing.flight.util.Constants;
import com.xiaoqing.flight.util.PreferenceUtils;
import com.xiaoqing.flight.util.ToastUtil;
import com.xiaoqing.flight.util.UserManager;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
        if (CommonUtils.isNetworkConnected(getActivity())) {
            getMoccApi().doLogin(userName, password, new ResponseListner<LoginUserInfoResponse>() {
                @Override public void onResponse(LoginUserInfoResponse response) {
                    progressDialog.dismiss();
                    mLayoutTop.setVisibility(View.GONE);
                    //UserManager.getInstance().getSystemMessage();
                    if (response != null && response.ResponseObject != null) {
                        if(response.ResponseObject.ResponseData != null && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                            User iAppObject = response.ResponseObject.ResponseData.IAppObject;
                            if ("S".equalsIgnoreCase(iAppObject.getGrant_S_M())) {
                                ToastUtil.showToast(getActivity(), R.drawable.toast_warning, "当前用户权限不足，请联系管理员");
                            } else {
                                iAppObject.setCodeCheck(response.getCodeCheck());
                                nativeToMain(iAppObject);
                                Executors.newSingleThreadExecutor().execute(new Runnable() {
                                    @Override public void run() {
                                        UserManager.getInstance().onLogin();
                                    }
                                });
                            }

                        } else {
                            String message = response.ResponseObject.ResponseErr;
                            ToastUtil.showToast(getActivity(), R.drawable.toast_warning, TextUtils.isEmpty(message) ? getString(R.string.get_data_error) : message);
                        }
                    }
                    //PreferenceUtils.getInstance().saveRememberPassword(mCheckbox.isChecked());
                    //PreferenceUtils.getInstance().saveUserName(userName);
                    //PreferenceUtils.getInstance().savePassword(password);
                }

                @Override public void onEmptyOrError(String message) {
                    progressDialog.dismiss();
                    mLayoutTop.setVisibility(View.GONE);
                    //siginInByDB(userName, password, progressDialog);
                    ToastUtil.showToast(getActivity(), R.drawable.toast_warning, TextUtils.isEmpty(message)? getString(R.string.get_data_error) : message);
                }
            });
        } else {
            siginInByDB(userName, password, progressDialog);
        }
    }

    private void siginInByDB(String userName, String password,
            CommonProgressDialog progressDialog) {
        UserDao userDao = FlightApplication.getDaoSession().getUserDao();
        List<User> list = userDao.queryBuilder()
                .where(UserDao.Properties.UserCode.eq(userName),
                        UserDao.Properties.UserPassWord.eq(password)).list();
        progressDialog.dismiss();
        if (list != null && list.size() > 0) {
            mLayoutTop.setVisibility(View.GONE);
            //UserManager.getInstance().getSystemMessage();
            User userInfo = list.get(0);
            if(userInfo != null && "M".equalsIgnoreCase(userInfo.getGrant_S_M())) {
                nativeToMain(userInfo);
            } else {
                ToastUtil.showToast(getActivity(), R.drawable.toast_warning, "当前用户权限不足，请联系管理员");
            }
        } else {
            ToastUtil.showToast(getActivity(), R.drawable.toast_warning,
                    getString(R.string.local_user_not_exit));
        }
    }

    /**
     * 跳转到Home 页面
     * @param user
     */
    private void nativeToMain(User user) {
        UserManager.getInstance().setUserInfo(user);
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }

    @OnClick(R.id.layout_remember_password) public void onRememberPasswordClick() {
        //if (mCheckbox.isChecked()) {
        //    mCheckbox.setChecked(false);
        //} else {
        //    mCheckbox.setChecked(true);
        //}

        startActivityForResult(new Intent(getActivity(), ResetPasswordActivity.class), 5);

    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == 5) {
                ToastUtil.showToast(getActivity(), R.drawable.toast_confirm, "密码修改成功！");
            }
        }

    }
}
