package com.platon.aton.component.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.platon.aton.R;
import com.platon.aton.component.widget.CustomUnderlineEditText;
import com.platon.aton.component.widget.ShadowButton;
import com.platon.aton.component.widget.ShadowDrawable;
import com.platon.aton.utils.DensityUtil;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.app.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

/**
 * @author matrixelement
 */
public class CommonEditDialogFragment extends DialogFragment {

    private final static int DEFAULT_LEFT_BUTTON_COLOR = R.color.color_0077ff;
    private final static int DEFAULT_RIGHT_BUTTON_COLOR = R.color.color_0077ff;

    private Context mContext;
    private String mTitle;
    private String mPreInputInfo;
    private int inputType;
    private ButtonConfig mTopButtonConfig;
    private ButtonConfig mBottomButtonConfig;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FixedDialog fixedDialog = createDialog(mContext, mTitle, mPreInputInfo, inputType, mTopButtonConfig, mBottomButtonConfig);
        fixedDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                if (dialog instanceof FixedDialog) {
                    FixedDialog d = (FixedDialog) dialog;
                    showSoftInput(d.etInputInfo);
                }
            }
        });
        return fixedDialog;
    }

    private static CommonEditDialogFragment create(String title, String preInputInfo, int inputType, ButtonConfig leftButtonConfig, ButtonConfig rightButtonConfig) {
        CommonEditDialogFragment dialogFragment = new CommonEditDialogFragment();
        dialogFragment.mTitle = title;
        dialogFragment.mPreInputInfo = preInputInfo;
        dialogFragment.inputType = inputType;
        dialogFragment.mTopButtonConfig = leftButtonConfig;
        dialogFragment.mBottomButtonConfig = rightButtonConfig;
        return dialogFragment;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void dismiss() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            View view = getDialog().getCurrentFocus();
            if (view instanceof TextView) {
                hideSoftInput((EditText) view);
            }
        }
        super.dismiss();
    }

    @Override
    public void dismissAllowingStateLoss() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            View view = getDialog().getCurrentFocus();
            if (view instanceof TextView) {
                hideSoftInput((EditText) view);
            }
        }
        super.dismissAllowingStateLoss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public static CommonEditDialogFragment createCommonEditDialogFragment(String title, int inputType, String leftText, OnDialogViewClickListener leftDialogViewClickListener, String rightText, OnDialogViewClickListener rightOnDialogViewClickListener) {
        return create(title, "", inputType, new ButtonConfig(leftText, DEFAULT_LEFT_BUTTON_COLOR, leftDialogViewClickListener, false), new ButtonConfig(rightText, DEFAULT_RIGHT_BUTTON_COLOR, rightOnDialogViewClickListener, true));
    }

    public static CommonEditDialogFragment createCommonEditDialogFragment(String title, int inputType, String leftText, String rightText, OnDialogViewClickListener rightOnDialogViewClickListener) {
        return create(title, "", inputType, new ButtonConfig(leftText, DEFAULT_LEFT_BUTTON_COLOR, null), new ButtonConfig(rightText, DEFAULT_RIGHT_BUTTON_COLOR, rightOnDialogViewClickListener));
    }

    public static CommonEditDialogFragment createCommonEditDialogFragment(String title, String preInputInfo, int inputType, String leftText, String rightText, OnDialogViewClickListener rightOnDialogViewClickListener) {
        return create(title, preInputInfo, inputType, new ButtonConfig(leftText, DEFAULT_LEFT_BUTTON_COLOR, rightOnDialogViewClickListener), new ButtonConfig(rightText, DEFAULT_RIGHT_BUTTON_COLOR, null));
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
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(true);
        }
    }

    /**
     * 创建通用型的对话框
     *
     * @param context
     * @param title   正文消息
     * @return
     */
    private FixedDialog createDialog(Context context, String title, String preInputInfo, int inputType,
                                     final ButtonConfig topButton,
                                     final ButtonConfig bottomButton) {

        final FixedDialog dialog = new FixedDialog(context);

        ShadowDrawable.setShadowDrawable(dialog.layoutContent,
                ContextCompat.getColor(context, R.color.color_ffffff),
                DensityUtil.dp2px(context, 6f),
                ContextCompat.getColor(context, R.color.color_33616161)
                , DensityUtil.dp2px(context, 10f),
                0,
                DensityUtil.dp2px(context, 2f));

        dialog.etInputInfo.setInputType(inputType);

        dialog.etInputInfo.setText(preInputInfo);
        dialog.etInputInfo.setSelection(preInputInfo.length());

        dialog.tvTitle.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);

        if (bottomButton == null || TextUtils.isEmpty(bottomButton.label)) {
            dialog.tvCancel.setVisibility(View.GONE);
        } else {
            dialog.tvCancel.setVisibility(View.VISIBLE);
            dialog.tvCancel.setText(bottomButton.label);
            RxView.clicks(dialog.tvCancel).subscribe(new Consumer<Object>() {
                @Override
                public void accept(Object unit) throws Exception {
                    dismiss();
                    if (bottomButton.listener != null) {
                        bottomButton.listener.onDialogViewClick(CommonEditDialogFragment.this, dialog.tvCancel, null);
                    }
                }
            });
        }

        if (topButton == null || TextUtils.isEmpty(topButton.label)) {
            dialog.buttonConfirm.setVisibility(View.GONE);
        } else {
            dialog.buttonConfirm.setVisibility(View.VISIBLE);
            dialog.buttonConfirm.setText(topButton.label);
            RxView.clicks(dialog.buttonConfirm)
                    .compose(RxUtils.getClickTransformer())
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object unit) throws Exception {
                            dismiss();
                            if (topButton.listener != null) {
                                String inputInfo = dialog.etInputInfo.getText().toString().trim();
                                Bundle bundle = new Bundle();
                                bundle.putString(Constants.Bundle.BUNDLE_TEXT, inputInfo);
                                topButton.listener.onDialogViewClick(CommonEditDialogFragment.this, dialog.buttonConfirm, bundle);
                            }
                        }
                    });
            RxTextView.textChanges(dialog.etInputInfo)
                    .subscribe(new Consumer<CharSequence>() {
                        @Override
                        public void accept(CharSequence charSequence) throws Exception {
                            String inputInfo = dialog.etInputInfo.getText().toString().trim();
                            dialog.buttonConfirm.setEnabled(inputInfo.length() >= (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ? 6 : 1));
                        }
                    });
        }

        if (!TextUtils.isEmpty(title)) {
            dialog.tvTitle.setText(title);
        }

        return dialog;
    }

    private void showSoftInput(final EditText editText) {
        if (mContext != null) {
            editText.requestFocus();
            InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(editText, 0);
        }
    }

    private void hideSoftInput(EditText editText) {
        if (mContext != null) {
            InputMethodManager mInputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
    }

    static class ButtonConfig {
        public CharSequence label;
        public int color;
        public OnDialogViewClickListener listener;
        public boolean enable;

        public ButtonConfig(CharSequence label, int color, OnDialogViewClickListener listener, boolean enable) {
            this.listener = listener;
            this.label = label;
            this.color = color;
            this.enable = enable;
        }

        public ButtonConfig(CharSequence label, int color, OnDialogViewClickListener listener) {
            this(label, color, listener, false);
        }
    }

   public class FixedDialog extends AppCompatDialog {

        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.et_input_info)
        public CustomUnderlineEditText etInputInfo;
        @BindView(R.id.button_confirm)
        ShadowButton buttonConfirm;
        @BindView(R.id.tv_cancel)
        TextView tvCancel;
        @BindView(R.id.layout_content)
        LinearLayout layoutContent;

        public FixedDialog(Context context) {
            this(context, R.style.CommonDialogStyle);
        }

        public FixedDialog(Context context, int theme) {
            super(context, theme);
            setContentView(R.layout.dialog_fragment_common_edit);
            ButterKnife.bind(this);
        }
    }
}
