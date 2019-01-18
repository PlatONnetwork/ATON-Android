package com.juzix.wallet.component.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class SendTransationDialogFragment extends BaseDialogFragment {

    @BindView(R.id.iv_close)
    ImageView ivClose;
    @BindView(R.id.tv_transaction_amount)
    TextView tvTransactionAmount;
    @BindView(R.id.tv_payment_info)
    TextView tvPaymentInfo;
    @BindView(R.id.tv_to_wallet_address)
    TextView tvToWalletAddress;
    @BindView(R.id.tv_fee_amount)
    TextView tvFeeAmount;

    private Unbinder unbinder;
    private OnSubmitClickListener mListener;

    public SendTransationDialogFragment setOnSubmitClickListener(OnSubmitClickListener mListener) {
        this.mListener = mListener;
        return this;
    }

    public static SendTransationDialogFragment newInstance(String transferAmount, String toAddress, double feeAmount) {
        SendTransationDialogFragment dialogFragment = new SendTransationDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Bundle.BUNDLE_TRANSFER_AMOUNT, transferAmount);
        bundle.putString(Constants.Bundle.BUNDLE_TO_ADDRESS, toAddress);
        bundle.putDouble(Constants.Bundle.BUNDLE_FEE_AMOUNT, feeAmount);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_send_transation, null, false);
        baseDialog.setContentView(contentView);
        setFullWidthEnable(true);
        setAnimation(R.style.Animation_slide_in_bottom);
        unbinder = ButterKnife.bind(this, contentView);
        return baseDialog;
    }

    @OnClick({R.id.iv_close, R.id.rtv_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.rtv_submit:
                if (mListener != null) {
                    dismiss();
                    mListener.onSubmitClick();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSubmitClickListener) {
            mListener = (OnSubmitClickListener) context;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        String transferAmount = getArguments().getString(Constants.Bundle.BUNDLE_TRANSFER_AMOUNT);
        String toAddress = getArguments().getString(Constants.Bundle.BUNDLE_TO_ADDRESS);
        double feeAmount = getArguments().getDouble(Constants.Bundle.BUNDLE_FEE_AMOUNT);

        tvTransactionAmount.setText(getString(R.string.amount_with_unit, NumberParserUtils.getPrettyBalance(transferAmount)));
        tvToWalletAddress.setText(toAddress);
        tvFeeAmount.setText(getString(R.string.amount_with_unit, NumberParserUtils.parseDoubleToPrettyNumber(feeAmount)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public interface OnSubmitClickListener {
        void onSubmitClick();
    }
}
