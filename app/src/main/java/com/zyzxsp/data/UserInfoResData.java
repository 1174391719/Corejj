package com.zyzxsp.data;

import java.io.Serializable;

public class UserInfoResData extends BaseResData {
    private Object bean;
    private Object beans;
    private UserInfo object;

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

    public UserInfo getObject() {
        return object;
    }

    public void setObject(UserInfo object) {
        this.object = object;
    }

    public class UserInfo implements Serializable {
        private String name;//			姓名
        private String phone;//			手机号
        private String email;//				邮箱

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

}
