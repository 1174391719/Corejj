package com.zyzxsp.bean;

import java.io.Serializable;

public class UserInfoResData extends BaseResData {
    private Object bean;
    private Object beans;
    private User object;

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

    public User getObject() {
        return object;
    }

    public void setObject(User object) {
        this.object = object;
    }

    public class User {
        UserInfo user;

        public UserInfo getUser() {
            return user;
        }

        public void setUser(UserInfo user) {
            this.user = user;
        }
    }

    public class UserInfo implements Serializable {
        private String name;//姓名
        private String phone;//手机号
        private String email;//邮箱
        private String avatarPath;

        public String getAvatarPath() {
            return avatarPath;
        }

        public void setAvatarPath(String avatarPath) {
            this.avatarPath = avatarPath;
        }

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
