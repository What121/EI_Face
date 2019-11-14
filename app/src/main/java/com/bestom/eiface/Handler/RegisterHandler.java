package com.bestom.eiface.Handler;

import android.os.Handler;
import android.os.Message;

import com.bestom.eiface.activity.RegisterActivity;

import java.lang.ref.WeakReference;

public class RegisterHandler extends Handler {
    private WeakReference<RegisterActivity>  mRegisterActivityWeakReference;

    public RegisterHandler(RegisterActivity registerActivity) {
        mRegisterActivityWeakReference = new WeakReference<>(registerActivity);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        byte[] clrFrame= (byte[]) msg.obj;
        switch (msg.what){
            case 99:
                mRegisterActivityWeakReference.get().finish();
                break;
            default:
                mRegisterActivityWeakReference.get().updateUI(msg.what,clrFrame);
                break;
        }
    }
}
