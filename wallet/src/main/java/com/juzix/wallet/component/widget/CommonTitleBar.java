package com.juzix.wallet.component.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.utils.DensityUtil;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;

/**
 * @author matrixelement
 */
public class CommonTitleBar extends LinearLayout {

    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.tv_right)
    TextView tvRight;

    private CharSequence mTitle;
    private int mTitleColor;
    private float mTitleSize;

    private CharSequence mRightText;
    private int mRightTextColor;
    private float mRightTextSize;

    private Drawable mLeftDrawable;
    private Drawable mRightDrawable;
    private Drawable mBackground;

    private Context mContext;
    private Unbinder unbinder;

    public CommonTitleBar(Context context) {
        this(context, null, 0);
        init(context);
    }

    public CommonTitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    @SuppressLint("ResourceType")
    public CommonTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CommonTitleBar, defStyleAttr, 0);

        mTitle = ta.getText(R.styleable.CommonTitleBar_ctb_title);
        mTitleColor = ta.getColor(R.styleable.CommonTitleBar_ctb_title_color, ContextCompat.getColor(context, R.color.color_000000));
        mTitleSize = ta.getDimensionPixelSize(R.styleable.CommonTitleBar_ctb_title_size, DensityUtil.sp2px(context, 16f));

        mRightText = ta.getText(R.styleable.CommonTitleBar_ctb_right_text);
        mRightTextColor = ta.getColor(R.styleable.CommonTitleBar_ctb_right_text_color, ContextCompat.getColor(context, R.color.color_000000));
        mRightTextSize = ta.getDimensionPixelSize(R.styleable.CommonTitleBar_ctb_right_text_size, DensityUtil.sp2px(context, 16f));

        mLeftDrawable = ta.getDrawable(R.styleable.CommonTitleBar_ctb_left_drawable);
        mRightDrawable = ta.getDrawable(R.styleable.CommonTitleBar_ctb_right_drawable);
        mBackground = ta.getDrawable(5);

        ta.recycle();

        init(context);
    }

    private void init(Context context) {

        this.mContext = context;

        unbinder = ButterKnife.bind(this, LayoutInflater.from(context).inflate(R.layout.layout_common_title_bar, this));

        setTitle(mTitle);
        setTitleColor(mTitleColor);
        setTitleSize(mTitleSize);
        setRightText(mRightText);
        setRightTextColor(mRightTextColor);
        setRightTextSize(mRightTextSize);
        setLeftDrawable(mLeftDrawable);
        setRightDrawable(mRightDrawable);
        setBackgroundDrawable(mBackground);
    }


    public CommonTitleBar title(CharSequence val) {
        setTitle(val);
        return this;
    }

    public CommonTitleBar titleColor(int val) {
        setTitleColor(val);
        return this;
    }

    public CommonTitleBar titleSize(float val) {
        setTitleSize(val);
        return this;
    }

    public CommonTitleBar rightText(CharSequence val) {
        setRightText(val);
        return this;
    }

    public CommonTitleBar rightTextColor(int val) {
        setRightTextColor(val);
        return this;
    }

    public CommonTitleBar rightTextSize(float val) {
        setRightTextSize(val);
        return this;
    }

    public CommonTitleBar leftDrawable(Drawable val) {
        setLeftDrawable(val);
        return this;
    }

    public CommonTitleBar rightDrawable(Drawable val) {
        setRightDrawable(val);
        return this;
    }

    public CommonTitleBar leftDrawableClickListener(OnClickListener clickListener) {
        setLeftDrawableClickListener(clickListener);
        return this;
    }

    public CommonTitleBar rightDrawableClickListener(OnClickListener clickListener) {
        setRightDrawableClickListener(clickListener);
        return this;
    }

    public CommonTitleBar background(Drawable val) {
        setBackgroundDrawable(val);
        return this;
    }

    public void setTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(GONE);
        } else {
            tvTitle.setText(title);
            tvTitle.setVisibility(VISIBLE);
        }
    }

    public void setTitle(CharSequence title) {

        RxView.clicks(tvTitle)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object object) throws Exception {
                        if (mContext instanceof Activity) {
                            BaseActivity baseActivity = (BaseActivity) mContext;
                            baseActivity.hideSoftInput();
                            baseActivity.finish();
                        }
                    }
                });

        if (TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(GONE);
        } else {
            tvTitle.setText(title);
            tvTitle.setVisibility(VISIBLE);
        }
    }

    public void setTitleColor(int color) {
        if (tvTitle.getVisibility() == VISIBLE) {
            tvTitle.setTextColor(color);
        }
    }

    public void setTitleSize(float size) {
        if (tvTitle.getVisibility() == VISIBLE) {
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        }
    }

    public void setRightText(CharSequence rightText) {
        if (TextUtils.isEmpty(rightText)) {
            tvRight.setVisibility(GONE);
        } else {
            tvRight.setText(rightText);
            tvRight.setVisibility(VISIBLE);
        }
    }

    public void setRightTextColor(int color) {
        if (tvRight.getVisibility() == VISIBLE) {
            tvRight.setTextColor(color);
        }
    }

    public void setRightTextSize(float size) {
        if (tvRight.getVisibility() == VISIBLE) {
            tvRight.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        }
    }

    public void setLeftDrawable(int leftImage) {

        RxView.clicks(ivLeft)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object object) throws Exception {
                        if (mContext instanceof Activity) {
                            BaseActivity baseActivity = (BaseActivity) mContext;
                            baseActivity.hideSoftInput();
                            baseActivity.finish();
                        }
                    }
                });

        if (leftImage == -1) {
            ivLeft.setVisibility(GONE);
        } else {
            ivLeft.setImageResource(leftImage);
            ivLeft.setVisibility(VISIBLE);
        }
    }

    public void setLeftDrawable(Drawable leftDrawable) {

        RxView.clicks(ivLeft)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object object) throws Exception {
                        if (mContext instanceof Activity) {
                            BaseActivity baseActivity = (BaseActivity) mContext;
                            baseActivity.hideSoftInput();
                            baseActivity.finish();
                        }
                    }
                });

        if (leftDrawable == null) {
            ivLeft.setVisibility(GONE);
        } else {
            ivLeft.setBackgroundDrawable(leftDrawable);
            ivLeft.setVisibility(VISIBLE);
        }
    }

    public void setRightDrawable(Drawable rightDrawable) {

        if (rightDrawable == null) {
            ivRight.setVisibility(GONE);
        } else {
            ivRight.setBackgroundDrawable(rightDrawable);
            ivRight.setVisibility(VISIBLE);
        }
    }

    public void setRightDrawable(int rightImage) {

        if (rightImage == -1) {
            ivRight.setVisibility(GONE);
        } else {
            ivRight.setImageResource(rightImage);
            ivRight.setVisibility(VISIBLE);
        }
    }

    public void setLeftDrawableClickListener(OnClickListener listener) {
        if (ivLeft.getVisibility() == VISIBLE) {
            RxView.clicks(ivLeft)
                    .throttleFirst(500, TimeUnit.MILLISECONDS)
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object object) throws Exception {
                            if (listener != null) {
                                listener.onClick(ivLeft);
                            }
                        }
                    });
        }
    }

    public void setRightDrawableClickListener(OnClickListener listener) {
        if (ivRight.getVisibility() == VISIBLE) {
            RxView.clicks(ivRight)
                    .throttleFirst(500, TimeUnit.MILLISECONDS)
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object object) throws Exception {
                            if (listener != null) {
                                listener.onClick(ivRight);
                            }
                        }
                    });
        }
    }

    public void build() {
        if (mContext instanceof BaseActivity) {
            ((BaseActivity) mContext).getContentView().addView(this, 0);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

}
