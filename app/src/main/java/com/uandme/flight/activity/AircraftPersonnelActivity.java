package com.uandme.flight.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.InjectView;
import com.uandme.autoloadlistview.LoadingFooter;
import com.uandme.autoloadlistview.zlistview.ListViewAdapter;
import com.uandme.flight.R;
import com.uandme.flight.util.WindowUtil;
import com.uandme.flight.widget.AutoLoadListView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by QingYang on 15/7/29.
 * 飞机上的人员
 */
public class AircraftPersonnelActivity extends BaseActivity {

    //@InjectView(R.id.swipe_refresh)
    //SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.mListview)
    AutoLoadListView mAutoLoadListView;

    private ListViewAdapter listViewAdapter;

    private List<Pair<String, Boolean>> userNames;

    @Override public int getContentView() {
        return R.layout.activity_aircraftpersonnel;
    }

    @Override protected void onloadData() {
        getTopBarTitle("机上人员姓名");
        getTopBarRight("提交");
        //// 顶部刷新的样式
        //mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light,
        //        android.R.color.holo_green_light, android.R.color.holo_blue_bright,
        //        android.R.color.holo_orange_light);
        //mSwipeRefreshLayout.setRefreshing(false);
        userNames = new ArrayList<Pair<String, Boolean>>();
        userNames.add(new Pair<String, Boolean>("边振霄", false));
        userNames.add(new Pair<String, Boolean>("刘忠龙", true));
        userNames.add(new Pair<String, Boolean>("王立群", false));
        userNames.add(new Pair<String, Boolean>("王恺斌", true));
        userNames.add(new Pair<String, Boolean>("王东旭", true));


        //mSwipeRefreshLayout.setOnRefreshListener(this);
        listViewAdapter = new ListViewAdapter(AircraftPersonnelActivity.this);
        listViewAdapter.setData(userNames);
        mAutoLoadListView.setAdapter(listViewAdapter);
        View view = View.inflate(this, R.layout.item_bottom_add, null);
        mAutoLoadListView.addFooterView(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                showDialog();
            }
        });
    }

    public void showDialog() {

        final AlertDialog alBuilder = new AlertDialog.Builder(this).create();
        View view = LayoutInflater.from(this).inflate(R.layout.prompt_dialog,
                null);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        TextView title = (TextView) view.findViewById(R.id.title);
        //title.setText(titleText);
        Button done = (Button) view.findViewById(R.id.done);
        //done.setText(buttonText);
        final EditText textView2 = (EditText) view.findViewById(R.id.textView2);
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.iv_checkbox);
        //textView2.setText(messageText);
        // alBuilder.setView(view);
        Window window = alBuilder.getWindow();
        alBuilder.show();
        alBuilder.setContentView(view);
        done.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                alBuilder.dismiss();
                if(!TextUtils.isEmpty(textView2.getText().toString().trim())){
                    userNames.add(
                            new Pair<String, Boolean>(textView2.getText().toString(), checkBox.isChecked()));
                    listViewAdapter.notifyDataSetChanged();
                }
            }
        });
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int) (WindowUtil.initWindow().getScreenWidth(this) / 1.2f); // 设置宽度
        lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;//显示dialog的时候,就显示软键盘
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;//就是这个属性导致不能获取焦点,默认的是FLAG_NOT_FOCUSABLE,故名思义不能获取输入焦点,
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER);
    }

    @Override public View.OnClickListener getRightOnClickListener() {
        return new View.OnClickListener() {
            @Override public void onClick(View v) {

            }
        };
    }
}
