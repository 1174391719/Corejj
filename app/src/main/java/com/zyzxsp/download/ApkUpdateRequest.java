package com.zyzxsp.download;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.zyzxsp.constant.ConstantUrl;
import com.zyzxsp.bean.UpdateApkData;
import com.zyzxsp.bean.UpdateBean;
import com.zyzxsp.dialog.UpdateApkDialog;
import com.zyzxsp.utils.ZLog;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import zxsp.com.netlibrary.CallBackUtil;
import zxsp.com.netlibrary.OkhttpUtil;

public class ApkUpdateRequest {
    public static final String TAG = "ApkUpdateRequest";

    /**
     * 请求是否升级apk
     */
    public static void requestUpdateApk(final Context context) {
        String url = ConstantUrl.headerUrl + ConstantUrl.appUpdateUrl;
        ZLog.d(TAG, "requestUpdateApk: 升级 apk url  " + url);
        int versioncode = PackageInfoUtil.getLocalVersion(context);
        JSONObject object = new JSONObject();
        String objectStr = "";
        try {
            object.put("version", String.valueOf(versioncode));
            objectStr = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ZLog.d(TAG, "requestUpdateApk:  版本号是 " + versioncode + "请求参数是  " + objectStr);
        OkhttpUtil.okHttpPostJson(url, objectStr, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                Call mcall = call;
                ZLog.d(TAG, "onFailure:  " + e.toString());

            }

            @Override
            public void onResponse(String response) {
                ZLog.d(TAG, "onResponse:  " + response);
                if (response == null) {
                    return;
                }
                Gson json = new Gson();
                UpdateBean dataBean = json.fromJson(response, UpdateBean.class);
                if ("0".equals(dataBean.getReturnCode())) {
                    UpdateApkData mUpdateApkData = dataBean.getObject();
                    if (!mUpdateApkData.isLatestFlag()) {
                        //不是是最新版本
                        DownLoadManagerSingleton mDownLoadManagerSingleton = DownLoadManagerSingleton.getSingleton();
                        UpdateApkDialog mUpdateApkDialogUtils = UpdateApkDialog.getInstance();
                        mUpdateApkDialogUtils.showUpdataApkDialog(context, mDownLoadManagerSingleton, mUpdateApkData);
                    } else {
                        ZLog.d(TAG, "onResponse. It's latest version. ");
                    }

                }
            }
        });
    }
}
