package com.corejj.view.pinnedRecycleView;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.corejj.callback.IntCallback;
import com.corejj.utils.ZLog;

public class PinnedItemDecoration extends RecyclerView.ItemDecoration {

    private View mPinnedView;
    private int mPinnedItemPosition = 0;
    private boolean mHasListener = false;
    private PinnedAdapter mAdapter;

    private IntCallback mOnPinnedViewClick = new IntCallback() {
        @Override
        public void onResponse(int arg) {
            mAdapter.onPinnedViewClick(mPinnedItemPosition);
        }
    };

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        if (parent == null) {
            return;
        }
        if (!mHasListener) {
            ((PinnedRecycleView) parent).setPinnedViewOnClickListener(mOnPinnedViewClick);
            mAdapter = (PinnedAdapter) parent.getAdapter();
            mHasListener = false;
        }

        RecyclerView.Adapter adapter = parent.getAdapter();
        int topMargin = 0;
        int nextTitleViewMarginTop = 0;
        for (int index = 1; index < parent.getChildCount(); index++) {
            int type = adapter.getItemViewType(parent.getChildAdapterPosition(parent.getChildAt(index)));
            if (type == 0) {
                nextTitleViewMarginTop = parent.getChildAt(index).getTop();
                if (nextTitleViewMarginTop < parent.getChildAt(index).getMeasuredHeight()) {
                    topMargin = nextTitleViewMarginTop - parent.getChildAt(index).getMeasuredHeight();
                }
                break;
            } else {
                topMargin = 0;
            }
        }

        mPinnedView = getPinnedView(parent);

        ((PinnedRecycleView) parent).setHasPinnedTitle(mPinnedView == null ? false : true);
        if (mPinnedView != null) {
            ((PinnedRecycleView) parent).setHasPinnedTitle(true);
            ((PinnedRecycleView) parent).setPinnedViewHeight(mPinnedView.getMeasuredHeight());
            int saveCount = c.save();
            c.translate(0, topMargin);
            mPinnedView.draw(c);
            c.restoreToCount(saveCount);
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
    }

    private View getPinnedView(RecyclerView parent) {
        RecyclerView.Adapter adapter = parent.getAdapter();
        View topView = parent.getChildAt(0);
        int topViewPosition = parent.getChildAdapterPosition(topView);
        for (int i = topViewPosition; i >= 0; i--) {
            if (adapter.getItemViewType(i) == 0) {
                mPinnedItemPosition = i;
                RecyclerView.ViewHolder pinnedViewHolder = adapter.onCreateViewHolder(parent, 0);
                adapter.onBindViewHolder(pinnedViewHolder, i);
                ensurePinnedHeaderViewLayout(pinnedViewHolder.itemView, parent);
                return pinnedViewHolder.itemView;
            }
        }
        return null;
    }

    private void ensurePinnedHeaderViewLayout(View pinView, RecyclerView recyclerView) {
        if (pinView.isLayoutRequested()) {
            /**
             * 用的是RecyclerView的宽度测量，和RecyclerView的宽度一样
             */
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) pinView.getLayoutParams();
            if (layoutParams == null) {
                throw new NullPointerException("PinnedHeaderItemDecoration");
            }
            int widthSpec = View.MeasureSpec.makeMeasureSpec(
                    recyclerView.getMeasuredWidth() - layoutParams.leftMargin - layoutParams.rightMargin, View.MeasureSpec.EXACTLY);

            int heightSpec;
            if (layoutParams.height > 0) {
                heightSpec = View.MeasureSpec.makeMeasureSpec(layoutParams.height, View.MeasureSpec.EXACTLY);
            } else {
                heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            }
            pinView.measure(widthSpec, heightSpec);
            pinView.layout(0, 0, pinView.getMeasuredWidth(), pinView.getMeasuredHeight());
        }
    }
}
