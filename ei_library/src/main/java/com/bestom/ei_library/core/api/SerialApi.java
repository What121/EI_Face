package com.bestom.ei_library.core.api;

import com.bestom.ei_library.commons.constant.Const;
import com.bestom.ei_library.commons.constant.SerialCmdCode;
import com.bestom.ei_library.commons.constant.StatusCode;
import com.bestom.ei_library.commons.listener.ParseCallback;
import com.bestom.ei_library.commons.utils.DataTurn;
import com.bestom.ei_library.commons.listener.RespSampleListener;

public class SerialApi extends BaseApi {
    private static final String TAG = "SerialApi";
    private com.bestom.ei_library.commons.utils.DataTurn DataTurn =new DataTurn();

    //55 A5 04 D3 00 13 E4
    public void getRadarInfo(RespSampleListener<Integer> listener) {
        registerAndRemoveObserver(SerialCmdCode.SERIAL_CMD_INFO, Const.DEF_TIMEOUT, listener, new ParseCallback() {
            @Override
            public void result(String data) {
                listener.onSuccess(StatusCode.SUCCESS.getCode(), DataTurn.HexToInt(data));
            }
        });
    }



}
