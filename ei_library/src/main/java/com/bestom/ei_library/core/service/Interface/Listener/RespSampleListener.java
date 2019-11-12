package com.bestom.ei_library.core.service.Interface.Listener;

public interface RespSampleListener<T> {

    void onSuccess(int code, T t);

    void onFailure(int code, String errMsg);
}
