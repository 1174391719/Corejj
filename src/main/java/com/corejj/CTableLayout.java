package com.corejj;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CTableLayout extends LinearLayout implements View.OnClickListener {
    private Context mContext;
    private List<View> mTabViews;
    private TextView mTabTextViewTemp;
    private View mTabIndicatorViewTemp;
    private OnItemSelectListener mListener;


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
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
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

    //**********************************************************************************************
    private void onTabSelected(View view) {
        mTabTextViewTemp = view.findViewById(R.id.tv_title);
        mTabIndicatorViewTemp = view.findViewById(R.id.v_indicator);
        mTabTextViewTemp.setTextColor(ContextCompat.getColor(mContext, R.color.common_black));
        mTabIndicatorViewTemp.setVisibility(VISIBLE);
    }

    private void onTabUnselected(View view) {
        mTabTextViewTemp = view.findViewById(R.id.tv_title);
        mTabIndicatorViewTemp = view.findViewById(R.id.v_indicator);
        mTabTextViewTemp.setTextColor(ContextCompat.getColor(mContext, R.color.common_base_text));
        mTabIndicatorViewTemp.setVisibility(INVISIBLE);
    }

    public interface OnItemSelectListener {
        void onItemSelected(int index);
    }
}
