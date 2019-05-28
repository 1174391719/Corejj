package com.zyzxsp.download;

import android.content.Context;

import com.google.gson.Gson;
import com.zyzxsp.constant.ConstantUrl;
import com.zyzxsp.bean.UpdateBean;
import com.zyzxsp.dialog.DialogPresenter;
import com.zyzxsp.dialog.DialogPresenterImpl;
import com.zyzxsp.utils.SharedPreferencesUtils;
import com.zyzxsp.utils.ZLog;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import zxsp.com.netlibrary.CallBackUtil;
import zxsp.com.netlibrary.OkhttpUtil;

public class UpdateVersion {
    public static final String TAG = "UpdateVersion";

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
                    final UpdateBean.Version version = dataBean.getObject().getVersion();
                    if (!dataBean.getObject().getVersion().isLatestFlag()) {
                        //不是是最新版本
                        DialogPresenterImpl.newInstance().choose(context, new DialogPresenter.Callback() {
                            @Override
                            public void onPositiveClick() {
                                DownLoadManagerSingleton.getSingleton().downLoadPackage(context, version);
                            }

                            @Override
                            public void onNegativeClick() {
                            }
                        }, "有新版可更新，是否更新", "取消", "确定");
                    } else {
                        ZLog.d("It's latest version. ");
                        DialogPresenterImpl.newInstance().confirm(context, null, "此版本为最新版本", "确定");
                    }
                }
            }
        });
    }

    public static void autoCheck(final Context context) {
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
                try {
                    UpdateBean dataBean = new Gson().fromJson(response, UpdateBean.class);
                    if ("0".equals(dataBean.getReturnCode())) {
                        final UpdateBean.Version version = dataBean.getObject().getVersion();
                        if (!version.isLatestFlag()) {
                            //不是是最新版
                            if (version.isUpgradeFlag()) {
                                //Force
                                DialogPresenterImpl.newInstance().confirm(context, new DialogPresenter.Callback() {
                                    @Override
                                    public void onPositiveClick() {
                                        DownLoadManagerSingleton.getSingleton().downLoadPackage(context, version);
                                    }

                                    @Override
                                    public void onNegativeClick() {
                                    }
                                }, "有新版本可更新，请下载", "确定");

                            } else if (SharedPreferencesUtils.getString(context.getApplicationContext(), version.getVersion()).equals("")) {
                                DialogPresenterImpl.newInstance().choose(context, new DialogPresenter.Callback() {
                                    @Override
                                    public void onPositiveClick() {
                                        DownLoadManagerSingleton.getSingleton().downLoadPackage(context, version);
                                    }

                                    @Override
                                    public void onNegativeClick() {
                                        SharedPreferencesUtils.putString(context, version.getVersion(), version.getVersion());
                                    }
                                }, "有新版本可更新，请下载", "取消", "确定");
                            }
                        }
                    }
                } catch (Exception e) {
                    ZLog.e(e.toString());
                }
            }
        });
    }
}
