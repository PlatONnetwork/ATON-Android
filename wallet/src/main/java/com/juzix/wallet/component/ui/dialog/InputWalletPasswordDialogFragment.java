package com.juzix.wallet.component.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class InputWalletPasswordDialogFragment extends BaseDialogFragment {

    @BindView(R.id.et_wallet_password)
    EditText etWalletPassword;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    Unbinder unbinder;

    private OnConfirmClickListener onConfirmClickListener;

    public InputWalletPasswordDialogFragment setOnConfirmClickListener(OnConfirmClickListener onConfirmClickListener) {
        this.onConfirmClickListener = onConfirmClickListener;
        return this;
    }

    public static InputWalletPasswordDialogFragment newInstance(String password) {
        InputWalletPasswordDialogFragment dialogFragment = new InputWalletPasswordDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Bundle.BUDLE_PASSWORD, password);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_input_wallet_password, null);
        baseDialog.setContentView(contentView);
        setFullWidthEnable(true);
        setGravity(Gravity.CENTER);
        unbinder = ButterKnife.bind(this, contentView);
        initViews();
        return baseDialog;
    }

    private void initViews() {

        showSoftInput(etWalletPassword);
        String password = getArguments().getString(Constants.Bundle.BUDLE_PASSWORD);
        etWalletPassword.setText(password);
        if (!TextUtils.isEmpty(password)) {
            etWalletPassword.setSelection(password.length());
        }

        etWalletPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                tvConfirm.setEnabled(s.toString().trim().length() >= 6);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick({R.id.tv_cancel, R.id.tv_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_confirm:
                String password = etWalletPassword.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getContext(), getString(R.string.password_cannot_be_empty), Toast.LENGTH_LONG);
                    return;
                }

                if (onConfirmClickListener != null) {
                    dismiss();
                    onConfirmClickListener.onConfirmClick(password);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public interface OnConfirmClickListener {

        void onConfirmClick(String password);

    }
}
