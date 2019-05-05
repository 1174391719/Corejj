package com.zyzxsp.utils;

public class XyErrorMessageUtils {
    private String NOT_LOGGED_IN = "NOT_LOGGED_IN";//	系统未注册
    private String PEER_NOT_FOUND = "PEER_NOT_FOUND";//	对方不在线
    private String PEER_DEVICE_NOT_FOUND ="PEER_DEVICE_NOT_FOUND";//	对方不在线
    private String TIME_OUT ="TIME_OUT";//	对方没有应答
    private String BUSY = "BUSY";//	对方忙
    private String INVALID_NEMONO ="INVALID_NEMONO";//无效呼叫号码
    private String INVALID_NUMBER ="INVALID_NUMBER";//无效呼叫号码
    private String INVALID_PASSWORD	="INVALID_PASSWORD";//密码错误
    private String INVALID_SESSION ="INVALID SESSION"	;//呼叫无效
    private String LOCAL_NET_DISCONNECT ="LOCAL_NET_DISCONNECT"	;//本地网络异常
    private String SIGNAL_TIMEOUT ="SIGNAL_TIMEOUT"	;//本地网络异常
    private String MEDIA_TIMEOUT ="MEDIA_TIMEOUT"	;//本地网络异常
    private String PEER_NET_DISCONNECT ="PEER_NET_DISCONNECT"	;//对方网络中断
    private String NOT_ALLOW_NEMO_CALL ="NOT_ALLOW_NEMO_CALL"	;//对方不允许陌生人呼叫
    private String USER_NOT_ALLOWED ="USER_NOT_ALLOWED"	;//对方不允许拨打
    private String TEL_REACH_MAXTIME ="TEL_REACH_MAXTIME"	;//已达最大呼叫时长
    private String CONFMGMT_KICKOUT ="CONFMGMT_KICKOUT"	;//您已被管理员请出会议
    private String CONFMGMT_CONFOVER ="CONFMGMT_CONFOVER"	;//主持人已结束会议
    private String MEETING_LOCKED ="MEETING_LOCKED"	;//会议已被锁定，无法加入，请联系会议发起人解锁后进入
    private String SDP_INVALID ="SDP_INVALID"	;//通讯失败，请升级到最新版本
    private String VERSION_STALE_LOCAL ="VERSION_STALE_LOCAL"	;//版本过低，请升级后再试
    private String VERSION_STALE_REMOTE ="VERSION_STALE_REMOTE"	;//对方版本过低，通话无法接通
    private String UNSUPPORT_CALL ="UNSUPPORT_CALL"	;//您的终端目前无法支持此类呼叫
    private String STREAM_RESOURCE_UNAVAILABLE ="STREAM_RESOURCE_UNAVAILABLE"	;//直播服务暂时不可用
    private String NO_UDP_PACKAGE ="NO_UDP_PACKAGE"	;//当前网络UDP被禁用
    private String APP_SESSION_EXCEED ="APP_SESSION_EXCEED"	;//您呼叫的会议已达最大支持人数
    private String CONF_SESSION_EXCEED ="CONF_SESSION_EXCEED"	;//您呼叫的会议已达最大支持人数
    private String OFFICE_NEMO_SESSION_EXCEED ="OFFICE_NEMO_SESSION_EXCEED"	;//您呼叫的会议已达最大支持人数
    private String NORMAL_CONF_SESSION_EXCEED ="NORMAL_CONF_SESSION_EXCEED"	;//您呼叫的会议已达最大支持人数
    private String LARGE_CONF_SESSION_EXCEED ="LARGE_CONF_SESSION_EXCEED"	;//您呼叫的会议已达最大支持人数
    private String NORMAL_OFFICE_NEMO_SESSION_EXCEED ="NORMAL_OFFICE_NEMO_SESSION_EXCEED"	;//您呼叫的会议已达最大支持人数
    private String NEN_NORMAL_CONF_SESSION_EXCEED ="NEN_NORMAL_CONF_SESSION_EXCEED"	;//您呼叫的会议已达最大支持人数
    private String EN_CONF_SESSION_EXCEED_LOW_BALANCE ="EN_CONF_SESSION_EXCEED_LOW_BALANCE"	;//企业会议容量使用现在达到上限，无法加入会议。请联系管理员购买更多会议端口
    private String SERVICE_EXPIRED ="SERVICE_EXPIRED"	;//服务已过期，请企业管理员更新服务许可证
    private String SERVICE_REACH_MAX_COUNT ="SERVICE_REACH_MAX_COUNT"	;//当前在线呼叫数已达服务上限
    private String ALREADY_IN_CALL ="ALREADY_IN_CALL"	;//已经在通话中
    private String SERVER_DISCONNECTED = "SERVER_DISCONNECTED";//	与服务器连接断开
    //其他	呼叫失败，请稍后重试
    public String getErrorString(String errorType){
        String returnStr = "";
        if(NOT_LOGGED_IN.equals(errorType)){
            returnStr = "系统未注册";
        }else if(PEER_NOT_FOUND.equals(errorType) || PEER_DEVICE_NOT_FOUND.equals(errorType)){
            returnStr = "对方不在线";
        }else if(TIME_OUT.equals(errorType)){
            returnStr = "对方没有应答";
        }else if(BUSY.equals(errorType)){
            returnStr = "对方忙";
        }else if(INVALID_NEMONO.equals(errorType) || INVALID_NUMBER.equals(errorType)){
            returnStr = "无效呼叫号码";
        }else if(INVALID_PASSWORD.equals(errorType)){
            returnStr = "密码错误";
        }else if(INVALID_SESSION.equals(errorType)){
            returnStr = "呼叫无效";
        }else if(LOCAL_NET_DISCONNECT.equals(errorType) || SIGNAL_TIMEOUT.equals(errorType) || MEDIA_TIMEOUT.equals(errorType)){
            returnStr = "本地网络异常";
        }else if(PEER_NET_DISCONNECT.equals(errorType)){
            returnStr = "对方网络中断";
        }else if(NOT_ALLOW_NEMO_CALL.equals(errorType)){
            returnStr = "对方不允许陌生人呼叫";
        }else if(USER_NOT_ALLOWED.equals(errorType)){
            returnStr = "对方不允许拨打";
        }else if(TEL_REACH_MAXTIME.equals(errorType)){
            returnStr = "已达最大呼叫时长";
        }else if(CONFMGMT_KICKOUT.equals(errorType)){
            returnStr = "您已被管理员请出会议";
        }else if(CONFMGMT_CONFOVER.equals(errorType)){
            returnStr = "主持人已结束会议";
        }else if(MEETING_LOCKED.equals(errorType)){
            returnStr = "会议已被锁定，无法加入，请联系会议发起人解锁后进入";
        }else if(SDP_INVALID.equals(errorType)){
            returnStr = "通讯失败，请升级到最新版本";
        }else if(VERSION_STALE_LOCAL.equals(errorType)){
            returnStr = "版本过低，请升级后再试";
        }else if(VERSION_STALE_REMOTE.equals(errorType)){
            returnStr = "对方版本过低，通话无法接通";
        }else if(UNSUPPORT_CALL.equals(errorType)){
            returnStr = "您的终端目前无法支持此类呼叫";
        }else if(STREAM_RESOURCE_UNAVAILABLE.equals(errorType)){
            returnStr = "直播服务暂时不可用";
        }else if(NO_UDP_PACKAGE.equals(errorType)){
            returnStr = "当前网络UDP被禁用";
        }else if(APP_SESSION_EXCEED.equals(errorType) || CONF_SESSION_EXCEED.equals(errorType)
                || OFFICE_NEMO_SESSION_EXCEED.equals(errorType)|| NORMAL_CONF_SESSION_EXCEED.equals(errorType)
                || LARGE_CONF_SESSION_EXCEED.equals(errorType)|| NORMAL_OFFICE_NEMO_SESSION_EXCEED.equals(errorType)
                || NEN_NORMAL_CONF_SESSION_EXCEED.equals(errorType)){
            returnStr = "您呼叫的会议已达最大支持人数";
        }else if(EN_CONF_SESSION_EXCEED_LOW_BALANCE.equals(errorType)){
            returnStr = "企业会议容量使用现在达到上限，无法加入会议。请联系管理员购买更多会议端口";
        }else if(SERVICE_EXPIRED.equals(errorType)){
            returnStr = "服务已过期，请企业管理员更新服务许可证";
        }else if(SERVICE_REACH_MAX_COUNT.equals(errorType)){
            returnStr = "当前在线呼叫数已达服务上限";
        }else if(ALREADY_IN_CALL.equals(errorType)){
            returnStr = "已经在通话中";
        }else if(SERVER_DISCONNECTED.equals(errorType)){
            returnStr = "与服务器连接断开";
        }else{
            returnStr = "呼叫失败，请稍后重试";
        }
        return returnStr;
    }
}
