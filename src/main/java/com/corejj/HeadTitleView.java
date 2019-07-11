package com.corejj;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HeadTitleView extends ConstraintLayout {
    private OnItemClickListener mOnHeaderClick;
    private LinearLayout mLeftLinLayout;
    private ImageView mLeftImageView;
    private TextView mTitle;
    private LinearLayout mRightLinLayout;
    private TextView mRightTextView;

    public HeadTitleView(Context context) {
        this(context, null);
    }

    public HeadTitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeadTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_head_title, this);
        initView();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.HeadTitleView, defStyleAttr, 0);
        int count = array.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = array.getIndex(i);
            if (attr == R.styleable.HeadTitleView_title) {
                mTitle.setText(array.getString(attr));
            } else if (attr == R.styleable.HeadTitleView_rightText) {
                mRightTextView.setText(array.getString(attr));
            }
        }
        array.recycle();
        mLeftLinLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnHeaderClick != null) {
                    mOnHeaderClick.leftClick(v);
                }
            }
        });

        mRightLinLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnHeaderClick != null) {
                    mOnHeaderClick.rightClick(v);
                }
            }
        });
    }


    public void setOnItemClickListener(OnItemClickListener mOnHeaderTitleViewClick) {
        this.mOnHeaderClick = mOnHeaderTitleViewClick;
    }


    //**********************************************************************************************
    private void initView() {
        mLeftLinLayout = findViewById(R.id.title_left_layout);
        mLeftImageView = findViewById(R.id.image_left);
        mTitle = findViewById(R.id.title_text);
        mRightLinLayout = findViewById(R.id.title_right_layout);
        mRightTextView = findViewById(R.id.tv_right);
    }

    public interface OnItemClickListener {
        void leftClick(View view);

        void rightClick(View view);
    }
}
