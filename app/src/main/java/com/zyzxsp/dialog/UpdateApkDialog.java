package com.zyzxsp.dialog;


import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.zyzxsp.R;
import com.zyzxsp.bean.UpdateApkData;
import com.zyzxsp.download.DownLoadManagerSingleton;

public class UpdateApkDialog {
    public static UpdateApkDialog instance;
    public static UpdateApkDialog getInstance(){
        if(instance == null){
            instance = new UpdateApkDialog();
        }
        return instance;

    }

    public void showUpdataApkDialog(final Context context, final DownLoadManagerSingleton mDownLoadManagerSingleton, final UpdateApkData dataBean){
        if(context == null){
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_updateapk_layout,null);
        LinearLayout cancleBtn = view.findViewById(R.id.update_apk_cancle);
        LinearLayout confirmBtn = view.findViewById(R.id.update_apk_confirm);
//        TextView messageText = view.findViewById(R.id.update_message);

        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(R.color.colorTransparent);
        if(dataBean != null){
            if(!TextUtils.isEmpty(dataBean.getMessage())){
//                messageText.setText(dataBean.getMessage());
            }
            if(dataBean.isUpgradeFlag()){
                //强制升级
                cancleBtn.setVisibility(View.GONE);
            } else {
                //非强制升级
                cancleBtn.setVisibility(View.VISIBLE);
            }
        }
        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mDownLoadManagerSingleton != null){
                    mDownLoadManagerSingleton.unregisterReceiverDestory(context);
                }
                dialog.dismiss();
            }
        });
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDownLoadManagerSingleton.downLoadPackage(context,dataBean);
                if(dataBean.isUpgradeFlag()){
                    //强制升级

                } else {
                    //非强制升级
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

}
