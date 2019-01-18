package com.juzix.wallet.component.widget;

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

import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.utils.DensityUtil;

/**
 * @author matrixelement
 */
public class CommonTitleBar extends LinearLayout {

    private final static int DEFAULT_TITLE_SIZE = 16;
    private TextView tvMiddleTitle;
    private ImageView ivLeft;
    private ImageView ivRight;
    private TextView tvRight;
    private Context context;
    private CharSequence middleText;
    private Drawable leftDrawable;
    private Drawable rightDrawable;
    private int background;
    private int titleColor;
    private float titleSize;
    private CharSequence rightText;

    public CommonTitleBar(Context context) {
        this(context, null, 0);
        init(context);
    }

    public CommonTitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public CommonTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CommonTitleBar, defStyleAttr, 0);

        middleText = ta.getText(R.styleable.CommonTitleBar_ctb_middle_text);
        leftDrawable = ta.getDrawable(R.styleable.CommonTitleBar_ctb_left_drawable);
        rightDrawable = ta.getDrawable(R.styleable.CommonTitleBar_ctb_right_drawable);
        background = ta.getColor(R.styleable.CommonTitleBar_ctb_background, ContextCompat.getColor(context, R.color.color_232e48));
        titleColor = ta.getColor(R.styleable.CommonTitleBar_ctb_title_color, ContextCompat.getColor(context, R.color.color_ffffff));
        titleSize = ta.getDimensionPixelSize(R.styleable.CommonTitleBar_ctb_title_size, DensityUtil.sp2px(context, DEFAULT_TITLE_SIZE));
        rightText = ta.getText(R.styleable.CommonTitleBar_ctb_right_text);

        ta.recycle();

        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.layout_common_title_bar, this);
        tvMiddleTitle = findViewById(R.id.tv_title);
        ivLeft = findViewById(R.id.iv_left);
        ivRight = findViewById(R.id.iv_right);
        tvRight = findViewById(R.id.tv_right);

        setMiddleTitle(middleText);
        setMiddleTitleColor(titleColor);
        setMiddleTitleSize(titleSize);
        setLeftDrawable(leftDrawable);
        setRightDrawable(rightDrawable);
        setBackgroundColor(background);
        setRightText(rightText);
    }

    public CommonTitleBar setMiddleTitle(String middleTitle) {
        if (TextUtils.isEmpty(middleTitle)) {
            tvMiddleTitle.setVisibility(GONE);
        } else {
            tvMiddleTitle.setText(middleTitle);
            tvMiddleTitle.setVisibility(VISIBLE);
        }

        return this;
    }

    public CommonTitleBar setMiddleTitle(CharSequence middleTitle) {
        if (TextUtils.isEmpty(middleTitle)) {
            tvMiddleTitle.setVisibility(GONE);
        } else {
            tvMiddleTitle.setText(middleTitle);
            tvMiddleTitle.setVisibility(VISIBLE);
        }

        return this;
    }

    public CommonTitleBar setRightText(CharSequence rightText) {

        if (TextUtils.isEmpty(rightText)) {
            tvRight.setVisibility(GONE);
        } else {
            tvRight.setText(rightText);
            tvRight.setVisibility(VISIBLE);
        }

        return this;
    }

    public CommonTitleBar setRightText(CharSequence rightText, OnClickListener listener) {

        tvRight.setOnClickListener(listener);

        if (TextUtils.isEmpty(rightText)) {
            tvRight.setVisibility(GONE);
        } else {
            tvRight.setText(rightText);
            tvRight.setVisibility(VISIBLE);
        }

        return this;
    }

    public CommonTitleBar setMiddleTitleColor(int color) {
        if (tvMiddleTitle.getVisibility() == VISIBLE) {
            tvMiddleTitle.setTextColor(color);
        }
        return this;
    }

    public CommonTitleBar setMiddleTitleSize(float size) {
        if (tvMiddleTitle.getVisibility() == VISIBLE) {
            tvMiddleTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        }
        return this;
    }

    public CommonTitleBar setLeftDrawable(int leftImage) {

        ivLeft.setOnClickListener(v -> {
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }
        });

        if (leftImage == -1) {
            ivLeft.setVisibility(GONE);
        } else {
            ivLeft.setImageResource(leftImage);
            ivLeft.setVisibility(VISIBLE);
        }

        return this;
    }

    public void setLeftDrawable(Drawable leftDrawable) {

        ivLeft.setOnClickListener(v -> {
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }
        });
        
        if (leftDrawable == null) {
            ivLeft.setVisibility(GONE);
        } else {
            ivLeft.setBackgroundDrawable(leftDrawable);
            ivLeft.setVisibility(VISIBLE);
        }
    }

    public CommonTitleBar setRightDrawable(Drawable rightDrawable) {
        if (rightDrawable == null) {
            ivRight.setVisibility(GONE);
        } else {
            ivRight.setBackgroundDrawable(rightDrawable);
            ivRight.setVisibility(VISIBLE);
        }
        return this;
    }

    public CommonTitleBar setRightDrawable(int rightImage, OnClickListener listener) {

        ivRight.setOnClickListener(listener);

        if (rightImage == -1) {
            ivRight.setVisibility(GONE);
        } else {
            ivRight.setImageResource(rightImage);
            ivRight.setVisibility(VISIBLE);
        }

        return this;
    }

    public void setLeftImageOnClickListener(OnClickListener listener) {
        if (ivLeft.getVisibility() == VISIBLE) {
            ivLeft.setOnClickListener(listener);
        }
    }

    public void setRightImageOnClickListener(OnClickListener listener) {
        if (ivRight.getVisibility() == VISIBLE) {
            ivRight.setOnClickListener(listener);
        }
    }

    public void build() {
        if (context instanceof BaseActivity) {
            ((BaseActivity) context).getContentView().addView(this, 0);
        }
    }

}
