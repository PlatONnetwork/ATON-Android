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
import com.juzix.wallet.component.widget.RoundedTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class VoteConfirmationDialogFragment extends BaseDialogFragment {

    @BindView(R.id.iv_close)
    ImageView       ivClose;
    @BindView(R.id.tv_amount)
    TextView        tvAmount;
    @BindView(R.id.tv_fee)
    TextView        tvFee;
    @BindView(R.id.rtv_confirm)
    RoundedTextView rtvConfirm;
    private Unbinder              unbinder;
    private OnSubmitClickListener mListener;

    public VoteConfirmationDialogFragment setOnSubmitClickListener(OnSubmitClickListener mListener) {
        this.mListener = mListener;
        return this;
    }

    public static VoteConfirmationDialogFragment newInstance(double fee, double amount) {
        VoteConfirmationDialogFragment dialogFragment = new VoteConfirmationDialogFragment();
        Bundle                         bundle         = new Bundle();
        bundle.putDouble(Constants.Bundle.BUNDLE_FEE, fee);
        bundle.putDouble(Constants.Bundle.BUNDLE_FEE_AMOUNT, amount);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_vote_confirmation, null);
        baseDialog.setContentView(contentView);
        setAnimation(R.style.Animation_slide_in_bottom);
        setFullWidthEnable(true);
        unbinder = ButterKnife.bind(this, contentView);
        return baseDialog;
    }

    @OnClick({R.id.iv_close, R.id.rtv_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.rtv_confirm:
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
        double fee = getArguments().getDouble(Constants.Bundle.BUNDLE_FEE);
        double feeAmount = getArguments().getDouble(Constants.Bundle.BUNDLE_FEE_AMOUNT);
        tvAmount.setText(getString(R.string.amount_with_unit, NumberParserUtils.parseDoubleToPrettyNumber(feeAmount)));
        tvFee.setText(getString(R.string.amount_with_unit, NumberParserUtils.parseDoubleToPrettyNumber(fee)));
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
