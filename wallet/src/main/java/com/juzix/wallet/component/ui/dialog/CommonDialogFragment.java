package com.juzix.wallet.component.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juzix.wallet.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author matrixelement
 */
public class CommonDialogFragment extends DialogFragment {

    private final static int DEFAULT_LEFT_BUTTON_COLOR = R.color.color_0077ff;
    private final static int DEFAULT_RIGHT_BUTTON_COLOR = R.color.color_0077ff;

    private String mTitle;
    private String mWarnTitle;
    private String mContent;
    private ButtonConfig mLeftButtonConfig;
    private ButtonConfig mRightButtonConfig;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createDialog(getActivity(), mTitle, mWarnTitle, mContent, mLeftButtonConfig, mRightButtonConfig);
    }

    public static CommonDialogFragment createCommontTitleWithTwoButton(String title, String content, String leftText, OnDialogViewClickListener leftDialogViewClickListener, String rightText, OnDialogViewClickListener rightOnDialogViewClickListener) {
        return create(title, null, content, new ButtonConfig(leftText, DEFAULT_LEFT_BUTTON_COLOR, leftDialogViewClickListener), new ButtonConfig(rightText, DEFAULT_RIGHT_BUTTON_COLOR, rightOnDialogViewClickListener));
    }

    public static CommonDialogFragment createWarnTitleWithTwoButton(String warnTitle, String content, String leftText, OnDialogViewClickListener leftDialogViewClickListener, String rightText, OnDialogViewClickListener rightOnDialogViewClickListener) {
        return create(null, warnTitle, content, new ButtonConfig(leftText, DEFAULT_LEFT_BUTTON_COLOR, leftDialogViewClickListener), new ButtonConfig(rightText, DEFAULT_RIGHT_BUTTON_COLOR, rightOnDialogViewClickListener));
    }

    public static CommonDialogFragment createCommonTitleWithOneButton(String title, String content, String leftText, OnDialogViewClickListener leftDialogViewClickListener) {
        return create(title, null, content, new ButtonConfig(leftText, DEFAULT_LEFT_BUTTON_COLOR, leftDialogViewClickListener), null);
    }

    public static CommonDialogFragment createWarnTitleWithOneButton(String warnTitle, String content, String leftText, OnDialogViewClickListener leftDialogViewClickListener) {
        return create(null, warnTitle, content, new ButtonConfig(leftText, DEFAULT_LEFT_BUTTON_COLOR, leftDialogViewClickListener), null);
    }

    private static CommonDialogFragment create(String title, String warnTitle, String content, ButtonConfig leftButtonConfig, ButtonConfig rightButtonConfig) {
        CommonDialogFragment dialogFragment = new CommonDialogFragment();
        dialogFragment.mWarnTitle = warnTitle;
        dialogFragment.mTitle = title;
        dialogFragment.mContent = content;
        dialogFragment.mLeftButtonConfig = leftButtonConfig;
        dialogFragment.mRightButtonConfig = rightButtonConfig;
        return dialogFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.72), ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setWindowAnimations(R.style.Animation_CommonDialog);
        }
    }

    /**
     * 创建通用型的对话框
     *
     * @param context
     * @param title   正文消息
     * @return
     */
    private FixedDialog createDialog(Context context, CharSequence title, CharSequence warnTitle, CharSequence content,
                                     final ButtonConfig leftButton,
                                     final ButtonConfig rightButton) {

        final FixedDialog dialog = new FixedDialog(context);

        dialog.tvTitle.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);

        dialog.tvWarnTitle.setVisibility(TextUtils.isEmpty(warnTitle) ? View.GONE : View.VISIBLE);

        if (leftButton == null || TextUtils.isEmpty(leftButton.label)) {
            dialog.tvCancel.setVisibility(View.GONE);
        } else {
            dialog.tvCancel.setVisibility(View.VISIBLE);
            dialog.tvCancel.setText(leftButton.label);
            dialog.tvCancel.setEnabled(leftButton.enable);
            dialog.tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (leftButton.listener != null) {
                        leftButton.listener.onDialogViewClick(CommonDialogFragment.this, dialog.tvCancel, null);
                    }
                }
            });
        }


        if (rightButton == null || TextUtils.isEmpty(rightButton.label)) {
            dialog.tvConfirm.setVisibility(View.GONE);
        } else {
            dialog.tvConfirm.setVisibility(View.VISIBLE);
            dialog.tvConfirm.setText(rightButton.label);
            dialog.tvConfirm.setEnabled(rightButton.enable);
            dialog.tvConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (rightButton.listener != null) {
                        rightButton.listener.onDialogViewClick(CommonDialogFragment.this, dialog.tvConfirm, null);
                    }
                }
            });
        }

        if (!TextUtils.isEmpty(title)) {
            dialog.tvTitle.setText(title);
        }

        if (!TextUtils.isEmpty(content)) {
            dialog.tvContent.setText(content);
        }

        if (!TextUtils.isEmpty(warnTitle)) {
            dialog.tvWarnTitle.setText(warnTitle);
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


        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_warn_title)
        TextView tvWarnTitle;
        @BindView(R.id.tv_cancel)
        TextView tvCancel;
        @BindView(R.id.tv_confirm)
        TextView tvConfirm;
        @BindView(R.id.tv_content)
        TextView tvContent;

        public FixedDialog(Context context) {
            this(context, R.style.CommonDialogStyle);
        }

        public FixedDialog(Context context, int theme) {
            super(context, theme);
            setContentView(R.layout.dialog_fragment_common);
            ButterKnife.bind(this);
        }
    }
}
