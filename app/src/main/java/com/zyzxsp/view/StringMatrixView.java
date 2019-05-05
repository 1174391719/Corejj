package com.zyzxsp.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;


import com.zyzxsp.R;

import java.util.List;
import java.util.Map;

/**
 * Created by tony on 10/24/16.
 * Sample:
 * StringMatrixView matrix = (StringMatrixView) findViewById(R.id.matrix);
 * List<Pair<String, String>> keys = new ArrayList<>();
 * keys.add(new Pair<>("name", "通道名称"));
 * keys.add(new Pair<>("codec", "Codec"));
 * keys.add(new Pair<>("resolution", "分辨率"));
 * keys.add(new Pair<>("fps", "帧率"));
 * keys.add(new Pair<>("bitRate", "码率"));
 * keys.add(new Pair<>("oneMore", "加一列"));
 *
 * ArrayList<Map<String, Object>> values = new ArrayList<>();
 * {
 *
 * HashMap<String, Object> map = new HashMap<>();
 * map.put("name", "视频发送");
 * map.put("codec", "H264 SVC");
 * map.put("resolution", "1080p");
 * map.put("fps", "10fps");
 * map.put("bitRate", "512K");
 * values.add(map);
 * }
 *
 * {
 *
 * HashMap<String, Object> map = new HashMap<>();
 * map.put("name", "音频发送");
 * map.put("codec", "Opus");
 * map.put("resolution", "---");
 * map.put("fps", "---");
 * map.put("bitRate", "512K");
 * values.add(map);
 * }
 * matrix.setValues(keys, values);
 */
//解析
public class StringMatrixView extends View {

    public static final int ROUND_RECT_SIZE = 10;
    public static final int MAX_CHAR_SIZE = 20;
    public static final String TEXT_LONG_SUFFIX = "...";
    public static final String EMPTY_TEXT = "----";
    private int mColWidth;
    @Nullable
    private List<Map<String, Object>> mValues;
    @Nullable
    private List<Pair<String, String>> mCols;
    private Paint mTextPaint;
    private Paint mBackgroundPaint;
    private int mMarginLeft;
    private int mMarginTop;
    private int mMarginRight;
    private int mMarginBottom;
    private RectF mTmpRect;
    private int mCellMargin;

    private int mDividerWidth;
    private Paint mDividerPaint;
    private float[] mTextWidths;
    private int mTextHeight;
    private int mFontDescent;
    private int mFontAscent;
    private int mLongTextSuffixLength;

    public StringMatrixView(Context context) {
        super(context);
        init(context);
    }

    public StringMatrixView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StringMatrixView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        Resources res = context.getResources();

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(res.getDimension(R.dimen.sp13));
        int length = TEXT_LONG_SUFFIX.length();
        float[] textSuffixWidths = new float[length];
        mTextPaint.getTextWidths(TEXT_LONG_SUFFIX, textSuffixWidths);
        mLongTextSuffixLength = sum(textSuffixWidths, length);

        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        mFontDescent = fontMetrics.descent;
        mFontAscent = fontMetrics.ascent;
        mTextHeight = mFontDescent - mFontAscent;

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(res.getColor(R.color.white_25));
        mTmpRect = new RectF();
        mCellMargin = 20;

        mDividerWidth = 2;
        mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDividerPaint.setColor(res.getColor(R.color.black_20));
        mDividerPaint.setStyle(Paint.Style.FILL);
        mTextWidths = new float[MAX_CHAR_SIZE];


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ViewGroup.LayoutParams lp = getLayoutParams();
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
            mMarginLeft = mlp.leftMargin;
            mMarginTop = mlp.topMargin;
            mMarginRight = mlp.rightMargin;
            mMarginBottom = mlp.bottomMargin;
        }

        int rowSize = mValues != null ? mValues.size() + 1 : 0;
        int colSize = mCols != null ? mCols.size() : 0;
//        int sumDividerWidth = mDividerWidth * (colSize - 1);
        int sumDividerHeight = rowSize > 0 ? mDividerWidth * (rowSize - 1) : 0;


        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                (mTextHeight + mCellMargin * 2) * rowSize + mMarginTop + mMarginBottom
                        + sumDividerHeight);
        int width = getMeasuredWidth() - mMarginLeft - mMarginRight;
        mColWidth = (colSize > 0 ? width / colSize : width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        int colSize = mCols != null ? mCols.size() : 0;
        int rowSize = mValues != null ? mValues.size() : 0;

        if (colSize > 1 && rowSize > 0) {

            mTmpRect.set(0, 0, width, height);
            canvas.drawRoundRect(mTmpRect, ROUND_RECT_SIZE, ROUND_RECT_SIZE, mBackgroundPaint);

            // title
            float y = mMarginTop + mCellMargin - mFontAscent;
            for (int i = 0; i < colSize; i++) {
                int x = mColWidth * i + mMarginLeft;
                if (i != 0) {
                    mTmpRect.set(x, 0, x + mDividerWidth, height);
                    canvas.drawRect(mTmpRect, mDividerPaint);
                }


                String text = mCols.get(i).second;
                // align center
                drawAlignCenterText(canvas, y, x, text, mTextPaint, mColWidth);

                // align left
//                x += mCellMargin;
//                canvas.drawText(text, x, y, mTextPaint);
            }
            y += mFontDescent;


            for (int j = 0; j < rowSize; j++) {
                y += mCellMargin;
                mTmpRect.set(0, y, width, y + mDividerWidth);
                canvas.drawRect(mTmpRect, mDividerPaint);

                y += (mDividerWidth + mCellMargin - mFontAscent);
                Map<String, Object> map = mValues.get(j);
                for (int i = 0; i < colSize; i++) {
                    int x = mColWidth * i + mMarginLeft;
                    Object value = map.get(mCols.get(i).first);
                    String text = value == null ? EMPTY_TEXT : String.valueOf(value);
                    // align center
                    drawAlignCenterText(canvas, y, x, text, mTextPaint, mColWidth);
                    // align left
//                    x += mCellMargin;
//                    canvas.drawText(text, x, y, mTextPaint);
                }
                y += mFontDescent;
            }

        }
    }

    private void drawAlignCenterText(Canvas canvas, float y, int x, String text, Paint textPaint, int colWidth) {
        int textLength = text.length();
        int visibleTextSize = Math.min(textLength, MAX_CHAR_SIZE);
        int count = textPaint.getTextWidths(text, 0, visibleTextSize, mTextWidths);
        int textWidth = sum(mTextWidths, count);
        if (textWidth > colWidth || count < textLength) {

            while (textWidth > (colWidth - mLongTextSuffixLength)) {
                count--;
                textWidth -= mTextWidths[count];
            }
            String drawText = text.substring(0, count - 1).concat(TEXT_LONG_SUFFIX);
            canvas.drawText(drawText, x + (colWidth - textWidth) / 2, y, textPaint);
        } else {
            canvas.drawText(text, x + (colWidth - textWidth) / 2, y, textPaint);
        }
    }


    static int sum(@NonNull float[] widths, int count) {
        float f = 0;
        for (int i = 0; i < count; i++) {
            f += widths[i];
        }
        return Float.valueOf(f).intValue();
    }

    @UiThread
    public void setValues(@Nullable List<Pair<String, String>> cols, @Nullable List<Map<String, Object>> values) {
        mCols = cols;
        mValues = values;
        requestLayout();
        setVisibility(values == null || values.isEmpty() ? GONE : VISIBLE);
    }
}
