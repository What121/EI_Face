package com.bestom.eiface.activity;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import com.bestom.ei_library.EIFace;
import com.bestom.ei_library.commons.utils.SPUtil;
import com.bestom.eiface.MyApp;
import com.bestom.eiface.R;
import com.bestom.ei_library.commons.constant.Settings;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
    private Context mContext ;
    private Activity mActivity;
    private Unbinder mUnbinder;

    @BindView(R.id.back) ImageView back_imgview;
    @BindView(R.id.shi_sw) Switch shi_sw;
    @BindView(R.id.ircamera_sw) Switch ir_sw;
    @BindView(R.id.relay_sw) Switch relay_sw;
    @BindView(R.id.led_sw) Switch led_sw;
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

        initview();
    }

    private void initview(){
        back_imgview.setOnClickListener(view -> finish());

        //IR框状态
        boolean irflag = SPUtil.getValue(mContext,Settings.FACE_IR,false);
        ir_sw.setChecked(irflag);
        ir_sw.setOnCheckedChangeListener((buttonView, isChecked)
                -> SPUtil.putValue(mContext, Settings.FACE_IR,isChecked));
        //打开/关闭人脸实时检测
        shi_sw.setChecked(MyApp.face_state);
        shi_sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            MyApp.face_state = isChecked;
            SPUtil.putValue(mContext, Settings.FACE_STATE,isChecked);
            Log.i(TAG,"face_state : "+MyApp.face_state);
        });

        //打开/关闭继电器
        relay_sw.setOnCheckedChangeListener((compoundButton, b)
                -> MyApp.mSysApi.writeRelay(b?"1":"0"));
        //打开/关闭LED
        led_sw.setOnCheckedChangeListener((compoundButton, b)
                -> MyApp.mSysApi.writeLed(b?"1":"0"));
        //识别次数
        passTimes_sp.setSelection(MyApp.face_times-1,true);
        passTimes_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MyApp.face_times = Integer.valueOf(parent.getItemAtPosition(position).toString());
                SPUtil.putValue(mContext,Settings.FACE_TIMES,MyApp.face_times);
                Log.i(TAG,"face_times : "+MyApp.face_times);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //识别阈值
        float Threshold = EIFace.GetRecognitionThreshold();
        int keyPosition = (int) Math.floor(Threshold/10);
        passKey_sp.setSelection(keyPosition-1,true);
        Log.d(TAG, "GetRecognitionThreshold "+EIFace.GetRecognitionThreshold());
        passKey_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                float val = Float.valueOf(adapterView.getItemAtPosition(position).toString())*100 ;
                EIFace.SetRecognitionThreshold(val);
                Log.i(TAG,"Threshold : "+val);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //人脸模型
        int FaceModel = EIFace.GetMinFaceDetectionSizePercent();
        int modelPosition = (int) Math.floor(FaceModel/10);
        faceModel_sp.setSelection(modelPosition-1,true);
        faceModel_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                int val = Integer.valueOf(adapterView.getItemAtPosition(position).toString()) ;
                EIFace.SetMinFaceDetectionSizePercent(val);
                Log.i(TAG,"MinFaceDetectionSizePercent : "+val);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
