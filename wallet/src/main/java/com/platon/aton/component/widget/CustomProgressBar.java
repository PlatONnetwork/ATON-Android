package com.platon.aton.component.widget;

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

import com.platon.framework.utils.AndroidUtil;
import com.platon.aton.R;

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
        int width = getWidth();
        int height = getHeight();
        int iconWidth = defaultIcon.getWidth();
        int iconHeight = defaultIcon.getHeight();
        float iconX = AndroidUtil.dip2px(mContext, ICON_TEXT_SPACING_DP);
        float iconY = (height / 2) - iconHeight / 2;
        float textX = iconWidth + 2 * iconX;
        float textY = (height / 2) - textRect.centerY();
        float maxWidth = width - textX - iconX;
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
        }
        canvas.drawBitmap(defaultIcon, iconX, iconY, mPaint);
        Bitmap bufferBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas bufferCanvas = new Canvas(bufferBitmap);
        bufferCanvas.drawText(newText, textX, textY, mPaint);
        // 设置混合模式
        mPaint.setXfermode(mPorterDuffXfermode);
        mPaint.setColor(ContextCompat.getColor(mContext, COLOR_LOADINGXFERMODE));
        int right = width * mProgress / 100;
        RectF rectF = new RectF(0, 0, right, height);
        bufferCanvas.drawRect(rectF, mPaint);
        canvas.drawBitmap(bufferBitmap, 0, 0, null);
        mPaint.setXfermode(null);
        int offset = (int)(right - iconX);
        if (offset > iconX) {
            if (offset < iconWidth){
                Rect  srcRcet1 = new Rect(0, 0, offset, iconHeight);
                RectF dstRcet1 = new RectF(iconX, iconY, offset + iconX, iconHeight + iconY);
                canvas.drawBitmap(loadingIcon, srcRcet1, dstRcet1, mPaint);
            }else {
                canvas.drawBitmap(loadingIcon, iconX, iconY, mPaint);
            }
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
}

