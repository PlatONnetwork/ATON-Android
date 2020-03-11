package com.platon.aton.component.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.ArrayList;

public class AutoTextView extends TextView {
    /**
     * 文本是否变化
     */

    boolean mIsDirty = false;
    AdaptableText mAdaptableText;

    public AutoTextView(Context context) {
        super(context);
        requestLayoutFor();
    }

    public AutoTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        requestLayoutFor();
    }

    public AutoTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        requestLayoutFor();
    }


    private void requestLayoutFor() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (getWidth() > 0) {
                    if (Build.VERSION.SDK_INT >= 16) {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                    requestLayout();
                }
            }
        });
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        super.onTextChanged(text, start, before, after);
        mIsDirty = true;
    }

    @Override
    public int getLineCount() {
        AdaptableText helper = getAdaptableText();
        return null == helper ? 0 : helper.getLineCount();
    }


    @Override

    public void setMaxLines(int maxLines) {

        super.setMaxLines(maxLines);

        mIsDirty = true;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = measureWidth(widthMeasureSpec);
        int h = measureHeight(heightMeasureSpec);
        setMeasuredDimension(w, h);
    }


    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            String text = getText().toString();
            result = (int) getPaint().measureText(text) + getPaddingLeft() + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @SuppressLint("NewApi")
    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 0;
            String text = getText().toString();
            if (!TextUtils.isEmpty(text)) {
                int lineCount = getLineCount();
                int maxLines = getMaxLines();
                lineCount = Math.min(maxLines, lineCount);
                int lineHeight = getLineHeight();
                result = lineCount * lineHeight;
            }
        }
        return result;
    }


    @Override
    @SuppressLint("NewApi")
    protected void onDraw(Canvas canvas) {
        getAdaptableText();
        if (mIsDirty) {
            mIsDirty = false;
            String text = getText().toString();
            int maxLines = getMaxLines();
            if (!mAdaptableText.getText().equals(text)) mAdaptableText.setText(text);
            if (mAdaptableText.getMaxLines() != maxLines) mAdaptableText.setMaxLines(maxLines);
        }
        mAdaptableText.draw(canvas);
    }


    @SuppressLint("NewApi")
    private AdaptableText getAdaptableText() {
        if (mAdaptableText == null) {
            int measuredWidth = getMeasuredWidth();
            if (measuredWidth <= 0) {
                return null;
            }
            TextPaint paint = getPaint();
            paint.setColor(getCurrentTextColor());
            paint.drawableState = getDrawableState();
            int paddingLeft = getPaddingLeft();
            int paddingRight = getPaddingRight();
            int lineHeight = getLineHeight();
            String text = getText().toString();
            mAdaptableText = new AdaptableText(text, paint, measuredWidth - paddingLeft - paddingRight, lineHeight);
            mAdaptableText.setMaxLines(getMaxLines());
        }
        return mAdaptableText;
    }


    private class AdaptableText {
        /**
         * 文本宽高
         */
        int mLineWidth, mLineHeight;
        /**
         * 最大行数
         */
        int mMaxLines;
        private TextPaint mPaint;
        private String mText;
        /**
         * 存储分割后的每行
         */
        ArrayList<String> strs = new ArrayList<String>();


        public AdaptableText(String text, TextPaint paint, int lineWidth, int lineHeight) {
            mLineHeight = lineHeight;
            mLineWidth = lineWidth;
            mPaint = paint;
            mText = text;
            parseText();
        }


        /**
         * 根据控件宽度，计算得出每行的字符串
         */
        private void parseText() {
            if (mLineWidth > 0 && mLineHeight > 0) {
                strs.clear();
                int start = 0;//行起始Index
                int curLineWidth = 0;//当前行宽
                for (int i = 0; i < mText.length(); i++) {
                    char ch = mText.charAt(i);//获取当前字符
                    float[] widths = new float[1];
                    String srt = String.valueOf(ch);
                    mPaint.getTextWidths(srt, widths);//获取这个字符的宽度
                    if (ch == '\n') {//如果是换行符，则当独一行
                        strs.add(mText.substring(start, i));
                        start = i + 1;
                        curLineWidth = 0;
                    } else {
                        curLineWidth += (int) (Math.ceil(widths[0]));//计算当前宽度
                        if (curLineWidth > mLineWidth) {//直到当前行宽度大于控件宽度，截取为一行
                            strs.add(mText.substring(start, i));
                            start = i;
                            i--;
                            curLineWidth = 0;
                        } else {
                            if (i == (mText.length() - 1)) {//剩余的单独一行
                                String s = mText.substring(start, mText.length());
                                if (!TextUtils.isEmpty(s)) {
                                    strs.add(s);
                                }
                            }
                        }
                    }
                }
            }
        }

        public void draw(Canvas canvas) {
            int lines = mMaxLines > 0 && mMaxLines <= strs.size() ? mMaxLines : strs.size();
            for (int i = 0; i < lines; i++) {
                String text = strs.get(i);
//                如果是最大行的最后一行但不是真实的最后一行则自动添加省略号
                if (i == lines - 1 && i < strs.size() - 1)
                    text = text.substring(0, text.length() - 3) + "...";
                canvas.drawText(text, getPaddingLeft(), getPaddingTop() + mPaint.getTextSize() + mLineHeight * i, mPaint);
            }
        }


        public void setText(String text) {

            mText = text;

            parseText();

        }

        public String getText() {

            return mText;

        }

        public void setMaxLines(int maxLines) {

            mMaxLines = maxLines;

        }


        public int getMaxLines() {

            return mMaxLines;

        }


        public int getLineCount() {

            return strs.size();

        }


        public int getLineEnd(int line) {
            int size = 0;
            for (int i = 0; i <= line; i++) {
                size += strs.get(i).length();
            }
            return size;
        }
    }
}
