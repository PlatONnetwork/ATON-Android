package com.juzix.wallet.component.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.juzhen.framework.util.AndroidUtil;
import com.juzix.wallet.R;

public class CustomProgressBar extends ProgressBar {
    private              Context            mContext;
    private              Paint              mPaint;
    private              PorterDuffXfermode mPorterDuffXfermode;
    private              int                mProgress;
    private              String             mText;
    private              int                mDefaultIconRes;
    private              int                mLoadingIconRes;
    private static final int                COLOR_DEFAULT        = R.color.color_105cfe;
    private static final int                COLOR_LOADING        = R.color.color_999999;
    private static final int                COLOR_LOADINGXFERMODE        = R.color.color_105cfe;
    private static final float              TEXT_SIZE_SP         = 12f;
    private static final float              ICON_TEXT_SPACING_DP = 5f;

    public CustomProgressBar(Context context) {
        super(context, null, android.R.attr.progressBarStyleHorizontal);
        mContext = context;
        init();
    }

    public CustomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public synchronized void setProgress(int progress, String text, int defaultIconRes, int loadingIconRes) {
        super.setProgress(progress);
        mProgress = progress > 100 ? 100 : progress;
        mText = text;
        mDefaultIconRes = defaultIconRes;
        mLoadingIconRes = loadingIconRes;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawIconAndText(canvas);
    }

    private void init() {
        setIndeterminate(false);
        setMax(100);
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setTextSize(AndroidUtil.sp2px(mContext, TEXT_SIZE_SP));
        mPaint.setTypeface(Typeface.MONOSPACE);
        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    }

    private void drawIconAndText(Canvas canvas) {
        if (mProgress < 100) {
            mPaint.setColor(ContextCompat.getColor(mContext, COLOR_LOADING));
        }else {
            setProgress(100);
            mPaint.setColor(ContextCompat.getColor(mContext, COLOR_DEFAULT));
        }
        String text = mText;
        Rect textRect = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), textRect);
        // 绘制图标和文字
        Bitmap defaultIcon = BitmapFactory.decodeResource(getResources(), mDefaultIconRes);
        Bitmap loadingIcon = BitmapFactory.decodeResource(getResources(), mLoadingIconRes);
        float iconX = AndroidUtil.dip2px(mContext, ICON_TEXT_SPACING_DP);
        float iconY = (getHeight() / 2) - defaultIcon.getHeight() / 2;
        float textX = defaultIcon.getWidth() + 2 * iconX;
        float textY = (getHeight() / 2) - textRect.centerY();
        float maxWidth = getWidth() - textX - iconX;
        String newText = text;
        if(text != null && text.length() > 0) {
            float textWidth = mPaint.measureText(text);
            if ( textWidth> maxWidth) {
                int subIndex = mPaint.breakText(text, 0, text.length(), true, maxWidth, null);
                newText = text.substring(0, subIndex - 2) + "...";
            }
        }
        canvas.drawText(newText, textX, textY, mPaint);
        if (mProgress == 100) {
            canvas.drawBitmap(loadingIcon, iconX, iconY, mPaint);
            return;
        }else {
            canvas.drawBitmap(defaultIcon, iconX, iconY, mPaint);
        }

        Bitmap bufferBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas bufferCanvas = new Canvas(bufferBitmap);
        bufferCanvas.drawText(newText, textX, textY, mPaint);
        // 设置混合模式
        mPaint.setXfermode(mPorterDuffXfermode);
        mPaint.setColor(ContextCompat.getColor(mContext, COLOR_LOADINGXFERMODE));
        int right = getWidth() * mProgress / 100;
        RectF rectF = new RectF(0, 0, right, getHeight());
        bufferCanvas.drawRect(rectF, mPaint);
        canvas.drawBitmap(bufferBitmap, 0, 0, null);
        mPaint.setXfermode(null);
        int r = (int)(right - iconX);
        int b = loadingIcon.getHeight();
        if (r > iconX) {
            Rect  srcRcet1 = new Rect(0, 0, r, b);
            RectF dstRcet1 = new RectF(iconX, iconY, r + iconX, b + iconY);
            canvas.drawBitmap(loadingIcon, srcRcet1, dstRcet1, mPaint);
        }
        if (!defaultIcon.isRecycled()) {
            defaultIcon.isRecycled();
        }
        if (!loadingIcon.isRecycled()) {
            loadingIcon.isRecycled();
        }
        if (!bufferBitmap.isRecycled()) {
            bufferBitmap.recycle();
        }
    }

    private float getOffsetX(float iconWidth, float textHalfWidth, float spacing, boolean isText) {
        float totalWidth = iconWidth + AndroidUtil.dip2px(mContext, spacing) + textHalfWidth * 2;
        // 文字偏移量
        if (isText) return totalWidth / 2 - iconWidth - spacing;
        // 图标偏移量
        return totalWidth / 2 - iconWidth;
    }

}

