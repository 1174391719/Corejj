package com.zyzxsp.data;

import java.io.Serializable;

public class BaseResData implements Serializable {
    /*{ 410001:缺少参数,
            0:成功，
        -999:系统异常}*/
    private String returnCode;//	结果编码

    /*缺少参数、系统异常*/
    private String returnMessage;//		结果描述

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }
}
