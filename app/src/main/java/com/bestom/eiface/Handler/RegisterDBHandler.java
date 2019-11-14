package com.bestom.eiface.Handler;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bestom.eiface.activity.RegisterDBActivity;

import java.lang.ref.WeakReference;

public class RegisterDBHandler extends Handler {
    private static final String TAG = "RegisterDBHandler";
    private final int INIT_DATA=111;

    private WeakReference<RegisterDBActivity>  mRegisterActivityWeakReference;

    public RegisterDBHandler(RegisterDBActivity registerDBActivity) {
        mRegisterActivityWeakReference = new WeakReference<>(registerDBActivity);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what){
            case INIT_DATA:
                mRegisterActivityWeakReference.get().supplementInitDB();
                Log.d(TAG, "handleMessage: Refresh DB to update");
                break;
            default:
                break;
        }
    }
}
