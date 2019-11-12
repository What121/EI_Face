package com.bestom.ei_library.Handler;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bestom.ei_library.core.service.SerialObservable;

public class SerialManagerHandler  extends Handler {
    private static final String TAG = "SerialManagerHandler";
    private static final int Replay_WHAT = 1;

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == Replay_WHAT) {
            Log.d("receiver", "SerialManagerHandler handleMessage: receive replay");
            // 设置到被观察者,通知更新
            SerialObservable.getInstance().setInfo(String.valueOf(msg.obj));
        }
    }


}
