package com.zyzxsp.download;

import android.content.Context;

import com.google.gson.Gson;
import com.zyzxsp.constant.ConstantUrl;
import com.zyzxsp.bean.UpdateBean;
import com.zyzxsp.dialog.DialogPresenter;
import com.zyzxsp.dialog.DialogPresenterImpl;
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
        String url = ConstantUrl.HOST + ConstantUrl.UPDATE_VERSION;
        int versioncode = PackageInfoUtil.getLocalVersion(context);
        JSONObject object = new JSONObject();
        String objectStr = "";
        try {
            object.put("version", String.valueOf(versioncode));
            object.put("platform", 0);
            objectStr = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ZLog.d("url:" + url + " versioncode:" + versioncode + " json:" + objectStr);
        OkhttpUtil.okHttpPostJson(url, objectStr, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                ZLog.e(e.toString());
            }

            @Override
            public void onResponse(String response) {
                ZLog.d(response);
                if (response == null) {
                    return;
                }
                UpdateBean dataBean = new Gson().fromJson(response, UpdateBean.class);
                if ("0".equals(dataBean.getReturnCode())) {
                    if (!dataBean.getObject().getVersion().isLatestFlag()) {
                        //不是是最新版本
                        DownLoadManagerSingleton mDownLoadManagerSingleton = DownLoadManagerSingleton.getSingleton();
                        UpdateApkDialog mUpdateApkDialogUtils = UpdateApkDialog.getInstance();
                        mUpdateApkDialogUtils.showUpdataApkDialog(context, mDownLoadManagerSingleton, dataBean.getObject().getVersion());
                    } else {
                        ZLog.d("It's latest version. ");
                        DialogPresenter presenter = new DialogPresenterImpl();
                        presenter.confirm(context, null, "此版本为最新版本", "确定");
                    }
                }
            }
        });
    }
}
