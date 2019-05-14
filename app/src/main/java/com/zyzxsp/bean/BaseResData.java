package com.zyzxsp.bean;

import java.io.Serializable;

public class BaseResData implements Serializable {

    private String returnCode;//	结果编码
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
