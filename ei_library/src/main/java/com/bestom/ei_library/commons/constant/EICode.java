package com.bestom.ei_library.commons.constant;

public enum EICode {

    DB_SUCCESS(0, "注册成功"),
    DB_ERROR(108, "注册失败"),
    DB_ERROR_RECORDID(109, "注册失败，算法返回recordid异常"),
    DB_ERROR_ID(110, "注册失败，ID已注册");

    private final int code;

    private final String msg;

    EICode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
