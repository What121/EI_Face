package com.bestom.ei_library.core.service.Interface.Listener;

public interface RespListener {

    void onSuccess(int code, String msg);

    void onFailure(int code, String errMsg);
}
