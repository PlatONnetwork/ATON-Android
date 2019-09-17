package com.juzix.wallet.component.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juzix.wallet.R;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.component.widget.ShadowDrawable;
import com.juzix.wallet.utils.DensityUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author matrixelement
 */
public class CommonTipsDialogFragment extends DialogFragment {

    private final static int DEFAULT_LEFT_BUTTON_COLOR = R.color.color_0077ff;
    private final static int DEFAULT_RIGHT_BUTTON_COLOR = R.color.color_0077ff;

    private Drawable mDrawable;
    private String mTitle;
    private String mContent;
    private ButtonConfig mLeftButtonConfig;
    private ButtonConfig mRightButtonConfig;
    private boolean mCancelable = true;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createDialog(getActivity(), mDrawable, mTitle, mContent, mLeftButtonConfig, mRightButtonConfig);
    }

    public static CommonTipsDialogFragment createDialogWithTitleAndTwoButton(Drawable drawable, String title, String content, String leftText, OnDialogViewClickListener leftDialogViewClickListener, String rightText, OnDialogViewClickListener rightOnDialogViewClickListener, boolean cancelable) {
        return create(drawable, title, content, new ButtonConfig(leftText, DEFAULT_LEFT_BUTTON_COLOR, leftDialogViewClickListener), new ButtonConfig(rightText, DEFAULT_RIGHT_BUTTON_COLOR, rightOnDialogViewClickListener), cancelable);
    }

    public static CommonTipsDialogFragment createDialogWithTitleAndTwoButton(Drawable drawable, String title, String content, String leftText, OnDialogViewClickListener leftDialogViewClickListener, String rightText, OnDialogViewClickListener rightOnDialogViewClickListener) {
        return create(drawable, title, content, new ButtonConfig(leftText, DEFAULT_LEFT_BUTTON_COLOR, leftDialogViewClickListener), new ButtonConfig(rightText, DEFAULT_RIGHT_BUTTON_COLOR, rightOnDialogViewClickListener), false);
    }

    public static CommonTipsDialogFragment createDialogWithTwoButton(Drawable drawable, String content, String leftText, OnDialogViewClickListener leftDialogViewClickListener, String rightText, OnDialogViewClickListener rightOnDialogViewClickListener) {
        return create(drawable, null, content, new ButtonConfig(leftText, DEFAULT_LEFT_BUTTON_COLOR, leftDialogViewClickListener), new ButtonConfig(rightText, DEFAULT_RIGHT_BUTTON_COLOR, rightOnDialogViewClickListener), true);
    }

    public static CommonTipsDialogFragment createDialogWithOneButton(Drawable drawable, String content, String leftText, OnDialogViewClickListener leftDialogViewClickListener) {
        return create(drawable, null, content, new ButtonConfig(leftText, DEFAULT_LEFT_BUTTON_COLOR, leftDialogViewClickListener), null, true);
    }

    public static CommonTipsDialogFragment createDialogWithTitleAndOneButton(Drawable drawable, String title, String content, String leftText, OnDialogViewClickListener leftDialogViewClickListener) {
        return create(drawable, title, content, new ButtonConfig(leftText, DEFAULT_LEFT_BUTTON_COLOR, leftDialogViewClickListener), null, true);
    }

    private static CommonTipsDialogFragment create(Drawable drawable, String title, String content, ButtonConfig leftButtonConfig, ButtonConfig rightButtonConfig, boolean cancelable) {
        CommonTipsDialogFragment dialogFragment = new CommonTipsDialogFragment();
        dialogFragment.mDrawable = drawable;
        dialogFragment.mTitle = title;
        dialogFragment.mContent = content;
        dialogFragment.mLeftButtonConfig = leftButtonConfig;
        dialogFragment.mRightButtonConfig = rightButtonConfig;
        dialogFragment.mCancelable = cancelable;
        return dialogFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.75), ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setWindowAnimations(R.style.Animation_CommonDialog);
            dialog.setCanceledOnTouchOutside(mCancelable);
            dialog.setCancelable(mCancelable);
        }
    }

    /**
     * 创建通用型的对话框
     *
     * @param context
     * @param title   正文消息
     * @return
     */
    private FixedDialog createDialog(Context context, Drawable drawable, CharSequence title, CharSequence content,
                                     final ButtonConfig leftButton,
                                     final ButtonConfig rightButton) {

        final FixedDialog dialog = new FixedDialog(context);

        ShadowDrawable.setShadowDrawable(dialog.layoutContent,
                ContextCompat.getColor(context, R.color.color_ffffff),
                DensityUtil.dp2px(context, 6f),
                ContextCompat.getColor(context, R.color.color_33616161)
                , DensityUtil.dp2px(context, 10f),
                0,
                DensityUtil.dp2px(context, 2f));

        dialog.textTitle.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);

        dialog.image.setImageDrawable(drawable);

        if (rightButton == null || TextUtils.isEmpty(rightButton.label)) {
            dialog.textCancel.setVisibility(View.GONE);
        } else {
            dialog.textCancel.setVisibility(View.VISIBLE);
            dialog.textCancel.setText(rightButton.label);
            dialog.textCancel.setEnabled(rightButton.enable);
            dialog.textCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (rightButton.listener != null) {
                        rightButton.listener.onDialogViewClick(CommonTipsDialogFragment.this, dialog.textCancel, null);
                    }
                }
            });
        }


        if (leftButton == null || TextUtils.isEmpty(leftButton.label)) {
            dialog.buttonConfirm.setVisibility(View.GONE);
        } else {
            dialog.buttonConfirm.setVisibility(View.VISIBLE);
            dialog.buttonConfirm.setText(leftButton.label);
            dialog.buttonConfirm.setEnabled(leftButton.enable);
            dialog.buttonConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (leftButton.listener != null) {
                        dismiss();
                        leftButton.listener.onDialogViewClick(CommonTipsDialogFragment.this, dialog.buttonConfirm, null);
                    }
                }
            });
        }

        if (!TextUtils.isEmpty(title)) {
            dialog.textTitle.setText(title);
        }

        if (!TextUtils.isEmpty(content)) {
            dialog.textContent.setText(content);
        }

        return dialog;
    }

    static class ButtonConfig {
        public CharSequence label;
        public int color;
        public boolean enable;
        public OnDialogViewClickListener listener;

        public ButtonConfig(CharSequence label, int color, OnDialogViewClickListener listener, boolean enable) {
            this.listener = listener;
            this.label = label;
            this.color = color;
            this.enable = enable;
        }

        public ButtonConfig(CharSequence label, int color, OnDialogViewClickListener listener) {
            this(label, color, listener, true);
        }
    }

    class FixedDialog extends AppCompatDialog {

        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.text_title)
        TextView textTitle;
        @BindView(R.id.text_content)
        TextView textContent;
        @BindView(R.id.button_confirm)
        ShadowButton buttonConfirm;
        @BindView(R.id.text_cancel)
        TextView textCancel;
        @BindView(R.id.layout_content)
        LinearLayout layoutContent;

        public FixedDialog(Context context) {
            this(context, R.style.CommonDialogStyle);
        }

        public FixedDialog(Context context, int theme) {
            super(context, theme);
            setContentView(R.layout.dialog_fragment_common_tips);
            ButterKnife.bind(this);
        }
    }
}
