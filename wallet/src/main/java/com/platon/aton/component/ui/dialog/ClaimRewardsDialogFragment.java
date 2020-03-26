package com.platon.aton.component.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.platon.aton.R;
import com.platon.aton.component.widget.ShadowButton;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.ClaimRewardInfo;
import com.platon.aton.utils.AmountUtil;
import com.platon.aton.utils.BigDecimalUtil;
import com.platon.aton.utils.CommonTextUtils;
import com.platon.aton.utils.DensityUtil;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.app.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class ClaimRewardsDialogFragment extends BaseDialogFragment {

    @BindView(R.id.tv_claim_rewards_amount)
    TextView tvClaimRewardsAmount;
    @BindView(R.id.tv_claim_rewards_tips)
    TextView tvClaimRewardsTips;
    @BindView(R.id.tv_fee_amount)
    TextView tvFeeAmount;
    @BindView(R.id.tv_claim_wallet)
    TextView tvClaimWallet;
    @BindView(R.id.tv_balance_amount)
    TextView tvBalanceAmount;
    @BindView(R.id.sbtn_confirm)
    ShadowButton sbtnConfirm;

    private Unbinder unbinder;
    private OnConfirmBtnClickListener mConfirmBtnClickListener;
    private Disposable mDisposable;

    public static ClaimRewardsDialogFragment newInstance(ClaimRewardInfo claimRewardInfo) {
        ClaimRewardsDialogFragment dialogFragment = new ClaimRewardsDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.Bundle.BUNDLE_DATA, claimRewardInfo);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    public ClaimRewardsDialogFragment setOnConfirmBtnClickListener(OnConfirmBtnClickListener confirmBtnClickListener) {
        this.mConfirmBtnClickListener = confirmBtnClickListener;
        return this;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_claim_rewards, null, false);
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

        ClaimRewardInfo claimRewardInfo = getArguments().getParcelable(Constants.Bundle.BUNDLE_DATA);

        tvClaimRewardsAmount.setText(CommonTextUtils.getPriceTextWithBold(AmountUtil.formatAmountText(claimRewardInfo.getClaimRewardAmount(), 12), ContextCompat.getColor(getContext(), R.color.color_105cfe), ContextCompat.getColor(getContext(), R.color.color_105cfe), DensityUtil.sp2px(getContext(), 12), DensityUtil.sp2px(getContext(), 22)));

        tvFeeAmount.setText(getString(R.string.amount_with_unit, AmountUtil.formatAmountText(claimRewardInfo.getFeeAmount())));

        tvBalanceAmount.setText(getString(R.string.msg_avaliable_balance, AmountUtil.formatAmountText(claimRewardInfo.getAvaliableBalanceAmount())));

        tvClaimWallet.setText(claimRewardInfo.getFromWalletName());

        sbtnConfirm.setEnabled(BigDecimalUtil.isNotSmaller(WalletManager.getInstance().getWalletByAddress(claimRewardInfo.getFromWalletAddress()).getFreeBalance(), claimRewardInfo.getFeeAmount()));

        mDisposable = RxView.clicks(sbtnConfirm)
                .compose(bindToLifecycle())
                .compose(RxUtils.getClickTransformer())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (mConfirmBtnClickListener != null) {
                            dismiss();
                            mConfirmBtnClickListener.onConfirmBtnClick();
                        }
                    }
                })
        ;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    public interface OnConfirmBtnClickListener {

        void onConfirmBtnClick();
    }

}
