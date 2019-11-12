package com.bestom.eiface.Handler;

import android.os.Handler;
import android.os.Message;

import com.bestom.eiface.activity.RegisterActivity;

public class RegisterHandler extends Handler {
    private RegisterActivity mRegisterActivity;

    public RegisterHandler(RegisterActivity registerActivity) {
        mRegisterActivity = registerActivity;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        byte[] clrFrame= (byte[]) msg.obj;
        switch (msg.what){
            case -1:

            case 0:

                mRegisterActivity.updateUI(msg.what,clrFrame);
                break;
            default:
                break;
        }
    }
}
