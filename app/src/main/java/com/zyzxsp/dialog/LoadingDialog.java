package com.zyzxsp.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zyzxsp.R;

public class LoadingDialog {
    private static LoadingDialog mInstance;
    private AlertDialog mDialog;

    public static LoadingDialog getInstance(){
        if(mInstance == null){
            mInstance =  new LoadingDialog();
        }
        return mInstance;
    }

    /**
     *
     * @param context
     * @param notice 提示文字
     * @param flag 点击屏幕是否消失标记
     */
    public void showLoadingHasNotice(Context context,String notice,boolean flag){
        if(context == null){
            return;
        }
        createShowLoadingDialog(context,notice,flag);
    }

    /**
     *
     * @param context
     * @param flag 点击屏幕是否消失标记
     */
    public void showLoadingNoNotice(Context context,boolean flag){
        if(context == null){
            return;
        }
        createShowLoadingDialog(context,null,flag);
    }

    public void createShowLoadingDialog(Context context,String notice,boolean flag){
        if(context == null){
           return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading_layout,null);
        TextView textMesage = view.findViewById(R.id.loading_message);
        if(!TextUtils.isEmpty(notice)){
            textMesage.setVisibility(View.VISIBLE);
            textMesage.setText(notice);
        }else{
            textMesage.setVisibility(View.GONE);
        }
        builder.setView(view);
        mDialog = builder.create();
        if(flag){
            mDialog.setCanceledOnTouchOutside(true);
        }else{
            mDialog.setCanceledOnTouchOutside(false);
        }
        //去除dialog背景白色
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.show();
    }

    public void dismisLoading(){
        if(mDialog != null){
            mDialog.dismiss();
        }
    }

}
