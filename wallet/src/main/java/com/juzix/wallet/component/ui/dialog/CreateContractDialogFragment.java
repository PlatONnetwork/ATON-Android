package com.juzix.wallet.component.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
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
public class CreateContractDialogFragment extends BaseDialogFragment {

    @BindView(R.id.iv_close)
    ImageView ivClose;
    @BindView(R.id.tv_fee_amount)
    TextView tvFeeAmount;
    @BindView(R.id.rtv_submit)
    RoundedTextView rtvSubmit;

    private Unbinder unbinder;
    private OnSubmitClickListener mListener;

    public CreateContractDialogFragment setOnSubmitClickListener(OnSubmitClickListener mListener) {
        this.mListener = mListener;
        return this;
    }

    public static CreateContractDialogFragment newInstance(double feeAmount) {
        CreateContractDialogFragment dialogFragment = new CreateContractDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putDouble(Constants.Bundle.BUNDLE_FEE_AMOUNT, feeAmount);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_fragment_create_contract, null, false);
        baseDialog.setContentView(contentView);
        setFullWidthEnable(true);
        setGravity(Gravity.BOTTOM);
        setAnimation(R.style.Animation_slide_in_bottom);
        unbinder = ButterKnife.bind(this, contentView);
        initViews();
        return baseDialog;
    }

    private void initViews() {

        double feeAmount = getArguments().getDouble(Constants.Bundle.BUNDLE_FEE_AMOUNT);
        tvFeeAmount.setText(getContext().getString(R.string.amount_with_unit, NumberParserUtils.getPrettyBalance(feeAmount)));
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
