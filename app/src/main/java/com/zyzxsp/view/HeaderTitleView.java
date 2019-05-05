package com.zyzxsp.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zyzxsp.R;

public class HeaderTitleView extends ConstraintLayout {
    private onHeaderTitleViewClick mOnHeaderTitleViewClick;
    private LinearLayout mLeftLinLayout;
    private ImageView mLeftImageView;
    private TextView mTitle;
    private LinearLayout mRightLinLayout;
    private ImageView mRightImageView;

    public HeaderTitleView(Context context) {
        this(context, null);
    }

    public HeaderTitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_head_title_layout, this);
        initView();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.HeaderTitleView, defStyleAttr, 0);
        int count = array.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = array.getIndex(i);
            switch (attr) {
                case R.styleable.HeaderTitleView_headerLeftDrawble:
                    mLeftImageView.setImageResource(array.getResourceId(attr, 0));
                    break;
                case R.styleable.HeaderTitleView_headerCenterTextColor:
                    mTitle.setTextColor(array.getColor(attr, Color.BLACK));
                    break;
                case R.styleable.HeaderTitleView_headerCenterTitle:
                    mTitle.setText(array.getString(attr));
                    break;
                case R.styleable.HeaderTitleView_headerCenterTextSize:
                    mTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, array.getDimensionPixelSize(attr, 0));
                    break;
                case R.styleable.HeaderTitleView_headerRightDrawable:
                    mRightImageView.setImageResource(array.getResourceId(attr, 0));
                    break;
                case R.styleable.HeaderTitleView_headerLeftDrawbleVisibility:
                    String visibleLeft = array.getString(attr);
                    setVisibility(mLeftImageView,visibleLeft);
                    break;
                case R.styleable.HeaderTitleView_headerRightDrawableVisibility:
                    String visibleRight = array.getString(attr);
                    setVisibility(mLeftImageView,visibleRight);
                    break;
            }
        }
        array.recycle();
        mLeftLinLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnHeaderTitleViewClick != null) {
                    mOnHeaderTitleViewClick.leftClick(v);
                }
            }
        });

        mRightLinLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnHeaderTitleViewClick != null) {
                    mOnHeaderTitleViewClick.rightClick(v);
                }
            }
        });
    }

    private void initView() {
        mLeftLinLayout = findViewById(R.id.title_left_layout);
        mLeftImageView = findViewById(R.id.image_left);
        mTitle = findViewById(R.id.title_text);
        mRightLinLayout = findViewById(R.id.title_right_layout);
        mRightImageView = findViewById(R.id.image_right);
    }

    private void setVisibility(View view, String visiable) {
        visiable = TextUtils.isEmpty(visiable) ? "visible" : visiable;
        switch (visiable) {
            case "gone":
                view.setVisibility(View.GONE);
                break;
            case "visible":
                view.setVisibility(View.VISIBLE);
                break;
            case "invisible":
                view.setVisibility(View.INVISIBLE);
                break;
            default:
                view.setVisibility(View.VISIBLE);
                break;
        }
    }

    public interface onHeaderTitleViewClick {
        void leftClick(View view);

        void rightClick(View view);
    }

    public void setmOnHeaderTitleViewClick(onHeaderTitleViewClick mOnHeaderTitleViewClick) {
        this.mOnHeaderTitleViewClick = mOnHeaderTitleViewClick;
    }
}
