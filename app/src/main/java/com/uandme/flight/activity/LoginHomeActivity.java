package com.uandme.flight.activity;

import android.app.Activity;
import android.os.Bundle;
import com.uandme.flight.R;
import com.uandme.flight.fragment.SignInFragment;

/**
 * Created by QingYang on 15/7/19.
 */
public class LoginHomeActivity extends Activity{

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginhome);

        SignInFragment signInFragment = new SignInFragment();
        signInFragment.setArguments(getIntent().getExtras());

        getFragmentManager().beginTransaction().add(R.id.container, signInFragment, "signin").commit();

    }

}
