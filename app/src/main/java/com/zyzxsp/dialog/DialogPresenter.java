package com.zyzxsp.dialog;

import android.content.Context;

/**
 * Created by Administrator on 2019/05/09.
 */

public interface DialogPresenter {
    void confirm(Context context, Callback callback, String title, String bottomText);

    void choose(Context context, Callback callback, String title, String negativeText, String positiveText);

    interface Callback {
        void onPositiveClick();

        void onNegativeClick();
    }
}
