package com.bestom.eiface.activity;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import com.bestom.eiface.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SettingsActivity extends AppCompatActivity {
    private Context mContext ;
    private Activity mActivity;
    private Unbinder mUnbinder;

    @BindView(R.id.back) ImageView back_imgview;
    @BindView(R.id.shi_sw) Switch shi_sw;
    @BindView(R.id.relay_sw) Switch relay_sw;
    @BindView(R.id.led_sw) Switch led_sw;
    @BindView(R.id.power_sw) Switch power_sw;
    @BindView(R.id.pass_NO) Spinner passTimes_sp;
    @BindView(R.id.face_key) Spinner passKey_sp;
    @BindView(R.id.face_model) Spinner faceModel_sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mContext=this;
        mActivity=this;
        mUnbinder = ButterKnife.bind(mActivity);



    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }



}
