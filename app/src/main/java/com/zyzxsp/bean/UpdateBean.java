package com.zyzxsp.bean;

import java.io.Serializable;

public class UpdateBean extends BaseResData {
    private Object bean;
    private Object beans;
    private Data object;

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Object getBeans() {
        return beans;
    }

    public void setBeans(Object beans) {
        this.beans = beans;
    }

    public Data getObject() {
        return object;
    }

    public void setObject(Data object) {
        this.object = object;
    }

    public class Data {
        Version version;

        public Version getVersion() {
            return version;
        }

        public void setVersion(Version version) {
            this.version = version;
        }
    }

    public class Version implements Serializable {
        private String version;//	版本号
        private String address;    //版本地址
        private String message;//版本信息
        private String md5;    //APP包md5值
        private boolean upgradeFlag;//		是否强制升级
        private boolean latestFlag;//		是否最新版本
        private String signature;//apk签名

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public boolean isUpgradeFlag() {
            return upgradeFlag;
        }

        public void setUpgradeFlag(boolean upgradeFlag) {
            this.upgradeFlag = upgradeFlag;
        }

        public boolean isLatestFlag() {
            return latestFlag;
        }

        public void setLatestFlag(boolean latestFlag) {
            this.latestFlag = latestFlag;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }
    }
}
