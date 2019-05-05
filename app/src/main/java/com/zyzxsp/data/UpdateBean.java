package com.zyzxsp.data;

import java.io.Serializable;
import java.util.List;

public class UpdateBean implements Serializable {

    /*{ 410001:缺少参数,
            0:成功，
        -999:系统异常}*/
    private String returnCode;//	结果编码

    /*缺少参数、系统异常*/
    private String returnMessage;//		结果描述

    private Object bean;//		无内容
    private List<Object> beans;//	无内容
    private UpdateApkData object;	//最新版本信息

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

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public List<Object> getBeans() {
        return beans;
    }

    public void setBeans(List<Object> beans) {
        this.beans = beans;
    }

    public UpdateApkData getObject() {
        return object;
    }

    public void setObject(UpdateApkData object) {
        this.object = object;
    }
}
