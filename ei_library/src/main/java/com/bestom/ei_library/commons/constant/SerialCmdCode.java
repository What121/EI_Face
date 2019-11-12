package com.bestom.ei_library.commons.constant;

import com.bestom.ei_library.commons.utils.DataTurn;

public enum SerialCmdCode {

    SERIAL_CMD_STATUS(0xd1),
    SERIAL_CMD_BAUDRATE(0xd2),
    SERIAL_CMD_INFO(0xd3);

    private final Integer value;
    private com.bestom.ei_library.commons.utils.DataTurn DataTurn =new DataTurn();

    SerialCmdCode(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public String getHexStr() {
        return DataTurn.IntToHex(value);
    }
}
