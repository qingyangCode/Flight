package com.xiaoqing.flight.activity;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.xiaoqing.flight.FlightApplication;
import com.xiaoqing.flight.R;
import com.xiaoqing.flight.data.dao.User;
import com.xiaoqing.flight.data.dao.UserDao;
import com.xiaoqing.flight.entity.ResetPasswordResponse;
import com.xiaoqing.flight.network.ResponseListner;
import com.xiaoqing.flight.util.CommonProgressDialog;
import com.xiaoqing.flight.util.Constants;
import com.xiaoqing.flight.util.DBManager;
import com.xiaoqing.flight.util.ToastUtil;
import com.xiaoqing.flight.util.UserManager;
import java.util.List;

/**
 * Created by QingYang on 15/9/20.
 */
public class ResetPasswordActivity extends BaseActivity{

    @InjectView(R.id.tv_old_pwd)
    EditText mEtOld;
    @InjectView(R.id.tv_new_pwd)
    EditText mEtNew;
    @InjectView(R.id.tv_new_pwd_confirm)
    EditText mEdConfirm;
    @InjectView(R.id.tv_userName)
    EditText mUserName;
    @InjectView(R.id.tv_top_bar_title)
    TextView mTitle;

    private boolean isCancel = false;


    @Override public int getContentView() {
        return R.layout.activity_resetpassword;
    }

    @Override protected void onloadData() {
        setTitle("修改密码");
        mTitle.setText("修改密码");


    }

    @OnClick(R.id.btn_resetPassword)
    public void onResetPwdClick() {
        final String userName = mUserName.getText().toString().trim();
        String oldPwd = mEtOld.getText().toString().trim();
        String newPwd = mEtNew.getText().toString().trim();
        final String confirmPwd = mEdConfirm.getText().toString().trim();

        if (TextUtils.isEmpty(userName)) {
            ToastUtil.showToast(mContext, R.drawable.toast_warning, "用户名不能为空");
            return;
        } else if (TextUtils.isEmpty(oldPwd)) {
            ToastUtil.showToast(mContext, R.drawable.toast_warning, "旧密码不能为空");
            return;
        } else if (TextUtils.isEmpty(newPwd)) {
            ToastUtil.showToast(mContext, R.drawable.toast_warning, "新密码不能为空");
            return;
        } else if (TextUtils.isEmpty(confirmPwd)) {
            ToastUtil.showToast(mContext, R.drawable.toast_warning, "确认密码不能为空");
            return;
        } else if (!newPwd.equals(confirmPwd)) {
            ToastUtil.showToast(mContext, R.drawable.toast_warning, "两次密码输入不一致，请重新输入");
            return;
        } else if (oldPwd.equals(confirmPwd)) {
            ToastUtil.showToast(mContext, R.drawable.toast_warning, "旧密码与新密码相同，请输入新密码");
            return;
        }

        final CommonProgressDialog progressDialog = new CommonProgressDialog(mContext);
        progressDialog.setTip("正在加载...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override public void onCancel(DialogInterface dialog) {
                isCancel = true;
            }
        });
        progressDialog.show();
        getMoccApi().changePassword(userName,oldPwd, confirmPwd, new ResponseListner<ResetPasswordResponse>() {
            @Override public void onResponse(ResetPasswordResponse response) {
                progressDialog.dismiss();
                if (isCancel) return;
                if (response != null && response.ResponseObject != null && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                    DBManager.getInstance().updateUserPassword(userName, confirmPwd);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    ToastUtil.showToast(mContext, R.drawable.toast_warning,
                            response.ResponseObject.ResponseErr);
                }
            }

            @Override public void onEmptyOrError(String message) {
                progressDialog.dismiss();
                if (isCancel) return;
                String toastMessage = "";
                if (!TextUtils.isEmpty(message)) {
                    toastMessage = message;
                } else {
                    toastMessage = getString(R.string.get_data_error);
                }
                ToastUtil.showToast(mContext, R.drawable.toast_warning, toastMessage);
            }
        });
    }


}
