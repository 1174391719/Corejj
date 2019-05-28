package com.zyzxsp.presenter;

import com.zyzxsp.constant.ConstantUrl;
import com.zyzxsp.utils.ZLog;

import java.util.HashMap;
import java.util.Map;

import zxsp.com.netlibrary.CallBackUtil;
import zxsp.com.netlibrary.OkhttpUtil;

/**
 * Created by Administrator on 2019/05/28.
 */

public class NetWorkPresenterImpl implements NetWorkPresenter {
    @Override
    public void getUserInfo(CallBackUtil.CallBackString callback) {
        if (MainPresenterImpl.getInstants().getUser() == null || MainPresenterImpl.getInstants().getUser().getToken() == null) {
            return;
        }
        String url = ConstantUrl.HOST + ConstantUrl.GET_USER_INFO;
        Map map = new HashMap();
        map.put("token", MainPresenterImpl.getInstants().getUser().getToken());
        ZLog.d("url:" + url);
        OkhttpUtil.okHttpPostJson(url, null, map, callback);
    }
}
