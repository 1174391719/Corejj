package com.corejj.view.pinnedRecycleView;

public interface PinnedAdapter {
    void onPinnedViewClick(int position);

    boolean isPinnedView(int position);
}
