package com.maxi.corejj.infrastucture;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;

public class PackageInfoUtil {
    public static String TAG="DownLoadManagerSingleton";
    public final static String SHA1 = "SHA1";
    /**
     * 获取版本号
     *
     * @throws PackageManager.NameNotFoundException
     */
    public static String getVersionName(Context context) throws PackageManager.NameNotFoundException {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        String version = packInfo.versionName;
        return version;
    }

    /**
     * 获取app签名md5值
     */
    public String getAppSignMd5Str(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            String signStr = encryptionMD5(sign.toByteArray());
            return signStr;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * MD5加密
     * @param byteStr 需要加密的内容
     * @return 返回 byteStr的md5值
     */
    public static String encryptionMD5(byte[] byteStr) {
        MessageDigest messageDigest = null;
        StringBuffer md5StrBuff = new StringBuffer();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(byteStr);
            byte[] byteArray = messageDigest.digest();
//            return Base64.encodeToString(byteArray,Base64.NO_WRAP);
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5StrBuff.toString();
    }


    /**
     * 获取单个文件的MD5值！

     * @param file
     * @return
     */

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }


    public static String getInstallApkSign(Context context) {
        if (getInstallArchiveInfo(context) != null) {
            return encryptionMD5(getPublicKey(getInstallArchiveInfo(context)).getBytes());
        } else {
            return null;
        }
    }

    private static byte[] getInstallArchiveInfo(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> apps = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
        Iterator<PackageInfo> iter = apps.iterator();
        while (iter.hasNext()) {
            PackageInfo info = iter.next();
            String packageName = info.packageName;
            //按包名读取签名
            if (packageName.equals(context.getPackageName())) {
                return info.signatures[0].toByteArray();
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static PackageInfo getPackageArchiveInfo(String archiveFilePath, int flags){
        // Workaround for https://code.google.com/p/android/issues/detail?id=9151#c8
        try{
            Class packageParserClass = Class.forName("android.content.pm.PackageParser");
            Class[] innerClasses = packageParserClass.getDeclaredClasses();
            Class packageParserPackageClass = null;
            for (Class innerClass : innerClasses){
                if (0 == innerClass.getName().compareTo("android.content.pm.PackageParser$Package")){
                    packageParserPackageClass = innerClass;
                    break;
                }
            }
            Constructor packageParserConstructor = packageParserClass.getConstructor(
                    String.class);
            Method parsePackageMethod = packageParserClass.getDeclaredMethod(
                    "parsePackage", File.class, String.class, DisplayMetrics.class, int.class);
            Method collectCertificatesMethod = packageParserClass.getDeclaredMethod(
                    "collectCertificates", packageParserPackageClass, int.class);
            Method generatePackageInfoMethod = packageParserClass.getDeclaredMethod(
                    "generatePackageInfo", packageParserPackageClass, int[].class, int.class, long.class, long.class);
            packageParserConstructor.setAccessible(true);
            parsePackageMethod.setAccessible(true);
            collectCertificatesMethod.setAccessible(true);
            generatePackageInfoMethod.setAccessible(true);

            Object packageParser = packageParserConstructor.newInstance(archiveFilePath);

            DisplayMetrics metrics = new DisplayMetrics();
            metrics.setToDefaults();

            final File sourceFile = new File(archiveFilePath);

            Object pkg = parsePackageMethod.invoke(
                    packageParser,
                    sourceFile,
                    archiveFilePath,
                    metrics,
                    0);
            if (pkg == null){
                return null;
            }

            if ((flags & PackageManager.GET_SIGNATURES) != 0){
                collectCertificatesMethod.invoke(packageParser, pkg, 0);
            }

            return (PackageInfo)generatePackageInfoMethod.invoke(null, pkg, null, flags, 0, 0);
        }
        catch (Exception e)
        {
            Log.e("Signature Monitor",
                    "android.content.pm.PackageParser reflection failed: " + e.toString());
        }

        return null;
    }


    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static byte[] getPackageArchiveInfoLZ(String apkFile, int flags){

        //这个是与显示有关的, 里面涉及到一些像素显示等等, 我们使用默认的情况
        DisplayMetrics metrics = new DisplayMetrics();
        metrics.setToDefaults();
        Object pkgParserPkg = null;
        Class[] typeArgs = null;
        Object[] valueArgs = null;
        try {
            Class<?> packageParserClass = Class.forName("android.content.pm.PackageParser");
            Constructor<?> packageParserConstructor = null;
            Object packageParser = null;
            //由于SDK版本问题，这里需做适配，来生成不同的构造函数
            if (Build.VERSION.SDK_INT > 20) {
                //无参数 constructor
                packageParserConstructor = packageParserClass.getDeclaredConstructor();
                packageParser = packageParserConstructor.newInstance();
                packageParserConstructor.setAccessible(true);//允许访问
                typeArgs = new Class[2];
                typeArgs[0] = File.class;
                typeArgs[1] = int.class;
                Method pkgParser_parsePackageMtd = packageParserClass.getDeclaredMethod("parsePackage", typeArgs);
                pkgParser_parsePackageMtd.setAccessible(true);
                valueArgs = new Object[2];
                valueArgs[0] = new File(apkFile);
                valueArgs[1] = PackageManager.GET_SIGNATURES;
                pkgParserPkg = pkgParser_parsePackageMtd.invoke(packageParser, valueArgs);
            } else {
                //低版本有参数 constructor
                packageParserConstructor = packageParserClass.getDeclaredConstructor(String.class);
                Object[] fileArgs = { apkFile };
                packageParser = packageParserConstructor.newInstance(fileArgs);
                packageParserConstructor.setAccessible(true);//允许访问

                typeArgs = new Class[4];
                typeArgs[0] = File.class;
                typeArgs[1] = String.class;
                typeArgs[2] = DisplayMetrics.class;
                typeArgs[3] = int.class;

                Method pkgParser_parsePackageMtd = packageParserClass.getDeclaredMethod("parsePackage", typeArgs);
                pkgParser_parsePackageMtd.setAccessible(true);

                valueArgs = new Object[4];
                valueArgs[0] = new File(apkFile);
                valueArgs[1] = apkFile;
                valueArgs[2] = metrics;
                valueArgs[3] = PackageManager.GET_SIGNATURES;
                pkgParserPkg = pkgParser_parsePackageMtd.invoke(packageParser, valueArgs);
            }

            typeArgs = new Class[2];
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                typeArgs[0] = pkgParserPkg.getClass();
                typeArgs[1] = boolean.class;
            } else {
                typeArgs[0] = pkgParserPkg.getClass();
                typeArgs[1] = int.class;
            }
            Method pkgParser_collectCertificatesMtd = packageParserClass.getDeclaredMethod("collectCertificates", typeArgs);
            valueArgs = new Object[2];
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                valueArgs[0] = pkgParserPkg;
                valueArgs[1] = false;
            } else {
                valueArgs[0] = pkgParserPkg;
                valueArgs[1] = PackageManager.GET_SIGNATURES;
            }
            pkgParser_collectCertificatesMtd.invoke(packageParser, valueArgs);
            // 应用程序信息包, 这个公开的, 不过有些函数变量没公开

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                Field packageInfoSigFld = pkgParserPkg.getClass().getDeclaredField("mSigningDetails");
                Object signingDetail = packageInfoSigFld.get(pkgParserPkg);

                Field packageInfoFld = signingDetail.getClass().getDeclaredField("signatures");
                Signature[] info = (Signature[]) packageInfoFld.get(signingDetail);
                return info[0].toByteArray();
            } else {
                Field packageInfoFld = pkgParserPkg.getClass().getDeclaredField("mSignatures");
                Signature[] info = (Signature[]) packageInfoFld.get(pkgParserPkg);
                return info[0].toByteArray();
            }
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "getUnInstallApkSign: ClassNotFoundException e = " + e);
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "getPackageArchiveInfo: NoSuchMethodException e = " + e);
            e.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG, "getPackageArchiveInfo:Exception e = " + e);
            e.printStackTrace();
        }
        return null;
    }


    /**
     *
     * @param context
     * @param apkFile 文件的全路径信息（包括apk文件的名称），如果是无效的apk文件，返回值为null
     * @return
     */
    public static String getApkSignatureByFilePath(Context context, String apkFile,String type){
        String tmp = null;
        PackageInfo newInfo = getPackageArchiveInfo(apkFile, PackageManager.GET_ACTIVITIES | PackageManager.GET_SIGNATURES);
        if(newInfo != null){
            if(newInfo.signatures != null && newInfo.signatures.length >0){
//                return newInfo.signatures[0];
                for (Signature sig : newInfo.signatures) {
                    if (SHA1.equals(type)) {
                        tmp = getSignatureString(sig, SHA1);
                        break;
                    }
                }
                return tmp;
            }
        }
        return null;
    }

    /**
     * 获取相应的类型的字符串（把签名的byte[]信息转换成16进制）
     *
     * @param sig
     * @param type
     *
     * @return
     */
    public static String getSignatureString(Signature sig, String type) {
        byte[] hexBytes = sig.toByteArray();
        String fingerprint = "error!";
        try {
            MessageDigest digest = MessageDigest.getInstance(type);
            if (digest != null) {
                byte[] digestBytes = digest.digest(hexBytes);
                StringBuilder sb = new StringBuilder();
                for (byte digestByte : digestBytes) {
                    sb.append((Integer.toHexString((digestByte & 0xFF) | 0x100)).substring(1, 3));
                }
                fingerprint = sb.toString();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return fingerprint;
    }

    public static String getApkSignatureByFilePathLZ(String apkPath) {
        byte[] unstallSignature = getPackageArchiveInfoLZ(apkPath, PackageManager.GET_ACTIVITIES | PackageManager.GET_SIGNATURES);
        if (unstallSignature != null) {
            return encryptionMD5(getPublicKey(unstallSignature).getBytes());
        } else {
            return null;
        }
    }

    private static String getPublicKey(byte[] signature) {
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature));
            return cert.getPublicKey().toString();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void deletDownLoadApk(File file){
        if(file.exists()){
            file.delete();
        }
    }

    /**
     * 获取当前应用本地的版本号
     *
     * @param activity
     * @return 发生错误返回-1
     */
    public static int getLocalVersion(Context activity) {
        try {
            PackageManager manager = activity.getPackageManager();
            PackageInfo info = manager.getPackageInfo(activity.getPackageName(), 0);
            int versionCode = info.versionCode;
            return versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
