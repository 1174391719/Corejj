package com.zyzxsp.presenter;

import com.zyzxsp.bean.RecentCallDataBean;
import com.zyzxsp.utils.ZLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/05/18.
 */

public class RecentCallPresenterImpl implements RecentCallPresenter {
    private List<RecentCallDataBean> mData = null;
    private List<RecentCallView> mViews = null;

    public RecentCallPresenterImpl() {
        ZLog.d("Init...");
        mData = new ArrayList<>();
        mViews = new ArrayList<>();

        RecentCallDataBean bean = new RecentCallDataBean();
        bean.setName("sff");
        bean.setTime("5655");
        mData.add(bean);

    }

    @Override
    public void removeView(RecentCallView view) {
        mViews.remove(view);
    }

    @Override
    public void addView(RecentCallView view) {
        mViews.add(view);
    }

    @Override
    public void updateData() {
        ZLog.d("");
        for (RecentCallView view : mViews) {
            view.update(mData);
        }
    }
}
