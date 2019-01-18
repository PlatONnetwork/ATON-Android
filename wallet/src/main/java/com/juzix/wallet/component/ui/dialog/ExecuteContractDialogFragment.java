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

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class ExecuteContractDialogFragment extends BaseDialogFragment {

    @BindView(R.id.iv_close)
    ImageView ivClose;
    @BindView(R.id.tv_contract_fee)
    TextView tvContractFee;
    @BindView(R.id.tv_payment_info)
    TextView tvPaymentInfo;
    @BindView(R.id.rtv_submit_contract)
    RoundedTextView rtvSubmitContract;
    @BindView(R.id.tv_execute_contract)
    TextView tvExecuteContract;
    @BindString(R.string.execute_contract_confirm)
    String confirmExecuteContract;
    @BindString(R.string.execute_contract_refuse)
    String refuseExecuteContract;

    private Unbinder unbinder;
    private OnSubmitClickListener mListener;

    public ExecuteContractDialogFragment setOnSubmitClickListener(OnSubmitClickListener mListener) {
        this.mListener = mListener;
        return this;
    }

    public static ExecuteContractDialogFragment newInstance(double feeAmount, int type) {
        ExecuteContractDialogFragment dialogFragment = new ExecuteContractDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putDouble(Constants.Bundle.BUNDLE_FEE_AMOUNT, feeAmount);
        bundle.putInt(Constants.Bundle.BUNDLE_TYPE, type);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_execute_contract, null);
        baseDialog.setContentView(contentView);
        setAnimation(R.style.Animation_slide_in_bottom);
        setFullWidthEnable(true);
        unbinder = ButterKnife.bind(this, contentView);
        initViews();
        return baseDialog;
    }

    private void initViews() {
        double feeAmount = getArguments().getDouble(Constants.Bundle.BUNDLE_FEE_AMOUNT);
        int type = getArguments().getInt(Constants.Bundle.BUNDLE_TYPE);
        tvPaymentInfo.setText(R.string.executeContractFee);
        tvContractFee.setText(getString(R.string.amount_with_unit, NumberParserUtils.parseDoubleToPrettyNumber(feeAmount)));
        tvExecuteContract.setText(type == 1 ? confirmExecuteContract : refuseExecuteContract);
    }

    @OnClick({R.id.iv_close, R.id.rtv_submit_contract})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.rtv_submit_contract:
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
