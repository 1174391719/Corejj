package com.zyzxsp.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zyzxsp.R;

/**
 * Created by Administrator on 2019/05/09.
 */

public class DialogPresenterImpl implements DialogPresenter {
    @Override
    public void confirm(Context context, final Callback callback, String title, String bottomText) {
        if (context == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_confirm, null);
        TextView titleTextView = view.findViewById(R.id.dialog_confirm_title);
        LinearLayout positive = view.findViewById(R.id.dialog_confirm_positive);
        TextView positiveTextView = view.findViewById(R.id.dialog_confirm_positive_text);
        titleTextView.setText(title);
        positiveTextView.setText(bottomText);

        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(R.color.colorTransparent);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (callback != null) {
                    callback.onPositiveClick();
                }
            }
        });
        dialog.show();
    }

    @Override
    public void choose(Context context, final Callback callback, String title, String negativeStr, String positiveStr) {
        if (context == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_choose, null);
        TextView titleTextView = view.findViewById(R.id.dialog_choose_title);
        LinearLayout positive = view.findViewById(R.id.dialog_choose_positive);
        LinearLayout negative = view.findViewById(R.id.dialog_choose_negative);
        TextView positiveTextView = view.findViewById(R.id.dialog_choose_positive_text);
        TextView negativeTextView = view.findViewById(R.id.dialog_choose_negative_text);
        titleTextView.setText(title);
        positiveTextView.setText(positiveStr);
        negativeTextView.setText(negativeStr);

        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(R.color.colorTransparent);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (callback != null) {
                    callback.onPositiveClick();
                }
            }
        });
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (callback != null) {
                    callback.onNegativeClick();
                }
            }
        });
        dialog.show();
    }
}
