package com.zyzxsp.bean;

import java.io.Serializable;

public class MeetingRoomBean extends BaseResData {
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
        MeetingRoomInfo yunConferenceInfo;

        public MeetingRoomInfo getYunConferenceInfo() {
            return yunConferenceInfo;
        }

        public void setYunConferenceInfo(MeetingRoomInfo yunConferenceInfo) {
            this.yunConferenceInfo = yunConferenceInfo;
        }
    }

    public class MeetingRoomInfo implements Serializable {
        private String password;
        private String conferenceLimt;
        private String conferenceName;
        private String conferenceAccount;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getConferenceLimt() {
            return conferenceLimt;
        }

        public void setConferenceLimt(String conferenceLimt) {
            this.conferenceLimt = conferenceLimt;
        }

        public String getConferenceName() {
            return conferenceName;
        }

        public void setConferenceName(String conferenceName) {
            this.conferenceName = conferenceName;
        }

        public String getConferenceAccount() {
            return conferenceAccount;
        }

        public void setConferenceAccount(String conferenceAccount) {
            this.conferenceAccount = conferenceAccount;
        }
    }
}
