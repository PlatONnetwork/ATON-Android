package com.platon.aton.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.platon.aton.R;

import java.security.MessageDigest;

public class GlideUtils {

    public static void loadImage(Context context, String httpUrl, ImageView imageView) {
        Glide.with(context).load(httpUrl).override(DensityUtil.getScreenWidth(context), DensityUtil.getScreenHeight(context))
                .error(R.drawable.icon_validators_default).placeholder(R.drawable.icon_validators_default).into(imageView);
    }

    /**
     * 指定默认图片和加载错误的图片
     *
     * @param context
     * @param httpUrl
     * @param imageView
     * @param resErrorId
     * @param resLoadId
     */
    public static void loadImage(Context context, String httpUrl, ImageView imageView, int resErrorId, int resLoadId) {
        Glide.with(context).load(httpUrl).error(resErrorId)
                .override(DensityUtil.getScreenWidth(context), DensityUtil.getScreenHeight(context)).placeholder(resLoadId).into(imageView);
    }

    /**
     * 指定图片的宽高
     *
     * @param context
     * @param httpUrl
     * @param imageView
     * @param width
     * @param height
     */
    public static void loadImage2(Context context, String httpUrl, ImageView imageView, int width, int height) {
        Glide.with(context).load(httpUrl).placeholder(R.drawable.icon_validators_default).error(R.drawable.icon_validators_default)
                .override(width, height).into(imageView);
    }

    /**
     * 动态指定图片宽高
     *
     * @param context
     * @param httpUrl
     * @param imageView
     * @param width
     * @param height
     */
    public static void loadImageNoCrop(Context context, String httpUrl, ImageView imageView, int width, int height) {
        Glide.with(context).load(httpUrl).placeholder(R.drawable.icon_validators_default).error(R.drawable.icon_validators_default)
                .override(width, height).into(imageView);
    }


    public static void loadImageNoCrop(Context context, String httpUrl, ImageView imageView) {
        Glide.with(context).load(httpUrl).placeholder(R.drawable.icon_validators_default).error(R.drawable.icon_validators_default).into(imageView);
    }

    public static void loadImageNoCrop2(Context context, String httpUrl, ImageView imageView) {
        Glide.with(context).load(httpUrl).into(imageView);
    }

    public static void loadImageNoCrop(Context context, String httpUrl, ImageView imageView, int width, int height, int resId) {
        Glide.with(context).load(httpUrl).placeholder(resId).error(resId).override(width, height).into(imageView);
    }

    public static void loadImage(Context context, String httpUrl, ImageView imageView, int resErrorId, int resLoadId, int width, int height) {
        Glide.with(context).load(httpUrl).placeholder(resLoadId).error(resErrorId).override(width, height).centerCrop()
                .into(imageView);
    }

    public static void loadDrawableImage(Context context, int resId, ImageView imageView, int width, int height) {
        Glide.with(context).load(resId).placeholder(R.drawable.icon_validators_default).error(R.drawable.icon_validators_default).override(width, height)
                .centerCrop().into(imageView);
    }

    public static void loadRound(Context context, String httpUrl, ImageView imageView) {
        Glide.with(context).load(httpUrl).transform(new GlideCircleTransform2())
                .override(DensityUtil.getScreenWidth(context), DensityUtil.getScreenHeight(context)).error(R.drawable.icon_validators_default)
                .placeholder(R.drawable.icon_validators_default).into(imageView);
    }

    public static void loadRound(Context context, String httpUrl, ImageView imageView, int resErrorId, int resLoadId) {
        Glide.with(context).load(httpUrl).transform(new GlideCircleTransform2()).error(resErrorId).placeholder(resLoadId)
                .into(imageView);
    }

    public static void loadRound(Context context, String httpUrl, ImageView imageView, int radius) {
        Glide.with(context).load(httpUrl).transform(new GlideRoundTransform(radius)).error(R.drawable.icon_validators_default).placeholder(R.drawable.icon_validators_default)
                .into(imageView);
    }

    private static class GlideCircleTransform2 extends BitmapTransformation {

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return circleCrop(pool, toTransform);
        }

        private static Bitmap circleCrop(BitmapPool pool, Bitmap source) {
            if (source == null)
                return null;
            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;
            // TODO this could be acquired from the pool too
            Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
            Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            return result;
        }

        @Override
        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

        }
    }

    private static class GlideRoundTransform extends BitmapTransformation {
        private float radius = 0f;

        public GlideRoundTransform() {
            this(4);
        }

        public GlideRoundTransform(int dp) {
            this.radius = Resources.getSystem().getDisplayMetrics().density * dp;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return roundCrop(pool, toTransform);
        }

        private Bitmap roundCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
            canvas.drawRoundRect(rectF, radius, radius, paint);
            return result;
        }


        @Override
        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

        }
    }

}
