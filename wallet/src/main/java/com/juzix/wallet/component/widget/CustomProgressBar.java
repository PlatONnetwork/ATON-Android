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
    private              int  mProgress;
//    private              int    mState;
    private              String mText;
    private              int    mIconRes;
    private static final int    COLOR_DEFAULT        = R.color.color_105cfe;
    private static final int    COLOR_LOADING        = R.color.color_999999;
    private static final int    COLOR_LOADINGXFERMODE        = R.color.color_105cfe;
    // IconTextProgressBar的状态
//    public static final int    STATE_DEFAULT        = 101;
//    public static final int    STATE_LOADING        = 102;
    // IconTextProgressBar的文字大小(sp)
    private static final float  TEXT_SIZE_SP         = 12f;
    // IconTextProgressBar的图标与文字间距(dp)
    private static final float  ICON_TEXT_SPACING_DP = 5f;

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

    public synchronized void setProgress(int progress, String text, int iconRes) {
        super.setProgress(progress);
        mProgress = progress >= 100 ? 100 : progress;
        mText = text;
        mIconRes = iconRes;
        invalidate();
    }

    /**
     * 文本
     */
    public synchronized void setText(String text) {
        mText = text;
        invalidate();
    }

    /**
     * 文本
     */
    public synchronized void setIcon(int iconRes) {
        mIconRes = iconRes;
        invalidate();
    }

    /**
     * 设置下载进度
     */
    public synchronized void setProgress(int progress) {
        mProgress = progress >= 100 ? 100 : progress;
        super.setProgress(mProgress);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawIconAndText(canvas);
    }

    private void init() {
        setIndeterminate(false);
        setIndeterminateDrawable(ContextCompat.getDrawable(mContext, android.R.drawable.progress_indeterminate_horizontal));
        setProgressDrawable(ContextCompat.getDrawable(mContext, R.drawable.pb_shape_load));
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
        Bitmap icon = BitmapFactory.decodeResource(getResources(), mIconRes);
        float iconX = AndroidUtil.dip2px(mContext, ICON_TEXT_SPACING_DP);
        float iconY = (getHeight() / 2) - icon.getHeight() / 2;
        float textX = icon.getWidth() + 2 * iconX;
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
        canvas.drawBitmap(icon, iconX, iconY, mPaint);
        canvas.drawText(newText, textX, textY, mPaint);

        if (mProgress == 100) return;

        Bitmap bufferBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas bufferCanvas = new Canvas(bufferBitmap);
        bufferCanvas.drawBitmap(icon, iconX, iconY, mPaint);
        bufferCanvas.drawText(newText, textX, textY, mPaint);
        // 设置混合模式
        mPaint.setXfermode(mPorterDuffXfermode);
        mPaint.setColor(ContextCompat.getColor(mContext, COLOR_LOADINGXFERMODE));
        RectF rectF = new RectF(0, 0, getWidth() * mProgress / 100, getHeight());
        // 绘制源图形
        bufferCanvas.drawRect(rectF, mPaint);
        // 绘制目标图
        canvas.drawBitmap(bufferBitmap, 0, 0, null);
        // 清除混合模式
        mPaint.setXfermode(null);

        if (!icon.isRecycled()) {
            icon.isRecycled();
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

