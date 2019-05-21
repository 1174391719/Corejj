package com.zyzxsp.presenter;

import com.zyzxsp.bean.RecentCallDataBean;

import java.util.List;

/**
 * Created by Administrator on 2019/05/18.
 */

public interface RecentCallPresenter {
    void addView(RecentCallView view);

    void removeView(RecentCallView view);

    void updateData();

    interface RecentCallView {
        void update(List<RecentCallDataBean> data);
    }
}
