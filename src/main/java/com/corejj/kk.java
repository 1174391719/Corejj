package com.corejj;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

public class kk {
    /**
     * 下载新版本
     *
     * @param context
     * @param url
     */
    public static void downLoadAPK(Context context, String url) {

        if (TextUtils.isEmpty(url)) {
            return;
        }

        try {
            String serviceString = Context.DOWNLOAD_SERVICE;
            final DownloadManager downloadManager = (DownloadManager) context.getSystemService(serviceString);

            Uri uri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.allowScanningByMediaScanner();
            request.setVisibleInDownloadsUi(true);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setMimeType("application/vnd.android.package-archive");

            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/juyoubang/", "juyoubang.apk");
            if (file.exists()) {
                file.delete();
            }
            request.setDestinationInExternalPublicDir(Environment.getExternalStorageDirectory().getAbsolutePath() + "/juyoubang/", "juyoubang.apk");
            long refernece = downloadManager.enqueue(request);
           // SharePreHelper.getIns().setLongData("refernece", refernece);
        } catch (Exception exception) {
          //  ToastUtils.init(context).show("更新失败");
        }

    }
}
