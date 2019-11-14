package com.bestom.ei_library.core.api;

import android.text.TextUtils;
import android.util.Log;

import com.bestom.ei_library.commons.constant.Const;
import com.bestom.ei_library.commons.constant.SerialCmdCode;
import com.bestom.ei_library.commons.constant.StatusCode;
import com.bestom.ei_library.commons.exceptions.SerialException;
import com.bestom.ei_library.commons.utils.DataTurn;
import com.bestom.ei_library.core.manager.Serial.SerialManager;
import com.bestom.ei_library.core.service.Interface.Listener.ParseCallback;
import com.bestom.ei_library.core.service.Interface.Listener.RespSampleListener;

public class SerialApi extends BaseApi {
    private static final String TAG = "SerialApi";
    private com.bestom.ei_library.commons.utils.DataTurn DataTurn =new DataTurn();

    public void setStatus(SerialCmdCode serialCmdCode, boolean flag, RespSampleListener listener) {
        if (flag){
            this.SCmds(serialCmdCode, "01", Const.DEF_TIMEOUT, listener);
        }else {
            this.SCmds(serialCmdCode, "00", Const.DEF_TIMEOUT, listener);
        }
    }

    public void getRadarInfo(SerialCmdCode serialCmdCode,RespSampleListener listener){
        this.SCmds(serialCmdCode,"", Const.DEF_TIMEOUT,listener);
    }

    private void SCmds(final SerialCmdCode serialCmdCode, final String body, long timeout, final RespSampleListener listener) {
        if (timeout < Const.DEF_TIMEOUT) {
            String errMsg = "timeout error，please value > 3000ms";
            listener.onFailure(StatusCode.PARAM_ERR.getCode(), errMsg);
            Log.e(TAG, errMsg);
            return;
        }
        //发送指令
        String cmd= jointCmd(serialCmdCode, body);
        Log.d(TAG, "SCmds: --> cmd：" + cmd);
        try {
            SerialManager.getInstance().sendHex(cmd);
        } catch (SerialException e) {
            listener.onFailure(StatusCode.SERIAL_ERR.getCode(), e.getMessage());
            Log.e(TAG, e.getMessage());
            return;
        }
        // 注册观察者
        registerAndRemoveObserver(serialCmdCode, timeout, listener, new ParseCallback() {
                    @Override
                    public void result(String data) {
                        if (!TextUtils.isEmpty(data)){
                            switch (serialCmdCode){
                                case SERIAL_CMD_STATUS:
                                    if (data.equals(body)){
                                        listener.onSuccess(StatusCode.SUCCESS.getCode(), StatusCode.SUCCESS.getMsg());
                                    }else {
                                        listener.onFailure(StatusCode.ERROR.getCode(), StatusCode.ERROR.getMsg());
                                    }
                                    break;
                                case SERIAL_CMD_BAUDRATE:
                                    listener.onSuccess(StatusCode.SUCCESS.getCode(), "no support deal");
                                    break;
                                case SERIAL_CMD_INFO:
                                    listener.onSuccess(StatusCode.SUCCESS.getCode(), DataTurn.HexToInt(data.substring(0,4)) );
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
        });

    }



}
