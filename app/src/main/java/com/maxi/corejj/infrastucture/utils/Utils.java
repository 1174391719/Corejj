package com.maxi.corejj.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewOutlineProvider;

import com.maxi.corejj.infrastucture.utils.L;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CUtils {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setViewCornerRadius(View view, final int radius) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP || view == null || radius <= 0) {
            return;
        }
        view.setOutlineProvider(new ViewOutlineProvider() {

            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
            }
        });
        view.setClipToOutline(true);
    }

    public static void saveBitmap2Gallery(Context context, Bitmap bmp, String picName) {

        String fileName = null;
        //系统相册目录
        String galleryPath = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                + File.separator + "Camera" + File.separator;
        // 声明文件对象
        File file = null;
        // 声明输出流
        FileOutputStream outStream = null;
        try {
            // 如果有目标文件，直接获得文件对象，否则创建一个以filename为名称的文件
            file = new File(galleryPath, picName + ".png");
            // 获得文件相对路径
            fileName = file.toString();
            // 获得输出流，如果文件中有内容，追加内容
            outStream = new FileOutputStream(fileName);
            if (null != outStream) {
                bmp.compress(Bitmap.CompressFormat.PNG, 90, outStream);
            }
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp, fileName, null);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    public static Bitmap viewToBitmap(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    public static CTime getTime(String time) {
        CTime cTime = new CTime();
        try {

            String[] startTimeArr = time.split(" ");
            String[] startTimeArrTemp1 = startTimeArr[0].split("-");
            String[] startTimeArrTemp2 = startTimeArr[1].split(":");
            cTime.year = startTimeArrTemp1[0];
            cTime.month = startTimeArrTemp1[1];
            cTime.day = startTimeArrTemp1[2];
            cTime.h = startTimeArrTemp2[0];
            cTime.m = startTimeArrTemp2[1];
            cTime.s = startTimeArrTemp2[2];

        } catch (Exception e) {
            L.e(e);
        }
        return cTime;
    }

    public static boolean isSelected(int value, int index) {
        switch (value) {
            case 1:
                return index == 0;
            case 2:
                return index == 1;
            case 3:
                return index == 0 || index == 1;
            case 4:
                return index == 2;
            case 5:
                return index == 0 || index == 2;
            case 6:
                return index == 1 || index == 2;
            case 7:
                return index == 0 || index == 1 || index == 2;
            default:
                break;
        }
        return false;
    }

    public static class CTime {
        public String year;
        public String month;
        public String day;
        public String h;
        public String m;
        public String s;
    }
}
