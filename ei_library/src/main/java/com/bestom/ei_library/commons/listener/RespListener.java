package com.bestom.ei_library.commons.listener;

public interface RespListener {

    void onSuccess(int code, String msg);

    void onFailure(int code, String errMsg);
}
