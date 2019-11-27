package com.bestom.ei_library.commons.listener;

public interface RespSampleListener<T> {

    void onSuccess(int code, T t);

    void onFailure(int code, String errMsg);
}
