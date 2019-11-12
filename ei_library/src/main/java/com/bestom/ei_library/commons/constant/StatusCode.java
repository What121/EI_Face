package com.bestom.ei_library.commons.constant;

public enum StatusCode {

    SUCCESS(0, "成功"),
    ERROR(1, "失败"),
    CHECK_ERR(2, "校验码错误"),
    FORMAT_ERR(3, "数据格式错误"),
    TIMEOUT(4, "应答超时"),
    PARAM_ERR(5, "参数错误"),
    SERIAL_ERR(6, "串口错误"),
    OTHER_ERR(7, "未知错误");

    private final int code;

    private final String msg;

    StatusCode(int code, String msg) {
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
