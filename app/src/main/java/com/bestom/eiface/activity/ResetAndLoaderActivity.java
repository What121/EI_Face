package com.bestom.eiface.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;
import com.bestom.eiface.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.bestom.eiface.MyApp.mSysApi;


public class ResetAndLoaderActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "ResetAndRecoveryActivi";
    private Activity mActivity;
    private Context mContext;
    private  int type;
    private static final boolean DEBUG = true;

    private long lastClickTime=0;
    private boolean flag=false;
    private static long DownClickTime=0;
    private Unbinder mUnbinder;
    @BindView(R.id.button_ok)
    Button btn_ok;
    @BindView(R.id.button_cancel)
    Button btn_cancel;
    @BindView(R.id.notify)
    TextView txt;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.codeimg)
    ImageView codeimg;

    @SuppressLint("LongLogTag")
    private static void LOG(String msg) {
        if ( DEBUG ) {
            Log.d(TAG, msg);
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity=this;
        mContext = this;
        requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.activity_notify_dialog);
        mUnbinder= ButterKnife.bind(mActivity);
		setFinishOnTouchOutside(false);
        getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
                android.R.drawable.ic_dialog_alert);

        type = getIntent().getIntExtra("type",0);

        Log.d(TAG, "onCreate: type"+type);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStart() {
        super.onStart();
        if(type==0){
            //reset
            txt.setText(getString(R.string.reset_title));
            txt.setTextColor(getColor(R.color.yellow));
            description.setText(getString(R.string.reset_info));
        }else  if (type==1){
            //recovery
            txt.setText(getString(R.string.loader_title));
            txt.setTextColor(getColor(R.color.red));
            description.setText(getString(R.string.loader_info));
        }
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_ok:
                    if (type==0){
                        //show code img
                        showCodeImg();
                    }else if (type==1){
                        //进入烧录模式
                        intoLoader();
                    }
                break;
            case R.id.button_cancel:
                    Gofinish();
                break;
            default:
                break;
        }
    }

    private void showCodeImg(){
        codeimg.setVisibility(View.VISIBLE);
        Log.d(TAG, "showCodeImg: ");
    }

    private void intoLoader(){
        mSysApi.Loader();
        Log.d(TAG, "intoLoader: ");
    }

    private void Gofinish(){
        this.finish();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode()==24) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (!flag) {
                    flag = true;
                    DownClickTime = System.currentTimeMillis();
                    Log.d(TAG, "dispatchKeyEvent: key down");
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                flag = false;
                long timeD = System.currentTimeMillis() - DownClickTime;
                Intent intent = new Intent(this, ResetAndLoaderActivity.class);
                Log.d(TAG, "dispatchKeyEvent: key up,timeD is " + timeD);
                if (timeD >= 1500) {
                    if (type == 0) {
                        showCodeImg();
                    } else if (type == 1) {
                        intoLoader();
                    }
                    Log.d(TAG, "long click: ");
                } else {
                    Gofinish();
                    Log.d(TAG, "gofinish: ");
                }
            }
        }
        return false;
    }

    @Override
	protected void onDestroy() {
		super.onDestroy();
        mUnbinder.unbind();
        LOG("onDestroy()");
	}



	@Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        LOG("onPause() : Entered.");
    }

}
