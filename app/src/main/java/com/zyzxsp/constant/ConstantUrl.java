package com.zyzxsp.constant;

public class ConstantUrl {
    //上海
//    public static String headerUrl="http://192.168.0.200:20183";
    //icvoicetransfer url
    public static String icvoicetransferUrl = "http://192.168.0.200:10111";
    //icvoiceconvertUrl url
    public static String icvoiceconvertUrl = "http://192.168.0.200:20184";

    //mock数据测试
    public static String headerUrl="http://zd.test.com";

    // App版本更新
    public static String appUpdateUrl = "/icbase/version/checkAppVersion";
    // 退出登录
    public static String logoutUrl = "/vcclient/user/logout";
    // 登录
    public static String loginUrl = "/vcclient/user/login";
    // 用户个人信息
    public static String getUserUrl =  "/vcclient/user/getUser";
    // 确认旧密码是否正确
    public static String validatePassword = "/vcclient/user/validatePassword";
    //修改用户密码
    public static String modifyPassword = "/vcclient/user/comfirmPassword";

}
