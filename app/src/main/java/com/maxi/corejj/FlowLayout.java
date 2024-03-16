package com.maxi.corejj;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowLayout extends ViewGroup {

    /**
     * 储存所有的view 按行记录
     */
    private List<List<View>> mAllViews = new ArrayList<List<View>>();
    /**
     * 记录每一行的高度
     */
    private List<Integer> mLineHeight = new ArrayList<Integer>();
    private String TAG = "TAG";

    public FlowLayout(Context context, AttributeSet attrs,
                      int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 置空 view 容器 和 lineHeight 容器  重新赋值
        //因为OnMeasure方法会走两次，第一次是实例化这个对象的时候高度和宽度都是0
        //之后走了OnSizeChange()方法后 又走了一次OnMeasure，所以要把第一次加进去的数据清空。
        mAllViews.clear();
        mLineHeight.clear();
        //得到上级容器为其推荐的宽高和计算模式
        int specWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int specHeighMode = MeasureSpec.getMode(heightMeasureSpec);
        int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int specHeighSize = MeasureSpec.getSize(heightMeasureSpec);
        // 计算出所有的 child 的 宽和高
//      measureChildren(specWidthSize, specHeighSize);
        // 记录如果是 warp_content 是设置的宽和高
        int width = 0;
        int height = 0;
        // 得到子view的个数
        int cCount = getChildCount();
        /**
         * 记录每一行的宽度，width不断取最大宽度
         */
        int lineWidth = 0;
        /**
         * 每一行的高度，累加至height
         */
        int lineHeight = 0;

        // 存储每一行所有的childView
        List<View> lineViews = new ArrayList<View>();

        for (int i = 0; i < cCount; i++) {
            // 得到每个子View
            View child = getChildAt(i);
            // 测量每个子View的宽高
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            // 当前子view的lp
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            // 子view的宽和高
            int cWidth = 0;
            int cheight = 0;
            // 当前子 view 实际占的宽
            cWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            // 当前子View 实际占的高
            cheight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            lineHeight = cheight;
            // 需要换行
            if (lineWidth + cWidth > specWidthSize) {
                width = Math.max(lineWidth, cWidth);// 取最大值
                lineWidth = cWidth; // 开启新行的时候重新累加width
                // 开启新行时累加 height
//              lineHeight = cheight; // 记录下一行的高度
                mAllViews.add(lineViews);
                mLineHeight.add(cheight);
                lineViews = new ArrayList<>();
                // 换行的时候把该 view 放进 集合里
                lineViews.add(child);// 这个  view(child) 是下一行的第一个view
                height += cheight; //每个View高度是一样的，直接累加
                Log.e("需要换行", "hight--" + height);
                Log.e("onMeasure", "AllViews.size()  --  > " + mAllViews.size());
            } else {
                // 不需要换行
                lineWidth += cWidth;//
                Log.e("不需要换行", "hight--" + height);
                // 不需要换行时 把子View add 进集合
                lineViews.add(child);
            }

            if (i == cCount - 1) {
                // 如果是最后一个view
                width = Math.max(lineWidth, cWidth);
                height += cheight;
                Log.e("最后一个view", "hight--" + height);
            }
        }
        // 循环结束后 把最后一行内容add进集合中
        mLineHeight.add(lineHeight); // 记录最后一行
        mAllViews.add(lineViews);
        // MeasureSpec.EXACTLY 表示设置了精确的值
        // 如果 mode 是 MeasureSpec.EXACTLY 时候，则不是 warp_content 用计算来的值，否则则用上级布局分给它的值
        setMeasuredDimension(
                specWidthMode == MeasureSpec.EXACTLY ? specWidthSize : width,
                specHeighMode == MeasureSpec.EXACTLY ? specHeighSize : height
        );
        Log.e("onMeasure", "mAllViews.size() -- > " + mAllViews.size() + "   mLineHeight.size() -- > " + mLineHeight.size() + "Height -- > " + height);
    }

    /**
     * 所有childView的位置的布局
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 当前行的最大高度
        int lineHeight = 0;
        // 存储每一行所有的childView
        List<View> lineViews = new ArrayList<View>();
        int left = 0;
        int top = 0;
        // 得到总行数
        int lineNums = mAllViews.size();
        for (int i = 0; i < lineNums; i++) {
            // 每一行的所有的views
            lineViews = mAllViews.get(i);
            // 当前行的最大高度
            lineHeight = mLineHeight.get(i);

            Log.e("onLayout", "第" + i + "行 ：" + lineViews.size() + "-------lineHeight" + lineHeight);

            // 遍历当前行所有的View
            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

                //计算childView的left,top,right,bottom
                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();

                child.layout(lc, tc, rc, bc);

                left += child.getMeasuredWidth() + lp.rightMargin + lp.leftMargin;
            }
            left = 0;
            top += lineHeight;
        }
        Log.v("onLayout", "onLayout   mAllViews.size() -- > " + mAllViews.size() + "   mLineHeight.size() -- > " + mLineHeight.size());
    }


    /**
     * 这个一定要设置，否则会包强转错误
     * 设置它支持 marginLayoutParams
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {

        return new MarginLayoutParams(getContext(), attrs);
    }
}