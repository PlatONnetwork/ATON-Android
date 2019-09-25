package com.juzix.wallet.component.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.component.widget.ShadowDrawable;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.juzix.wallet.utils.DensityUtil;
import com.juzix.wallet.utils.JZWalletUtil;
import com.juzix.wallet.utils.RxUtils;
import com.juzix.wallet.utils.StringUtil;

import java.io.Serializable;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;

/**
 * @author matrixelement
 */
public class SendTransactionDialogFragment extends BaseDialogFragment {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_amount)
    TextView tvAmount;
    @BindView(R.id.table_transaction_info)
    LinearLayout tableTransactionInfo;
    @BindView(R.id.sbtn_confirm)
    ShadowButton sbtnConfirm;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.layout_content)
    ConstraintLayout layoutContent;
    @BindString(R.string.pay_wallet)
    String payWallet;

    private Unbinder unbinder;
    private Context mContext;
    private OnConfirmBtnClickListener mListener;


    public SendTransactionDialogFragment setOnConfirmBtnClickListener(OnConfirmBtnClickListener mListener) {
        this.mListener = mListener;
        return this;
    }

    public static SendTransactionDialogFragment newInstance(String title, String amount, Map<String, String> map) {
        SendTransactionDialogFragment dialogFragment = new SendTransactionDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Bundle.BUNDLE_TRANSFER_AMOUNT, amount);
        bundle.putString(Constants.Bundle.BUNDLE_TEXT, title);
        bundle.putSerializable(Constants.Bundle.BUNDLE_MAP, (Serializable) map);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    public static SendTransactionDialogFragment newInstance(String transferAmount, Map<String, String> map) {
        SendTransactionDialogFragment dialogFragment = new SendTransactionDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Bundle.BUNDLE_TRANSFER_AMOUNT, transferAmount);
        bundle.putSerializable(Constants.Bundle.BUNDLE_MAP, (Serializable) map);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_send_transaction, null, false);
        baseDialog.setContentView(contentView);
        setFullWidthEnable(true);
        setHorizontalMargin(DensityUtil.dp2px(getContext(), 14f));
        setyOffset(DensityUtil.dp2px(getContext(), 4f));
        setAnimation(R.style.Animation_slide_in_bottom);
        unbinder = ButterKnife.bind(this, contentView);
        initViews();
        return baseDialog;
    }

    private void initViews() {

        String title = getArguments().getString(Constants.Bundle.BUNDLE_TEXT);
        String amount = getArguments().getString(Constants.Bundle.BUNDLE_TRANSFER_AMOUNT);
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        if (!TextUtils.isEmpty(amount)) {
            tvAmount.setText(StringUtil.formatBalance(amount));
        }

        Map<String, String> data = (Map<String, String>) getArguments().getSerializable(Constants.Bundle.BUNDLE_MAP);

        for (Map.Entry<String, String> entry : data.entrySet()) {
            tableTransactionInfo.addView(buildTransactionInfoItemLayout(entry.getKey(), entry.getValue()));
        }

        ShadowDrawable.setShadowDrawable(layoutContent,
                ContextCompat.getColor(context, R.color.color_ffffff),
                DensityUtil.dp2px(context, 6f),
                ContextCompat.getColor(context, R.color.color_33616161)
                , DensityUtil.dp2px(context, 10f),
                0,
                DensityUtil.dp2px(context, 2f));

        RxView.clicks(sbtnConfirm)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        if (mListener != null) {
                            dismiss();
                            mListener.onConfirmBtnClick();
                        }
                    }
                });
        RxView.clicks(tvCancel)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        dismiss();
                    }
                });
    }

    private View buildTransactionInfoItemLayout(String key, String value) {
        View contentView = inflateTransactionItemView();
        TextView keyText = contentView.findViewById(R.id.tv_key);
        TextView valueText = contentView.findViewById(R.id.tv_value);
        TextView walletAddress = contentView.findViewById(R.id.tv_wallet_address);
        keyText.setText(key);
        boolean isWalletAddressItem = isWalletAddressItem(key, value);
        walletAddress.setVisibility(isWalletAddressItem ? View.VISIBLE : View.GONE);
        if (isWalletAddressItem) {
            String[] array = value.split(":", 2);
            valueText.setText(array[0]);
            walletAddress.setText(AddressFormatUtil.formatAddress(array[1]));
        } else {
            valueText.setText(value);
        }
        contentView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return contentView;
    }

    private boolean isWalletAddressItem(String key, String value) {
        if (payWallet.equals(key) && value.contains(":")) {
            String[] array = value.split(":", 2);
            if (JZWalletUtil.isValidAddress(array[1])) {
                return true;
            }
        }
        return false;
    }

    private View inflateTransactionItemView() {
        return LayoutInflater.from(mContext).inflate(R.layout.include_transaction_info_item, null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public interface OnConfirmBtnClickListener {

        void onConfirmBtnClick();
    }
}
