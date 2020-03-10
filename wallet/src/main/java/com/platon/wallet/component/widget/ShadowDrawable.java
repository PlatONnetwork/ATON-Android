package com.platon.wallet.component.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.View;

public class ShadowDrawable extends StateListDrawable {

    // 无圆角，不建议使用
    public static final int CORNER_NONE = 0;
    // 左上角为圆角
    public static final int CORNER_TOP_LEFT = 1;
    // 右上角为圆角
    public static final int CORNER_TOP_RIGHT = 1 << 1;
    // 右下角为圆角
    public static final int CORNER_BOTTOM_LEFT = 1 << 2;
    // 右下角为圆角
    public static final int CORNER_BOTTOM_RIGHT = 1 << 3;
    //全圆角
    public static final int CORNER_ALL = CORNER_TOP_LEFT | CORNER_TOP_RIGHT | CORNER_BOTTOM_LEFT | CORNER_BOTTOM_RIGHT;

    public static final int SHADOW_TOP = 1;
    public static final int SHADOW_LEFT = 1 << 1;
    public static final int SHADOW_RIGHT = 1 << 2;
    public static final int SHADOW_BOTTOM = 1 << 3;
    public static final int SHADOW_ALL = SHADOW_TOP | SHADOW_LEFT | SHADOW_RIGHT | SHADOW_BOTTOM;
    /**
     * 阴影画笔
     */
    private Paint mShadowPaint;
    /**
     * 背景画笔
     */
    private Paint mBgPaint;
    /**
     * 阴影半径
     */
    private int mShadowRadius;
    /**
     * 背景形状
     */
    private int mShape;
    /**
     * 背景圆角半径
     */
    private int mShapeRadius;
    /**
     * 阴影x轴上偏移量
     */
    private int mOffsetX;
    /**
     * 阴影y轴上偏移量
     */
    private int mOffsetY;
    /**
     * 背景颜色，多个的话可以设置为渐变
     */
    private int mBgColor[];
    /**
     * 圆角模式
     */
    private int mCornersMode;

    private int mShadowsMode;
    /**
     * 是否需要画内容背景
     */
    private boolean mNeedSetContentBg;
    /**
     * 背景区域
     */
    private RectF mRect;

    public final static int SHAPE_ROUND = 1;
    public final static int SHAPE_CIRCLE = 2;

    private ShadowDrawable(int shape, int[] bgColor, int shapeRadius, int cornersMode, int shadowsMode, int shadowColor, int shadowRadius, int offsetX, int offsetY, boolean needDrawBg) {
        this.mShape = shape;
        this.mBgColor = bgColor;
        this.mShapeRadius = shapeRadius;
        this.mCornersMode = cornersMode;
        this.mShadowsMode = shadowsMode;
        this.mShadowRadius = shadowRadius;
        this.mOffsetX = offsetX;
        this.mOffsetY = offsetY;
        this.mNeedSetContentBg = needDrawBg;

        mShadowPaint = new Paint();
        mShadowPaint.setColor(Color.TRANSPARENT);
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setShadowLayer(shadowRadius, offsetX, offsetY, shadowColor);
        mShadowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));

        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);

        if (mBgColor != null) {
            if (mBgColor.length == 1) {
                mBgPaint.setColor(mBgColor[0]);
            } else {
                mBgPaint.setShader(new LinearGradient(mRect.left, mRect.height() / 2, mRect.right,
                        mRect.height() / 2, mBgColor, null, Shader.TileMode.CLAMP));
            }
        }
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        if (mShadowsMode == SHADOW_ALL) {
            mRect = new RectF(left + mShadowRadius - mOffsetX, top + mShadowRadius - mOffsetY, right - mShadowRadius - mOffsetX,
                    bottom - mShadowRadius - mOffsetY);
        } else if (mShadowsMode == SHADOW_TOP) {
            mRect = new RectF(left - mOffsetX, top + mShadowRadius - mOffsetY, right - mOffsetX,
                    bottom - mOffsetY);
        } else if (mShadowsMode == SHADOW_LEFT) {
            mRect = new RectF(left + mShadowRadius - mOffsetX, top - mOffsetY, right - mOffsetX,
                    bottom - mOffsetY);
        } else if (mShadowsMode == SHADOW_RIGHT) {
            mRect = new RectF(left - mOffsetX, top - mOffsetY, right - mShadowRadius - mOffsetX,
                    bottom - mOffsetY);
        } else if (mShadowsMode == SHADOW_BOTTOM) {
            mRect = new RectF(left - mOffsetX, top - mOffsetY, right - mOffsetX,
                    bottom - mShadowRadius - mOffsetY);
        } else if (mShadowsMode == (SHADOW_BOTTOM | SHADOW_LEFT | SHADOW_RIGHT)) {
            mRect = new RectF(left + mShadowRadius - mOffsetX, top - mOffsetY, right - mShadowRadius - mOffsetX,
                    bottom - mShadowRadius - mOffsetY);
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        //画阴影
        drawShadow(canvas);
        //画内容背景
        drawContentBackground(canvas);
    }

    private void setContentBgPaint() {
        if (mBgColor != null) {
            if (mBgColor.length == 1) {
                mBgPaint.setColor(mBgColor[0]);
            } else {
                mBgPaint.setShader(new LinearGradient(mRect.left, mRect.height() / 2, mRect.right,
                        mRect.height() / 2, mBgColor, null, Shader.TileMode.CLAMP));
            }
        }
    }

    private void drawShadow(Canvas canvas) {
        if (mShape == SHAPE_ROUND) {
            canvas.drawRoundRect(mRect, mShapeRadius, mShapeRadius, mShadowPaint);
        } else {
            canvas.drawCircle(mRect.centerX(), mRect.centerY(), Math.min(mRect.width(), mRect.height()) / 2, mShadowPaint);
        }
    }

    private void drawContentBackground(Canvas canvas) {
        if (mNeedSetContentBg) {
            if (mShape == SHAPE_ROUND) {
                canvas.drawRoundRect(mRect, mShapeRadius, mShapeRadius, mBgPaint);
                // 异或，相同为0，不同为1
                int notRoundedCorners = mCornersMode ^ CORNER_ALL;
                //左上角
                if ((notRoundedCorners & CORNER_TOP_LEFT) != 0) {
                    canvas.drawRect(mRect.left, 0, mRect.left + mShapeRadius, mShapeRadius, mBgPaint);
                }
                //左下角
                if ((notRoundedCorners & CORNER_BOTTOM_LEFT) != 0) {
                    canvas.drawRect(mRect.left, mRect.bottom - mShapeRadius, mRect.left + mShapeRadius, mRect.bottom, mBgPaint);
                }
                //右上角
                if ((notRoundedCorners & CORNER_TOP_RIGHT) != 0) {
                    canvas.drawRect(mRect.right - mShapeRadius, 0, mRect.right, mShapeRadius, mBgPaint);
                }
                //右下角
                if ((notRoundedCorners & CORNER_BOTTOM_RIGHT) != 0) {
                    canvas.drawRect(mRect.right - mShapeRadius, mRect.bottom - mShapeRadius, mRect.right, mRect.bottom,
                            mBgPaint);
                }
            } else {
                canvas.drawCircle(mRect.centerX(), mRect.centerY(), Math.min(mRect.width(), mRect.height()) / 2, mBgPaint);
            }
        }

    }

    @Override
    public void setAlpha(int alpha) {
        mShadowPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mShadowPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public static void setShadowDrawable(View view, Drawable drawable) {
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setBackground(view, drawable);
    }


    public static void setShadowDrawable(View view, int shapeRadius, int shadowColor, int shadowRadius, int offsetX, int offsetY) {
        ShadowDrawable drawable = new ShadowDrawable.Builder()
                .setShapeRadius(shapeRadius)
                .setShadowColor(shadowColor)
                .setShadowRadius(shadowRadius)
                .setOffsetX(offsetX)
                .setOffsetY(offsetY)
                .setNeedSetContentBg(false)
                .builder();
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setBackground(view, drawable);
    }

    public static void setShadowDrawableWithShadowMode(View view, int shapeRadius, int shadowColor, int shadowRadius, int offsetX, int offsetY, int shadowsMode) {
        ShadowDrawable drawable = new ShadowDrawable.Builder()
                .setShapeRadius(shapeRadius)
                .setShadowColor(shadowColor)
                .setShadowRadius(shadowRadius)
                .setOffsetX(offsetX)
                .setOffsetY(offsetY)
                .setNeedSetContentBg(false)
                .setShadowsMode(shadowsMode)
                .builder();
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setBackground(view, drawable);
    }

    public static void setShadowDrawable(View view, int bgColor, int shapeRadius, int shadowColor, int shadowRadius, int offsetX, int offsetY) {
        ShadowDrawable drawable = new ShadowDrawable.Builder()
                .setBgColor(bgColor)
                .setShapeRadius(shapeRadius)
                .setShadowColor(shadowColor)
                .setShadowRadius(shadowRadius)
                .setOffsetX(offsetX)
                .setOffsetY(offsetY)
                .setNeedSetContentBg(true)
                .builder();
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setBackground(view, drawable);
    }

    public static void setShadowDrawableWithShadowMode(View view, int bgColor, int shapeRadius, int shadowColor, int shadowRadius, int offsetX, int offsetY, int shadowsMode) {
        ShadowDrawable drawable = new ShadowDrawable.Builder()
                .setBgColor(bgColor)
                .setShapeRadius(shapeRadius)
                .setShadowColor(shadowColor)
                .setShadowRadius(shadowRadius)
                .setOffsetX(offsetX)
                .setOffsetY(offsetY)
                .setNeedSetContentBg(true)
                .setShadowsMode(shadowsMode)
                .builder();
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setBackground(view, drawable);
    }

    public static void setShadowDrawableWithCornersMode(View view, int bgColor, int shapeRadius, int cornersMode, int shadowColor, int shadowRadius, int offsetX, int offsetY) {
        ShadowDrawable drawable = new Builder()
                .setBgColor(bgColor)
                .setShapeRadius(shapeRadius)
                .setCornersMode(cornersMode)
                .setShadowColor(shadowColor)
                .setShadowRadius(shadowRadius)
                .setOffsetX(offsetX)
                .setOffsetY(offsetY)
                .setNeedSetContentBg(true)
                .builder();
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setBackground(view, drawable);
    }

    public static void setShadowDrawable(View view, int shape, int bgColor, int shapeRadius, int shadowColor, int shadowRadius, int offsetX, int offsetY) {
        ShadowDrawable drawable = new ShadowDrawable.Builder()
                .setShape(shape)
                .setBgColor(bgColor)
                .setShapeRadius(shapeRadius)
                .setShadowColor(shadowColor)
                .setShadowRadius(shadowRadius)
                .setOffsetX(offsetX)
                .setOffsetY(offsetY)
                .setNeedSetContentBg(true)
                .builder();
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setBackground(view, drawable);
    }

    public static void setShadowDrawable(View view, int[] bgColor, int shapeRadius, int shadowColor, int shadowRadius, int offsetX, int offsetY) {
        ShadowDrawable drawable = new ShadowDrawable.Builder()
                .setBgColor(bgColor)
                .setShapeRadius(shapeRadius)
                .setShadowColor(shadowColor)
                .setShadowRadius(shadowRadius)
                .setOffsetX(offsetX)
                .setOffsetY(offsetY)
                .setNeedSetContentBg(true)
                .builder();
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setBackground(view, drawable);
    }

    public static class Builder {
        private int mShape;
        private int mShapeRadius;
        private int mShadowColor;
        private int mShadowRadius;
        private int mCornersMode;
        private int mShadowsMode;
        private int mOffsetX = 0;
        private int mOffsetY = 0;
        private int[] mBgColor;
        private boolean mNeedSetContentBg;

        public Builder() {
            mShape = ShadowDrawable.SHAPE_ROUND;
            mShapeRadius = 12;
            mShadowColor = Color.parseColor("#4d000000");
            mShadowRadius = 18;
            mCornersMode = CORNER_ALL;
            mShadowsMode = SHADOW_ALL;
            mOffsetX = 0;
            mOffsetY = 0;
            mBgColor = new int[1];
            mBgColor[0] = Color.TRANSPARENT;
            mNeedSetContentBg = false;
        }

        public Builder setShape(int mShape) {
            this.mShape = mShape;
            return this;
        }

        public Builder setShapeRadius(int shapeRadius) {
            this.mShapeRadius = shapeRadius;
            return this;
        }

        public Builder setCornersMode(int cornersMode) {
            this.mCornersMode = cornersMode;
            return this;
        }

        public Builder setShadowsMode(int shadowsMode) {
            this.mShadowsMode = shadowsMode;
            return this;
        }

        public Builder setShadowColor(int shadowColor) {
            this.mShadowColor = shadowColor;
            return this;
        }

        public Builder setShadowRadius(int shadowRadius) {
            this.mShadowRadius = shadowRadius;
            return this;
        }

        public Builder setOffsetX(int OffsetX) {
            this.mOffsetX = OffsetX;
            return this;
        }

        public Builder setOffsetY(int OffsetY) {
            this.mOffsetY = OffsetY;
            return this;
        }

        public Builder setBgColor(int BgColor) {
            this.mBgColor[0] = BgColor;
            return this;
        }

        public Builder setBgColor(int[] BgColor) {
            this.mBgColor = BgColor;
            return this;
        }

        public Builder setNeedSetContentBg(boolean needSetContentBg) {
            this.mNeedSetContentBg = needSetContentBg;
            return this;
        }

        public ShadowDrawable builder() {
            return new ShadowDrawable(mShape, mBgColor, mShapeRadius, mCornersMode, mShadowsMode, mShadowColor, mShadowRadius, mOffsetX, mOffsetY, mNeedSetContentBg);
        }
    }
}
