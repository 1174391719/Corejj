package com.maxi.corejj;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class CTableLayout extends LinearLayout implements View.OnClickListener {
    private Context mContext;

    private TextView mTabTextViewTemp;
    private View mTabIndicatorViewTemp;

    private List<View> mTabViews;
    private OnItemSelectListener mListener;

    private int mSelectedTextColor = R.color.common_black;
    private int mSelectedTextSize = R.dimen.base_text_size;
    private boolean mSelectedTextBold = false;

    private int mUnSelectedTextColor = R.color.common_base_text;
    private int mUnSelectedTextSize = R.dimen.base_text_size;
    private boolean mUnSelectedTextBold = false;

    public CTableLayout(Context context) {
        this(context, null);
    }

    public CTableLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CTableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mTabViews = new ArrayList<>();
    }

    public void setTitle(List<String> titles) {
        TextView textView;
        for (String s : titles) {
            final View tab = LayoutInflater.from(mContext).inflate(R.layout.item_table_layout, null);
            LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            lp.weight = 1;
            textView = tab.findViewById(R.id.tv_title);
            textView.setText(s);
            tab.setLayoutParams(lp);
            tab.setOnClickListener(this);
            mTabViews.add(tab);
            addView(tab);
        }
        onClick(mTabViews.get(0));
    }

    public void setOnItemSelectListener(OnItemSelectListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View view) {
        View v;
        for (int i = 0; i < mTabViews.size(); i++) {
            v = mTabViews.get(i);
            if (v == view) {
                onTabSelected(v);
                if (mListener != null) {
                    mListener.onItemSelected(i);
                }
            } else {
                onTabUnselected(v);
            }
        }
    }

    public void selectItem(int index) {
        onClick(mTabViews.get(index));
    }

    public void setSelectedTextColor(int color) {
        mSelectedTextColor = color;
    }

    public void setSelectedTextSize(int size) {
        mSelectedTextSize = size;
    }

    public void setSelectedTextBold(boolean bold) {
        mSelectedTextBold = bold;
    }

    public void setUnSelectedTextColor(int color) {
        mUnSelectedTextColor = color;
    }

    public void setUnSelectedTextSize(int size) {
        mUnSelectedTextSize = size;
    }

    public void setUnSelectedTextBold(boolean bold) {
        mUnSelectedTextBold = bold;
    }

    //**********************************************************************************************
    private void onTabSelected(View view) {
        mTabTextViewTemp = view.findViewById(R.id.tv_title);
        mTabIndicatorViewTemp = view.findViewById(R.id.v_indicator);
        mTabTextViewTemp.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(mSelectedTextSize));
        mTabTextViewTemp.setTextColor(ContextCompat.getColor(mContext, mSelectedTextColor));
        mTabTextViewTemp.setTypeface(mSelectedTextBold ? Typeface.defaultFromStyle(Typeface.BOLD) : Typeface.defaultFromStyle(Typeface.NORMAL));
        mTabIndicatorViewTemp.setVisibility(VISIBLE);
    }

    private void onTabUnselected(View view) {
        mTabTextViewTemp = view.findViewById(R.id.tv_title);
        mTabIndicatorViewTemp = view.findViewById(R.id.v_indicator);

        mTabTextViewTemp.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(mUnSelectedTextSize));
        mTabTextViewTemp.setTextColor(ContextCompat.getColor(mContext, mUnSelectedTextColor));
        mTabTextViewTemp.setTypeface(mUnSelectedTextBold ? Typeface.defaultFromStyle(Typeface.BOLD) : Typeface.defaultFromStyle(Typeface.NORMAL));
        mTabIndicatorViewTemp.setVisibility(INVISIBLE);
    }

    public interface OnItemSelectListener {
        void onItemSelected(int index);
    }
}
