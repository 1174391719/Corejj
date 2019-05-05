package com.zyzxsp.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.Signature;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.zyzxsp.data.UpdateApkData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownLoadManagerSingleton {
    public static String TAG = "DownLoadManagerSingleton";
    private Context mContext;
    private static volatile DownLoadManagerSingleton mSingleton;
    private List<DownLoadBroadCastReceiver> mRecevierList = new ArrayList<>();
    private DownloadManager mDownloadManager;
    private File mPackageFile;
    private String mMyPackagePath;
    private String mMd5FromServer;
    private String mSingFromServer;
//    public String APP_NAME = "app-debug.apk";
    public String APP_NAME;

    public static DownLoadManagerSingleton getSingleton() {
        if (mSingleton == null) {
            synchronized (DownLoadManagerSingleton.class) {
                if (mSingleton == null) {
                    mSingleton = new DownLoadManagerSingleton();
                }
            }
        }
        return mSingleton;
    }

    public void downLoadPackage(Context context,UpdateApkData mUpdateApkData) {
        if (context == null || mUpdateApkData == null) {
            return;
        }
        this.mContext = context;
        APP_NAME = context.getPackageName();
        Log.d(TAG, "downLoadPackage: APP_NAME  " + APP_NAME);
        this.mMd5FromServer = mUpdateApkData.getMd5();
        this.mSingFromServer = mUpdateApkData.getSignature();
        String downLoadUrl = mUpdateApkData.getAddress();

        Log.i("downLoad", "downLoadPackage");
        mDownloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
//        Uri uri = Uri.parse("http://192.168.1.103/app-release.apk");
        Uri uri = Uri.parse(downLoadUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        // 创建一个文件夹对象，赋值为外部存储器的目录
        String sdcardDirPath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();
        mMyPackagePath = sdcardDirPath + File.separator + APP_NAME;
        mPackageFile = new File(mMyPackagePath);
        Log.d(TAG, "downLoadPackage:  mPackageFile  "  +mPackageFile);
        if (mPackageFile.exists()) {
            boolean del = mPackageFile.delete();
            Log.d(TAG, "downloadApk: 删除已有的apk: " + del);
        }
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, APP_NAME);
        request.setTitle("下载最新版本App");
        request.setDescription("您正在下载最新版本的在线视频App");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType("application/vnd.android.package-archive");
        // 设置为可被媒体扫描器找到
        request.allowScanningByMediaScanner();
        // 设置为可见和可管理
        request.setVisibleInDownloadsUi(true);
        long mDownLoadId = mDownloadManager.enqueue(request);


        DownLoadBroadCastReceiver downLoadBroadCastReceiver = new DownLoadBroadCastReceiver(mDownLoadId);
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        context.registerReceiver(downLoadBroadCastReceiver, mIntentFilter);
        mRecevierList.add(downLoadBroadCastReceiver);


    }

    /**
     * 安装已存在的apk
     */
    private void installExsitApk(Context context, String path) {
        File file = new File(path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    //下载到本地后执行安装
    private void installAPK(Context context, long downloadId) {
        //获取下载文件的Uri
        Uri downloadFileUri = mDownloadManager.getUriForDownloadedFile(downloadId);
        if (downloadFileUri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            mContext.startActivity(intent);
            unregisterReceiver(context, downloadId);
        }
    }


    public void unregisterReceiver(Context context, long id) {
        for (int i = 0; i < mRecevierList.size(); i++) {
            if (mRecevierList.get(i).downloadId == id) {
                context.unregisterReceiver(mRecevierList.get(i));
            }
        }
    }

    public void unregisterReceiverDestory(Context context){
        if(mRecevierList != null && mRecevierList.size() > 0){
            for (int i = 0; i < mRecevierList.size(); i++) {
                context.unregisterReceiver(mRecevierList.get(i));
            }
        }

    }

    public class DownLoadBroadCastReceiver extends BroadcastReceiver {
        private long downloadId;
        private DownloadManager mDManagerRe;

        public DownLoadBroadCastReceiver(long id) {
            this.downloadId = id;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            intent.getAction();
            String serviceString = context.DOWNLOAD_SERVICE;
            mDManagerRe = (DownloadManager) context.getSystemService(serviceString);
            Log.d(TAG, "onReceive: 收到receive");
            checkStatus(context);
        }


        //检查下载状态
        private void checkStatus(Context context) {
            Log.d(TAG, "checkStatus");
            DownloadManager.Query query = new DownloadManager.Query();
            //通过下载的id查找
            query.setFilterById(downloadId);
            Cursor c = mDManagerRe.query(query);
            if (c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                Log.d(TAG, "checkStatus  status  " +status);
                switch (status) {
                    //下载暂停
                    case DownloadManager.STATUS_PAUSED:
                        break;
                    //下载延迟
                    case DownloadManager.STATUS_PENDING:
                        break;
                    //正在下载
                    case DownloadManager.STATUS_RUNNING:
                        break;
                    //下载完成
                    case DownloadManager.STATUS_SUCCESSFUL:
                        //下载完成安装APK
//                        installAPK(context,downloadId);
                        Log.d(TAG, "checkStatus  status  下载完成");
                        verificationPackage(context, mMd5FromServer, mSingFromServer, downloadId);
                        break;
                    //下载失败
                    case DownloadManager.STATUS_FAILED:
                        Toast.makeText(mContext, "下载失败", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            c.close();
        }

        /**
         * 验证下载包
         */
        private void verificationPackage(Context context, String md5FromServer, String singFromServer, long downloadId) {
            Log.d(TAG, "verificationPackage: md5FromServer  " + md5FromServer);
            Log.d(TAG, "verificationPackage: singFromServer  " + singFromServer);
            Log.d(TAG, "verificationPackage: mPackageFilePath  " + mMyPackagePath);
            File packageFile = new File(mMyPackagePath);
            String packageMd5 = PackageInfoUtil.getFileMD5(packageFile);
            Log.d(TAG, "verificationPackage: 下载的apk Md5 值 " + packageMd5);
            //已安装的pak签名
            String mInstallApkSign = PackageInfoUtil.getInstallApkSign(context);
            Log.d(TAG, "verificationPackage: 已安装的apk的签名   " + mInstallApkSign);
            //sdcard里下载的pak签名
//            String apkSdCardShaStr = PackageInfoUtil.getApkSignatureByFilePath(context, mMyPackagePath, PackageInfoUtil.SHA1);
            String apkSdCardShaStr = PackageInfoUtil.getApkSignatureByFilePathLZ(mMyPackagePath);
            Log.d(TAG, "verificationPackage: sdcard里下载的apk签名  " + apkSdCardShaStr);
            if (!TextUtils.isEmpty(packageMd5) && packageMd5.equalsIgnoreCase(md5FromServer)) {
                //包未被篡改
                if (!TextUtils.isEmpty(mInstallApkSign) && mInstallApkSign.equals(singFromServer) && mInstallApkSign.equals(apkSdCardShaStr)) {
                    //签名验证成功
                    installAPK(context, downloadId);
                }else{
                    Log.d(TAG, "verificationPackage: 签名值不一致");
                    PackageInfoUtil.deletDownLoadApk(packageFile);
                }
            } else {
                //包被篡改
                Log.d(TAG, "verificationPackage: MD5值不一致");
                PackageInfoUtil.deletDownLoadApk(packageFile);
            }
        }


    }


}
