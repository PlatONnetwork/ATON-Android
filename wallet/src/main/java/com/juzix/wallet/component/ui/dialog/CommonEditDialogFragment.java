package com.juzix.wallet.component.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatDialog;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

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
    private ButtonConfig mLeftButtonConfig;
    private ButtonConfig mRightButtonConfig;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FixedDialog fixedDialog = createDialog(mContext, mTitle, mPreInputInfo, inputType, mLeftButtonConfig, mRightButtonConfig);
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
        dialogFragment.mLeftButtonConfig = leftButtonConfig;
        dialogFragment.mRightButtonConfig = rightButtonConfig;
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

    public static CommonEditDialogFragment createCommonEditDialogFragment(String title, int inputType, String leftText, OnDialogViewClickListener leftDialogViewClickListener, String rightText, OnDialogViewClickListener rightOnDialogViewClickListener) {
        return create(title, "", inputType, new ButtonConfig(leftText, DEFAULT_LEFT_BUTTON_COLOR, leftDialogViewClickListener), new ButtonConfig(rightText, DEFAULT_RIGHT_BUTTON_COLOR, rightOnDialogViewClickListener));
    }

    public static CommonEditDialogFragment createCommonEditDialogFragment(String title, int inputType, String leftText, String rightText, OnDialogViewClickListener rightOnDialogViewClickListener) {
        return create(title, "", inputType, new ButtonConfig(leftText, DEFAULT_LEFT_BUTTON_COLOR, null), new ButtonConfig(rightText, DEFAULT_RIGHT_BUTTON_COLOR, rightOnDialogViewClickListener));
    }

    public static CommonEditDialogFragment createCommonEditDialogFragment(String title, String preInputInfo, int inputType, String leftText, String rightText, OnDialogViewClickListener rightOnDialogViewClickListener) {
        return create(title, preInputInfo, inputType, new ButtonConfig(leftText, DEFAULT_LEFT_BUTTON_COLOR, null), new ButtonConfig(rightText, DEFAULT_RIGHT_BUTTON_COLOR, rightOnDialogViewClickListener));
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
                                     final ButtonConfig leftButton,
                                     final ButtonConfig rightButton) {

        final FixedDialog dialog = new FixedDialog(context);

        dialog.etInputInfo.setInputType(inputType);

        if (inputType == EditorInfo.TYPE_CLASS_TEXT) {
            dialog.etInputInfo.setFilters(new InputFilter.LengthFilter[]{new InputFilter.LengthFilter(12)});
        }

        dialog.etInputInfo.setText(preInputInfo);
        dialog.etInputInfo.setSelection(preInputInfo.length());

        dialog.tvTitle.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);

        if (leftButton == null || TextUtils.isEmpty(leftButton.label)) {
            dialog.tvCancel.setVisibility(View.GONE);
        } else {
            dialog.tvCancel.setVisibility(View.VISIBLE);
            dialog.tvCancel.setText(leftButton.label);
            RxView.clicks(dialog.tvCancel).subscribe(new Consumer<Unit>() {
                @Override
                public void accept(Unit unit) throws Exception {
                    dismiss();
                    if (leftButton.listener != null) {
                        leftButton.listener.onDialogViewClick(CommonEditDialogFragment.this, dialog.tvCancel, null);
                    }
                }
            });
        }

        if (rightButton == null || TextUtils.isEmpty(rightButton.label)) {
            dialog.tvConfirm.setVisibility(View.GONE);
        } else {
            dialog.tvConfirm.setVisibility(View.VISIBLE);
            dialog.tvConfirm.setText(rightButton.label);
            RxView.clicks(dialog.tvConfirm).subscribe(new Consumer<Unit>() {
                @Override
                public void accept(Unit unit) throws Exception {
                    dismiss();
                    if (rightButton.listener != null) {
                        String inputInfo = dialog.etInputInfo.getText().toString().trim();
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.Bundle.BUNDLE_TEXT, inputInfo);
                        rightButton.listener.onDialogViewClick(CommonEditDialogFragment.this, dialog.tvConfirm, bundle);
                    }
                }
            });
            RxTextView.textChanges(dialog.etInputInfo).skipInitialValue().subscribe(new Consumer<CharSequence>() {
                @Override
                public void accept(CharSequence charSequence) throws Exception {
                    String inputInfo = dialog.etInputInfo.getText().toString().trim();
                    dialog.tvConfirm.setEnabled(inputInfo.length() >= (inputType == EditorInfo.TYPE_TEXT_VARIATION_PASSWORD ? 6 : 1));
                }
            });
        }

        if (!TextUtils.isEmpty(title)) {
            dialog.tvTitle.setText(title);
        }

        return dialog;
    }

    private void showSoftInput(final EditText editText) {
        editText.requestFocus();
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(editText, 0);
            }
        }, 100);
    }

    private void hideSoftInput(EditText editText) {
        InputMethodManager mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    static class ButtonConfig {
        public CharSequence label;
        public int color;
        public OnDialogViewClickListener listener;

        public ButtonConfig(CharSequence label, int color, OnDialogViewClickListener listener, boolean enable) {
            this.listener = listener;
            this.label = label;
            this.color = color;
        }

        public ButtonConfig(CharSequence label, int color, OnDialogViewClickListener listener) {
            this(label, color, listener, true);
        }
    }

    class FixedDialog extends AppCompatDialog {

        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.et_input_info)
        EditText etInputInfo;
        @BindView(R.id.tv_cancel)
        TextView tvCancel;
        @BindView(R.id.tv_confirm)
        TextView tvConfirm;

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
